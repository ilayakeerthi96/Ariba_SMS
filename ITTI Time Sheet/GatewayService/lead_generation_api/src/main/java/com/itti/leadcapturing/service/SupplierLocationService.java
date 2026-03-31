

package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.SupplierLocation;
import com.itti.leadcapturing.model.SupplierDepartment;
import com.itti.leadcapturing.model.SupplierUser;
import com.itti.leadcapturing.model.Supplier;
import com.itti.leadcapturing.repo.SupplierLocationRepository;
import com.itti.leadcapturing.repo.SupplierDepartmentRepository;
import com.itti.leadcapturing.repo.SupplierUserRepository;
import com.itti.leadcapturing.repo.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SupplierLocationService {

    @Autowired
    private SupplierLocationRepository locationRepository;

    @Autowired
    private SupplierDepartmentRepository departmentRepository;

    @Autowired
    private SupplierUserRepository userRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Transactional
    public SupplierLocation createLocation(Long supplierId, SupplierLocation location) {
        try {
            log.info("🔵 Creating supplier location for supplier ID: {}", supplierId);
            
            Supplier supplier = supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            
            location.setSupplier(supplier);
            location.setIsDeleted(false);
            
            SupplierLocation saved = locationRepository.save(location);
            
            log.info("✅ Supplier location created: {} (ID: {})", saved.getLocationName(), saved.getId());
            return saved;
            
        } catch (Exception e) {
            log.error("❌ Error creating supplier location", e);
            throw new RuntimeException("Failed to create location: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public SupplierLocation getLocation(Long id) {
        try {
            log.info("📥 Fetching supplier location ID: {}", id);
            
            SupplierLocation location = locationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Location not found with ID: " + id));
            
            // ✅ CRITICAL: Initialize lazy collections within transaction
            if (location.getDepartments() != null) {
                location.getDepartments().size(); // Force initialization
                location.getDepartments().forEach(dept -> {
                    if (dept.getUsers() != null) {
                        dept.getUsers().size();
                    }
                });
            }
            
            log.info("✅ Found supplier location: {}", location.getLocationName());
            return location;
            
        } catch (Exception e) {
            log.error("❌ Error fetching supplier location ID: {}", id, e);
            throw new RuntimeException("Failed to fetch location: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<SupplierLocation> getLocationsBySupplier(Long supplierId) {
        try {
            log.info("📥 Fetching supplier locations for supplier ID: {}", supplierId);
            
            List<SupplierLocation> locations = locationRepository.findBySupplierId(supplierId);
            
            // ✅ CRITICAL: Initialize lazy collections within transaction
            locations.forEach(location -> {
                if (location.getDepartments() != null) {
                    location.getDepartments().size();
                    log.info("   Location '{}' has {} departments", 
                             location.getLocationName(), 
                             location.getDepartments().size());
                }
            });
            
            log.info("✅ Found {} supplier locations", locations.size());
            return locations;
            
        } catch (Exception e) {
            log.error("❌ Error fetching supplier locations for supplier ID: {}", supplierId, e);
            throw new RuntimeException("Failed to fetch locations: " + e.getMessage(), e);
        }
    }

    @Transactional
    public SupplierLocation updateLocation(Long id, SupplierLocation locationReq) {
        try {
            log.info("🔵 Updating supplier location ID: {}", id);
            
            SupplierLocation existingLocation = locationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Location not found"));

            // Update only provided fields
            if (locationReq.getLocationName() != null && !locationReq.getLocationName().trim().isEmpty()) {
                existingLocation.setLocationName(locationReq.getLocationName());
            }
            if (locationReq.getLocationType() != null && !locationReq.getLocationType().trim().isEmpty()) {
                existingLocation.setLocationType(locationReq.getLocationType());
            }
            if (locationReq.getLocationContactName() != null && !locationReq.getLocationContactName().trim().isEmpty()) {
                existingLocation.setLocationContactName(locationReq.getLocationContactName());
            }
            if (locationReq.getLocationContactEmail() != null && !locationReq.getLocationContactEmail().trim().isEmpty()) {
                existingLocation.setLocationContactEmail(locationReq.getLocationContactEmail());
            }
            if (locationReq.getLocationContactPhone() != null && !locationReq.getLocationContactPhone().trim().isEmpty()) {
                existingLocation.setLocationContactPhone(locationReq.getLocationContactPhone());
            }
            if (locationReq.getAddressLine1() != null && !locationReq.getAddressLine1().trim().isEmpty()) {
                existingLocation.setAddressLine1(locationReq.getAddressLine1());
            }
            if (locationReq.getAddressLine2() != null) {
                existingLocation.setAddressLine2(locationReq.getAddressLine2());
            }
            if (locationReq.getCity() != null && !locationReq.getCity().trim().isEmpty()) {
                existingLocation.setCity(locationReq.getCity());
            }
            if (locationReq.getState() != null && !locationReq.getState().trim().isEmpty()) {
                existingLocation.setState(locationReq.getState());
            }
            if (locationReq.getPostalCode() != null && !locationReq.getPostalCode().trim().isEmpty()) {
                existingLocation.setPostalCode(locationReq.getPostalCode());
            }
            if (locationReq.getCountry() != null && !locationReq.getCountry().trim().isEmpty()) {
                existingLocation.setCountry(locationReq.getCountry());
            }
            if (locationReq.getLandlineNumber() != null) {
                existingLocation.setLandlineNumber(locationReq.getLandlineNumber());
            }
            if (locationReq.getFaxNumber() != null) {
                existingLocation.setFaxNumber(locationReq.getFaxNumber());
            }

            SupplierLocation updated = locationRepository.save(existingLocation);
            
            // Initialize for response
            if (updated.getDepartments() != null) {
                updated.getDepartments().size();
            }
            
            log.info("✅ Supplier location updated: {}", updated.getLocationName());
            return updated;
            
        } catch (Exception e) {
            log.error("❌ Error updating supplier location ID: {}", id, e);
            throw new RuntimeException("Failed to update location: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteLocation(Long id) {
        try {
            log.info("🔵 Soft deleting supplier location ID: {}", id);
            
            SupplierLocation location = locationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Location not found"));

            // Soft delete all departments and their users
            if (location.getDepartments() != null && !location.getDepartments().isEmpty()) {
                for (SupplierDepartment department : location.getDepartments()) {
                    deleteDepartmentRecursive(department);
                }
            }

            // Soft delete the location itself
            location.setIsDeleted(true);
            location.setDeletedAt(LocalDateTime.now());
            locationRepository.save(location);
            
            log.info("✅ Supplier location soft deleted: {}", location.getLocationName());
            
        } catch (Exception e) {
            log.error("❌ Error deleting supplier location ID: {}", id, e);
            throw new RuntimeException("Failed to delete location: " + e.getMessage(), e);
        }
    }

    @Transactional
    private void deleteDepartmentRecursive(SupplierDepartment department) {
        // Soft delete all users under this department
        if (department.getUsers() != null && !department.getUsers().isEmpty()) {
            for (SupplierUser user : department.getUsers()) {
                user.setIsDeleted(true);
                user.setDeletedAt(LocalDateTime.now());
                userRepository.save(user);
            }
        }

        // Soft delete the department itself
        department.setIsDeleted(true);
        department.setDeletedAt(LocalDateTime.now());
        departmentRepository.save(department);
    }

    @Transactional
    public void hardDeleteLocation(Long id) {
        SupplierLocation location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        // Hard delete all departments and users
        if (location.getDepartments() != null && !location.getDepartments().isEmpty()) {
            for (SupplierDepartment department : location.getDepartments()) {
                if (department.getUsers() != null && !department.getUsers().isEmpty()) {
                    userRepository.deleteAll(department.getUsers());
                }
                departmentRepository.delete(department);
            }
        }

        // Hard delete the location
        locationRepository.delete(location);
    }

    @Transactional
    public SupplierLocation restoreLocation(Long id) {
        SupplierLocation location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        location.setIsDeleted(false);
        location.setDeletedAt(null);

        // Restore all related departments and users
        if (location.getDepartments() != null && !location.getDepartments().isEmpty()) {
            for (SupplierDepartment department : location.getDepartments()) {
                restoreDepartmentRecursive(department);
            }
        }

        return locationRepository.save(location);
    }

    @Transactional
    private void restoreDepartmentRecursive(SupplierDepartment department) {
        if (department.getUsers() != null && !department.getUsers().isEmpty()) {
            for (SupplierUser user : department.getUsers()) {
                user.setIsDeleted(false);
                user.setDeletedAt(null);
                userRepository.save(user);
            }
        }

        department.setIsDeleted(false);
        department.setDeletedAt(null);
        departmentRepository.save(department);
    }

    @Transactional(readOnly = true)
    public SupplierLocation getLocationWithHierarchy(Long id) {
        SupplierLocation location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        // Force initialization of lazy loaded collections
        if (location.getDepartments() != null) {
            location.getDepartments().forEach(department -> {
                if (department.getUsers() != null) {
                    department.getUsers().size();
                }
            });
        }

        return location;
    }

    @Transactional(readOnly = true)
    public Long countLocationsBySupplier(Long supplierId) {
        List<SupplierLocation> locations = getLocationsBySupplier(supplierId);
        return (long) locations.size();
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return locationRepository.existsById(id);
    }
}