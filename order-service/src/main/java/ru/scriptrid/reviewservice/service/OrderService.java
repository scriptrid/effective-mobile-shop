package ru.scriptrid.reviewservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.scriptrid.common.dto.*;
import ru.scriptrid.common.exception.FrozenOrganizationException;
import ru.scriptrid.common.exception.FrozenUserException;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.reviewservice.exceptions.InvalidCustomerException;
import ru.scriptrid.reviewservice.exceptions.OrderNotFoundException;
import ru.scriptrid.reviewservice.exceptions.RefundTimeException;
import ru.scriptrid.reviewservice.exceptions.ReservationException;
import ru.scriptrid.reviewservice.model.dto.OrderCreateDto;
import ru.scriptrid.reviewservice.model.dto.OrderDto;
import ru.scriptrid.reviewservice.model.entity.OrderEntity;
import ru.scriptrid.reviewservice.repository.OrderRepository;

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

    public OrderService(WebProductService webProductService,
                        WebUserService webUserService,
                        @Value("${orders.commission}") BigDecimal commission, WebOrganizationService webOrganizationService,
                        OrderRepository orderRepository) {
        this.webProductService = webProductService;
        this.webUserService = webUserService;
        this.commission = commission;
        this.webOrganizationService = webOrganizationService;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderDto addOrder(OrderCreateDto dto, JwtAuthenticationToken token) {
        ProductDto product = webProductService.getDto(dto.productId());
        if (product.quantityInStock() < dto.quantity()) {
            log.warn("Error during reservation: insufficient quantity of product by id \"{}\": expected: {} found: {}",
                    product.id(), dto.quantity(), product.quantityInStock());
            throw new ReservationException(dto.productId(), dto.quantity());
        }
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
        try {
            webProductService.reserveProduct(dto.productId(), dto.quantity());
        } catch (WebClientResponseException.BadRequest e) {
            log.warn("Error during reservation", e);
            throw new ReservationException(e, dto.productId(), dto.quantity());
        }
        return addReservedOrder(dto, product, seller.id(), token.getId());

    }

    private OrderDto addReservedOrder(OrderCreateDto dto, ProductDto product, long sellerId, long customerId) {
        try {
            BigDecimal total = product.price().multiply(BigDecimal.valueOf(dto.quantity()));
            BigDecimal sellersIncome = total.subtract(total.multiply(commission));
            TransactionDto transactionDto = webUserService.transferMoney(new TransactionCreateDto(customerId, sellerId, total, sellersIncome));

            OrderEntity order = orderRepository.save(toOrderEntity(product, dto.quantity(), transactionDto));

            return toOrderDto(order);
        } catch (Throwable e) {
            webProductService.returnProduct(dto.productId(), dto.quantity());
            throw e;
        }
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
        if (Duration.between(timeOfRequest, order.getTimeOfOrder()).compareTo(Duration.ofDays(1)) > 0) {
            log.warn("The time since order with id \"{}\" is greater than 1 day", orderId);
            throw new RefundTimeException(Duration.between(timeOfRequest, order.getTimeOfOrder()));
        }
        TransactionDto returningTransaction = webUserService.returnMoney(order.getTransactionId());
        order.setIsReturned(true);
        order.setReturningTransactionId(returningTransaction.id());

        return toOrderDto(order);
    }


    private OrderDto toOrderDto(OrderEntity entity) {
        return new OrderDto(
                entity.getId(),
                entity.getTimeOfOrder(),
                entity.getTransactionId(),
                entity.getProductId(),
                entity.getCustomerId(),
                entity.getSellerId(),
                entity.getQuantityOfProduct(),
                entity.getProductPrice(),
                entity.getTotalAmount(),
                entity.getIsReturned(),
                entity.getReturningTransactionId()
        );
    }

    private OrderEntity toOrderEntity(ProductDto product, int quantity, TransactionDto dto) {
        OrderEntity entity = new OrderEntity();
        entity.setCustomerId(dto.sourceId());
        entity.setProductId(product.id());
        entity.setTransactionId(dto.id());
        entity.setProductPrice(product.price());
        entity.setQuantityOfProduct(quantity);
        entity.setTotalAmount(dto.sourceDelta());
        entity.setTimeOfOrder(ZonedDateTime.now());
        return entity;
    }

    public List<OrderDto> getOrders(Long userId) {
        List<OrderEntity> orders;
        if (userId == null) {
            orders = orderRepository.findAll();
        } else {
            orders = orderRepository.findByCustomerId(userId);
        }
        return orders
                .stream()
                .map(this::toOrderDto)
                .sorted(Comparator.comparing(OrderDto::timeOfOrder))
                .toList();
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
