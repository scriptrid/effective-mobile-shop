package ru.scriptrid.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.scriptrid.common.dto.OrganizationDto;
import ru.scriptrid.common.dto.ProductDto;
import ru.scriptrid.common.dto.TransactionCreateDto;
import ru.scriptrid.common.dto.UserDto;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.orderservice.model.dto.OrderCreateDto;
import ru.scriptrid.orderservice.model.dto.OrderDto;
import ru.scriptrid.orderservice.model.entity.OrderEntity;
import ru.scriptrid.orderservice.model.entity.TransactionEntity;
import ru.scriptrid.orderservice.repository.OrderRepository;
import ru.scriptrid.orderservice.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

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
        UserDto seller = webUserService.getDto(sellersOrganization.ownerId());
        webProductService.reserveProduct(dto.productId(), dto.quantity());
        return addReservedOrder(dto, product,  seller.id(), token.getId());

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
}
