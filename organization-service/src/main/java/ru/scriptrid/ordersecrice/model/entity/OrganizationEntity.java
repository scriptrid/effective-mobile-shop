package ru.scriptrid.ordersecrice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "organizations")
public class OrganizationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organization_entity_seq")
    @SequenceGenerator(name = "organization_entity_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "owner", nullable = false)
    private Long ownerId;

    @Column(name = "description")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "is_frozen")
    private Boolean isFrozen = false;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

}