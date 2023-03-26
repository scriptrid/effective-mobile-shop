package ru.scriptrid.userservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_entity_seq")
    @SequenceGenerator(name = "transaction_entity_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    @Column(name = "destination_id", nullable = false)
    private Long destinationId;

    @Column(name = "source_delta", nullable = false, precision = 19, scale = 2)
    private BigDecimal sourceDelta;

    @Column(name = "destination_delta", nullable = false, precision = 19, scale = 2)
    private BigDecimal destinationDelta;

    @Column(name = "is_return", nullable = false)
    private Boolean isReturn = false;

    @Column(name = "time_of_transaction", nullable = false)
    private ZonedDateTime timeOfTransaction;

}