package ru.scriptrid.productservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "discounts")
public class DiscountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "discount_entity_seq")
    @SequenceGenerator(name = "discount_entity_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToMany
    @JoinTable(name = "discounts_products",
            joinColumns = @JoinColumn(name = "discount_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<ProductEntity> products = new LinkedHashSet<>();


    @Column(name = "discount_start", nullable = false)
    private ZonedDateTime discountStart;

    @Column(name = "discount_end")
    private ZonedDateTime discountEnd;

    @Column(name = "price_modifier", nullable = false, precision = 19, scale = 2)
    private BigDecimal priceModifier;

}