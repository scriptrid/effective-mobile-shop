package ru.scriptrid.orderservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "transaction_entity")
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_entity_seq")
    @SequenceGenerator(name = "transaction_entity_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "total", nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    @Column(name = "sellers_income", nullable = false, precision = 19, scale = 2)
    private BigDecimal sellersIncome;

}