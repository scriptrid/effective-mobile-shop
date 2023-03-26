package ru.scriptrid.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.scriptrid.productservice.model.entity.ProductEntity;

import java.util.Collection;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    Set<ProductEntity> findByIdIn(Collection<Long> ids);
    boolean existsByProductName(String productName);
}