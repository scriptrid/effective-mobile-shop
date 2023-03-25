package ru.scriptrid.orderservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "order_entity")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_entity_seq")
    @SequenceGenerator(name = "order_entity_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

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


}