package com.desitech.vyaparsathi.audit.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Data
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String action;
    private String entity;
    private String entityId;
    @Column(length = 2000)
    private String details;
    private LocalDateTime timestamp;
}
