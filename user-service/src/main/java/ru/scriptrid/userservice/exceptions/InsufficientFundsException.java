package ru.scriptrid.userservice.exceptions;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class InsufficientFundsException extends RuntimeException {
    private final BigDecimal balance;
    private final BigDecimal total;

    public InsufficientFundsException(BigDecimal balance, BigDecimal total) {
        this.balance = balance;
        this.total = total;
    }
}
