

package com.itti.leadcapturing.web;

import com.itti.leadcapturing.model.SupplierLocation;
import com.itti.leadcapturing.service.SupplierLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/supplier-location")
@CrossOrigin(origins = "*")
@Slf4j
public class SupplierLocationController {

    @Autowired
    private SupplierLocationService locationService;

    /**
     * POST /api/supplier-location/{supplierId}
     * Create location under supplier
     */
    @PostMapping("/{supplierId}")
    public ResponseEntity<?> createLocation(
            @PathVariable Long supplierId,
            @Valid @RequestBody SupplierLocation location) {
        try {
            log.info("📥 Creating supplier location for supplier ID: {}", supplierId);
            
            SupplierLocation createdLocation = locationService.createLocation(supplierId, location);
            
            log.info("✅ Supplier location created: {} (ID: {})", 
                     createdLocation.getLocationName(), createdLocation.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Location created successfully");
            response.put("data", createdLocation);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            log.error("❌ Error creating supplier location", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * ✅ FIXED: GET /api/supplier-location/{id}
     * Get location by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getLocation(@PathVariable Long id) {
        try {
            log.info("📥 Fetching supplier location ID: {}", id);
            
            SupplierLocation location = locationService.getLocation(id);
            
            log.info("✅ Found supplier location: {}", location.getLocationName());
            
            // ✅ Return direct object
            return ResponseEntity.ok(location);
            
        } catch (RuntimeException e) {
            log.error("❌ Supplier location not found: {}", id, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Location not found");
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching supplier location: {}", id, e);
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * ✅ FIXED: GET /api/supplier-location/supplier/{supplierId}
     * Get all locations by supplier ID
     */
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<?> getLocationsBySupplier(@PathVariable Long supplierId) {
        try {
            log.info("📥 Fetching supplier locations for supplier ID: {}", supplierId);
            
            List<SupplierLocation> locations = locationService.getLocationsBySupplier(supplierId);
            
            log.info("✅ Found {} supplier locations", locations.size());
            
            // ✅ CRITICAL: Return direct list (not wrapped)
            return ResponseEntity.ok(locations);
            
        } catch (Exception e) {
            log.error("❌ Error fetching supplier locations for supplier ID: {}", supplierId, e);
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch locations");
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PUT /api/supplier-location/{id}
     * Update location by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody SupplierLocation locationReq) {
        try {
            log.info("📥 Updating supplier location ID: {}", id);
            
            SupplierLocation updatedLocation = locationService.updateLocation(id, locationReq);
            
            log.info("✅ Supplier location updated: {}", updatedLocation.getLocationName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Location updated successfully");
            response.put("data", updatedLocation);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.error("❌ Error updating supplier location", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * DELETE /api/supplier-location/{id}
     * Delete location and cascade delete departments and users (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long id) {
        try {
            log.info("📥 Deleting supplier location ID: {}", id);
            
            locationService.deleteLocation(id);
            
            log.info("✅ Supplier location deleted successfully");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Location deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.error("❌ Error deleting supplier location", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}