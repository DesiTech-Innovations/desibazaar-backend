package com.desitech.desibazaar.changelog.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "change_log")
@Data
public class ChangeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    private String operation;

    @Column(name = "payload_json")
    private String payloadJson;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}