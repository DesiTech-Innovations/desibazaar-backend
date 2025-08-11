package com.desitech.vyaparsathi.changelog.controller;

import com.desitech.vyaparsathi.changelog.dto.ChangeLogDto;
import com.desitech.vyaparsathi.changelog.service.ChangeLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/changelog")
@PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
public class ChangeLogController {

    @Autowired
    private ChangeLogService service;

    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<List<ChangeLogDto>> getChangeLogs(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        List<ChangeLogDto> logs = service.getChangeLogsForEntity(entityType, entityId);
        if (logs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(logs);
    }
}