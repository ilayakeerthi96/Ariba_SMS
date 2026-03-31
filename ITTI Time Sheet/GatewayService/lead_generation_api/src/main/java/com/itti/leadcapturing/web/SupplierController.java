
package com.itti.leadcapturing.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itti.leadcapturing.model.Supplier;
import com.itti.leadcapturing.service.SupplierService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/supplier")
@CrossOrigin(origins = "*")
public class SupplierController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);

    @Autowired
    private SupplierService supplierService;

    /**
     * ✅ FIXED: GET /api/supplier
     * Get all suppliers
     */
    @GetMapping
    public ResponseEntity<?> getAllSuppliers() {
        try {
            logger.info("[GET ALL SUPPLIERS] Request received");
            
            List<Supplier> suppliers = supplierService.getAllSuppliers();
            
            logger.info("[GET ALL SUPPLIERS] Success - Found {} suppliers", suppliers.size());
            
            // ✅ CRITICAL: Return direct list (not wrapped)
            // Your frontend expects array directly
            return ResponseEntity.ok(suppliers);
            
        } catch (Exception e) {
            logger.error("[GET ALL SUPPLIERS] Exception occurred:", e);
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch suppliers");
            response.put("error", e.getClass().getSimpleName());
            response.put("details", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * ✅ FIXED: GET /api/supplier/{id}
     * Get supplier by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getSupplierById(@PathVariable Long id) {
        try {
            logger.info("[GET SUPPLIER] Fetching supplier with ID: {}", id);
            
            Supplier supplier = supplierService.getSupplierById(id);
            
            logger.info("[GET SUPPLIER] Success - Supplier: {}", supplier.getCompanyName());
            
            // ✅ Return direct supplier object
            return ResponseEntity.ok(supplier);
            
        } catch (RuntimeException e) {
            logger.error("[GET SUPPLIER] RuntimeException for ID {}: {}", id, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Supplier not found");
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception e) {
            logger.error("[GET SUPPLIER] Exception for ID {}", id, e);
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /api/supplier
     * Create a new supplier
     */
    @PostMapping
    public ResponseEntity<?> createSupplier(@Valid @RequestBody Supplier supplier) {
        try {
            logger.info("[CREATE SUPPLIER] Creating supplier: {}", supplier.getCompanyName());
            
            Supplier savedSupplier = supplierService.createSupplier(supplier);
            
            logger.info("[CREATE SUPPLIER] Success - ID: {}", savedSupplier.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Supplier created successfully");
            response.put("data", savedSupplier);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("[CREATE SUPPLIER] Exception:", e);
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * PUT /api/supplier/{id}
     * Update supplier by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSupplier(
            @PathVariable Long id,
            @RequestBody Supplier supplierReq) {
        try {
            logger.info("[UPDATE SUPPLIER] Updating supplier ID: {}", id);
            
            Supplier updated = supplierService.updateSupplier(id, supplierReq);
            
            logger.info("[UPDATE SUPPLIER] Success - ID: {}", id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Supplier updated successfully");
            response.put("data", updated);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("[UPDATE SUPPLIER] RuntimeException for ID {}: {}", id, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * DELETE /api/supplier/{id}
     * Delete supplier
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable Long id) {
        try {
            logger.info("[DELETE SUPPLIER] Deleting supplier ID: {}", id);
            
            supplierService.deleteSupplier(id);
            
            logger.info("[DELETE SUPPLIER] Success - ID: {}", id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Supplier deleted successfully!");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("[DELETE SUPPLIER] RuntimeException for ID {}: {}", id, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}