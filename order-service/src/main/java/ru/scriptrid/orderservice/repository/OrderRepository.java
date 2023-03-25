package ru.scriptrid.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.scriptrid.orderservice.model.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}