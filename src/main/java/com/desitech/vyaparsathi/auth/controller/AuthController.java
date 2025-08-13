package com.desitech.vyaparsathi.auth.controller;

import com.desitech.vyaparsathi.auth.dto.AuthRequest;
import com.desitech.vyaparsathi.auth.dto.AuthResponse;
import com.desitech.vyaparsathi.auth.dto.ForgotPinRequest;
import com.desitech.vyaparsathi.auth.dto.RegisterRequest;
import com.desitech.vyaparsathi.auth.dto.ResetPinRequest;
import com.desitech.vyaparsathi.auth.service.AuthService;
import com.desitech.vyaparsathi.auth.service.ResetTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ResetTokenService resetTokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        String token = authService.authenticateAndGenerateToken(request.getUsername(), request.getPin());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.registerNewUser(request);
        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/forgot-pin")
    public ResponseEntity<String> forgotPin(@Valid @RequestBody ForgotPinRequest request) {
        String token = resetTokenService.createResetToken(request.getUsername());
        // In a real application, you would send this token via SMS or email.
        return ResponseEntity.ok("Reset token created: " + token);
    }

    @PostMapping("/reset-pin")
    public ResponseEntity<String> resetPin(@Valid @RequestBody ResetPinRequest request) {
        authService.resetUserPin(request.getUsername(), request.getToken(), request.getNewPin());
        return ResponseEntity.ok("PIN reset successful.");
    }
}