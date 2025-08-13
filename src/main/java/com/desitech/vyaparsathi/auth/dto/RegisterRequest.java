package com.desitech.vyaparsathi.auth.dto;

import com.desitech.vyaparsathi.auth.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "PIN is required")
    private String pin;
    @NotNull(message = "Role is required")
    private Role role;
}