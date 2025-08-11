package com.desitech.vyaparsathi.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "PIN is required")
    private String pin;
}