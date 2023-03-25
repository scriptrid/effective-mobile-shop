package ru.scriptrid.productservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "request_new_product_entity")
public class RequestNewProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_new_product_entity_seq")
    @SequenceGenerator(name = "request_new_product_entity_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity_in_stock", nullable = false)
    private Integer quantityInStock;

    @ElementCollection
    @Column(name = "tag")
    @CollectionTable(name = "request_new_product_tags", joinColumns = @JoinColumn(name = "request_product_id"))
    private Set<String> tags = new LinkedHashSet<>();

    @Lob
    @Column(name = "specs")
    private String specs;

}