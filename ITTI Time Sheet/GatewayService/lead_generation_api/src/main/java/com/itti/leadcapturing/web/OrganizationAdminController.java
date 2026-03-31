

// // package com.itti.leadcapturing.web;

// // import com.itti.leadcapturing.dto.*;
// // import com.itti.leadcapturing.model.Buyer;
// // import com.itti.leadcapturing.service.OrganizationAdminService;
// // import com.itti.leadcapturing.service.BuyerService;
// // import jakarta.validation.Valid;
// // import lombok.RequiredArgsConstructor;
// // import lombok.extern.slf4j.Slf4j;

// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.http.HttpStatus;
// // import org.springframework.http.ResponseEntity;
// // import org.springframework.web.bind.annotation.*;
// // import java.util.Map;

// // import java.util.HashMap;
// // import java.util.List;

// // @RestController
// // @RequestMapping("/api/organization-admin")
// // @RequiredArgsConstructor
// // @Slf4j
// // @CrossOrigin(origins = "*", maxAge = 3600)
// // public class OrganizationAdminController {

// //    @Autowired
// //    private OrganizationAdminService organizationAdminService;
// //     @Autowired
// //     private BuyerService buyerService;

// //     // ============================================
// //     // 1. CREATE ORGANIZATION ADMIN
// //     // ============================================
// //     @PostMapping
// //     public ResponseEntity<ApiResponse<OrganizationAdminResponse>> createOrganizationAdmin(
// //             @Valid @RequestBody OrganizationAdminCreateRequest request,
// //             @RequestHeader(value = "X-Creator-Admin-Id", required = false) Long creatorAdminId) {
        
// //         log.info("📥 Creating OrganizationAdmin: {}", request.getEmail());
// //         ApiResponse<OrganizationAdminResponse> response = 
// //             organizationAdminService.createOrganizationAdmin(request, creatorAdminId);
        
// //         if (response.getSuccess()) {
// //             return ResponseEntity.status(HttpStatus.CREATED).body(response);
// //         } else {
// //             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
// //         }
// //     }

// //     // ============================================
// //     // 2. LOGIN ORGANIZATION ADMIN
// //     // ============================================
// //     @PostMapping("/auth/login")
// //     public ResponseEntity<ApiResponse<OrganizationAdminResponse>> loginOrganizationAdmin(
// //             @Valid @RequestBody OrganizationAdminLoginRequest request) {
        
// //         log.info("🔐 OrganizationAdmin login attempt: {}", request.getEmail());
// //         ApiResponse<OrganizationAdminResponse> response = 
// //             organizationAdminService.loginOrganizationAdmin(request);
        
// //         if (response.getSuccess()) {
// //             return ResponseEntity.ok(response);
// //         } else {
// //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
// //         }
// //     }

// //     // ============================================
// //     // 3. ✅ CHANGE PASSWORD - FIXED TO USE PUT
// //     // ============================================
// //     @PutMapping("/change-password/{adminId}")
// //     public ResponseEntity<ApiResponse<String>> changePassword(
// //             @PathVariable Long adminId,
// //             @Valid @RequestBody ChangePasswordRequest request) {
        
// //         log.info("🔐 Change password request for admin ID: {}", adminId);
// //         ApiResponse<String> response = organizationAdminService.changePassword(adminId, request);
        
// //         if (response.getSuccess()) {
// //             return ResponseEntity.ok(response);
// //         } else {
// //             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
// //         }
// //     }

// //     // ============================================
// //     // 4. GET BY ID
// //     // ============================================
// //     @GetMapping("/{id}")
// //     public ResponseEntity<ApiResponse<OrganizationAdminResponse>> getOrganizationAdminById(
// //             @PathVariable Long id) {
        
// //         log.info("📥 Fetching OrganizationAdmin: {}", id);
// //         ApiResponse<OrganizationAdminResponse> response = 
// //             organizationAdminService.getOrganizationAdminById(id);
        
// //         if (response.getSuccess()) {
// //             return ResponseEntity.ok(response);
// //         } else {
// //             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
// //         }
// //     }

// //     // ============================================
// //     // 5. GET BY COMPANY
// //     // ============================================
// //     @GetMapping("/company/{companyName}")
// //     public ResponseEntity<ApiResponse<List<OrganizationAdminResponse>>> getAdminsByCompany(
// //             @PathVariable String companyName) {
        
// //         log.info("📥 Fetching admins for company: {}", companyName);
// //         ApiResponse<List<OrganizationAdminResponse>> response = 
// //             organizationAdminService.getAdminsByCompany(companyName);
        
// //         return ResponseEntity.ok(response);
// //     }

// //     // ============================================
// //     // 6. GET ALL
// //     // ============================================
// //     @GetMapping("/all")
// //     public ResponseEntity<ApiResponse<List<OrganizationAdminResponse>>> getAllOrganizationAdmins() {
// //         log.info("📥 Fetching all OrganizationAdmins");
// //         ApiResponse<List<OrganizationAdminResponse>> response = 
// //             organizationAdminService.getAllOrganizationAdmins();
        
// //         return ResponseEntity.ok(response);
// //     }

// //     // ============================================
// //     // 7. UPDATE
// //     // ============================================
// //     @PutMapping("/{id}")
// //     public ResponseEntity<ApiResponse<OrganizationAdminResponse>> updateOrganizationAdmin(
// //             @PathVariable Long id,
// //             @Valid @RequestBody OrganizationAdminUpdateRequest request) {
        
// //         log.info("✏️ Updating OrganizationAdmin: {}", id);
// //         ApiResponse<OrganizationAdminResponse> response = 
// //             organizationAdminService.updateOrganizationAdmin(id, request);
        
// //         if (response.getSuccess()) {
// //             return ResponseEntity.ok(response);
// //         } else {
// //             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
// //         }
// //     }

// //     // ============================================
// //     // 8. DEACTIVATE
// //     // ============================================
// //     @PutMapping("/{id}/deactivate")
// //     public ResponseEntity<ApiResponse<String>> deactivateOrganizationAdmin(@PathVariable Long id) {
// //         log.info("🚫 Deactivating OrganizationAdmin: {}", id);
// //         ApiResponse<String> response = organizationAdminService.deactivateOrganizationAdmin(id);
        
// //         if (response.getSuccess()) {
// //             return ResponseEntity.ok(response);
// //         } else {
// //             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
// //         }
// //     }

// //     // ============================================
// //     // 9. ACTIVATE
// //     // ============================================
// //     @PutMapping("/{id}/activate")
// //     public ResponseEntity<ApiResponse<String>> activateOrganizationAdmin(@PathVariable Long id) {
// //         log.info("✅ Activating OrganizationAdmin: {}", id);
// //         ApiResponse<String> response = organizationAdminService.activateOrganizationAdmin(id);
        
// //         if (response.getSuccess()) {
// //             return ResponseEntity.ok(response);
// //         } else {
// //             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
// //         }
// //     }

// //     // ============================================
// //     // 10. DELETE
// //     // ============================================
// //     @DeleteMapping("/{id}")
// //     public ResponseEntity<ApiResponse<String>> deleteOrganizationAdmin(@PathVariable Long id) {
// //         log.info("🗑️ Deleting OrganizationAdmin: {}", id);
// //         ApiResponse<String> response = organizationAdminService.deleteOrganizationAdmin(id);
        
// //         if (response.getSuccess()) {
// //             return ResponseEntity.ok(response);
// //         } else {
// //             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
// //         }
// //     }


// //     // File: src/main/java/com/itti/leadcapturing/web/OrganizationAdminController.java
// // // ✅ ADD THESE ENDPOINTS

// // /**
// //  * POST /api/organization-admin/{adminId}/buyer
// //  * Organization Admin creates a buyer
// //  */
// // @PostMapping("/{adminId}/buyer")
// // public ResponseEntity<?> createBuyer(
// //         @PathVariable Long adminId,
// //         @Valid @RequestBody BuyerCreateByAdminDTO buyerDTO) {
// //     try {
// //         log.info("📥 Org Admin {} creating buyer: {}", adminId, buyerDTO.getCompanyName());
        
// //         Buyer createdBuyer = buyerService.createBuyerByOrganizationAdmin(buyerDTO, adminId);
        
// //         log.info("✅ Buyer created successfully: {} (ID: {})", 
// //                  createdBuyer.getCompanyName(), createdBuyer.getId());
        
// //         Map<String, Object> response = new HashMap<>();
// //         response.put("success", true);
// //         response.put("message", "Buyer created successfully");
// //         response.put("data", createdBuyer);
        
// //         return ResponseEntity.status(HttpStatus.CREATED).body(response);
        
// //     } catch (Exception e) {
// //         log.error("❌ Error creating buyer", e);
        
// //         Map<String, Object> response = new HashMap<>();
// //         response.put("success", false);
// //         response.put("message", "Failed to create buyer: " + e.getMessage());
        
// //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
// //     }
// // }

// // /**
// //  * GET /api/organization-admin/{adminId}/buyers
// //  * Get all buyers created by this Organization Admin
// //  */
// // @GetMapping("/{adminId}/buyers")
// // public ResponseEntity<?> getBuyersCreatedByAdmin(@PathVariable Long adminId) {
// //     try {
// //         log.info("📥 Fetching buyers created by Org Admin: {}", adminId);
        
// //         List<Buyer> buyers = buyerService.getBuyersByOrganizationAdmin(adminId);
        
// //         Map<String, Object> response = new HashMap<>();
// //         response.put("success", true);
// //         response.put("count", buyers.size());
// //         response.put("data", buyers);
        
// //         return ResponseEntity.ok(response);
        
// //     } catch (Exception e) {
// //         log.error("❌ Error fetching buyers", e);
        
// //         Map<String, Object> response = new HashMap<>();
// //         response.put("success", false);
// //         response.put("message", "Failed to fetch buyers");
        
// //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
// //     }
// // }


// //     // ============================================
// //     // 11. HEALTH CHECK
// //     // ============================================
// //     @GetMapping("/health")
// //     public ResponseEntity<String> healthCheck() {
// //         return ResponseEntity.ok("✅ OrganizationAdmin Service is running!");
// //     }
// // }


// package com.itti.leadcapturing.web;

// import com.itti.leadcapturing.dto.*;
// import com.itti.leadcapturing.model.Buyer;
// import com.itti.leadcapturing.model.OrganizationAdmin;
// import com.itti.leadcapturing.service.OrganizationAdminService;
// import com.itti.leadcapturing.service.BuyerService;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import java.util.Map;

// import java.util.HashMap;
// import java.util.List;

// @RestController
// @RequestMapping("/api/organization-admin")
// @RequiredArgsConstructor
// @Slf4j
// @CrossOrigin(origins = "*", maxAge = 3600)
// public class OrganizationAdminController {

//     @Autowired
//     private OrganizationAdminService organizationAdminService;
    
//     @Autowired
//     private BuyerService buyerService;

//     // ============================================
//     // EXISTING METHODS (1-11) - Keep as is
//     // ============================================
    
//     @PostMapping
//     public ResponseEntity<ApiResponse<OrganizationAdminResponse>> createOrganizationAdmin(
//             @Valid @RequestBody OrganizationAdminCreateRequest request,
//             @RequestHeader(value = "X-Creator-Admin-Id", required = false) Long creatorAdminId) {
        
//         log.info("📥 Creating OrganizationAdmin: {}", request.getEmail());
//         ApiResponse<OrganizationAdminResponse> response = 
//             organizationAdminService.createOrganizationAdmin(request, creatorAdminId);
        
//         if (response.getSuccess()) {
//             return ResponseEntity.status(HttpStatus.CREATED).body(response);
//         } else {
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     @PostMapping("/auth/login")
//     public ResponseEntity<ApiResponse<OrganizationAdminResponse>> loginOrganizationAdmin(
//             @Valid @RequestBody OrganizationAdminLoginRequest request) {
        
//         log.info("🔐 OrganizationAdmin login attempt: {}", request.getEmail());
//         ApiResponse<OrganizationAdminResponse> response = 
//             organizationAdminService.loginOrganizationAdmin(request);
        
//         if (response.getSuccess()) {
//             return ResponseEntity.ok(response);
//         } else {
//             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//         }
//     }

//     @PutMapping("/change-password/{adminId}")
//     public ResponseEntity<ApiResponse<String>> changePassword(
//             @PathVariable Long adminId,
//             @Valid @RequestBody ChangePasswordRequest request) {
        
//         log.info("🔐 Change password request for admin ID: {}", adminId);
//         ApiResponse<String> response = organizationAdminService.changePassword(adminId, request);
        
//         if (response.getSuccess()) {
//             return ResponseEntity.ok(response);
//         } else {
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<ApiResponse<OrganizationAdminResponse>> getOrganizationAdminById(
//             @PathVariable Long id) {
        
//         log.info("📥 Fetching OrganizationAdmin: {}", id);
//         ApiResponse<OrganizationAdminResponse> response = 
//             organizationAdminService.getOrganizationAdminById(id);
        
//         if (response.getSuccess()) {
//             return ResponseEntity.ok(response);
//         } else {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//         }
//     }

//     @GetMapping("/company/{companyName}")
//     public ResponseEntity<ApiResponse<List<OrganizationAdminResponse>>> getAdminsByCompany(
//             @PathVariable String companyName) {
        
//         log.info("📥 Fetching admins for company: {}", companyName);
//         ApiResponse<List<OrganizationAdminResponse>> response = 
//             organizationAdminService.getAdminsByCompany(companyName);
        
//         return ResponseEntity.ok(response);
//     }

//     @GetMapping("/all")
//     public ResponseEntity<ApiResponse<List<OrganizationAdminResponse>>> getAllOrganizationAdmins() {
//         log.info("📥 Fetching all OrganizationAdmins");
//         ApiResponse<List<OrganizationAdminResponse>> response = 
//             organizationAdminService.getAllOrganizationAdmins();
        
//         return ResponseEntity.ok(response);
//     }

//     @PutMapping("/{id}")
//     public ResponseEntity<ApiResponse<OrganizationAdminResponse>> updateOrganizationAdmin(
//             @PathVariable Long id,
//             @Valid @RequestBody OrganizationAdminUpdateRequest request) {
        
//         log.info("✏️ Updating OrganizationAdmin: {}", id);
//         ApiResponse<OrganizationAdminResponse> response = 
//             organizationAdminService.updateOrganizationAdmin(id, request);
        
//         if (response.getSuccess()) {
//             return ResponseEntity.ok(response);
//         } else {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//         }
//     }

//     @PutMapping("/{id}/deactivate")
//     public ResponseEntity<ApiResponse<String>> deactivateOrganizationAdmin(@PathVariable Long id) {
//         log.info("🚫 Deactivating OrganizationAdmin: {}", id);
//         ApiResponse<String> response = organizationAdminService.deactivateOrganizationAdmin(id);
        
//         if (response.getSuccess()) {
//             return ResponseEntity.ok(response);
//         } else {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//         }
//     }

//     @PutMapping("/{id}/activate")
//     public ResponseEntity<ApiResponse<String>> activateOrganizationAdmin(@PathVariable Long id) {
//         log.info("✅ Activating OrganizationAdmin: {}", id);
//         ApiResponse<String> response = organizationAdminService.activateOrganizationAdmin(id);
        
//         if (response.getSuccess()) {
//             return ResponseEntity.ok(response);
//         } else {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//         }
//     }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<ApiResponse<String>> deleteOrganizationAdmin(@PathVariable Long id) {
//         log.info("🗑️ Deleting OrganizationAdmin: {}", id);
//         ApiResponse<String> response = organizationAdminService.deleteOrganizationAdmin(id);
        
//         if (response.getSuccess()) {
//             return ResponseEntity.ok(response);
//         } else {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//         }
//     }

//     @GetMapping("/health")
//     public ResponseEntity<String> healthCheck() {
//         return ResponseEntity.ok("✅ OrganizationAdmin Service is running!");
//     }

//     // ============================================
//     // 🆕 12. ORGANIZATION ADMIN CREATES BUYER
//     // ============================================
    
//    /**
//  * POST /api/organization-admin/{adminId}/buyer
//  * Organization Admin creates a buyer with complete hierarchy
//  */
// @PostMapping("/{adminId}/buyer")
// public ResponseEntity<?> createBuyer(
//         @PathVariable Long adminId,
//         @Valid @RequestBody Buyer buyer) {
//     try {
//         log.info("========================================");
//         log.info("📥 Org Admin {} creating buyer: {}", adminId, buyer.getCompanyName());
//         log.info("   Organization Company: {}", buyer.getOrganizationCompanyName());
//         log.info("   Admin ID from path: {}", adminId);
//         log.info("========================================");
        
//         // ✅ STEP 1: Fetch Organization Admin
//         OrganizationAdmin orgAdmin = organizationAdminService.getOrganizationAdminByIdDirect(adminId);
        
//         if (orgAdmin == null) {
//             log.error("❌ Organization Admin not found: {}", adminId);
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Organization Admin not found");
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//         }
        
//         log.info("  [✓] Found Org Admin: {}", orgAdmin.getFullName());
//         log.info("  [✓] Org Admin Company: {}", orgAdmin.getCompanyName());
        
//         // ✅ STEP 2: Verify company name matches (if provided)
//         if (buyer.getOrganizationCompanyName() != null 
//             && !orgAdmin.getCompanyName().equals(buyer.getOrganizationCompanyName())) {
//             log.error("❌ Company name mismatch!");
//             log.error("   Expected: {}", orgAdmin.getCompanyName());
//             log.error("   Provided: {}", buyer.getOrganizationCompanyName());
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Organization company name mismatch! " +
//                 "Expected: '" + orgAdmin.getCompanyName() + "', " +
//                 "Provided: '" + buyer.getOrganizationCompanyName() + "'");
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
        
//         // ✅ STEP 3: Set organization linkage (CRITICAL!)
//         buyer.setCreatedByOrgAdmin(orgAdmin);
//         buyer.setOrganizationCompanyName(orgAdmin.getCompanyName());
        
//         log.info("  [✓] Set organization linkage:");
//         log.info("     Created By Admin ID: {}", adminId);
//         log.info("     Created By Admin Name: {}", orgAdmin.getFullName());
//         log.info("     Organization: {}", orgAdmin.getCompanyName());
        
//         // ✅ STEP 4: Create buyer using existing createBuyer method
//         // This handles the complete hierarchy (locations, departments, users)
//         Buyer createdBuyer = buyerService.createBuyer(buyer);
        
//         log.info("========================================");
//         log.info("✅ BUYER CREATED BY ORG ADMIN");
//         log.info("========================================");
//         log.info("Buyer ID: {}", createdBuyer.getId());
//         log.info("Buyer Company: {}", createdBuyer.getCompanyName());
//         log.info("Organization Company: {}", createdBuyer.getOrganizationCompanyName());
//         log.info("Created By Admin ID: {}", createdBuyer.getCreatedByOrgAdmin() != null ? createdBuyer.getCreatedByOrgAdmin().getId() : "NULL");
//         log.info("========================================");
        
//         Map<String, Object> response = new HashMap<>();
//         response.put("success", true);
//         response.put("message", "Buyer created successfully and linked to your organization");
//         response.put("data", createdBuyer);
        
//         return ResponseEntity.status(HttpStatus.CREATED).body(response);
        
//     } catch (Exception e) {
//         log.error("========================================");
//         log.error("❌ ERROR CREATING BUYER BY ORG ADMIN {}", adminId);
//         log.error("========================================");
//         log.error("Error: {}", e.getMessage());
//         e.printStackTrace();
//         log.error("========================================");
        
//         Map<String, Object> response = new HashMap<>();
//         response.put("success", false);
//         response.put("message", "Failed to create buyer: " + e.getMessage());
        
//         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//     }
// }

// /**
//  * GET /api/organization-admin/{adminId}/buyers
//  * Get all buyers created by this Organization Admin
//  */
// @GetMapping("/{adminId}/buyers")
// public ResponseEntity<?> getBuyersCreatedByAdmin(@PathVariable Long adminId) {
//     try {
//         log.info("📥 Fetching buyers created by Org Admin: {}", adminId);
        
//         List<Buyer> buyers = buyerService.getBuyersByOrganizationAdmin(adminId);
        
//         Map<String, Object> response = new HashMap<>();
//         response.put("success", true);
//         response.put("count", buyers.size());
//         response.put("data", buyers);
        
//         return ResponseEntity.ok(response);
        
//     } catch (Exception e) {
//         log.error("❌ Error fetching buyers for admin {}", adminId, e);
        
//         Map<String, Object> response = new HashMap<>();
//         response.put("success", false);
//         response.put("message", "Failed to fetch buyers");
        
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//     }
// }


//     // ============================================
//     // HELPER METHOD
//     // ============================================
    
//     /**
//      * Convert Buyer entity to BuyerCreateByAdminDTO
//      * ✅ This preserves the complete nested structure from frontend
//      */
//     // private BuyerCreateByAdminDTO convertBuyerToBuyerCreateByAdminDTO(Buyer buyer) {
//     //     BuyerCreateByAdminDTO dto = new BuyerCreateByAdminDTO();
        
//     //     // Basic fields
//     //     dto.setCompanyName(buyer.getCompanyName());
//     //     dto.setCompanyType(buyer.getCompanyType());
//     //     dto.setContactPersonName(buyer.getContactPersonName());
//     //     dto.setContactPersonDesignation(buyer.getContactPersonDesignation());
//     //     dto.setContactPersonEmail(buyer.getContactPersonEmail());
//     //     dto.setContactPersonPhone(buyer.getContactPersonPhone());
//     //     dto.setAddressLine1(buyer.getAddressLine1());
//     //     dto.setAddressLine2(buyer.getAddressLine2());
//     //     dto.setCity(buyer.getCity());
//     //     dto.setState(buyer.getState());
//     //     dto.setPostalCode(buyer.getPostalCode());
//     //     dto.setCountry(buyer.getCountry());
//     //     dto.setGstNumber(buyer.getGstNumber());
//     //     dto.setPanNumber(buyer.getPanNumber());
//     //     dto.setCinNumber(buyer.getCinNumber());
//     //     dto.setWebsite(buyer.getWebsite());
        
//     //     // ✅ CRITICAL: Organization company name
//     //     dto.setOrganizationCompanyName(buyer.getOrganizationCompanyName());
        
//     //     // ✅ IMPORTANT: Preserve complete nested structure
//     //     dto.setLocations(buyer.getLocations());
        
//     //     return dto;
//     // }
// }


package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.*;
import com.itti.leadcapturing.model.Buyer;
import com.itti.leadcapturing.model.OrganizationAdmin;
import com.itti.leadcapturing.service.OrganizationAdminService;
import com.itti.leadcapturing.service.BuyerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * ✅ VERIFIED: Organization Admin Controller
 * - Only Organization Admin can create buyers
 * - Buyers are linked to admin's organization
 */
@RestController
@RequestMapping("/api/organization-admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrganizationAdminController {

    @Autowired
    private OrganizationAdminService organizationAdminService;
    
    @Autowired
    private BuyerService buyerService;

    // ============================================
    // 1. CREATE ORGANIZATION ADMIN
    // ============================================
    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationAdminResponse>> createOrganizationAdmin(
            @Valid @RequestBody OrganizationAdminCreateRequest request,
            @RequestHeader(value = "X-Creator-Admin-Id", required = false) Long creatorAdminId) {
        
        log.info("📥 Creating OrganizationAdmin: {}", request.getEmail());
        ApiResponse<OrganizationAdminResponse> response = 
            organizationAdminService.createOrganizationAdmin(request, creatorAdminId);
        
        if (response.getSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ============================================
    // 2. LOGIN ORGANIZATION ADMIN
    // ============================================
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<OrganizationAdminResponse>> loginOrganizationAdmin(
            @Valid @RequestBody OrganizationAdminLoginRequest request) {
        
        log.info("🔐 OrganizationAdmin login attempt: {}", request.getEmail());
        ApiResponse<OrganizationAdminResponse> response = 
            organizationAdminService.loginOrganizationAdmin(request);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // ============================================
    // 3. CHANGE PASSWORD
    // ============================================
    @PutMapping("/change-password/{adminId}")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @PathVariable Long adminId,
            @Valid @RequestBody ChangePasswordRequest request) {
        
        log.info("🔐 Change password request for admin ID: {}", adminId);
        ApiResponse<String> response = organizationAdminService.changePassword(adminId, request);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ============================================
    // 4. GET BY ID
    // ============================================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationAdminResponse>> getOrganizationAdminById(
            @PathVariable Long id) {
        
        log.info("📥 Fetching OrganizationAdmin: {}", id);
        ApiResponse<OrganizationAdminResponse> response = 
            organizationAdminService.getOrganizationAdminById(id);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // ============================================
    // 5. GET BY COMPANY
    // ============================================
    @GetMapping("/company/{companyName}")
    public ResponseEntity<ApiResponse<List<OrganizationAdminResponse>>> getAdminsByCompany(
            @PathVariable String companyName) {
        
        log.info("📥 Fetching admins for company: {}", companyName);
        ApiResponse<List<OrganizationAdminResponse>> response = 
            organizationAdminService.getAdminsByCompany(companyName);
        
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 6. GET ALL
    // ============================================
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<OrganizationAdminResponse>>> getAllOrganizationAdmins() {
        log.info("📥 Fetching all OrganizationAdmins");
        ApiResponse<List<OrganizationAdminResponse>> response = 
            organizationAdminService.getAllOrganizationAdmins();
        
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 7. UPDATE
    // ============================================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationAdminResponse>> updateOrganizationAdmin(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationAdminUpdateRequest request) {
        
        log.info("✏️ Updating OrganizationAdmin: {}", id);
        ApiResponse<OrganizationAdminResponse> response = 
            organizationAdminService.updateOrganizationAdmin(id, request);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // ============================================
    // 8. DEACTIVATE
    // ============================================
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateOrganizationAdmin(@PathVariable Long id) {
        log.info("🚫 Deactivating OrganizationAdmin: {}", id);
        ApiResponse<String> response = organizationAdminService.deactivateOrganizationAdmin(id);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // ============================================
    // 9. ACTIVATE
    // ============================================
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<String>> activateOrganizationAdmin(@PathVariable Long id) {
        log.info("✅ Activating OrganizationAdmin: {}", id);
        ApiResponse<String> response = organizationAdminService.activateOrganizationAdmin(id);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // ============================================
    // 10. DELETE
    // ============================================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOrganizationAdmin(@PathVariable Long id) {
        log.info("🗑️ Deleting OrganizationAdmin: {}", id);
        ApiResponse<String> response = organizationAdminService.deleteOrganizationAdmin(id);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // ============================================
    // 11. HEALTH CHECK
    // ============================================
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("✅ OrganizationAdmin Service is running!");
    }

    // ============================================
    // ✅ 12. ORGANIZATION ADMIN CREATES BUYER
    // ============================================
    /**
     * POST /api/organization-admin/{adminId}/buyer
     * ✅ ONLY WAY to create buyers
     * Organization Admin creates buyer company with complete hierarchy
     */
    @PostMapping("/{adminId}/buyer")
    public ResponseEntity<?> createBuyer(
            @PathVariable Long adminId,
            @Valid @RequestBody Buyer buyer) {
        try {
            log.info("========================================");
            log.info("📥 ORG ADMIN {} CREATING BUYER", adminId);
            log.info("========================================");
            log.info("Buyer Company: {}", buyer.getCompanyName());
            log.info("Organization Company: {}", buyer.getOrganizationCompanyName());
            log.info("Admin ID: {}", adminId);
            
            // ✅ STEP 1: Fetch Organization Admin
            OrganizationAdmin orgAdmin = organizationAdminService.getOrganizationAdminByIdDirect(adminId);
            
            if (orgAdmin == null) {
                log.error("❌ Organization Admin not found: {}", adminId);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Organization Admin not found with ID: " + adminId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            log.info("  [✓] Found Org Admin: {}", orgAdmin.getFullName());
            log.info("  [✓] Org Admin Company: {}", orgAdmin.getCompanyName());
            
            // ✅ STEP 2: Verify company name matches (if provided)
            if (buyer.getOrganizationCompanyName() != null 
                && !orgAdmin.getCompanyName().equals(buyer.getOrganizationCompanyName())) {
                log.error("❌ Company name mismatch!");
                log.error("   Expected: {}", orgAdmin.getCompanyName());
                log.error("   Provided: {}", buyer.getOrganizationCompanyName());
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Organization company name mismatch! " +
                    "Expected: '" + orgAdmin.getCompanyName() + "', " +
                    "Provided: '" + buyer.getOrganizationCompanyName() + "'");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // ✅ STEP 3: Set organization linkage (CRITICAL!)
            buyer.setCreatedByOrgAdmin(orgAdmin);
            buyer.setOrganizationCompanyName(orgAdmin.getCompanyName());
            
            log.info("  [✓] Organization linkage set:");
            log.info("     Created By Admin ID: {}", adminId);
            log.info("     Created By Admin Name: {}", orgAdmin.getFullName());
            log.info("     Organization: {}", orgAdmin.getCompanyName());
            
            // ✅ STEP 4: Create buyer using service
            Buyer createdBuyer = buyerService.createBuyer(buyer);
            
            log.info("========================================");
            log.info("✅ BUYER CREATED BY ORG ADMIN");
            log.info("========================================");
            log.info("Buyer ID: {}", createdBuyer.getId());
            log.info("Buyer Company: {}", createdBuyer.getCompanyName());
            log.info("Organization Company: {}", createdBuyer.getOrganizationCompanyName());
            log.info("Created By Admin ID: {}", createdBuyer.getCreatedByOrgAdmin() != null ? 
                     createdBuyer.getCreatedByOrgAdmin().getId() : "NULL");
            log.info("========================================");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Buyer created successfully and linked to your organization");
            response.put("data", createdBuyer);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("❌ ERROR CREATING BUYER BY ORG ADMIN {}", adminId);
            log.error("========================================");
            log.error("Error: {}", e.getMessage());
            e.printStackTrace();
            log.error("========================================");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to create buyer: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ============================================
    // ✅ 13. GET BUYERS CREATED BY ADMIN
    // ============================================
    /**
     * GET /api/organization-admin/{adminId}/buyers
     * Get all buyers created by this Organization Admin
     */
/**
 * GET /api/organization-admin/{adminId}/buyers
 * Get all buyers created by this Organization Admin
 */
@GetMapping("/{adminId}/buyers")
public ResponseEntity<?> getBuyersCreatedByAdmin(@PathVariable Long adminId) {
    try {
        log.info("========================================");
        log.info("📥 API REQUEST: Get Buyers by Admin ID: {}", adminId);
        log.info("========================================");
        
        // Validate adminId
        if (adminId == null || adminId <= 0) {
            log.error("❌ Invalid admin ID: {}", adminId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Invalid admin ID");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // Fetch buyers
        List<Buyer> buyers = buyerService.getBuyersByOrganizationAdmin(adminId);
        
        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Buyers retrieved successfully");
        response.put("adminId", adminId);
        response.put("count", buyers.size());
        response.put("data", buyers);
        
        log.info("========================================");
        log.info("✅ API RESPONSE: Returning {} buyers", buyers.size());
        log.info("========================================");
        
        return ResponseEntity.ok(response);
        
    } catch (RuntimeException e) {
        log.error("========================================");
        log.error("❌ RUNTIME ERROR: {}", e.getMessage());
        log.error("========================================");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", e.getMessage());
        response.put("error", "RUNTIME_ERROR");
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        
    } catch (Exception e) {
        log.error("========================================");
        log.error("❌ INTERNAL ERROR");
        log.error("========================================");
        log.error("Error Type: {}", e.getClass().getName());
        log.error("Error Message: {}", e.getMessage());
        e.printStackTrace();
        log.error("========================================");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Failed to fetch buyers");
        response.put("error", e.getClass().getSimpleName());
        response.put("details", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
    // ============================================
    // ✅ 14. GET BUYERS BY ORGANIZATION COMPANY
    // ============================================
    /**
     * GET /api/organization-admin/company/{companyName}/buyers
     * Get all buyers for an organization
     */
    @GetMapping("/company/{companyName}/buyers")
    public ResponseEntity<?> getBuyersByOrganization(@PathVariable String companyName) {
        try {
            log.info("📥 Fetching buyers for organization: {}", companyName);
            
            List<Buyer> buyers = buyerService.getBuyersByOrganizationCompanyName(companyName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Buyers retrieved successfully");
            response.put("organization", companyName);
            response.put("count", buyers.size());
            response.put("data", buyers);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching buyers for organization {}", companyName, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch buyers: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}