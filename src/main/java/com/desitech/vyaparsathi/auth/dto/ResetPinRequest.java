package com.desitech.vyaparsathi.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPinRequest {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Token is required")
    private String token;
    @NotBlank(message = "New PIN is required")
    private String newPin;
}