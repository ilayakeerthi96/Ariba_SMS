
package com.itti.leadcapturing.web;

import com.itti.leadcapturing.model.Location;
import com.itti.leadcapturing.service.LocationService;
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
@RequestMapping("/api/location")
@CrossOrigin(origins = "*")
@Slf4j
public class LocationController {

    @Autowired
    private LocationService locationService;

    // Create Location under Buyer
    @PostMapping("/{buyerId}")
    public ResponseEntity<?> addLocation(@PathVariable Long buyerId, @Valid @RequestBody Location location) {
        try {
            log.info("📥 Creating location for buyer ID: {}", buyerId);
            
            Location saved = locationService.createLocation(buyerId, location);
            
            log.info("✅ Location created successfully: {} (ID: {})", saved.getLocationName(), saved.getId());
            
            // Return wrapped response for consistency
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Location created successfully");
            response.put("data", saved);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("❌ Error creating location for buyer ID: {}", buyerId, e);
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ✅ FIXED: Get all locations of a buyer
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<?> getLocationsByBuyer(@PathVariable Long buyerId) {
        try {
            log.info("📥 Fetching locations for buyer ID: {}", buyerId);
            
            List<Location> locations = locationService.getLocationsByBuyer(buyerId);
            
            log.info("✅ Found {} locations", locations.size());
            
            // ✅ CRITICAL: Return direct list (not wrapped)
            // Frontend expects array directly
            return ResponseEntity.ok(locations);
            
        } catch (Exception e) {
            log.error("❌ Error fetching locations for buyer ID: {}", buyerId, e);
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ✅ FIXED: Get location by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getLocationById(@PathVariable Long id) {
        try {
            log.info("📥 Fetching location ID: {}", id);
            
            Location location = locationService.getLocationById(id);
            
            log.info("✅ Found location: {}", location.getLocationName());
            
            return ResponseEntity.ok(location);
            
        } catch (RuntimeException e) {
            log.error("❌ Location not found: {}", id, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Location not found");
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching location: {}", id, e);
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Update Location
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLocation(@PathVariable Long id, @Valid @RequestBody Location req) {
        try {
            log.info("📥 Updating location ID: {}", id);
            
            Location updated = locationService.updateLocation(id, req);
            
            log.info("✅ Location updated successfully");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updated);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error updating location", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Delete Location
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long id) {
        try {
            log.info("📥 Deleting location ID: {}", id);
            
            locationService.deleteLocation(id);
            
            log.info("✅ Location deleted successfully");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Location deleted");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error deleting location", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}