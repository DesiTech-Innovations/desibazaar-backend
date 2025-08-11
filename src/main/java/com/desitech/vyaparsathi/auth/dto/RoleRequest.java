package com.desitech.vyaparsathi.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequest {
    @NotBlank(message = "Role is required")
    private String role;
}