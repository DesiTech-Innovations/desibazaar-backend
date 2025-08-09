package com.desitech.desibazaar.auth.controller;

import com.desitech.desibazaar.auth.entity.User;
import com.desitech.desibazaar.auth.repository.UserRepository;
import com.desitech.desibazaar.auth.security.JwtUtil;
import com.desitech.desibazaar.auth.service.ResetTokenService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResetTokenService resetTokenService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty())
            return ResponseEntity.status(401).body("Invalid credentials");

        User user = userOpt.get();
        if (!user.isActive())
            return ResponseEntity.status(403).body("User inactive");

        if (!passwordEncoder.matches(request.getPin(), user.getPinHash()))
            return ResponseEntity.status(401).body("Invalid credentials");

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent())
            return ResponseEntity.badRequest().body("Username already exists");

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPinHash(passwordEncoder.encode(request.getPin()));
        user.setRole("OWNER");
        user.setActive(true);

        userRepository.save(user);

        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/forgot-pin")
    public ResponseEntity<?> forgotPin(@RequestBody ForgotPinRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        String token = resetTokenService.createResetToken(request.getUsername());
        // TODO: send token via SMS or email in real app
        return ResponseEntity.ok("Reset token: " + token);
    }

    @PostMapping("/reset-pin")
    public ResponseEntity<?> resetPin(@RequestBody ResetPinRequest request) {
        if (!resetTokenService.validateToken(request.getToken())) {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOpt.get();
        user.setPinHash(passwordEncoder.encode(request.getNewPin()));
        userRepository.save(user);
        resetTokenService.deleteToken(request.getToken());
        return ResponseEntity.ok("PIN reset successful");
    }

    @Data
    static class ForgotPinRequest {
        private String username;
    }

    @Data
    static class ResetPinRequest {
        private String username;
        private String token;
        private String newPin;
    }

    @Data
    static class AuthRequest {
        private String username;
        private String pin;
    }

    @Data
    @AllArgsConstructor
    static class AuthResponse {
        private String token;
    }

    @Data
    static class RegisterRequest {
        private String username;
        private String pin;
    }
}
