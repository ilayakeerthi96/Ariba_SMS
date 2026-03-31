
package com.itti.leadcapturing.web;

import com.itti.leadcapturing.model.Department;
import com.itti.leadcapturing.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/department")
@CrossOrigin(origins = "*")
@Slf4j
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    // Create Department under Location
    @PostMapping("/{locationId}")
    public ResponseEntity<?> addDepartment(@PathVariable Long locationId,
                                           @Valid @RequestBody Department department) {
        try {
            log.info("📥 Creating department for location ID: {}", locationId);
            
            Department saved = departmentService.createDepartment(locationId, department);
            
            log.info("✅ Department created: {} (ID: {})", saved.getDepartmentName(), saved.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Department created successfully");
            response.put("data", saved);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("❌ Error creating department", e);
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ✅ FIXED: Get all Departments for a Location
    @GetMapping("/location/{locationId}")
    public ResponseEntity<?> getDepartmentsByLocation(@PathVariable Long locationId) {
        try {
            log.info("📥 Fetching departments for location ID: {}", locationId);
            
            List<Department> departments = departmentService.getDepartmentsByLocation(locationId);
            
            log.info("✅ Found {} departments", departments.size());
            
            // ✅ CRITICAL: Return direct list (not wrapped)
            return ResponseEntity.ok(departments);
            
        } catch (Exception e) {
            log.error("❌ Error fetching departments for location ID: {}", locationId, e);
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ✅ FIXED: Get department by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartmentById(@PathVariable Long id) {
        try {
            log.info("📥 Fetching department ID: {}", id);
            
            Department department = departmentService.getDepartmentById(id);
            
            log.info("✅ Found department: {}", department.getDepartmentName());
            
            return ResponseEntity.ok(department);
            
        } catch (RuntimeException e) {
            log.error("❌ Department not found: {}", id, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Department not found");
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching department: {}", id, e);
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Update Department
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody Department req) {
        try {
            log.info("📥 Updating department ID: {}", id);
            
            Department updated = departmentService.updateDepartment(id, req);
            
            log.info("✅ Department updated: {}", updated.getDepartmentName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updated);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error updating department", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Delete Department
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        try {
            log.info("📥 Deleting department ID: {}", id);
            
            departmentService.deleteDepartment(id);
            
            log.info("✅ Department deleted successfully");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Department deleted");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error deleting department", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}