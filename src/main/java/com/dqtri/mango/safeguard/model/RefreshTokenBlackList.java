package com.dqtri.mango.safeguard.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "refresh_token_black_list")
public class RefreshTokenBlackList extends BaseEntity {
    @Column(name = "email", length = 320, nullable = false)
    private String email;
    @Column(name = "token",nullable = false, unique = true)
    private String token;
    @Column(name = "expiration_date", nullable = false)
    private Instant expirationDate;
}

