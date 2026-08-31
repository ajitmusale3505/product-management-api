package com.zestindia.productmanagement.entity;

import com.zestindia.productmanagement.enums.Role;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_username", columnList = "username")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            nullable = false,
            unique = true,
            length = 100
    )
    private String username;


    @Column(
            nullable = false,
            length = 255
    )
    private String password;


    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private Role role;


    @Column(nullable = false)
    private Boolean enabled = true;


    @Column(
            name = "created_on",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdOn;


    @PrePersist
    protected void onCreate() {

        if (createdOn == null) {
            createdOn = LocalDateTime.now();
        }

        if (enabled == null) {
            enabled = true;
        }
    }
}