package com.itti.leadcapturing.web;

import com.itti.leadcapturing.model.SupplierDepartment;
import com.itti.leadcapturing.model.SupplierLocation;
import com.itti.leadcapturing.service.SupplierDepartmentService;
import com.itti.leadcapturing.repo.SupplierLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/supplier-department")
@CrossOrigin(origins = "*")
public class SupplierDepartmentController {

    @Autowired
    private SupplierDepartmentService departmentService;

    @Autowired
    private SupplierLocationRepository locationRepository;

    /**
     * POST /api/supplier-department/{locationId}
     * Create department under location
     * 
     * locationId is the ID of the SupplierLocation under which this department will be created
     */
    @PostMapping("/{locationId}")
    public ResponseEntity<?> createDepartment(
            @PathVariable Long locationId,
            @Valid @RequestBody SupplierDepartment department) {
        try {
            // IMPORTANT: Fetch and set the location for the department
            SupplierLocation location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Location not found with ID: " + locationId));
            
            // Set the location relationship
            department.setLocation(location);
            
            SupplierDepartment createdDept = departmentService.createDepartment(locationId, department);
            return ResponseEntity.ok(createdDept);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * GET /api/supplier-department/{id}
     * Get department by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartment(@PathVariable Long id) {
        try {
            SupplierDepartment department = departmentService.getDepartment(id);
            return ResponseEntity.ok(department);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Department not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * GET /api/supplier-department/location/{locationId}
     * Get departments by location ID
     * 
     * IMPORTANT: This route MUST come BEFORE the /{id} route in the URL pattern
     * Otherwise Spring will interpret "location" as an ID
     */
    @GetMapping("/location/{locationId}")
    public ResponseEntity<?> getDepartmentsByLocation(@PathVariable Long locationId) {
        try {
            List<SupplierDepartment> departments = departmentService.getDepartmentsByLocation(locationId);
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch departments for location: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * PUT /api/supplier-department/{id}
     * Update department by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody SupplierDepartment deptReq) {
        try {
            SupplierDepartment updatedDept = departmentService.updateDepartment(id, deptReq);
            return ResponseEntity.ok(updatedDept);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * DELETE /api/supplier-department/{id}
     * Delete department and cascade delete users (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Department deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}