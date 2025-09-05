package com.desitech.vyaparsathi.receiving.dto;

import lombok.Data;

@Data
public class ReceivingTicketDTO {
    private Long receivingId;
    private String reason;
    private String description;
    private String raisedBy;
}
