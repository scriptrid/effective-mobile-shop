package ru.scriptrid.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.scriptrid.productservice.model.entity.ProductEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    Set<ProductEntity> findByIdIn(Collection<Long> ids);
    boolean existsByProductName(String productName);

    @Query("select p from ProductEntity p left join fetch p.discounts where p.id = ?1")
    Optional<ProductEntity> findByIdWithDiscounts(long id);

    @Query("select p from ProductEntity p left join fetch p.discounts ")
    List<ProductEntity> findAllWithDiscounts();
}