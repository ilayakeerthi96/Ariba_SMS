
package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.Supplier;
import com.itti.leadcapturing.model.SupplierLocation;
import com.itti.leadcapturing.model.SupplierDepartment;
import com.itti.leadcapturing.model.SupplierUser;
import com.itti.leadcapturing.repo.SupplierRepository;
import com.itti.leadcapturing.repo.SupplierLocationRepository;
import com.itti.leadcapturing.repo.SupplierDepartmentRepository;
import com.itti.leadcapturing.repo.SupplierUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierLocationRepository locationRepository;

    @Autowired
    private SupplierDepartmentRepository departmentRepository;

    @Autowired
    private SupplierUserRepository userRepository;

    /**
     * Create a new supplier
     */
    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        try {
            log.info("🔵 Creating supplier: {}", supplier.getCompanyName());
            supplier.setIsDeleted(false);
            Supplier saved = supplierRepository.save(supplier);
            log.info("✅ Supplier created successfully: {} (ID: {})", saved.getCompanyName(), saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("❌ Error creating supplier", e);
            throw new RuntimeException("Failed to create supplier: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ CRITICAL FIX: Get all suppliers with COMPLETE nested hierarchy
     */
    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliers() {
        try {
            log.info("📥 Fetching all suppliers with complete hierarchy");
            List<Supplier> suppliers = supplierRepository.findByIsDeletedFalse();
            
            // ✅ CRITICAL: Force initialization of ENTIRE hierarchy
            suppliers.forEach(supplier -> {
                log.info("🔍 Processing supplier: {} (ID: {})", supplier.getCompanyName(), supplier.getId());
                
                // Initialize locations
                if (supplier.getLocations() != null) {
                    supplier.getLocations().size(); // Force initialization
                    log.info("   📍 Locations: {}", supplier.getLocations().size());
                    
                    // For each location, initialize departments
                    supplier.getLocations().forEach(location -> {
                        log.info("      🏢 Location: {} (ID: {})", location.getLocationName(), location.getId());
                        
                        if (location.getDepartments() != null) {
                            location.getDepartments().size(); // Force initialization
                            log.info("         📂 Departments: {}", location.getDepartments().size());
                            
                            // For each department, initialize users
                            location.getDepartments().forEach(department -> {
                                log.info("            🏷️  Dept: {} (ID: {})", department.getDepartmentName(), department.getId());
                                
                                if (department.getUsers() != null) {
                                    department.getUsers().size(); // Force initialization
                                    log.info("               👥 Users: {}", department.getUsers().size());
                                }
                            });
                        }
                    });
                }
            });
            
            log.info("✅ Found {} suppliers with complete hierarchy", suppliers.size());
            return suppliers;
            
        } catch (Exception e) {
            log.error("❌ Error fetching suppliers", e);
            throw new RuntimeException("Failed to fetch suppliers: " + e.getMessage(), e);
        }
    }

    /**
     * Get all suppliers including deleted
     */
    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliersIncludingDeleted() {
        try {
            log.info("📥 Fetching all suppliers including deleted");
            List<Supplier> suppliers = supplierRepository.findAll();
            
            // Initialize lazy collections
            suppliers.forEach(supplier -> {
                if (supplier.getLocations() != null) {
                    supplier.getLocations().size();
                }
            });
            
            log.info("✅ Found {} suppliers (including deleted)", suppliers.size());
            return suppliers;
        } catch (Exception e) {
            log.error("❌ Error fetching suppliers", e);
            throw new RuntimeException("Failed to fetch suppliers: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ CRITICAL FIX: Get supplier by ID with COMPLETE nested hierarchy
     */
    @Transactional(readOnly = true)
    public Supplier getSupplierById(Long id) {
        try {
            log.info("📥 Fetching supplier with ID: {} (with complete hierarchy)", id);
            
            Supplier supplier = supplierRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));
            
            log.info("🔍 Found supplier: {}", supplier.getCompanyName());
            
            // ✅ CRITICAL: Force initialization of ENTIRE hierarchy
            if (supplier.getLocations() != null) {
                supplier.getLocations().size();
                log.info("   📍 Locations: {}", supplier.getLocations().size());
                
                supplier.getLocations().forEach(location -> {
                    log.info("      🏢 Location: {}", location.getLocationName());
                    
                    if (location.getDepartments() != null) {
                        location.getDepartments().size();
                        log.info("         📂 Departments: {}", location.getDepartments().size());
                        
                        location.getDepartments().forEach(department -> {
                            log.info("            🏷️  Dept: {}", department.getDepartmentName());
                            
                            if (department.getUsers() != null) {
                                department.getUsers().size();
                                log.info("               👥 Users: {}", department.getUsers().size());
                            }
                        });
                    }
                });
            }
            
            log.info("✅ Supplier {} loaded with complete hierarchy", supplier.getId());
            return supplier;
            
        } catch (Exception e) {
            log.error("❌ Error fetching supplier with ID: {}", id, e);
            throw new RuntimeException("Failed to fetch supplier: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ COMPLETELY FIXED: Update supplier basic information
     * Updates EXISTING supplier instead of creating new one
     */
    @Transactional
    public Supplier updateSupplier(Long id, Supplier supplierReq) {
        try {
            log.info("🔵 Updating supplier with ID: {}", id);
            
            // ✅ CRITICAL FIX: Fetch existing supplier first
            Supplier existingSupplier = supplierRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));

            log.info("   Found existing supplier: {}", existingSupplier.getCompanyName());

            // ✅ Update fields only if provided (not null and not empty)
            if (supplierReq.getCompanyName() != null && !supplierReq.getCompanyName().trim().isEmpty()) {
                log.info("   Updating companyName: {} -> {}", existingSupplier.getCompanyName(), supplierReq.getCompanyName());
                existingSupplier.setCompanyName(supplierReq.getCompanyName());
            }

            if (supplierReq.getCompanyType() != null && !supplierReq.getCompanyType().trim().isEmpty()) {
                existingSupplier.setCompanyType(supplierReq.getCompanyType());
            }

            if (supplierReq.getIndustrySector() != null && !supplierReq.getIndustrySector().trim().isEmpty()) {
                existingSupplier.setIndustrySector(supplierReq.getIndustrySector());
            }

            if (supplierReq.getContactPersonName() != null && !supplierReq.getContactPersonName().trim().isEmpty()) {
                existingSupplier.setContactPersonName(supplierReq.getContactPersonName());
            }

            if (supplierReq.getContactPersonDesignation() != null && !supplierReq.getContactPersonDesignation().trim().isEmpty()) {
                existingSupplier.setContactPersonDesignation(supplierReq.getContactPersonDesignation());
            }

            if (supplierReq.getContactPersonEmail() != null && !supplierReq.getContactPersonEmail().trim().isEmpty()) {
                log.info("   Updating email: {} -> {}", existingSupplier.getContactPersonEmail(), supplierReq.getContactPersonEmail());
                existingSupplier.setContactPersonEmail(supplierReq.getContactPersonEmail());
            }

            if (supplierReq.getContactPersonPhone() != null && !supplierReq.getContactPersonPhone().trim().isEmpty()) {
                existingSupplier.setContactPersonPhone(supplierReq.getContactPersonPhone());
            }

            if (supplierReq.getAddressLine1() != null && !supplierReq.getAddressLine1().trim().isEmpty()) {
                existingSupplier.setAddressLine1(supplierReq.getAddressLine1());
            }

            if (supplierReq.getAddressLine2() != null && !supplierReq.getAddressLine2().trim().isEmpty()) {
                existingSupplier.setAddressLine2(supplierReq.getAddressLine2());
            }

            if (supplierReq.getCity() != null && !supplierReq.getCity().trim().isEmpty()) {
                existingSupplier.setCity(supplierReq.getCity());
            }

            if (supplierReq.getState() != null && !supplierReq.getState().trim().isEmpty()) {
                existingSupplier.setState(supplierReq.getState());
            }

            if (supplierReq.getPostalCode() != null && !supplierReq.getPostalCode().trim().isEmpty()) {
                existingSupplier.setPostalCode(supplierReq.getPostalCode());
            }

            if (supplierReq.getCountry() != null && !supplierReq.getCountry().trim().isEmpty()) {
                existingSupplier.setCountry(supplierReq.getCountry());
            }

            if (supplierReq.getGstNumber() != null && !supplierReq.getGstNumber().trim().isEmpty()) {
                existingSupplier.setGstNumber(supplierReq.getGstNumber());
            }

            if (supplierReq.getPanNumber() != null && !supplierReq.getPanNumber().trim().isEmpty()) {
                existingSupplier.setPanNumber(supplierReq.getPanNumber());
            }

            if (supplierReq.getTanNumber() != null && !supplierReq.getTanNumber().trim().isEmpty()) {
                existingSupplier.setTanNumber(supplierReq.getTanNumber());
            }

            if (supplierReq.getWebsite() != null && !supplierReq.getWebsite().trim().isEmpty()) {
                existingSupplier.setWebsite(supplierReq.getWebsite());
            }

            // ✅ Save the EXISTING supplier (not create new one)
            Supplier updatedSupplier = supplierRepository.save(existingSupplier);
            
            // Initialize collections for response
            if (updatedSupplier.getLocations() != null) {
                updatedSupplier.getLocations().size();
            }
            
            log.info("✅ Supplier updated successfully: {} (ID: {})", 
                     updatedSupplier.getCompanyName(), updatedSupplier.getId());
            
            return updatedSupplier;
            
        } catch (Exception e) {
            log.error("❌ Error updating supplier with ID: {}", id, e);
            e.printStackTrace();
            throw new RuntimeException("Failed to update supplier: " + e.getMessage(), e);
        }
    }

    /**
     * Delete supplier and cascade delete all related data (soft delete)
     */
    @Transactional
    public void deleteSupplier(Long id) {
        try {
            log.info("🔵 Soft deleting supplier with ID: {}", id);
            
            Supplier supplier = supplierRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));

            // Soft delete all locations, departments, and users
            if (supplier.getLocations() != null && !supplier.getLocations().isEmpty()) {
                supplier.getLocations().forEach(location -> {
                    deleteLocationRecursive(location);
                });
            }

            // Soft delete supplier
            supplier.setIsDeleted(true);
            supplier.setDeletedAt(LocalDateTime.now());
            supplierRepository.save(supplier);
            
            log.info("✅ Supplier soft deleted successfully: {} (ID: {})", 
                     supplier.getCompanyName(), supplier.getId());
            
        } catch (Exception e) {
            log.error("❌ Error deleting supplier with ID: {}", id, e);
            throw new RuntimeException("Failed to delete supplier: " + e.getMessage(), e);
        }
    }

    /**
     * Recursively delete location and all its departments and users
     */
    @Transactional
    private void deleteLocationRecursive(SupplierLocation location) {
        if (location.getDepartments() != null && !location.getDepartments().isEmpty()) {
            location.getDepartments().forEach(department -> {
                deleteDepartmentRecursive(department);
            });
        }

        location.setIsDeleted(true);
        location.setDeletedAt(LocalDateTime.now());
        locationRepository.save(location);
    }

    /**
     * Recursively delete department and all its users
     */
    @Transactional
    private void deleteDepartmentRecursive(SupplierDepartment department) {
        if (department.getUsers() != null && !department.getUsers().isEmpty()) {
            department.getUsers().forEach(user -> {
                user.setIsDeleted(true);
                user.setDeletedAt(LocalDateTime.now());
                userRepository.save(user);
            });
        }

        department.setIsDeleted(true);
        department.setDeletedAt(LocalDateTime.now());
        departmentRepository.save(department);
    }

    /**
     * Search suppliers by company name
     */
    @Transactional(readOnly = true)
    public List<Supplier> searchByCompanyName(String companyName) {
        return supplierRepository.findByCompanyNameContainingIgnoreCaseAndIsDeletedFalse(companyName);
    }

    /**
     * Search suppliers by industry sector
     */
    @Transactional(readOnly = true)
    public List<Supplier> searchByIndustrySector(String industrySector) {
        return supplierRepository.findByIndustrySectorAndIsDeletedFalse(industrySector);
    }

    /**
     * Search suppliers by state
     */
    @Transactional(readOnly = true)
    public List<Supplier> searchByState(String state) {
        return supplierRepository.findByStateAndIsDeletedFalse(state);
    }

    /**
     * Get count of active suppliers
     */
    @Transactional(readOnly = true)
    public Long getActiveSupplierCount() {
        return supplierRepository.countByIsDeletedFalse();
    }

    /**
     * Restore a deleted supplier
     */
    @Transactional
    public Supplier restoreSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        supplier.setIsDeleted(false);
        supplier.setDeletedAt(null);

        // Restore all related locations, departments, and users
        if (supplier.getLocations() != null && !supplier.getLocations().isEmpty()) {
            supplier.getLocations().forEach(location -> {
                restoreLocationRecursive(location);
            });
        }

        return supplierRepository.save(supplier);
    }

    /**
     * Recursively restore location and all its departments and users
     */
    @Transactional
    private void restoreLocationRecursive(SupplierLocation location) {
        if (location.getDepartments() != null && !location.getDepartments().isEmpty()) {
            location.getDepartments().forEach(department -> {
                restoreDepartmentRecursive(department);
            });
        }

        location.setIsDeleted(false);
        location.setDeletedAt(null);
        locationRepository.save(location);
    }

    /**
     * Recursively restore department and all its users
     */
    @Transactional
    private void restoreDepartmentRecursive(SupplierDepartment department) {
        if (department.getUsers() != null && !department.getUsers().isEmpty()) {
            department.getUsers().forEach(user -> {
                user.setIsDeleted(false);
                user.setDeletedAt(null);
                userRepository.save(user);
            });
        }

        department.setIsDeleted(false);
        department.setDeletedAt(null);
        departmentRepository.save(department);
    }

    /**
     * Get supplier with all its locations, departments, and users (eager loading)
     */
    @Transactional(readOnly = true)
    public Supplier getSupplierWithHierarchy(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        // Force initialization of lazy loaded collections
        if (supplier.getLocations() != null) {
            supplier.getLocations().forEach(location -> {
                if (location.getDepartments() != null) {
                    location.getDepartments().forEach(department -> {
                        if (department.getUsers() != null) {
                            department.getUsers().size();
                        }
                    });
                }
            });
        }

        return supplier;
    }

    /**
     * Get all suppliers with their complete hierarchy
     */
    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliersWithHierarchy() {
        List<Supplier> suppliers = supplierRepository.findByIsDeletedFalse();
        
        suppliers.forEach(supplier -> {
            if (supplier.getLocations() != null) {
                supplier.getLocations().forEach(location -> {
                    if (location.getDepartments() != null) {
                        location.getDepartments().forEach(department -> {
                            if (department.getUsers() != null) {
                                department.getUsers().size();
                            }
                        });
                    }
                });
            }
        });

        return suppliers;
    }

    /**
     * Get supplier statistics
     */
    @Transactional(readOnly = true)
    public SupplierStatistics getSupplierStatistics(Long id) {
        Supplier supplier = getSupplierWithHierarchy(id);
        
        int locationCount = supplier.getLocations() != null ? supplier.getLocations().size() : 0;
        int departmentCount = 0;
        int userCount = 0;

        if (supplier.getLocations() != null) {
            for (SupplierLocation location : supplier.getLocations()) {
                if (location.getDepartments() != null) {
                    departmentCount += location.getDepartments().size();
                    for (SupplierDepartment department : location.getDepartments()) {
                        if (department.getUsers() != null) {
                            userCount += department.getUsers().size();
                        }
                    }
                }
            }
        }

        return new SupplierStatistics(supplier.getId(), supplier.getCompanyName(), 
                                     locationCount, departmentCount, userCount);
    }

    /**
     * Helper class for supplier statistics
     */
    public static class SupplierStatistics {
        private Long supplierId;
        private String companyName;
        private int locationCount;
        private int departmentCount;
        private int userCount;

        public SupplierStatistics(Long supplierId, String companyName, int locationCount, 
                                 int departmentCount, int userCount) {
            this.supplierId = supplierId;
            this.companyName = companyName;
            this.locationCount = locationCount;
            this.departmentCount = departmentCount;
            this.userCount = userCount;
        }

        // Getters
        public Long getSupplierId() { return supplierId; }
        public String getCompanyName() { return companyName; }
        public int getLocationCount() { return locationCount; }
        public int getDepartmentCount() { return departmentCount; }
        public int getUserCount() { return userCount; }
    }
}