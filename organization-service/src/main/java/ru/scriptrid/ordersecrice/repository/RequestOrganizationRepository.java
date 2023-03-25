package ru.scriptrid.ordersecrice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.scriptrid.ordersecrice.model.entity.RequestOrganizationEntity;

@Repository
public interface RequestOrganizationRepository extends JpaRepository<RequestOrganizationEntity, Long> {

}