package ru.scriptrid.ordersecrice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.scriptrid.ordersecrice.model.entity.OrganizationEntity;

import java.util.List;

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long>, JpaSpecificationExecutor<OrganizationEntity> {
    List<OrganizationEntity> findByIsDeletedFalse();
    OrganizationEntity findByName(String name);
    boolean existsByName(String name);
}