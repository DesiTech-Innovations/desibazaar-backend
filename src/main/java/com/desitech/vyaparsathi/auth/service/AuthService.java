package com.desitech.vyaparsathi.auth.service;

import com.desitech.vyaparsathi.auth.dto.RegisterRequest;
import com.desitech.vyaparsathi.auth.entity.User;
import com.desitech.vyaparsathi.auth.entity.RefreshToken;
import com.desitech.vyaparsathi.auth.repository.UserRepository;
import com.desitech.vyaparsathi.auth.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ResetTokenService resetTokenService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public String authenticateAndGenerateToken(String username, String pin) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or PIN"));

        if (!user.isActive()) {
            throw new BadCredentialsException("User is inactive");
        }

        if (!passwordEncoder.matches(pin, user.getPinHash())) {
            throw new BadCredentialsException("Invalid username or PIN");
        }

        return jwtUtil.generateAccessToken(user.getUsername(), user.getRole().name());
    }

    public void registerNewUser(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPinHash(passwordEncoder.encode(request.getPin()));
        user.setRole(request.getRole());
        user.setActive(true);
        userRepository.save(user);
    }

    @Transactional
    public void resetUserPin(String username, String token, String newPin) {
        if (!resetTokenService.validateToken(token)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setPinHash(passwordEncoder.encode(newPin));
        userRepository.save(user);
        resetTokenService.deleteToken(token);
    }

    // Call this on login to issue refresh token
    public String createRefreshToken(String username) {
        return refreshTokenService.createRefreshToken(username).getToken();
    }
    public String refreshAccessToken(String refreshToken) {
        RefreshToken token = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (refreshTokenService.isExpired(token)) {
            refreshTokenService.deleteByUsername(token.getUsername());
            throw new RuntimeException("Refresh token expired");
        }
        return jwtUtil.generateRefreshToken(token.getUsername());
    }
}