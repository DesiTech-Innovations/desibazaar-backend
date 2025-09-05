package com.desitech.vyaparsathi.receiving.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ReceivingTicketAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiving_ticket_id", nullable = false)
    private ReceivingTicket receivingTicket;

    private String fileName;
    private String fileType;
    private String filePath; // Or store the file directly as a byte array using @Lob
}
