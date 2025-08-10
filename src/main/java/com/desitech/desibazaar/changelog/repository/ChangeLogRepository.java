package com.desitech.desibazaar.changelog.repository;

import com.desitech.desibazaar.changelog.entity.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {
}