package ru.scriptrid.productservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_entity_seq")
    @SequenceGenerator(name = "product_entity_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "product_name", nullable = false, unique = true)
    private String productName;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "organization_id", nullable = false)
    private long organizationId;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "quantity_in_stock", nullable = false)
    private Integer quantityInStock;

    @ElementCollection
    @Column(name = "tag")
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    private Set<String> tags = new LinkedHashSet<>();

    @Lob
    @Column(name = "specs")
    private String specs;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductEntity that = (ProductEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}