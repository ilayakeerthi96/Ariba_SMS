package com.itti.leadcapturing.web;

import com.itti.leadcapturing.model.SupplierUser;
import com.itti.leadcapturing.model.SupplierDepartment;
import com.itti.leadcapturing.service.SupplierUserService;
import com.itti.leadcapturing.repo.SupplierDepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/supplier-user")
@CrossOrigin(origins = "*")
public class SupplierUserController {

    @Autowired
    private SupplierUserService userService;

    @Autowired
    private SupplierDepartmentRepository departmentRepository;

    /**
     * POST /api/supplier-user/{departmentId}
     * Create user under department
     * Role is automatically set to ROLE_SUPPLIER
     */
    @PostMapping("/{departmentId}")
    public ResponseEntity<?> createUser(
            @PathVariable Long departmentId,
            @Valid @RequestBody SupplierUser user) {
        try {
            // Validate password is not empty
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Password cannot be null or empty");
                return ResponseEntity.status(400).body(response);
            }

            // Fetch and set the department for the user
            SupplierDepartment department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new RuntimeException("Department not found with ID: " + departmentId));
            
            user.setDepartment(department);
            
            SupplierUser createdUser = userService.createUser(user, departmentId);
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * GET /api/supplier-user
     * Get all supplier users
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<SupplierUser> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch users");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * GET /api/supplier-user/{id}
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        try {
            SupplierUser user = userService.getUser(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * PUT /api/supplier-user/{id}
     * Update user by ID
     * 
     * IMPORTANT: Password is optional during update
     * If not provided, existing password is retained
     * Email uniqueness is checked excluding current user
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody SupplierUser userReq) {
        try {
            SupplierUser updatedUser = userService.updateUser(id, userReq);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * DELETE /api/supplier-user/{id}
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
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}