package com.desitech.vyaparsathi.auth.dto;

import com.desitech.vyaparsathi.auth.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoleRequest {
    @NotNull(message = "Role is required")
    private Role role;
}