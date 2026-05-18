

// package com.itti.leadcapturing.service;

// import com.itti.leadcapturing.model.Supplier;
// import com.itti.leadcapturing.model.SupplierLocation;
// import com.itti.leadcapturing.model.SupplierDepartment;
// import com.itti.leadcapturing.model.SupplierUser;
// import com.itti.leadcapturing.repo.SupplierRepository;
// import com.itti.leadcapturing.repo.SupplierLocationRepository;
// import com.itti.leadcapturing.repo.SupplierDepartmentRepository;
// import com.itti.leadcapturing.repo.SupplierUserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import lombok.extern.slf4j.Slf4j;

// import java.time.LocalDateTime;
// import java.util.Base64;
// import java.util.List;

// @Service
// @Slf4j
// public class SupplierService {

//     @Autowired
//     private SupplierRepository supplierRepository;

//     @Autowired
//     private SupplierLocationRepository locationRepository;

//     @Autowired
//     private SupplierDepartmentRepository departmentRepository;

//     @Autowired
//     private SupplierUserRepository userRepository;

//     @Autowired
//     private PasswordEncoder passwordEncoder;

//     // ============================================
//     // LOGO PROCESSING HELPER (mirrors BuyerService.processLogo exactly)
//     // ============================================

//     private void processLogo(Supplier supplier, String logoBase64, String logoFilename, String logoContentType) {
//         try {
//             if (logoBase64 != null && !logoBase64.isEmpty()) {
//                 // Remove data URL prefix if present (data:image/png;base64,...)
//                 String base64Data = logoBase64;
//                 if (logoBase64.contains(",")) {
//                     base64Data = logoBase64.split(",")[1];
//                 }
//                 byte[] logoBytes = Base64.getDecoder().decode(base64Data);
//                 supplier.setLogoData(logoBytes);
//                 supplier.setLogoFilename(logoFilename);
//                 supplier.setLogoContentType(logoContentType);
//                 log.info("✅ Supplier logo processed: {} bytes, type: {}", logoBytes.length, logoContentType);
//             }
//         } catch (Exception e) {
//             log.error("❌ Error processing supplier logo: {}", e.getMessage());
//             throw new RuntimeException("Failed to process logo: " + e.getMessage());
//         }
//     }

//     // ============================================
//     // GET SUPPLIER LOGO AS BASE64
//     // ============================================

//     @Transactional(readOnly = true)
//     public String getSupplierLogoBase64(Long supplierId) {
//         log.info("📥 GET SUPPLIER LOGO BASE64 - Supplier ID: {}", supplierId);
//         try {
//             Supplier supplier = supplierRepository.findByIdWithLogoData(supplierId)
//                     .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + supplierId));

//             log.info("  Supplier        : {}", supplier.getCompanyName());
//             log.info("  logoFilename    : {}", supplier.getLogoFilename());
//             log.info("  logoContentType : {}", supplier.getLogoContentType());
//             log.info("  logoData null?  : {}", supplier.getLogoData() == null);
//             log.info("  logoData bytes  : {}", supplier.getLogoData() != null ? supplier.getLogoData().length : 0);

//             if (supplier.getLogoData() == null || supplier.getLogoData().length == 0) {
//                 log.warn("  ⚠️ logoData is EMPTY for supplier: {}", supplierId);
//                 return null;
//             }

//             String contentType = (supplier.getLogoContentType() != null && !supplier.getLogoContentType().trim().isEmpty())
//                     ? supplier.getLogoContentType() : "image/png";

//             String base64Data = Base64.getEncoder().encodeToString(supplier.getLogoData());
//             String dataUrl = "data:" + contentType + ";base64," + base64Data;

//             log.info("  ✅ Logo ready — {} chars, type: {}", dataUrl.length(), contentType);
//             return dataUrl;

//         } catch (RuntimeException e) {
//             log.error("  ❌ {}", e.getMessage());
//             throw e;
//         } catch (Exception e) {
//             log.error("  ❌ Unexpected error: {}", e.getMessage());
//             return null;
//         }
//     }

//     // ============================================
//     // GET RAW LOGO BYTES
//     // ============================================

//     @Transactional(readOnly = true)
//     public byte[] getSupplierLogoBytes(Long supplierId) {
//         try {
//             Supplier supplier = supplierRepository.findById(supplierId)
//                     .orElseThrow(() -> new RuntimeException("Supplier not found"));
//             return supplier.getLogoData();
//         } catch (Exception e) {
//             log.error("❌ Error getting logo bytes for supplier {}: {}", supplierId, e.getMessage());
//             return null;
//         }
//     }

//     // ============================================
//     // CREATE SUPPLIER — ✅ KEY FIX: processLogo() now called before save
//     // ============================================

//     @Transactional
//     public Supplier createSupplier(Supplier supplier) {
//         try {
//             log.info("========================================");
//             log.info("🔵 CREATING SUPPLIER: {}", supplier.getCompanyName());
//             log.info("  logoBase64 present : {}", supplier.getLogoBase64() != null && !supplier.getLogoBase64().isEmpty());
//             log.info("  logoFilename       : {}", supplier.getLogoFilename());
//             log.info("  logoContentType    : {}", supplier.getLogoContentType());
//             log.info("========================================");

//             supplier.setIsDeleted(false);

//             // ✅ KEY FIX: Decode logoBase64 → logoData BEFORE saving.
//             // The old code never called processLogo() here — that's why logo_data was NULL in DB.
//             if (supplier.getLogoBase64() != null && !supplier.getLogoBase64().isEmpty()) {
//                 processLogo(
//                     supplier,
//                     supplier.getLogoBase64(),
//                     supplier.getLogoFilename(),
//                     supplier.getLogoContentType()
//                 );
//             }

//             if (supplier.getLocations() != null) {
//                 log.info("📦 Locations: {}", supplier.getLocations().size());
//                 for (SupplierLocation location : supplier.getLocations()) {
//                     location.setSupplier(supplier);
//                     location.setIsDeleted(false);

//                     if (location.getDepartments() != null) {
//                         for (SupplierDepartment department : location.getDepartments()) {
//                             department.setLocation(location);
//                             department.setIsDeleted(false);

//                             if (department.getUsers() != null) {
//                                 for (SupplierUser user : department.getUsers()) {
//                                     user.setDepartment(department);
//                                     user.setIsDeleted(false);

//                                     // ✅ BCrypt encode password
//                                     if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
//                                         user.setPassword(passwordEncoder.encode(user.getPassword()));
//                                         log.info("            🔐 Password encoded for user: {}", user.getEmail());
//                                     } else {
//                                         log.error("            ❌ NO PASSWORD for user: {}", user.getEmail());
//                                         throw new RuntimeException("Password is required for user: " + user.getEmail());
//                                     }
//                                 }
//                             }
//                         }
//                     }
//                 }
//             }

//             Supplier saved = supplierRepository.save(supplier);

//             log.info("========================================");
//             log.info("✅ SUPPLIER SAVED SUCCESSFULLY");
//             log.info("  ID         : {}", saved.getId());
//             log.info("  Company    : {}", saved.getCompanyName());
//             log.info("  Logo saved : {}", saved.getLogoData() != null && saved.getLogoData().length > 0);
//             log.info("========================================");

//             return saved;

//         } catch (Exception e) {
//             log.error("❌ ERROR CREATING SUPPLIER: {}", e.getMessage());
//             e.printStackTrace();
//             throw new RuntimeException("Failed to create supplier: " + e.getMessage(), e);
//         }
//     }

//     // ============================================
//     // GET ALL SUPPLIERS
//     // ============================================

//     @Transactional(readOnly = true)
//     public List<Supplier> getAllSuppliers() {
//         try {
//             log.info("📥 Fetching all suppliers with complete hierarchy");
//             List<Supplier> suppliers = supplierRepository.findByIsDeletedFalse();
//             suppliers.forEach(supplier -> {
//                 if (supplier.getLocations() != null) {
//                     supplier.getLocations().size();
//                     supplier.getLocations().forEach(location -> {
//                         if (location.getDepartments() != null) {
//                             location.getDepartments().size();
//                             location.getDepartments().forEach(department -> {
//                                 if (department.getUsers() != null) department.getUsers().size();
//                             });
//                         }
//                     });
//                 }
//             });
//             log.info("✅ Found {} suppliers", suppliers.size());
//             return suppliers;
//         } catch (Exception e) {
//             log.error("❌ Error fetching suppliers", e);
//             throw new RuntimeException("Failed to fetch suppliers: " + e.getMessage(), e);
//         }
//     }

//     // ============================================
//     // GET SUPPLIER BY ID
//     // ============================================

//     @Transactional(readOnly = true)
//     public Supplier getSupplierById(Long id) {
//         try {
//             log.info("📥 Fetching supplier with ID: {}", id);
//             Supplier supplier = supplierRepository.findById(id)
//                     .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));
//             if (supplier.getLocations() != null) {
//                 supplier.getLocations().size();
//                 supplier.getLocations().forEach(location -> {
//                     if (location.getDepartments() != null) {
//                         location.getDepartments().size();
//                         location.getDepartments().forEach(department -> {
//                             if (department.getUsers() != null) department.getUsers().size();
//                         });
//                     }
//                 });
//             }
//             log.info("✅ Supplier loaded: {}", supplier.getCompanyName());
//             return supplier;
//         } catch (Exception e) {
//             log.error("❌ Error fetching supplier with ID: {}", id, e);
//             throw new RuntimeException("Failed to fetch supplier: " + e.getMessage(), e);
//         }
//     }

//     // ============================================
//     // UPDATE SUPPLIER
//     // ============================================

//     @Transactional
//     public Supplier updateSupplier(Long id, Supplier supplierReq) {
//         try {
//             log.info("🔵 Updating supplier with ID: {}", id);
//             Supplier existingSupplier = supplierRepository.findById(id)
//                     .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));

//             if (supplierReq.getCompanyName() != null && !supplierReq.getCompanyName().trim().isEmpty())
//                 existingSupplier.setCompanyName(supplierReq.getCompanyName());
//             if (supplierReq.getCompanyType() != null && !supplierReq.getCompanyType().trim().isEmpty())
//                 existingSupplier.setCompanyType(supplierReq.getCompanyType());
//             if (supplierReq.getIndustrySector() != null && !supplierReq.getIndustrySector().trim().isEmpty())
//                 existingSupplier.setIndustrySector(supplierReq.getIndustrySector());

//             // ✅ Update logo if new base64 provided
//             if (supplierReq.getLogoBase64() != null && !supplierReq.getLogoBase64().isEmpty()) {
//                 processLogo(existingSupplier, supplierReq.getLogoBase64(),
//                             supplierReq.getLogoFilename(), supplierReq.getLogoContentType());
//                 log.info("✅ Logo updated for supplier: {}", id);
//             }

//             if (supplierReq.getContactPersonName() != null && !supplierReq.getContactPersonName().trim().isEmpty())
//                 existingSupplier.setContactPersonName(supplierReq.getContactPersonName());
//             if (supplierReq.getContactPersonDesignation() != null && !supplierReq.getContactPersonDesignation().trim().isEmpty())
//                 existingSupplier.setContactPersonDesignation(supplierReq.getContactPersonDesignation());
//             if (supplierReq.getContactPersonEmail() != null && !supplierReq.getContactPersonEmail().trim().isEmpty())
//                 existingSupplier.setContactPersonEmail(supplierReq.getContactPersonEmail());
//             if (supplierReq.getContactPersonPhone() != null && !supplierReq.getContactPersonPhone().trim().isEmpty())
//                 existingSupplier.setContactPersonPhone(supplierReq.getContactPersonPhone());
//             if (supplierReq.getAddressLine1() != null && !supplierReq.getAddressLine1().trim().isEmpty())
//                 existingSupplier.setAddressLine1(supplierReq.getAddressLine1());
//             if (supplierReq.getAddressLine2() != null)
//                 existingSupplier.setAddressLine2(supplierReq.getAddressLine2());
//             if (supplierReq.getCity() != null && !supplierReq.getCity().trim().isEmpty())
//                 existingSupplier.setCity(supplierReq.getCity());
//             if (supplierReq.getState() != null && !supplierReq.getState().trim().isEmpty())
//                 existingSupplier.setState(supplierReq.getState());
//             if (supplierReq.getPostalCode() != null && !supplierReq.getPostalCode().trim().isEmpty())
//                 existingSupplier.setPostalCode(supplierReq.getPostalCode());
//             if (supplierReq.getCountry() != null && !supplierReq.getCountry().trim().isEmpty())
//                 existingSupplier.setCountry(supplierReq.getCountry());
//             if (supplierReq.getGstNumber() != null && !supplierReq.getGstNumber().trim().isEmpty())
//                 existingSupplier.setGstNumber(supplierReq.getGstNumber());
//             if (supplierReq.getPanNumber() != null && !supplierReq.getPanNumber().trim().isEmpty())
//                 existingSupplier.setPanNumber(supplierReq.getPanNumber());
//             if (supplierReq.getTanNumber() != null && !supplierReq.getTanNumber().trim().isEmpty())
//                 existingSupplier.setTanNumber(supplierReq.getTanNumber());
//             if (supplierReq.getWebsite() != null && !supplierReq.getWebsite().trim().isEmpty())
//                 existingSupplier.setWebsite(supplierReq.getWebsite());

//             if (supplierReq.getLocations() != null) {
//                 existingSupplier.getLocations().clear();
//                 for (SupplierLocation newLocation : supplierReq.getLocations()) {
//                     newLocation.setSupplier(existingSupplier);
//                     newLocation.setIsDeleted(false);
//                     if (newLocation.getDepartments() != null) {
//                         for (SupplierDepartment newDepartment : newLocation.getDepartments()) {
//                             newDepartment.setLocation(newLocation);
//                             newDepartment.setIsDeleted(false);
//                             if (newDepartment.getUsers() != null) {
//                                 for (SupplierUser newUser : newDepartment.getUsers()) {
//                                     newUser.setDepartment(newDepartment);
//                                     newUser.setIsDeleted(false);
//                                     if (newUser.getPassword() != null && !newUser.getPassword().trim().isEmpty()) {
//                                         newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
//                                     }
//                                 }
//                             }
//                         }
//                     }
//                     existingSupplier.getLocations().add(newLocation);
//                 }
//             }

//             Supplier updatedSupplier = supplierRepository.save(existingSupplier);
//             if (updatedSupplier.getLocations() != null) updatedSupplier.getLocations().size();
//             log.info("✅ Supplier updated: {} (ID: {})", updatedSupplier.getCompanyName(), updatedSupplier.getId());
//             return updatedSupplier;

//         } catch (Exception e) {
//             log.error("❌ Error updating supplier with ID: {}", id, e);
//             throw new RuntimeException("Failed to update supplier: " + e.getMessage(), e);
//         }
//     }

//     // ============================================
//     // DELETE SUPPLIER
//     // ============================================

//     @Transactional
//     public void deleteSupplier(Long id) {
//         try {
//             log.info("🔵 Soft deleting supplier with ID: {}", id);
//             Supplier supplier = supplierRepository.findById(id)
//                     .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));
//             if (supplier.getLocations() != null && !supplier.getLocations().isEmpty()) {
//                 supplier.getLocations().forEach(location -> deleteLocationRecursive(location));
//             }
//             supplier.setIsDeleted(true);
//             supplier.setDeletedAt(LocalDateTime.now());
//             supplierRepository.save(supplier);
//             log.info("✅ Supplier soft deleted: {} (ID: {})", supplier.getCompanyName(), supplier.getId());
//         } catch (Exception e) {
//             log.error("❌ Error deleting supplier with ID: {}", id, e);
//             throw new RuntimeException("Failed to delete supplier: " + e.getMessage(), e);
//         }
//     }

//     private void deleteLocationRecursive(SupplierLocation location) {
//         if (location.getDepartments() != null && !location.getDepartments().isEmpty()) {
//             location.getDepartments().forEach(department -> deleteDepartmentRecursive(department));
//         }
//         location.setIsDeleted(true);
//         location.setDeletedAt(LocalDateTime.now());
//         locationRepository.save(location);
//     }

//     private void deleteDepartmentRecursive(SupplierDepartment department) {
//         if (department.getUsers() != null && !department.getUsers().isEmpty()) {
//             department.getUsers().forEach(user -> {
//                 user.setIsDeleted(true);
//                 user.setDeletedAt(LocalDateTime.now());
//                 userRepository.save(user);
//             });
//         }
//         department.setIsDeleted(true);
//         department.setDeletedAt(LocalDateTime.now());
//         departmentRepository.save(department);
//     }

//     @Transactional(readOnly = true)
//     public List<Supplier> getAllSuppliersIncludingDeleted() {
//         List<Supplier> suppliers = supplierRepository.findAll();
//         suppliers.forEach(s -> { if (s.getLocations() != null) s.getLocations().size(); });
//         return suppliers;
//     }

//     @Transactional(readOnly = true)
//     public List<Supplier> searchByCompanyName(String companyName) {
//         return supplierRepository.findByCompanyNameContainingIgnoreCaseAndIsDeletedFalse(companyName);
//     }

//     @Transactional(readOnly = true)
//     public List<Supplier> searchByIndustrySector(String industrySector) {
//         return supplierRepository.findByIndustrySectorAndIsDeletedFalse(industrySector);
//     }

//     @Transactional(readOnly = true)
//     public List<Supplier> searchByState(String state) {
//         return supplierRepository.findByStateAndIsDeletedFalse(state);
//     }

//     @Transactional(readOnly = true)
//     public Long getActiveSupplierCount() {
//         return supplierRepository.countByIsDeletedFalse();
//     }

//     @Transactional
//     public Supplier restoreSupplier(Long id) {
//         Supplier supplier = supplierRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Supplier not found"));
//         supplier.setIsDeleted(false);
//         supplier.setDeletedAt(null);
//         if (supplier.getLocations() != null) {
//             supplier.getLocations().forEach(location -> {
//                 if (location.getDepartments() != null) {
//                     location.getDepartments().forEach(department -> {
//                         if (department.getUsers() != null) {
//                             department.getUsers().forEach(user -> {
//                                 user.setIsDeleted(false);
//                                 user.setDeletedAt(null);
//                                 userRepository.save(user);
//                             });
//                         }
//                         department.setIsDeleted(false);
//                         department.setDeletedAt(null);
//                         departmentRepository.save(department);
//                     });
//                 }
//                 location.setIsDeleted(false);
//                 location.setDeletedAt(null);
//                 locationRepository.save(location);
//             });
//         }
//         return supplierRepository.save(supplier);
//     }

//     @Transactional(readOnly = true)
//     public Supplier getSupplierWithHierarchy(Long id) {
//         Supplier supplier = supplierRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Supplier not found"));
//         if (supplier.getLocations() != null) {
//             supplier.getLocations().forEach(location -> {
//                 if (location.getDepartments() != null) {
//                     location.getDepartments().forEach(department -> {
//                         if (department.getUsers() != null) department.getUsers().size();
//                     });
//                 }
//             });
//         }
//         return supplier;
//     }

//     @Transactional(readOnly = true)
//     public List<Supplier> getAllSuppliersWithHierarchy() {
//         return getAllSuppliers();
//     }

//     @Transactional(readOnly = true)
//     public SupplierStatistics getSupplierStatistics(Long id) {
//         Supplier supplier = getSupplierWithHierarchy(id);
//         int locationCount = supplier.getLocations() != null ? supplier.getLocations().size() : 0;
//         int departmentCount = 0;
//         int userCount = 0;
//         if (supplier.getLocations() != null) {
//             for (SupplierLocation location : supplier.getLocations()) {
//                 if (location.getDepartments() != null) {
//                     departmentCount += location.getDepartments().size();
//                     for (SupplierDepartment department : location.getDepartments()) {
//                         if (department.getUsers() != null) userCount += department.getUsers().size();
//                     }
//                 }
//             }
//         }
//         return new SupplierStatistics(supplier.getId(), supplier.getCompanyName(), locationCount, departmentCount, userCount);
//     }

//     public static class SupplierStatistics {
//         private Long supplierId;
//         private String companyName;
//         private int locationCount;
//         private int departmentCount;
//         private int userCount;

//         public SupplierStatistics(Long supplierId, String companyName, int locationCount, int departmentCount, int userCount) {
//             this.supplierId = supplierId;
//             this.companyName = companyName;
//             this.locationCount = locationCount;
//             this.departmentCount = departmentCount;
//             this.userCount = userCount;
//         }

//         public Long getSupplierId() { return supplierId; }
//         public String getCompanyName() { return companyName; }
//         public int getLocationCount() { return locationCount; }
//         public int getDepartmentCount() { return departmentCount; }
//         public int getUserCount() { return userCount; }
//     }
//     // ============================================================
// // 2. ADD to SupplierService.java
// // ============================================================

//     @Transactional
//     public void updateTheme(Long id, String theme) {
//         log.info("🎨 Updating theme for Supplier ID: {} → {}", id, theme);
//         Supplier supplier = supplierRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));
//         supplier.setTheme(theme);
//         supplierRepository.save(supplier);
//         log.info("✅ Supplier theme updated: {}", theme);
//     }

//     @Transactional(readOnly = true)
//     public String getTheme(Long id) {
//         Supplier supplier = supplierRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));
//         return supplier.getTheme() != null ? supplier.getTheme() : "white";
//     }
// }


package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.Supplier;
import com.itti.leadcapturing.model.SupplierLocation;
import com.itti.leadcapturing.model.SupplierDepartment;
import com.itti.leadcapturing.model.SupplierUser;
import com.itti.leadcapturing.dto.SupplierRegistrationRequest;
import com.itti.leadcapturing.model.ApprovalStatus;
import com.itti.leadcapturing.repo.SupplierRepository;
import com.itti.leadcapturing.repo.SupplierLocationRepository;
import com.itti.leadcapturing.repo.SupplierDepartmentRepository;
import com.itti.leadcapturing.repo.SupplierUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;


import java.time.LocalDateTime;
import java.util.Base64;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ============================================================
    // LOGO PROCESSING HELPER
    // ============================================================

    private void processLogo(Supplier supplier, String logoBase64, String logoFilename, String logoContentType) {
        try {
            if (logoBase64 != null && !logoBase64.isEmpty()) {
                String base64Data = logoBase64;
                if (logoBase64.contains(",")) {
                    base64Data = logoBase64.split(",")[1];
                }
                byte[] logoBytes = Base64.getDecoder().decode(base64Data);
                supplier.setLogoData(logoBytes);
                supplier.setLogoFilename(logoFilename);
                supplier.setLogoContentType(logoContentType);
                log.info("✅ Supplier logo processed: {} bytes, type: {}", logoBytes.length, logoContentType);
            }
        } catch (Exception e) {
            log.error("❌ Error processing supplier logo: {}", e.getMessage());
            throw new RuntimeException("Failed to process logo: " + e.getMessage());
        }
    }

    // ============================================================
    // GET SUPPLIER LOGO AS BASE64
    // ============================================================

    @Transactional(readOnly = true)
    public String getSupplierLogoBase64(Long supplierId) {
        log.info("📥 GET SUPPLIER LOGO BASE64 - Supplier ID: {}", supplierId);
        try {
            Supplier supplier = supplierRepository.findByIdWithLogoData(supplierId)
                    .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + supplierId));

            if (supplier.getLogoData() == null || supplier.getLogoData().length == 0) {
                log.warn("  ⚠️ logoData is EMPTY for supplier: {}", supplierId);
                return null;
            }

            String contentType = (supplier.getLogoContentType() != null && !supplier.getLogoContentType().trim().isEmpty())
                    ? supplier.getLogoContentType() : "image/png";

            String base64Data = Base64.getEncoder().encodeToString(supplier.getLogoData());
            String dataUrl = "data:" + contentType + ";base64," + base64Data;

            log.info("  ✅ Logo ready — {} chars, type: {}", dataUrl.length(), contentType);
            return dataUrl;

        } catch (RuntimeException e) {
            log.error("  ❌ {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("  ❌ Unexpected error: {}", e.getMessage());
            return null;
        }
    }

    // ============================================================
    // GET RAW LOGO BYTES
    // ============================================================

    @Transactional(readOnly = true)
    public byte[] getSupplierLogoBytes(Long supplierId) {
        try {
            Supplier supplier = supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            return supplier.getLogoData();
        } catch (Exception e) {
            log.error("❌ Error getting logo bytes for supplier {}: {}", supplierId, e.getMessage());
            return null;
        }
    }

    // ============================================================
    // CREATE SUPPLIER
    // ============================================================

    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        try {
            log.info("========================================");
            log.info("🔵 CREATING SUPPLIER: {}", supplier.getCompanyName());
            log.info("========================================");

            supplier.setIsDeleted(false);
            // New suppliers always start as PENDING — approval workflow will handle progression
            supplier.setApprovalStatus(ApprovalStatus.PENDING);

            if (supplier.getLogoBase64() != null && !supplier.getLogoBase64().isEmpty()) {
                processLogo(supplier, supplier.getLogoBase64(),
                        supplier.getLogoFilename(), supplier.getLogoContentType());
            }

            if (supplier.getLocations() != null) {
                for (SupplierLocation location : supplier.getLocations()) {
                    location.setSupplier(supplier);
                    location.setIsDeleted(false);

                    if (location.getDepartments() != null) {
                        for (SupplierDepartment department : location.getDepartments()) {
                            department.setLocation(location);
                            department.setIsDeleted(false);

                            if (department.getUsers() != null) {
                                for (SupplierUser user : department.getUsers()) {
                                    user.setDepartment(department);
                                    user.setIsDeleted(false);

                                    if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                                        user.setPassword(passwordEncoder.encode(user.getPassword()));
                                    } else {
                                        throw new RuntimeException("Password is required for user: " + user.getEmail());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Supplier saved = supplierRepository.save(supplier);
            log.info("✅ SUPPLIER SAVED — ID: {}, Status: {}", saved.getId(), saved.getApprovalStatus());
            return saved;

        } catch (Exception e) {
            log.error("❌ ERROR CREATING SUPPLIER: {}", e.getMessage());
            throw new RuntimeException("Failed to create supplier: " + e.getMessage(), e);
        }
    }

    // ============================================================
    // ✅ GET ALL APPROVED SUPPLIERS (used in dashboard table + RFQ)
    // This is what the org-admin dashboard and RFQ supplier picker use.
    // Only APPROVED suppliers are visible/usable.
    // ============================================================

    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliers() {
        try {
            log.info("📥 Fetching all APPROVED suppliers");
            List<Supplier> suppliers = supplierRepository.findAllApproved();
            suppliers.forEach(supplier -> {
                if (supplier.getLocations() != null) {
                    supplier.getLocations().size();
                    supplier.getLocations().forEach(location -> {
                        if (location.getDepartments() != null) {
                            location.getDepartments().size();
                            location.getDepartments().forEach(department -> {
                                if (department.getUsers() != null) department.getUsers().size();
                            });
                        }
                    });
                }
            });
            log.info("✅ Found {} APPROVED suppliers", suppliers.size());
            return suppliers;
        } catch (Exception e) {
            log.error("❌ Error fetching approved suppliers", e);
            throw new RuntimeException("Failed to fetch suppliers: " + e.getMessage(), e);
        }
    }

    // ============================================================
    // GET ALL SUPPLIERS INCLUDING PENDING/REJECTED (admin-only use)
    // Used only by the org-admin when managing/reviewing all suppliers.
    // ============================================================

    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliersForAdmin() {
        try {
            log.info("📥 Fetching ALL suppliers (admin view — all statuses)");
            List<Supplier> suppliers = supplierRepository.findByIsDeletedFalse();
            suppliers.forEach(supplier -> {
                if (supplier.getLocations() != null) {
                    supplier.getLocations().size();
                    supplier.getLocations().forEach(location -> {
                        if (location.getDepartments() != null) {
                            location.getDepartments().size();
                            location.getDepartments().forEach(department -> {
                                if (department.getUsers() != null) department.getUsers().size();
                            });
                        }
                    });
                }
            });
            log.info("✅ Found {} total suppliers (all statuses)", suppliers.size());
            return suppliers;
        } catch (Exception e) {
            log.error("❌ Error fetching all suppliers", e);
            throw new RuntimeException("Failed to fetch suppliers: " + e.getMessage(), e);
        }
    }

    // ============================================================
    // GET SUPPLIER BY ID
    // ============================================================

    @Transactional(readOnly = true)
    public Supplier getSupplierById(Long id) {
        try {
            log.info("📥 Fetching supplier with ID: {}", id);
            Supplier supplier = supplierRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));
            if (supplier.getLocations() != null) {
                supplier.getLocations().size();
                supplier.getLocations().forEach(location -> {
                    if (location.getDepartments() != null) {
                        location.getDepartments().size();
                        location.getDepartments().forEach(department -> {
                            if (department.getUsers() != null) department.getUsers().size();
                        });
                    }
                });
            }
            log.info("✅ Supplier loaded: {}", supplier.getCompanyName());
            return supplier;
        } catch (Exception e) {
            log.error("❌ Error fetching supplier with ID: {}", id, e);
            throw new RuntimeException("Failed to fetch supplier: " + e.getMessage(), e);
        }
    }

    // ============================================================
    // UPDATE SUPPLIER
    // ============================================================

    @Transactional
    public Supplier updateSupplier(Long id, Supplier supplierReq) {
        try {
            log.info("🔵 Updating supplier with ID: {}", id);
            Supplier existingSupplier = supplierRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));

            if (supplierReq.getCompanyName() != null && !supplierReq.getCompanyName().trim().isEmpty())
                existingSupplier.setCompanyName(supplierReq.getCompanyName());
            if (supplierReq.getCompanyType() != null && !supplierReq.getCompanyType().trim().isEmpty())
                existingSupplier.setCompanyType(supplierReq.getCompanyType());
            if (supplierReq.getIndustrySector() != null && !supplierReq.getIndustrySector().trim().isEmpty())
                existingSupplier.setIndustrySector(supplierReq.getIndustrySector());

            if (supplierReq.getLogoBase64() != null && !supplierReq.getLogoBase64().isEmpty()) {
                processLogo(existingSupplier, supplierReq.getLogoBase64(),
                        supplierReq.getLogoFilename(), supplierReq.getLogoContentType());
            }

            if (supplierReq.getContactPersonName() != null && !supplierReq.getContactPersonName().trim().isEmpty())
                existingSupplier.setContactPersonName(supplierReq.getContactPersonName());
            if (supplierReq.getContactPersonDesignation() != null && !supplierReq.getContactPersonDesignation().trim().isEmpty())
                existingSupplier.setContactPersonDesignation(supplierReq.getContactPersonDesignation());
            if (supplierReq.getContactPersonEmail() != null && !supplierReq.getContactPersonEmail().trim().isEmpty())
                existingSupplier.setContactPersonEmail(supplierReq.getContactPersonEmail());
            if (supplierReq.getContactPersonPhone() != null && !supplierReq.getContactPersonPhone().trim().isEmpty())
                existingSupplier.setContactPersonPhone(supplierReq.getContactPersonPhone());
            if (supplierReq.getAddressLine1() != null && !supplierReq.getAddressLine1().trim().isEmpty())
                existingSupplier.setAddressLine1(supplierReq.getAddressLine1());
            if (supplierReq.getAddressLine2() != null)
                existingSupplier.setAddressLine2(supplierReq.getAddressLine2());
            if (supplierReq.getCity() != null && !supplierReq.getCity().trim().isEmpty())
                existingSupplier.setCity(supplierReq.getCity());
            if (supplierReq.getState() != null && !supplierReq.getState().trim().isEmpty())
                existingSupplier.setState(supplierReq.getState());
            if (supplierReq.getPostalCode() != null && !supplierReq.getPostalCode().trim().isEmpty())
                existingSupplier.setPostalCode(supplierReq.getPostalCode());
            if (supplierReq.getCountry() != null && !supplierReq.getCountry().trim().isEmpty())
                existingSupplier.setCountry(supplierReq.getCountry());
            if (supplierReq.getGstNumber() != null && !supplierReq.getGstNumber().trim().isEmpty())
                existingSupplier.setGstNumber(supplierReq.getGstNumber());
            if (supplierReq.getPanNumber() != null && !supplierReq.getPanNumber().trim().isEmpty())
                existingSupplier.setPanNumber(supplierReq.getPanNumber());
            if (supplierReq.getTanNumber() != null && !supplierReq.getTanNumber().trim().isEmpty())
                existingSupplier.setTanNumber(supplierReq.getTanNumber());
            if (supplierReq.getWebsite() != null && !supplierReq.getWebsite().trim().isEmpty())
                existingSupplier.setWebsite(supplierReq.getWebsite());

            if (supplierReq.getLocations() != null) {
                existingSupplier.getLocations().clear();
                for (SupplierLocation newLocation : supplierReq.getLocations()) {
                    newLocation.setSupplier(existingSupplier);
                    newLocation.setIsDeleted(false);
                    if (newLocation.getDepartments() != null) {
                        for (SupplierDepartment newDepartment : newLocation.getDepartments()) {
                            newDepartment.setLocation(newLocation);
                            newDepartment.setIsDeleted(false);
                            if (newDepartment.getUsers() != null) {
                                for (SupplierUser newUser : newDepartment.getUsers()) {
                                    newUser.setDepartment(newDepartment);
                                    newUser.setIsDeleted(false);
                                    if (newUser.getPassword() != null && !newUser.getPassword().trim().isEmpty()) {
                                        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
                                    }
                                }
                            }
                        }
                    }
                    existingSupplier.getLocations().add(newLocation);
                }
            }

            Supplier updatedSupplier = supplierRepository.save(existingSupplier);
            if (updatedSupplier.getLocations() != null) updatedSupplier.getLocations().size();
            log.info("✅ Supplier updated: {} (ID: {})", updatedSupplier.getCompanyName(), updatedSupplier.getId());
            return updatedSupplier;

        } catch (Exception e) {
            log.error("❌ Error updating supplier with ID: {}", id, e);
            throw new RuntimeException("Failed to update supplier: " + e.getMessage(), e);
        }
    }

    // ============================================================
    // DELETE SUPPLIER
    // ============================================================

    @Transactional
    public void deleteSupplier(Long id) {
        try {
            log.info("🔵 Soft deleting supplier with ID: {}", id);
            Supplier supplier = supplierRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));
            if (supplier.getLocations() != null && !supplier.getLocations().isEmpty()) {
                supplier.getLocations().forEach(location -> deleteLocationRecursive(location));
            }
            supplier.setIsDeleted(true);
            supplier.setDeletedAt(LocalDateTime.now());
            supplierRepository.save(supplier);
            log.info("✅ Supplier soft deleted: {} (ID: {})", supplier.getCompanyName(), supplier.getId());
        } catch (Exception e) {
            log.error("❌ Error deleting supplier with ID: {}", id, e);
            throw new RuntimeException("Failed to delete supplier: " + e.getMessage(), e);
        }
    }

    private void deleteLocationRecursive(SupplierLocation location) {
        if (location.getDepartments() != null && !location.getDepartments().isEmpty()) {
            location.getDepartments().forEach(department -> deleteDepartmentRecursive(department));
        }
        location.setIsDeleted(true);
        location.setDeletedAt(LocalDateTime.now());
        locationRepository.save(location);
    }

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

    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliersIncludingDeleted() {
        List<Supplier> suppliers = supplierRepository.findAll();
        suppliers.forEach(s -> { if (s.getLocations() != null) s.getLocations().size(); });
        return suppliers;
    }

    @Transactional(readOnly = true)
    public List<Supplier> searchByCompanyName(String companyName) {
        // ✅ Only search approved suppliers for general use
        return supplierRepository.findApprovedByCompanyNameContaining(companyName);
    }

    @Transactional(readOnly = true)
    public List<Supplier> searchByIndustrySector(String industrySector) {
        return supplierRepository.findByIndustrySectorAndIsDeletedFalse(industrySector);
    }

    @Transactional(readOnly = true)
    public List<Supplier> searchByState(String state) {
        return supplierRepository.findByStateAndIsDeletedFalse(state);
    }

    @Transactional(readOnly = true)
    public Long getActiveSupplierCount() {
        return supplierRepository.countByIsDeletedFalse();
    }

    // ============================================================
// SELF-REGISTRATION
// ============================================================

// @Transactional
// public Supplier registerSupplier(SupplierRegistrationRequest req) {
//     log.info("🔵 SUPPLIER SELF-REGISTER: {} | Email: {}", req.getCompanyName(), req.getEmail());

//     if (userRepository.existsByEmail(req.getEmail())) {
//         throw new RuntimeException("An account with this email already exists.");
//     }

//     // Build User (password will be encoded by createSupplier)
//     SupplierUser user = new SupplierUser();
//     user.setFirstName(req.getFirstName());
//     user.setLastName(req.getLastName());
//     user.setEmail(req.getEmail());
//     user.setPhone(req.getPhone());
//     user.setDesignation(req.getDesignation());
//     user.setEmployeeId("SUP-" + System.currentTimeMillis());
//     user.setPassword(req.getPassword());
//     user.setIsDeleted(false);

//     // Build default Department
//     SupplierDepartment dept = new SupplierDepartment();
//     dept.setDepartmentName("General");
//     dept.setDepartmentDescription("Default department");
//     dept.setCategoryOfProducts("General");
//     dept.setIsDeleted(false);
//     dept.setUsers(new java.util.ArrayList<>(java.util.List.of(user)));

//     // Build Head Office Location
//     SupplierLocation location = new SupplierLocation();
//     location.setLocationName("Head Office");
//     location.setLocationType("Head Office");
//     location.setLocationContactName(req.getFirstName() + " " + req.getLastName());
//     location.setLocationContactEmail(req.getEmail());
//     location.setLocationContactPhone(req.getPhone());
//     location.setAddressLine1(req.getAddressLine1());
//     location.setAddressLine2(req.getAddressLine2());
//     location.setCity(req.getCity());
//     location.setState(req.getState());
//     location.setPostalCode(req.getPostalCode());
//     location.setCountry(req.getCountry());
//     location.setIsDeleted(false);
//     location.setDepartments(new java.util.ArrayList<>(java.util.List.of(dept)));

//     // Build Supplier
//     Supplier supplier = new Supplier();
//     supplier.setCompanyName(req.getCompanyName());
//     supplier.setCompanyType(req.getCompanyType());
//     supplier.setIndustrySector(req.getIndustrySector());
//     supplier.setWebsite(req.getWebsite());
//     supplier.setGstNumber(req.getGstNumber());
//     supplier.setPanNumber(req.getPanNumber());
//     supplier.setContactPersonName(req.getFirstName() + " " + req.getLastName());
//     supplier.setContactPersonDesignation(req.getDesignation());
//     supplier.setContactPersonEmail(req.getEmail());
//     supplier.setContactPersonPhone(req.getPhone());
//     supplier.setAddressLine1(req.getAddressLine1());
//     supplier.setAddressLine2(req.getAddressLine2());
//     supplier.setCity(req.getCity());
//     supplier.setState(req.getState());
//     supplier.setPostalCode(req.getPostalCode());
//     supplier.setCountry(req.getCountry());
//     supplier.setCreatedByCompanyName(req.getOrganizationCompanyName());
//     supplier.setLocations(new java.util.ArrayList<>(java.util.List.of(location)));

//     // createSupplier handles encoding passwords + setting relationships
//     return createSupplier(supplier);
// }

@Transactional
public Supplier registerSupplier(SupplierRegistrationRequest req) {
    log.info("🔵 SUPPLIER SELF-REGISTER: {} | Email: {}", req.getCompanyName(), req.getEmail());

    if (userRepository.existsByEmail(req.getEmail())) {
        throw new RuntimeException("An account with this email already exists.");
    }

    SupplierUser user = new SupplierUser();
    user.setFirstName(req.getFirstName());
    user.setLastName(req.getLastName());
    user.setEmail(req.getEmail());
    user.setPhone(req.getPhone());
    user.setDesignation(req.getDesignation());
    user.setEmployeeId("SUP-" + System.currentTimeMillis());
    user.setPassword(req.getPassword());
    user.setIsDeleted(false);

    SupplierDepartment dept = new SupplierDepartment();
    dept.setDepartmentName("General");
    dept.setDepartmentDescription("Default department");
    dept.setCategoryOfProducts("General");
    dept.setIsDeleted(false);
    dept.setUsers(new java.util.ArrayList<>(java.util.List.of(user)));

    SupplierLocation location = new SupplierLocation();
    location.setLocationName("Head Office");
    location.setLocationType("Head Office");
    location.setLocationContactName(req.getFirstName() + " " + req.getLastName());
    location.setLocationContactEmail(req.getEmail());
    location.setLocationContactPhone(req.getPhone());
    location.setAddressLine1(req.getAddressLine1());
    location.setAddressLine2(req.getAddressLine2());
    location.setCity(req.getCity());
    location.setState(req.getState());
    location.setPostalCode(req.getPostalCode());
    location.setCountry(req.getCountry());
    location.setIsDeleted(false);
    location.setDepartments(new java.util.ArrayList<>(java.util.List.of(dept)));

    Supplier supplier = new Supplier();
    supplier.setCompanyName(req.getCompanyName());
    supplier.setCompanyType(req.getCompanyType());
    supplier.setIndustrySector(req.getIndustrySector());
    supplier.setWebsite(req.getWebsite());
    supplier.setGstNumber(req.getGstNumber());
    supplier.setPanNumber(req.getPanNumber());
    supplier.setContactPersonName(req.getFirstName() + " " + req.getLastName());
    supplier.setContactPersonDesignation(req.getDesignation());
    supplier.setContactPersonEmail(req.getEmail());
    supplier.setContactPersonPhone(req.getPhone());
    supplier.setAddressLine1(req.getAddressLine1());
    supplier.setAddressLine2(req.getAddressLine2());
    supplier.setCity(req.getCity());
    supplier.setState(req.getState());
    supplier.setPostalCode(req.getPostalCode());
    supplier.setCountry(req.getCountry());
    supplier.setCreatedByCompanyName(req.getOrganizationCompanyName());
    supplier.setLocations(new java.util.ArrayList<>(java.util.List.of(location)));

    // ✅ FIX: process logo if provided during self-registration
    if (req.getLogoBase64() != null && !req.getLogoBase64().isEmpty()) {
        supplier.setLogoBase64(req.getLogoBase64());
        supplier.setLogoFilename(req.getLogoFilename() != null ? req.getLogoFilename() : "logo.png");
        supplier.setLogoContentType(req.getLogoContentType() != null ? req.getLogoContentType() : "image/png");
    }

    return createSupplier(supplier);
}

    @Transactional
    public Supplier restoreSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        supplier.setIsDeleted(false);
        supplier.setDeletedAt(null);
        if (supplier.getLocations() != null) {
            supplier.getLocations().forEach(location -> {
                if (location.getDepartments() != null) {
                    location.getDepartments().forEach(department -> {
                        if (department.getUsers() != null) {
                            department.getUsers().forEach(user -> {
                                user.setIsDeleted(false);
                                user.setDeletedAt(null);
                                userRepository.save(user);
                            });
                        }
                        department.setIsDeleted(false);
                        department.setDeletedAt(null);
                        departmentRepository.save(department);
                    });
                }
                location.setIsDeleted(false);
                location.setDeletedAt(null);
                locationRepository.save(location);
            });
        }
        return supplierRepository.save(supplier);
    }

    @Transactional(readOnly = true)
    public Supplier getSupplierWithHierarchy(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        if (supplier.getLocations() != null) {
            supplier.getLocations().forEach(location -> {
                if (location.getDepartments() != null) {
                    location.getDepartments().forEach(department -> {
                        if (department.getUsers() != null) department.getUsers().size();
                    });
                }
            });
        }
        return supplier;
    }

    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliersWithHierarchy() {
        return getAllSuppliers(); // returns only approved
    }

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
                        if (department.getUsers() != null) userCount += department.getUsers().size();
                    }
                }
            }
        }
        return new SupplierStatistics(supplier.getId(), supplier.getCompanyName(), locationCount, departmentCount, userCount);
    }

    public static class SupplierStatistics {
        private Long supplierId;
        private String companyName;
        private int locationCount;
        private int departmentCount;
        private int userCount;

        public SupplierStatistics(Long supplierId, String companyName, int locationCount, int departmentCount, int userCount) {
            this.supplierId = supplierId;
            this.companyName = companyName;
            this.locationCount = locationCount;
            this.departmentCount = departmentCount;
            this.userCount = userCount;
        }

        public Long getSupplierId() { return supplierId; }
        public String getCompanyName() { return companyName; }
        public int getLocationCount() { return locationCount; }
        public int getDepartmentCount() { return departmentCount; }
        public int getUserCount() { return userCount; }
    }

    @Transactional
    public void updateTheme(Long id, String theme) {
        log.info("🎨 Updating theme for Supplier ID: {} → {}", id, theme);
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));
        supplier.setTheme(theme);
        supplierRepository.save(supplier);
        log.info("✅ Supplier theme updated: {}", theme);
    }

    @Transactional(readOnly = true)
    public String getTheme(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));
        return supplier.getTheme() != null ? supplier.getTheme() : "white";
    }
}