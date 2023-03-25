package ru.scriptrid.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.scriptrid.orderservice.model.entity.TransactionEntity;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
}