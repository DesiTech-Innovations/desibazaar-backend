package com.desitech.vyaparsathi.changelog.repository;

import com.desitech.vyaparsathi.changelog.entity.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {
    List<ChangeLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);

    @Query("SELECT MAX(c.seqNo) FROM ChangeLog c")
    Long findMaxSeqNo();
}