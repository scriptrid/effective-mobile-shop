package ru.scriptrid.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.scriptrid.productservice.model.entity.ProductEntity;

@Repository
public interface RequestNewProductRepository extends JpaRepository<ProductEntity, Long> {
}