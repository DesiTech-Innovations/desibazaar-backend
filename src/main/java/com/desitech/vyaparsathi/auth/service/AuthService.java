package com.desitech.vyaparsathi.auth.service;

import com.desitech.vyaparsathi.auth.entity.User;
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

    public String authenticateAndGenerateToken(String username, String pin) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or PIN"));

        if (!user.isActive()) {
            throw new BadCredentialsException("User is inactive");
        }

        if (!passwordEncoder.matches(pin, user.getPinHash())) {
            throw new BadCredentialsException("Invalid username or PIN");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getRole());
    }

    public void registerNewUser(String username, String pin) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPinHash(passwordEncoder.encode(pin));
        user.setRole("OWNER");
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
}