package com.desitech.vyaparsathi.changelog.service;

import com.desitech.vyaparsathi.changelog.dto.ChangeLogDto;
import com.desitech.vyaparsathi.changelog.entity.ChangeLog;
import com.desitech.vyaparsathi.changelog.mapper.ChangeLogMapper;
import com.desitech.vyaparsathi.changelog.repository.ChangeLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;

@Service
public class ChangeLogService {

    @Autowired
    private ChangeLogRepository repository;

    @Autowired
    private ChangeLogMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void append(String entityType, Long entityId, String operation, Object payload, String deviceId) {
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            // Log the error but don't fail the main transaction
            System.err.println("Failed to serialize changelog payload: " + e.getMessage());
            payloadJson = "{}";
        }

        ChangeLog log = new ChangeLog();
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setOperation(operation);
        log.setPayloadJson(payloadJson);
        log.setDeviceId(deviceId);

        // Use a synchronized block to prevent race conditions on sequence number generation
        synchronized (this) {
            Long maxSeqNo = repository.findMaxSeqNo();
            log.setSeqNo(maxSeqNo == null ? 1L : maxSeqNo + 1);
        }

        repository.save(log);
    }

    public List<ChangeLogDto> getChangeLogsForEntity(String entityType, Long entityId) {
        if (entityType == null || entityId == null) {
            return Collections.emptyList();
        }
        List<ChangeLog> logs = repository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
        return mapper.toDtoList(logs);
    }
}