
package com.itti.leadcapturing.service;

import com.itti.leadcapturing.dto.BuyerCreateByAdminDTO;
import com.itti.leadcapturing.model.Buyer;
import com.itti.leadcapturing.model.Location;
import com.itti.leadcapturing.model.OrganizationAdmin;
import com.itti.leadcapturing.model.Department;
import com.itti.leadcapturing.model.User;
import com.itti.leadcapturing.repo.BuyerRepository;
import com.itti.leadcapturing.repo.OrganizationAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class BuyerService {

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private OrganizationAdminRepository organizationAdminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ============================================
    // ✅ NEW: LOGO PROCESSING HELPER METHOD
    // ============================================
    
    private void processLogo(Buyer buyer, String logoBase64, String logoFilename, String logoContentType) {
        try {
            if (logoBase64 != null && !logoBase64.isEmpty()) {
                // Remove data URL prefix if present (data:image/png;base64,...)
                String base64Data = logoBase64;
                if (logoBase64.contains(",")) {
                    base64Data = logoBase64.split(",")[1];
                }
                
                byte[] logoBytes = Base64.getDecoder().decode(base64Data);
                buyer.setLogoData(logoBytes);
                buyer.setLogoFilename(logoFilename);
                buyer.setLogoContentType(logoContentType);
                
                log.info("✅ Logo processed: {} bytes, type: {}", logoBytes.length, logoContentType);
            }
        } catch (Exception e) {
            log.error("❌ Error processing logo: {}", e.getMessage());
            throw new RuntimeException("Failed to process logo: " + e.getMessage());
        }
    }


// ============================================
// REPLACE getBuyerLogoBase64 IN BuyerService.java
// ============================================

@Transactional(readOnly = true)
public String getBuyerLogoBase64(Long buyerId) {
    log.info("========================================");
    log.info("📥 GET BUYER LOGO BASE64 - Buyer ID: {}", buyerId);
    log.info("========================================");

    try {
        // ✅ Use native query — forces MySQL to load the LONGBLOB column fully
        Buyer buyer = buyerRepository.findByIdWithLogoData(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found with ID: " + buyerId));

        log.info("  Buyer           : {}", buyer.getCompanyName());
        log.info("  logoFilename    : {}", buyer.getLogoFilename());
        log.info("  logoContentType : {}", buyer.getLogoContentType());
        log.info("  logoData null?  : {}", buyer.getLogoData() == null);
        log.info("  logoData bytes  : {}", buyer.getLogoData() != null ? buyer.getLogoData().length : 0);

        if (buyer.getLogoData() == null || buyer.getLogoData().length == 0) {
            log.warn("  ⚠️ logoData is EMPTY — logo was not saved to DB during buyer creation");
            return null;
        }

        String contentType = (buyer.getLogoContentType() != null && !buyer.getLogoContentType().trim().isEmpty())
                ? buyer.getLogoContentType()
                : "image/png";

        String base64Data = Base64.getEncoder().encodeToString(buyer.getLogoData());
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

// ============================================
    // 🆕 ORGANIZATION ADMIN CREATES BUYER (WITH LOGO)
    // ============================================
    
    @Transactional
    public Buyer createBuyerByOrganizationAdmin(BuyerCreateByAdminDTO dto, Long orgAdminId) {
        try {
            log.info("========================================");
            log.info("🔵 CREATING BUYER BY ORGANIZATION ADMIN");
            log.info("========================================");
            log.info("Company: {}", dto.getCompanyName());
            log.info("Org Admin ID: {}", orgAdminId);
            log.info("Organization Company: {}", dto.getOrganizationCompanyName());
            log.info("Logo present: {}", dto.getLogoBase64() != null && !dto.getLogoBase64().isEmpty());
            
            // ✅ STEP 1: Fetch Organization Admin
            OrganizationAdmin orgAdmin = organizationAdminRepository.findById(orgAdminId)
                    .orElseThrow(() -> new RuntimeException("Organization Admin not found with ID: " + orgAdminId));
            
            log.info("  [✓] Found Org Admin: {}", orgAdmin.getFullName());
            log.info("  [✓] Org Admin Company: {}", orgAdmin.getCompanyName());
            
            // ✅ STEP 2: Verify company name matches
            if (!orgAdmin.getCompanyName().equals(dto.getOrganizationCompanyName())) {
                throw new RuntimeException(
                    "Organization company name mismatch! " +
                    "Expected: '" + orgAdmin.getCompanyName() + "', " +
                    "Provided: '" + dto.getOrganizationCompanyName() + "'"
                );
            }
            
            // ✅ STEP 3: Create buyer with organization link
            Buyer buyer = new Buyer();
            buyer.setCompanyName(dto.getCompanyName());
            buyer.setCompanyType(dto.getCompanyType());
            buyer.setContactPersonName(dto.getContactPersonName());
            buyer.setContactPersonDesignation(dto.getContactPersonDesignation());
            buyer.setContactPersonEmail(dto.getContactPersonEmail());
            buyer.setContactPersonPhone(dto.getContactPersonPhone());
            buyer.setAddressLine1(dto.getAddressLine1());
            buyer.setAddressLine2(dto.getAddressLine2());
            buyer.setCity(dto.getCity());
            buyer.setState(dto.getState());
            buyer.setPostalCode(dto.getPostalCode());
            buyer.setCountry(dto.getCountry());
            buyer.setGstNumber(dto.getGstNumber());
            buyer.setPanNumber(dto.getPanNumber());
            buyer.setCinNumber(dto.getCinNumber());
            buyer.setWebsite(dto.getWebsite());
            
            // ✅ NEW: Process logo if provided
            if (dto.getLogoBase64() != null && !dto.getLogoBase64().isEmpty()) {
                processLogo(buyer, dto.getLogoBase64(), dto.getLogoFilename(), dto.getLogoContentType());
            }
            
            // ✅ CRITICAL: Set organization linkage
            buyer.setCreatedByOrgAdmin(orgAdmin);
            buyer.setOrganizationCompanyName(orgAdmin.getCompanyName());
            buyer.setIsDeleted(false);
            
            // ✅ STEP 4: Process locations (if provided in DTO)
            if (dto.getLocations() != null && !dto.getLocations().isEmpty()) {
                List<Location> locations = new ArrayList<>();
                
                for (Location loc : dto.getLocations()) {
                    loc.setBuyer(buyer);
                    loc.setIsDeleted(false);
                    
                    if (loc.getDepartments() != null) {
                        for (Department dept : loc.getDepartments()) {
                            dept.setLocation(loc);
                            dept.setIsDeleted(false);
                            
                            if (dept.getUsers() != null) {
                                for (User user : dept.getUsers()) {
                                    user.setDepartment(dept);
                                    user.setLocation(loc);
                                    user.setBuyer(buyer);
                                    user.setIsDeleted(false);
                                    
                                    // Encode password
                                    if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                                        user.setPassword(passwordEncoder.encode(user.getPassword()));
                                    }
                                }
                            }
                        }
                    }
                    
                    locations.add(loc);
                }
                
                buyer.setLocations(locations);
            }
            
            // ✅ STEP 5: Save buyer (cascade saves locations, departments, users)
            Buyer savedBuyer = buyerRepository.save(buyer);
            
            log.info("========================================");
            log.info("✅ BUYER CREATED BY ORG ADMIN");
            log.info("========================================");
            log.info("Buyer ID: {}", savedBuyer.getId());
            log.info("Buyer Company: {}", savedBuyer.getCompanyName());
            log.info("Organization Company: {}", savedBuyer.getOrganizationCompanyName());
            log.info("Logo saved: {}", savedBuyer.getLogoData() != null);
            log.info("Created By: {}", orgAdmin.getFullName());
            log.info("========================================");
            
            return savedBuyer;
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("❌ ERROR CREATING BUYER BY ORG ADMIN");
            log.error("========================================");
            log.error("Error: {}", e.getMessage());
            e.printStackTrace();
            log.error("========================================");
            throw new RuntimeException("Failed to create buyer: " + e.getMessage(), e);
        }
    }

    // ============================================
    // ✅ NEW: GET BUYER LOGO AS BASE64
    // ============================================
    
  
    // ============================================
    // ✅ NEW: GET RAW LOGO BYTES
    // ============================================
    
    @Transactional(readOnly = true)
    public byte[] getBuyerLogoBytes(Long buyerId) {
        try {
            Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
            
            return buyer.getLogoData();
        } catch (Exception e) {
            log.error("❌ Error getting logo bytes for buyer {}: {}", buyerId, e.getMessage());
            return null;
        }
    }

    /**
     * ✅ COMPLETELY FIXED: Get buyers created by Organization Admin
     * Solution: Use @EntityGraph for locations only, then manually initialize nested collections
     */
    @Transactional(readOnly = true)
    public List<Buyer> getBuyersByOrganizationAdmin(Long adminId) {
        try {
            log.info("========================================");
            log.info("📥 FETCHING BUYERS FOR ORG ADMIN: {}", adminId);
            log.info("========================================");
            
            // Verify admin exists first
            OrganizationAdmin admin = organizationAdminRepository.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Organization Admin not found with ID: " + adminId));
            
            log.info("  [✓] Found Org Admin: {}", admin.getFullName());
            log.info("  [✓] Company: {}", admin.getCompanyName());
            
            // ✅ CRITICAL FIX: Fetch buyers using @EntityGraph (loads locations only)
            List<Buyer> buyers = buyerRepository.findByCreatedByOrgAdminId(adminId);
            
            log.info("  [✓] Found {} buyers", buyers.size());
            
            // ✅ CRITICAL: Manually initialize nested collections level by level
            for (int i = 0; i < buyers.size(); i++) {
                Buyer buyer = buyers.get(i);
                log.info("");
                log.info("  Buyer {}: {}", i + 1, buyer.getCompanyName());
                log.info("    └─ Logo: {}", buyer.getLogoData() != null ? "Present" : "Not present");
                
                // Locations are already loaded by @EntityGraph
                if (buyer.getLocations() != null) {
                    int locationCount = buyer.getLocations().size();
                    log.info("    └─ Locations: {}", locationCount);
                    
                    // Now manually load departments for each location
                    for (Location location : buyer.getLocations()) {
                        if (location.getDepartments() != null) {
                            int deptCount = location.getDepartments().size(); // This triggers load
                            log.info("       └─ Location '{}' → Departments: {}", 
                                     location.getLocationName(), deptCount);
                            
                            // Now manually load users for each department
                            for (Department department : location.getDepartments()) {
                                if (department.getUsers() != null) {
                                    int userCount = department.getUsers().size(); // This triggers load
                                    log.info("          └─ Department '{}' → Users: {}", 
                                             department.getDepartmentName(), userCount);
                                }
                            }
                        }
                    }
                }
                
                // Initialize created by admin info
                if (buyer.getCreatedByOrgAdmin() != null) {
                    buyer.getCreatedByOrgAdmin().getFullName();
                }
            }
            
            log.info("========================================");
            log.info("✅ BUYERS FETCHED SUCCESSFULLY");
            log.info("========================================");
            log.info("Total Buyers: {}", buyers.size());
            log.info("Organization: {}", admin.getCompanyName());
            log.info("========================================");
            
            return buyers;
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("❌ ERROR FETCHING BUYERS FOR ADMIN: {}", adminId);
            log.error("========================================");
            log.error("Error Type: {}", e.getClass().getName());
            log.error("Error Message: {}", e.getMessage());
            e.printStackTrace();
            log.error("========================================");
            throw new RuntimeException("Failed to fetch buyers: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Buyer> getBuyersByOrganizationCompanyName(String companyName) {
        try {
            log.info("📥 Fetching buyers for organization: {}", companyName);
            
            List<Buyer> buyers = buyerRepository.findByOrganizationCompanyName(companyName);
            
            // ✅ Manually initialize nested collections
            buyers.forEach(buyer -> {
                if (buyer.getLocations() != null) {
                    buyer.getLocations().forEach(location -> {
                        if (location.getDepartments() != null) {
                            location.getDepartments().forEach(dept -> {
                                if (dept.getUsers() != null) {
                                    dept.getUsers().size();
                                }
                            });
                        }
                    });
                }
            });
            
            log.info("✅ Found {} buyers", buyers.size());
            return buyers;
            
        } catch (Exception e) {
            log.error("❌ Error fetching buyers for organization", e);
            throw new RuntimeException("Failed to fetch buyers: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Buyer createBuyer(Buyer buyer) {
        try {
            log.info("========================================");
            log.info("🔵 CREATING BUYER");
            log.info("========================================");
            log.info("Company: {}", buyer.getCompanyName());
            
            if (buyer.getLocations() == null) {
                buyer.setLocations(new ArrayList<>());
            }
            buyer.setIsDeleted(false);
            
            log.info("📦 RECEIVED FROM FRONTEND:");
            log.info("   Buyer locations: {}", buyer.getLocations().size());
            
            for (int i = 0; i < buyer.getLocations().size(); i++) {
                Location location = buyer.getLocations().get(i);
                
                log.info("   Location {}: {} (departments: {})", 
                         i, 
                         location.getLocationName(),
                         location.getDepartments() == null ? 0 : location.getDepartments().size());
                
                location.setBuyer(buyer);
                location.setIsDeleted(false);
                
                if (location.getDepartments() == null) {
                    location.setDepartments(new ArrayList<>());
                }
                
                for (int j = 0; j < location.getDepartments().size(); j++) {
                    Department department = location.getDepartments().get(j);
                    
                    log.info("      Department {}: {} (users: {})", 
                             j,
                             department.getDepartmentName(),
                             department.getUsers() == null ? 0 : department.getUsers().size());
                    
                    department.setLocation(location);
                    department.setIsDeleted(false);
                    
                    if (department.getUsers() == null) {
                        department.setUsers(new ArrayList<>());
                    }
                    
                    for (int k = 0; k < department.getUsers().size(); k++) {
                        User user = department.getUsers().get(k);
                        
                        log.info("         User {}: {} {} ({})", 
                                 k,
                                 user.getFirstName(),
                                 user.getLastName(),
                                 user.getEmail());
                        
                        user.setDepartment(department);
                        user.setLocation(location);
                        user.setBuyer(buyer);
                        user.setIsDeleted(false);
                        
                        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                            String encodedPassword = passwordEncoder.encode(user.getPassword());
                            user.setPassword(encodedPassword);
                            log.info("            🔐 Password encoded for user: {}", user.getEmail());
                        } else {
                            log.error("            ❌ NO PASSWORD for user: {}", user.getEmail());
                            throw new RuntimeException("Password is required for user: " + user.getEmail());
                        }
                    }
                }
            }
            
            log.info("");
            log.info("💾 Saving buyer to database...");
            Buyer savedBuyer = buyerRepository.save(buyer);
            
            log.info("");
            log.info("========================================");
            log.info("✅ BUYER SAVED SUCCESSFULLY");
            log.info("========================================");
            log.info("Buyer ID: {}", savedBuyer.getId());
            log.info("Company: {}", savedBuyer.getCompanyName());
            log.info("========================================");
            
            return savedBuyer;
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("❌ ERROR CREATING BUYER");
            log.error("========================================");
            log.error("Error: {}", e.getMessage());
            e.printStackTrace();
            log.error("========================================");
            throw new RuntimeException("Failed to create buyer: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Buyer> getAllBuyers() {
        try {
            log.info("📥 Fetching all buyers...");
            List<Buyer> buyers = buyerRepository.findAll();
            
            // ✅ Initialize lazy collections
            buyers.forEach(buyer -> {
                if (buyer.getLocations() != null) {
                    buyer.getLocations().forEach(location -> {
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
            
            log.info("✅ Found {} buyer(s)", buyers.size());
            return buyers;
            
        } catch (Exception e) {
            log.error("❌ Error fetching buyers", e);
            throw new RuntimeException("Failed to fetch buyers: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Buyer getBuyerById(Long id) {
        try {
            log.info("📥 Fetching buyer with ID: {}", id);
            
            Buyer buyer = buyerRepository.findByIdWithAllRelations(id)
                    .orElseThrow(() -> new RuntimeException("Buyer not found with ID: " + id));
            
            // ✅ Initialize nested collections
            if (buyer.getLocations() != null) {
                buyer.getLocations().forEach(location -> {
                    if (location.getDepartments() != null) {
                        location.getDepartments().forEach(dept -> {
                            if (dept.getUsers() != null) {
                                dept.getUsers().size();
                            }
                        });
                    }
                });
            }
            
            log.info("✅ Buyer found: {}", buyer.getCompanyName());
            return buyer;
            
        } catch (Exception e) {
            log.error("❌ Error fetching buyer: {}", id, e);
            throw new RuntimeException("Failed to fetch buyer: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Buyer updateBuyer(Long id, Buyer buyerReq) {
        try {
            log.info("🔵 Updating buyer with ID: {}", id);
            
            Buyer existingBuyer = buyerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Buyer not found with ID: " + id));

            if (buyerReq.getCompanyName() != null && !buyerReq.getCompanyName().trim().isEmpty()) {
                existingBuyer.setCompanyName(buyerReq.getCompanyName());
            }
            if (buyerReq.getCompanyType() != null && !buyerReq.getCompanyType().trim().isEmpty()) {
                existingBuyer.setCompanyType(buyerReq.getCompanyType());
            }
            
            // ✅ NEW: Update logo if provided
            if (buyerReq.getLogoData() != null) {
                existingBuyer.setLogoData(buyerReq.getLogoData());
                existingBuyer.setLogoFilename(buyerReq.getLogoFilename());
                existingBuyer.setLogoContentType(buyerReq.getLogoContentType());
                log.info("✅ Logo updated for buyer: {}", id);
            }
            
            if (buyerReq.getContactPersonName() != null && !buyerReq.getContactPersonName().trim().isEmpty()) {
                existingBuyer.setContactPersonName(buyerReq.getContactPersonName());
            }
            if (buyerReq.getContactPersonDesignation() != null) {
                existingBuyer.setContactPersonDesignation(buyerReq.getContactPersonDesignation());
            }
            if (buyerReq.getContactPersonEmail() != null && !buyerReq.getContactPersonEmail().trim().isEmpty()) {
                existingBuyer.setContactPersonEmail(buyerReq.getContactPersonEmail());
            }
            if (buyerReq.getContactPersonPhone() != null && !buyerReq.getContactPersonPhone().trim().isEmpty()) {
                existingBuyer.setContactPersonPhone(buyerReq.getContactPersonPhone());
            }
            if (buyerReq.getAddressLine1() != null) {
                existingBuyer.setAddressLine1(buyerReq.getAddressLine1());
            }
            if (buyerReq.getAddressLine2() != null) {
                existingBuyer.setAddressLine2(buyerReq.getAddressLine2());
            }
            if (buyerReq.getCity() != null) {
                existingBuyer.setCity(buyerReq.getCity());
            }
            if (buyerReq.getState() != null) {
                existingBuyer.setState(buyerReq.getState());
            }
            if (buyerReq.getPostalCode() != null) {
                existingBuyer.setPostalCode(buyerReq.getPostalCode());
            }
            if (buyerReq.getCountry() != null) {
                existingBuyer.setCountry(buyerReq.getCountry());
            }
            if (buyerReq.getGstNumber() != null) {
                existingBuyer.setGstNumber(buyerReq.getGstNumber());
            }
            if (buyerReq.getPanNumber() != null) {
                existingBuyer.setPanNumber(buyerReq.getPanNumber());
            }
            if (buyerReq.getCinNumber() != null) {
                existingBuyer.setCinNumber(buyerReq.getCinNumber());
            }
            if (buyerReq.getWebsite() != null) {
                existingBuyer.setWebsite(buyerReq.getWebsite());
            }

            if (buyerReq.getLocations() != null) {
                existingBuyer.getLocations().clear();

                for (Location newLocation : buyerReq.getLocations()) {
                    newLocation.setBuyer(existingBuyer);
                    newLocation.setIsDeleted(false);
                    
                    if (newLocation.getDepartments() != null) {
                        for (Department newDepartment : newLocation.getDepartments()) {
                            newDepartment.setLocation(newLocation);
                            newDepartment.setIsDeleted(false);
                            
                            if (newDepartment.getUsers() != null) {
                                for (User newUser : newDepartment.getUsers()) {
                                    newUser.setDepartment(newDepartment);
                                    newUser.setLocation(newLocation);
                                    newUser.setBuyer(existingBuyer);
                                    newUser.setIsDeleted(false);
                                    
                                    if (newUser.getPassword() != null && !newUser.getPassword().trim().isEmpty()) {
                                        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
                                    }
                                }
                            }
                        }
                    }
                    
                    existingBuyer.getLocations().add(newLocation);
                }
            }

            Buyer updatedBuyer = buyerRepository.save(existingBuyer);
            log.info("✅ Buyer updated: {}", updatedBuyer.getId());
            return updatedBuyer;
            
        } catch (Exception e) {
            log.error("❌ Error updating buyer with ID: {}", id, e);
            throw new RuntimeException("Failed to update buyer: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteBuyer(Long id) {
        try {
            log.info("🗑️  Soft deleting buyer with ID: {}", id);
            
            Buyer buyer = buyerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Buyer not found with ID: " + id));

            buyer.setIsDeleted(true);
            buyer.setDeletedAt(LocalDateTime.now());

            if (buyer.getLocations() != null) {
                for (Location location : buyer.getLocations()) {
                    location.setIsDeleted(true);
                    location.setDeletedAt(LocalDateTime.now());

                    if (location.getDepartments() != null) {
                        for (Department department : location.getDepartments()) {
                            department.setIsDeleted(true);
                            department.setDeletedAt(LocalDateTime.now());

                            if (department.getUsers() != null) {
                                for (User user : department.getUsers()) {
                                    user.setIsDeleted(true);
                                    user.setDeletedAt(LocalDateTime.now());
                                }
                            }
                        }
                    }
                }
            }

            buyerRepository.save(buyer);
            log.info("✅ Buyer soft deleted: {}", buyer.getCompanyName());
            
        } catch (Exception e) {
            log.error("❌ Error deleting buyer with ID: {}", id, e);
            throw new RuntimeException("Failed to delete buyer: " + e.getMessage(), e);
        }
    }
}