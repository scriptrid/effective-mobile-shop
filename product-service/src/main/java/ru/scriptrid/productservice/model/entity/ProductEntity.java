package ru.scriptrid.productservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.util.*;

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

    @Column(name = "description", length = 2048)
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
    private Set<String> tags = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "product_specs",
            joinColumns = {@JoinColumn(name = "product_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "spec_name")
    @Column(name = "spec_value")
    private Map<String, String> specs = new HashMap<>();

    @ManyToMany(mappedBy = "products")
    private Set<DiscountEntity> discounts = new LinkedHashSet<>();

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