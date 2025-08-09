package com.desitech.desibazaar.auth.service;

import com.desitech.desibazaar.auth.entity.ResetToken;
import com.desitech.desibazaar.auth.repository.ResetTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        return tokenRepository.findByToken(token)
                .filter(t -> t.getExpiry().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    public void deleteToken(String token) {
        tokenRepository.deleteByToken(token);
    }
}
