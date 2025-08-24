
package com.desitech.vyaparsathi.auth.controller;
import com.desitech.vyaparsathi.common.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    @Autowired
    private UserManagementService userManagementService;

    // List all users
    @GetMapping
    public List<UserDto> listUsers() {
        try {
            List<UserDto> users = userManagementService.listAllUsers();
            logger.info("Listed all users");
            return users;
        } catch (Exception e) {
            logger.error("Error listing all users: {}", e.getMessage(), e);
            throw new ApplicationException("Failed to list users", e);
        }
    }

    // Activate / deactivate user
    @PostMapping("/{id}/status")
    public ResponseEntity<String> changeStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        try {
            userManagementService.changeUserStatus(id, request.isActive());
            logger.info("Changed status for user id={}, active={}", id, request.isActive());
            return ResponseEntity.ok("User status updated successfully.");
        } catch (Exception e) {
            logger.error("Error changing status for user id={}: {}", id, e.getMessage(), e);
            throw new ApplicationException("Failed to change user status", e);
        }
    }

    // Change role
    @PostMapping("/{id}/role")
    public ResponseEntity<String> changeRole(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        try {
            userManagementService.changeUserRole(id, request.getRole());
            logger.info("Changed role for user id={}, role={}", id, request.getRole());
            return ResponseEntity.ok("User role updated successfully.");
        } catch (Exception e) {
            logger.error("Error changing role for user id={}: {}", id, e.getMessage(), e);
            throw new ApplicationException("Failed to change user role", e);
        }
    }
}