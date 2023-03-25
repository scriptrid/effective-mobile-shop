package ru.scriptrid.orderservice.exceptions;

import lombok.Getter;

@Getter
public class TransactionNotFoundException extends RuntimeException {
    private final Long transactionId;

    public TransactionNotFoundException(Long transactionId) {
        this.transactionId = transactionId;
    }
}
