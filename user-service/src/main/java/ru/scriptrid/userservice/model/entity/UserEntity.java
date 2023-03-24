package ru.scriptrid.userservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_entity_seq")
    @SequenceGenerator(name = "user_entity_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;



    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "is_frozen")
    private Boolean isFrozen = false;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

}