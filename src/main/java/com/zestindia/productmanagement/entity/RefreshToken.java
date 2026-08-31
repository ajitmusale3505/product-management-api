package com.zestindia.productmanagement.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_token", columnList = "token")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            nullable = false,
            unique = true,
            length = 500
    )
    private String token;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;


    @Column(
            name = "expiry_date",
            nullable = false
    )
    private LocalDateTime expiryDate;


    @Column(nullable = false)
    private Boolean revoked = false;


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

        if (revoked == null) {
            revoked = false;
        }
    }
}