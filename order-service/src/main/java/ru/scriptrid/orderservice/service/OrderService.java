package ru.scriptrid.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.scriptrid.common.dto.OrganizationDto;
import ru.scriptrid.common.dto.ProductDto;
import ru.scriptrid.common.dto.TransactionCreateDto;
import ru.scriptrid.common.dto.UserDto;
import ru.scriptrid.common.exception.FrozenOrganizationException;
import ru.scriptrid.common.exception.FrozenUserException;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.orderservice.exceptions.InvalidCustomerException;
import ru.scriptrid.orderservice.exceptions.OrderNotFoundException;
import ru.scriptrid.orderservice.exceptions.RefundTimeException;
import ru.scriptrid.orderservice.exceptions.TransactionNotFoundException;
import ru.scriptrid.orderservice.model.dto.OrderCreateDto;
import ru.scriptrid.orderservice.model.dto.OrderDto;
import ru.scriptrid.orderservice.model.entity.OrderEntity;
import ru.scriptrid.orderservice.model.entity.TransactionEntity;
import ru.scriptrid.orderservice.repository.OrderRepository;
import ru.scriptrid.orderservice.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebProductService webProductService;
    private final WebUserService webUserService;
    private final WebOrganizationService webOrganizationService;
    private final BigDecimal commission;
    private final TransactionRepository transactionRepository;

    public OrderService(WebProductService webProductService,
                        WebUserService webUserService,
                        @Value("${orders.commission}") BigDecimal commission, WebOrganizationService webOrganizationService,
                        OrderRepository orderRepository, TransactionRepository transactionRepository) {
        this.webProductService = webProductService;
        this.webUserService = webUserService;
        this.commission = commission;
        this.webOrganizationService = webOrganizationService;
        this.orderRepository = orderRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public OrderDto addOrder(OrderCreateDto dto, JwtAuthenticationToken token) {
        ProductDto product = webProductService.getDto(dto.productId());

        OrganizationDto sellersOrganization = webOrganizationService.getDto(product.organizationId());
        if (sellersOrganization.isFrozen()) {
            log.warn("Seller organization with id \"{}\" is frozen", sellersOrganization.id());
            throw new FrozenOrganizationException(sellersOrganization.id());
        }

        UserDto seller = webUserService.getDto(sellersOrganization.ownerId());
        if (seller.isFrozen()) {
            log.warn("Seller user with id \"{}\" is frozen", seller.id());
            throw new FrozenUserException(seller.id());
        }

        webProductService.reserveProduct(dto.productId(), dto.quantity());
        return addReservedOrder(dto, product, seller.id(), token.getId());

    }

    @Transactional
    public OrderDto refundOrder(long customerId, long orderId, ZonedDateTime timeOfRequest) {
        if (!orderRepository.existsById(orderId)) {
            log.warn("The order with id \"{}\" not found", orderId);
            throw new OrderNotFoundException(orderId);
        }

        OrderEntity order = getOrderById(orderId);

        if (customerId != order.getCustomerId()) {
            log.warn("The user with id \"{}\" is not the customer of order with id \"{}\"", customerId, orderId);
            throw new InvalidCustomerException(order.getId(), customerId);
        }
        if (Duration.between(timeOfRequest, order.getTimeOfOrder()).toDays() > 1) {
            log.warn("The time since order with id \"{}\" is greater than 1 day", orderId);
            throw new RefundTimeException(Duration.between(timeOfRequest, order.getTimeOfOrder()));
        }
        order.setIsReturned(true);
        TransactionEntity orderTransaction = getTransaction(order.getTransactionId());
        TransactionCreateDto transactionCreateDto = new TransactionCreateDto(
                orderTransaction.getCustomerId(),
                orderTransaction.getSellerId(),
                orderTransaction.getTotal().multiply(BigDecimal.valueOf(-1)),
                orderTransaction.getSellersIncome().multiply(BigDecimal.valueOf(-1))
        );
        webUserService.transferMoney(transactionCreateDto);
        return toOrderDto(order);
    }

    private OrderDto addReservedOrder(OrderCreateDto dto, ProductDto product, long sellerId, long customerId) {
        try {
            BigDecimal total = product.price().multiply(BigDecimal.valueOf(dto.quantity()));
            BigDecimal sellersIncome = total.subtract(total.multiply(commission));

            TransactionCreateDto transactionCreateDto = new TransactionCreateDto(customerId, sellerId, total, sellersIncome);
            webUserService.transferMoney(transactionCreateDto);

            TransactionEntity transaction = transactionRepository.save(toTransactionEntity(transactionCreateDto));
            OrderEntity order = orderRepository.save(toOrderEntity(product, dto.quantity(), transaction));

            return toOrderDto(order);
        } catch (Throwable e) {
            webProductService.returnProduct(dto.productId(), dto.quantity());
            throw e;
        }
    }


    private OrderDto toOrderDto(OrderEntity entity) {
        return new OrderDto(
                entity.getId(),
                entity.getTimeOfOrder(),
                entity.getTransactionId(),
                entity.getProductId(),
                entity.getCustomerId(),
                entity.getQuantityOfProduct(),
                entity.getProductPrice(),
                entity.getTotalAmount(),
                entity.getIsReturned()
        );
    }

    private OrderEntity toOrderEntity(ProductDto product, int quantity, TransactionEntity transaction) {
        OrderEntity entity = new OrderEntity();
        entity.setCustomerId(transaction.getCustomerId());
        entity.setProductId(product.id());
        entity.setTransactionId(transaction.getId());
        entity.setProductPrice(product.price());
        entity.setQuantityOfProduct(quantity);
        entity.setTotalAmount(transaction.getTotal());
        entity.setTimeOfOrder(ZonedDateTime.now());
        return entity;
    }

    private TransactionEntity toTransactionEntity(TransactionCreateDto transactionCreateDto) {
        TransactionEntity entity = new TransactionEntity();
        entity.setCustomerId(transactionCreateDto.customerId());
        entity.setSellerId(transactionCreateDto.sellerId());
        entity.setTotal(transactionCreateDto.total());
        entity.setSellersIncome(transactionCreateDto.sellersIncome());
        return entity;
    }

    public List<OrderDto> getUsersOrders(long userId) {
        return orderRepository.findAll()
                .stream()
                .filter(order -> order.getCustomerId() == userId)
                .map(this::toOrderDto)
                .sorted(Comparator.comparing(OrderDto::timeOfOrder))
                .toList();
    }

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toOrderDto)
                .sorted(Comparator.comparing(OrderDto::timeOfOrder))
                .toList();
    }

    private TransactionEntity getTransaction(long id) {
        return transactionRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("Transaction with id \"{}\" not found", id);
                    return new TransactionNotFoundException(id);
                }
        );
    }

    private OrderEntity getOrderById(long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> {
                    log.warn("The order with id \"{}\" not found", orderId);
                    return new OrderNotFoundException(orderId);
                }
        );
    }
}
