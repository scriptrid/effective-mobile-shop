package ru.scriptrid.userservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.scriptrid.common.dto.TransactionCreateDto;
import ru.scriptrid.common.dto.TransactionDto;
import ru.scriptrid.userservice.service.TransactionService;

@RestController
@RequestMapping("/api/transaction/")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PreAuthorize("hasAuthority('ROLE_SERVICE')")
    @PutMapping("/transfer")
    public TransactionDto transferMoney(@RequestBody TransactionCreateDto dto) {
       return transactionService.transferMoney(dto);
    }

    @PreAuthorize("hasAuthority('ROLE_SERVICE')")
    @PutMapping("/transfer/return")
    public TransactionDto returnMoney(@RequestBody long transactionId) {
        return transactionService.returnMoney(transactionId);
    }
}
