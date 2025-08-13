package com.desitech.vyaparsathi.auth.controller;

import com.desitech.vyaparsathi.auth.dto.RoleRequest;
import com.desitech.vyaparsathi.auth.dto.StatusRequest;
import com.desitech.vyaparsathi.auth.dto.UserDto;
import com.desitech.vyaparsathi.auth.service.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    // List all users
    @GetMapping
    public List<UserDto> listUsers() {
        return userManagementService.listAllUsers();
    }

    // Activate / deactivate user
    @PostMapping("/{id}/status")
    public ResponseEntity<String> changeStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        userManagementService.changeUserStatus(id, request.isActive());
        return ResponseEntity.ok("User status updated successfully.");
    }

    // Change role
    @PostMapping("/{id}/role")
    public ResponseEntity<String> changeRole(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        userManagementService.changeUserRole(id, request.getRole());
        return ResponseEntity.ok("User role updated successfully.");
    }
}