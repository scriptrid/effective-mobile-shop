package ru.scriptrid.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.scriptrid.common.dto.TransactionCreateDto;
import ru.scriptrid.common.dto.TransactionDto;
import ru.scriptrid.userservice.exceptions.InsufficientFundsException;
import ru.scriptrid.userservice.exceptions.TransactionNotFoundException;
import ru.scriptrid.userservice.model.entity.TransactionEntity;
import ru.scriptrid.userservice.model.entity.UserEntity;
import ru.scriptrid.userservice.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Service
@Slf4j
public class TransactionService {

    private final UserService userService;
    private final TransactionRepository transactionRepository;

    public TransactionService(UserService userService,
                              TransactionRepository transactionRepository) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionDto transferMoney(TransactionCreateDto dto) {
        UserEntity customer = userService.getUserById(dto.customerId());
        UserEntity seller = userService.getUserById(dto.sellerId());

        if (customer.getBalance().compareTo(dto.total()) < 0) {
            log.warn("Customer by id \"{}\" has not enough money", customer.getId());
            throw new InsufficientFundsException(customer.getBalance(), dto.total());
        }

        customer.setBalance(customer.getBalance().subtract(dto.total()));
        seller.setBalance(seller.getBalance().add(dto.sellersIncome()));


        TransactionEntity transaction = transactionRepository.save(toTransactionEntity(dto));
        return toTransactionDto(transaction);
    }

    @Transactional
    public TransactionDto returnMoney(long originalTransactionId) {
        TransactionEntity originalTransaction = getEntityById(originalTransactionId);

        UserEntity customer = userService.getUserById(originalTransaction.getSourceId());
        UserEntity seller = userService.getUserById(originalTransaction.getDestinationId());

        TransactionEntity returnTransaction = new TransactionEntity();
        returnTransaction.setDestinationId(customer.getId());
        returnTransaction.setSourceId(seller.getId());
        returnTransaction.setSourceDelta(originalTransaction.getDestinationDelta().multiply(BigDecimal.valueOf(-1)));
        returnTransaction.setDestinationDelta(originalTransaction.getSourceDelta().multiply(BigDecimal.valueOf(-1)));
        returnTransaction.setIsReturn(true);
        returnTransaction.setTimeOfTransaction(ZonedDateTime.now());

        customer.setBalance(customer.getBalance().add(returnTransaction.getDestinationDelta()));
        seller.setBalance(seller.getBalance().add(returnTransaction.getSourceDelta()));

        log.info("Money successfully returned. Original transaction id: {}", originalTransactionId);
        return toTransactionDto(transactionRepository.save(returnTransaction));
    }

    private TransactionEntity toTransactionEntity(TransactionCreateDto transactionCreateDto) {
        TransactionEntity entity = new TransactionEntity();
        entity.setSourceId(transactionCreateDto.customerId());
        entity.setDestinationId(transactionCreateDto.sellerId());
        entity.setSourceDelta(transactionCreateDto.total().multiply(BigDecimal.valueOf(-1)));
        entity.setDestinationDelta(transactionCreateDto.sellersIncome());
        entity.setTimeOfTransaction(ZonedDateTime.now());
        return entity;
    }

    private TransactionEntity getEntityById(long id) {
        return transactionRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("Transaction with id \"{}\" not found", id);
                    return new TransactionNotFoundException(id);
                }
        );
    }

    private TransactionDto toTransactionDto(TransactionEntity transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getSourceId(),
                transaction.getDestinationId(),
                transaction.getSourceDelta(),
                transaction.getDestinationDelta(),
                transaction.getIsReturn(),
                transaction.getTimeOfTransaction()
        );
    }
}
