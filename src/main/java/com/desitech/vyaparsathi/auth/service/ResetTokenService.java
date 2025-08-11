package com.desitech.vyaparsathi.auth.service;

import com.desitech.vyaparsathi.auth.entity.ResetToken;
import com.desitech.vyaparsathi.auth.repository.ResetTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResetTokenService {

    private final ResetTokenRepository tokenRepository;

    public ResetTokenService(ResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String createResetToken(String username) {
        // Token valid for 15 minutes
        ResetToken token = new ResetToken();
        token.setUsername(username);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiry(LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(token);
        return token.getToken();
    }

    public boolean validateToken(String token) {
        Optional<ResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return false;
        }
        ResetToken resetToken = tokenOpt.get();
        return resetToken.getExpiry().isAfter(LocalDateTime.now());
    }

    @Transactional
    public void deleteToken(String token) {
        tokenRepository.deleteByToken(token);
    }

    @Scheduled(cron = "0 0 * * * ?") // Runs at the beginning of every hour
    @Transactional
    public void deleteExpiredTokens() {
        tokenRepository.deleteAllExpiredTokens();
    }
}