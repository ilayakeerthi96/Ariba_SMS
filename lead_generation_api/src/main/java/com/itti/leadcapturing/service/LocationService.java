

package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.Location;
import com.itti.leadcapturing.model.Buyer;
import com.itti.leadcapturing.repo.LocationRepository;
import com.itti.leadcapturing.repo.BuyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private BuyerRepository buyerRepository;

    @Transactional
    public Location createLocation(Long buyerId, Location location) {
        try {
            log.info("🔵 Creating location for buyer ID: {}", buyerId);
            
            Buyer buyer = buyerRepository.findById(buyerId)
                    .orElseThrow(() -> new RuntimeException("Buyer not found"));

            location.setBuyer(buyer);
            location.setIsDeleted(false);

            Location saved = locationRepository.save(location);
            
            log.info("✅ Location created: {} (ID: {})", saved.getLocationName(), saved.getId());
            return saved;
            
        } catch (Exception e) {
            log.error("❌ Error creating location", e);
            throw new RuntimeException("Failed to create location: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Location> getLocationsByBuyer(Long buyerId) {
        try {
            log.info("📥 Fetching locations for buyer ID: {}", buyerId);
            
            List<Location> locations = locationRepository.findByBuyerId(buyerId);
            
            // ✅ CRITICAL: Initialize lazy collections within transaction
            locations.forEach(location -> {
                if (location.getDepartments() != null) {
                    location.getDepartments().size(); // Force initialization
                    log.info("   Location '{}' has {} departments", 
                             location.getLocationName(), 
                             location.getDepartments().size());
                }
            });
            
            log.info("✅ Found {} locations", locations.size());
            return locations;
            
        } catch (Exception e) {
            log.error("❌ Error fetching locations for buyer ID: {}", buyerId, e);
            throw new RuntimeException("Failed to fetch locations: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Location getLocationById(Long id) {
        try {
            log.info("📥 Fetching location ID: {}", id);
            
            Location location = locationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Location not found with ID: " + id));
            
            // ✅ Initialize lazy collections
            if (location.getDepartments() != null) {
                location.getDepartments().size();
            }
            
            log.info("✅ Found location: {}", location.getLocationName());
            return location;
            
        } catch (Exception e) {
            log.error("❌ Error fetching location ID: {}", id, e);
            throw new RuntimeException("Failed to fetch location: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Location updateLocation(Long id, Location locationReq) {
        try {
            log.info("🔵 Updating location ID: {}", id);
            
            Location existing = locationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Location not found"));

            // Update only provided fields
            if (locationReq.getLocationName() != null && !locationReq.getLocationName().trim().isEmpty()) {
                existing.setLocationName(locationReq.getLocationName());
            }
            
            if (locationReq.getLocationType() != null && !locationReq.getLocationType().trim().isEmpty()) {
                existing.setLocationType(locationReq.getLocationType());
            }
            
            if (locationReq.getLocationContactName() != null && !locationReq.getLocationContactName().trim().isEmpty()) {
                existing.setLocationContactName(locationReq.getLocationContactName());
            }
            
            if (locationReq.getLocationContactEmail() != null && !locationReq.getLocationContactEmail().trim().isEmpty()) {
                existing.setLocationContactEmail(locationReq.getLocationContactEmail());
            }
            
            if (locationReq.getLocationContactPhone() != null && !locationReq.getLocationContactPhone().trim().isEmpty()) {
                existing.setLocationContactPhone(locationReq.getLocationContactPhone());
            }
            
            if (locationReq.getAddressLine1() != null && !locationReq.getAddressLine1().trim().isEmpty()) {
                existing.setAddressLine1(locationReq.getAddressLine1());
            }
            
            if (locationReq.getAddressLine2() != null) {
                existing.setAddressLine2(locationReq.getAddressLine2());
            }
            
            if (locationReq.getCity() != null && !locationReq.getCity().trim().isEmpty()) {
                existing.setCity(locationReq.getCity());
            }
            
            if (locationReq.getState() != null && !locationReq.getState().trim().isEmpty()) {
                existing.setState(locationReq.getState());
            }
            
            if (locationReq.getPostalCode() != null && !locationReq.getPostalCode().trim().isEmpty()) {
                existing.setPostalCode(locationReq.getPostalCode());
            }
            
            if (locationReq.getCountry() != null && !locationReq.getCountry().trim().isEmpty()) {
                existing.setCountry(locationReq.getCountry());
            }
            
            if (locationReq.getLandlineNumber() != null) {
                existing.setLandlineNumber(locationReq.getLandlineNumber());
            }
            
            if (locationReq.getFaxNumber() != null) {
                existing.setFaxNumber(locationReq.getFaxNumber());
            }

            Location updated = locationRepository.save(existing);
            
            // Initialize for response
            if (updated.getDepartments() != null) {
                updated.getDepartments().size();
            }
            
            log.info("✅ Location updated: {}", updated.getLocationName());
            return updated;
            
        } catch (Exception e) {
            log.error("❌ Error updating location ID: {}", id, e);
            throw new RuntimeException("Failed to update location: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteLocation(Long id) {
        try {
            log.info("🔵 Soft deleting location ID: {}", id);
            
            Location location = locationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Location not found"));

            // Soft delete location
            location.setIsDeleted(true);
            location.setDeletedAt(LocalDateTime.now());
            locationRepository.save(location);

            // Soft delete all departments and their users
            if (location.getDepartments() != null) {
                location.getDepartments().forEach(department -> {
                    department.setIsDeleted(true);
                    department.setDeletedAt(LocalDateTime.now());

                    // Soft delete all users in department
                    if (department.getUsers() != null) {
                        department.getUsers().forEach(user -> {
                            user.setIsDeleted(true);
                            user.setDeletedAt(LocalDateTime.now());
                        });
                    }
                });
            }
            
            
            log.info("✅ Location soft deleted: {}", location.getLocationName());
            
        } catch (Exception e) {
            log.error("❌ Error deleting location ID: {}", id, e);
            throw new RuntimeException("Failed to delete location: " + e.getMessage(), e);
        }
    }
}