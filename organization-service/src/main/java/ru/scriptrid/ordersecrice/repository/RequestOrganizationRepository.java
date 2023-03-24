package ru.scriptrid.ordersecrice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.scriptrid.ordersecrice.model.entity.RequestOrganizationEntity;

public interface RequestOrganizationRepository extends JpaRepository<RequestOrganizationEntity, Long> {

}