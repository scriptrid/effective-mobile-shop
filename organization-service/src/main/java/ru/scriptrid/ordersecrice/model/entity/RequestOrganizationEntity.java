package ru.scriptrid.ordersecrice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "request_organization_entity")
public class RequestOrganizationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_organization_entity_seq")
    @SequenceGenerator(name = "request_organization_entity_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    @Lob
    @Column(name = "organization_description")
    private String organizationDescription;

    @Column(name = "organization_owner", nullable = false)
    private String organizationOwner;

}