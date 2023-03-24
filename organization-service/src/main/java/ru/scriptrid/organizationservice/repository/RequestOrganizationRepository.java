package ru.scriptrid.organizationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.scriptrid.organizationservice.model.entity.RequestOrganizationEntity;

public interface RequestOrganizationRepository extends JpaRepository<RequestOrganizationEntity, Long> {

}