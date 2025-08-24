package com.desitech.vyaparsathi.audit.service;

import com.desitech.vyaparsathi.audit.AuditLogDto;
import com.desitech.vyaparsathi.audit.entity.AuditLog;
import com.desitech.vyaparsathi.audit.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogService {
    @Autowired
    private AuditLogRepository repository;

    public void log(String username, String action, String entity, String entityId, String details) {
        AuditLog log = new AuditLog();
        log.setUsername(username);
        log.setAction(action);
        log.setEntity(entity);
        log.setEntityId(entityId);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        repository.save(log);
    }

    public List<AuditLogDto> getLogs(LocalDateTime start, LocalDateTime end) {
        return repository.findByTimestampBetweenOrderByTimestampDesc(start, end)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<AuditLogDto> getLogsByUser(String username) {
        return repository.findByUsernameOrderByTimestampDesc(username)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private AuditLogDto toDto(AuditLog log) {
        return new AuditLogDto(
                log.getId(),
                log.getUsername(),
                log.getAction(),
                log.getEntity(),
                log.getEntityId(),
                log.getDetails(),
                log.getTimestamp()
        );
    }
}
