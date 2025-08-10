package com.desitech.desibazaar.auth.controller;

import com.desitech.desibazaar.auth.entity.User;
import com.desitech.desibazaar.auth.repository.UserRepository;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private final UserRepository userRepository;

    public UserManagementController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // List all users
    @GetMapping
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    // Activate / deactivate user
    @PostMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty())
            return ResponseEntity.badRequest().body("User not found");

        User user = userOpt.get();
        user.setActive(request.isActive());
        userRepository.save(user);
        return ResponseEntity.ok("User status updated");
    }

    // Change role
    @PostMapping("/{id}/role")
    public ResponseEntity<?> changeRole(@PathVariable Long id, @RequestBody RoleRequest request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty())
            return ResponseEntity.badRequest().body("User not found");

        User user = userOpt.get();
        user.setRole(request.getRole());
        userRepository.save(user);
        return ResponseEntity.ok("User role updated");
    }

    @Data
    static class StatusRequest {
        private boolean active;
    }

    @Data
    static class RoleRequest {
        private String role; // OWNER, STAFF, ADMIN
    }
}
