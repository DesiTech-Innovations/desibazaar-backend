package com.desitech.desibazaar.changelog.service;

import com.desitech.desibazaar.changelog.dto.ChangeLogDto;
import com.desitech.desibazaar.changelog.entity.ChangeLog;
import com.desitech.desibazaar.changelog.mapper.ChangeLogMapper;
import com.desitech.desibazaar.changelog.repository.ChangeLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChangeLogService {

    @Autowired
    private ChangeLogRepository repository;

    @Autowired
    private ChangeLogMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;  // Add Jackson ObjectMapper bean if not present

    public void append(String entityType, Long entityId, String operation, Object payload, String deviceId) {
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            payloadJson = "{}";  // Fallback
        }

        ChangeLogDto dto = new ChangeLogDto();
        dto.setEntityType(entityType);
        dto.setEntityId(entityId);
        dto.setOperation(operation);
        dto.setPayloadJson(payloadJson);
        dto.setDeviceId(deviceId);
        dto.setSeqNo(repository.count() + 1);  // Simple seq; use atomic for prod

        ChangeLog log = mapper.toEntity(dto);
        repository.save(log);
    }
}