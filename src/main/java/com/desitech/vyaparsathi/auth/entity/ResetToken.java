package com.desitech.vyaparsathi.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reset_token")
@Data
@NoArgsConstructor
public class ResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private LocalDateTime expiry;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean used = false;
}