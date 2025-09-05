package com.desitech.vyaparsathi.receiving.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ReceivingTicketAttachmentDTO {
    private Long receivingTicketId;
    private MultipartFile file;
}
