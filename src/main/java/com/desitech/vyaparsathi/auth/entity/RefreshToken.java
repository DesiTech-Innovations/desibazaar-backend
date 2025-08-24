package com.desitech.vyaparsathi.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(name = "created_at", nullable = false)
    private Instant createdDate = Instant.now();

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean revoked;
}