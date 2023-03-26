package ru.scriptrid.reviewservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_entity_seq")
    @SequenceGenerator(name = "order_entity_seq")
    @Column(name = "order_id", nullable = false)
    private Long id;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "quantity_of_product", nullable = false)
    private Integer quantityOfProduct;

    @Column(name = "product_price", precision = 19, scale = 2)
    private BigDecimal productPrice;

    @Column(name = "total", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "time_of_order", nullable = false)
    private ZonedDateTime timeOfOrder;

    @Column(name = "is_returned", nullable = false)
    private Boolean isReturned = false;

    @Column(name = "returning_transaction_id")
    private Long returningTransactionId = null;

}