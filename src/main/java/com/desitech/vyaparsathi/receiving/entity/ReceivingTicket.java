package com.desitech.vyaparsathi.receiving.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class ReceivingTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiving_id", nullable = false)
    private Receiving receiving;

    private String reason; // e.g., "Quantity Shortage", "Wrong Item", "Damaged"
    private String description;
    private String status; // e.g., "Open", "In Progress", "Resolved", "Closed"
    private LocalDateTime raisedAt;
    private String raisedBy;

    @OneToMany(mappedBy = "receivingTicket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceivingTicketAttachment> attachments;
}
