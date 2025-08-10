package com.desitech.desibazaar.changelog.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChangeLogDto {
    private String entityType;
    private Long entityId;
    private String operation;
    private String payloadJson;
    private String deviceId;
    private Long seqNo;
    private LocalDateTime createdAt;
}