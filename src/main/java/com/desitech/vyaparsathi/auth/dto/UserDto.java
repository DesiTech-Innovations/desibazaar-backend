package com.desitech.vyaparsathi.auth.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String role;
    private boolean active;
}