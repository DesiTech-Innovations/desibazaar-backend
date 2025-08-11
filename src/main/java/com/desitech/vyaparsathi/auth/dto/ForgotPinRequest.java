package com.desitech.vyaparsathi.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPinRequest {
    @NotBlank(message = "Username is required")
    private String username;
}