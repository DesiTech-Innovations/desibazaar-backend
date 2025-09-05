
package com.desitech.vyaparsathi.auth.controller;
import com.desitech.vyaparsathi.auth.dto.*;
import com.desitech.vyaparsathi.auth.entity.User;
import com.desitech.vyaparsathi.common.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desitech.vyaparsathi.auth.service.AuthService;
import com.desitech.vyaparsathi.auth.service.ResetTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private ResetTokenService resetTokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        User user = authService.getUserByUsername(request.getUsername());
        String token = authService.authenticateAndGenerateToken(user, request.getPin());
        String refreshToken = authService.createRefreshToken(user.getUsername());

        logger.info("User login successful: {}", request.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, refreshToken, user.getRole().name()));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        try {
            authService.registerNewUser(request);
            logger.info("User registered: {}", request.getUsername());
            return ResponseEntity.ok("User registered successfully.");
        } catch (Exception e) {
            logger.error("Registration failed for user {}: {}", request.getUsername(), e.getMessage(), e);
            throw new ApplicationException("Registration failed", e);
        }
    }

    @PostMapping("/forgot-pin")
    public ResponseEntity<String> forgotPin(@Valid @RequestBody ForgotPinRequest request) {
        try {
            String token = resetTokenService.createResetToken(request.getUsername());
            logger.info("Reset token created for user {}", request.getUsername());
            // In a real application, you would send this token via SMS or email.
            return ResponseEntity.ok("Reset token created: " + token);
        } catch (Exception e) {
            logger.error("Failed to create reset token for user {}: {}", request.getUsername(), e.getMessage(), e);
            throw new ApplicationException("Failed to create reset token", e);
        }
    }

    @PostMapping("/reset-pin")
    public ResponseEntity<String> resetPin(@Valid @RequestBody ResetPinRequest request) {
        try {
            authService.resetUserPin(request.getUsername(), request.getToken(), request.getNewPin());
            logger.info("PIN reset for user {}", request.getUsername());
            return ResponseEntity.ok("PIN reset successful.");
        } catch (Exception e) {
            logger.error("Failed to reset PIN for user {}: {}", request.getUsername(), e.getMessage(), e);
            throw new ApplicationException("Failed to reset PIN", e);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> body) {
        try {
            String refreshToken = body.get("refreshToken");
            AuthResponse response = authService.refreshAccessToken(refreshToken);
            logger.info("Refreshed access token using refresh token");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to refresh access token: {}", e.getMessage(), e);
            throw new ApplicationException("Failed to refresh access token", e);
        }
    }
}