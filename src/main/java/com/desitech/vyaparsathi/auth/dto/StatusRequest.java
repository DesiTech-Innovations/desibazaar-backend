package com.desitech.vyaparsathi.auth.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class StatusRequest {
    @NotNull(message = "Active status is required")
    private boolean active;
}