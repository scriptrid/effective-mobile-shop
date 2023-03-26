package ru.scriptrid.reviewservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "reviews")
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_entity_seq")
    @SequenceGenerator(name = "review_entity_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "text", length = 2048)
    private String text;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "time_of_review", nullable = false)
    private ZonedDateTime timeOfReview;

}