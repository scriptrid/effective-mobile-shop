package ru.scriptrid.ordersecrice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "requests_organization")
public class RequestOrganizationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_organization_entity_seq")
    @SequenceGenerator(name = "request_organization_entity_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "organization_name", nullable = false)
    private String name;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "organization_description", length = 2048)
    private String description;

    @Column(name = "organization_owner", nullable = false)
    private Long ownerId;

}