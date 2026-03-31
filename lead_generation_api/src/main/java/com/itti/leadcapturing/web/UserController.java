package com.itti.leadcapturing.web;

import com.itti.leadcapturing.model.User;
import com.itti.leadcapturing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * POST /api/users/{departmentId}
     * Create user under department
     */
    @PostMapping("/{departmentId}")
    public ResponseEntity<?> createUser(
            @PathVariable Long departmentId,
            @Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user, departmentId);
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * GET /api/users
     * Get all buyer users
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch users");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * GET /api/users/{id}
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        try {
            User user = userService.getUser(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * PUT /api/users/{id}
     * Update user by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User userReq) {
        try {
            User updatedUser = userService.updateUser(id, userReq);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * DELETE /api/users/{id}
     * Delete user (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage() != null ? e.getMessage() : "Failed to delete user");
            return ResponseEntity.status(500).body(response);
        }
    }
}