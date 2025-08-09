package com.desitech.desibazaar.auth.repository;

import com.desitech.desibazaar.auth.entity.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
    Optional<ResetToken> findByToken(String token);
    void deleteByToken(String token);
}
