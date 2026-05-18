// package com.itti.leadcapturing.web;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;

// import com.itti.leadcapturing.model.Buyer;
// import com.itti.leadcapturing.service.BuyerService;
// import com.itti.leadcapturing.service.LoginService;
// import com.itti.leadcapturing.dto.LoginRequest;
// import com.itti.leadcapturing.dto.LoginResponse;

// import jakarta.validation.Valid;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;

// /**
//  * ✅ FIXED: Buyer Controller
//  */
// @RestController
// @RequestMapping("/api/buyer")
// @CrossOrigin(origins = "*")
// @Slf4j
// public class BuyerController {

//     @Autowired
//     private BuyerService buyerService;
    
//     @Autowired
//     private LoginService loginService;

//     // ============================================
//     // ✅ GET BUYER BY ID
//     // ============================================
//     @GetMapping("/{id}")
//     public ResponseEntity<?> getBuyerById(@PathVariable Long id) {
//         try {
//             log.info("========================================");
//             log.info("📥 GET BUYER BY ID: {}", id);
//             log.info("========================================");
            
//             Buyer buyer = buyerService.getBuyerById(id);
            
//             log.info("✅ Buyer found: {}", buyer.getCompanyName());
//             log.info("   Locations: {}", buyer.getLocations() != null ? buyer.getLocations().size() : 0);
            
//             if (buyer.getLocations() != null) {
//                 buyer.getLocations().forEach(location -> {
//                     log.info("     Location: {}", location.getLocationName());
//                     if (location.getDepartments() != null) {
//                         log.info("       Departments: {}", location.getDepartments().size());
//                         location.getDepartments().forEach(dept -> {
//                             log.info("         Department: {}", dept.getDepartmentName());
//                             if (dept.getUsers() != null) {
//                                 log.info("           Users: {}", dept.getUsers().size());
//                             }
//                         });
//                     }
//                 });
//             }
            
//             log.info("========================================");
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Buyer fetched successfully");
//             response.put("data", buyer);
            
//             return ResponseEntity.ok(response);
            
//         } catch (RuntimeException e) {
//             log.error("========================================");
//             log.error("❌ BUYER NOT FOUND: {}", id);
//             log.error("Error: {}", e.getMessage());
//             log.error("========================================");
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Buyer not found with ID: " + id);
            
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
//         } catch (Exception e) {
//             log.error("========================================");
//             log.error("❌ INTERNAL ERROR FETCHING BUYER: {}", id);
//             log.error("========================================");
//             log.error("Error Type: {}", e.getClass().getName());
//             log.error("Error Message: {}", e.getMessage());
//             e.printStackTrace();
//             log.error("========================================");
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to fetch buyer");
//             response.put("error", e.getMessage());
            
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }


//     // Add these methods to your existing BuyerController

// /**
//  * ✅ NEW: Get buyer logo as base64
//  * GET /api/buyer/{id}/logo/base64
//  */

// // ============================================
// // REPLACE getBuyerLogoBase64 IN BuyerController.java
// // ============================================

// @GetMapping("/{id}/logo/base64")
// public ResponseEntity<String> getBuyerLogoBase64(@PathVariable Long id) {
//     try {
//         log.info("📥 GET BUYER LOGO BASE64: {}", id);

//         String logoBase64 = buyerService.getBuyerLogoBase64(id);

//         if (logoBase64 == null || logoBase64.trim().isEmpty()) {
//             log.warn("⚠️ No logo data for buyer: {}", id);
//             return ResponseEntity.noContent().build(); // 204
//         }

//         log.info("✅ Returning logo — {} chars", logoBase64.length());

//         // ✅ Use header() not contentType() — avoids Spring/Jackson JSON-encoding the string
//         return ResponseEntity.ok()
//                 .header("Content-Type", "text/plain;charset=UTF-8")
//                 .header("Cache-Control", "public, max-age=3600")
//                 .body(logoBase64);

//     } catch (RuntimeException e) {
//         log.error("❌ Buyer not found: {}", id);
//         return ResponseEntity.noContent().build();
//     } catch (Exception e) {
//         log.error("❌ Error: {}", e.getMessage());
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//     }
// }

//     /**
//  * ✅ NEW: Get buyer logo as raw bytes
//  * GET /api/buyer/{id}/logo
//  */
//     @GetMapping("/{id}/logo")
//     public ResponseEntity<byte[]> getBuyerLogo(@PathVariable Long id) {
//         try {
//             log.info("========================================");
//             log.info("📥 GET BUYER LOGO (RAW): {}", id);
//             log.info("========================================");
            
//             Buyer buyer = buyerService.getBuyerById(id);
            
//             if (buyer.getLogoData() == null || buyer.getLogoData().length == 0) {
//                 log.info("⚠️ No logo data for buyer: {}", id);
//                 log.info("========================================");
//                 return ResponseEntity.notFound().build();
//             }
            
//             HttpHeaders headers = new HttpHeaders();
            
//             String contentType = buyer.getLogoContentType();
//             if (contentType == null || contentType.isEmpty()) {
//                 contentType = "image/png";
//             }
            
//             headers.setContentType(MediaType.parseMediaType(contentType));
//             headers.setContentLength(buyer.getLogoData().length);
            
//             log.info("✅ Logo returned. Size: {} bytes, Type: {}", 
//                      buyer.getLogoData().length, contentType);
//             log.info("========================================");
            
//             return new ResponseEntity<>(buyer.getLogoData(), headers, HttpStatus.OK);
            
//         } catch (RuntimeException e) {
//             log.error("========================================");
//             log.error("❌ Buyer not found: {}", id);
//             log.error("========================================");
//             return ResponseEntity.notFound().build();
            
//         } catch (Exception e) {
//             log.error("========================================");
//             log.error("❌ Error getting logo for buyer: {}", id);
//             log.error("Error: {}", e.getMessage());
//             log.error("========================================");
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//         }
//     }

//         // ============================================
//     // ✅ GET ALL BUYERS
//     // ============================================
//     @GetMapping
//     public ResponseEntity<?> getAllBuyers() {
//         try {
//             log.info("========================================");
//             log.info("📥 GET ALL BUYERS");
//             log.info("========================================");
            
//             List<Buyer> buyers = buyerService.getAllBuyers();
            
//             log.info("✅ Found {} buyers", buyers.size());
            
//             buyers.forEach(buyer -> {
//                 if (buyer.getLocations() != null) {
//                     buyer.getLocations().size();
//                     log.info("   Buyer: {} has {} locations", 
//                              buyer.getCompanyName(), 
//                              buyer.getLocations().size());
//                 }
//             });
            
//             log.info("========================================");
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Buyers fetched successfully");
//             response.put("count", buyers.size());
//             response.put("data", buyers);
            
//             return ResponseEntity.ok(response);
            
//         } catch (Exception e) {
//             log.error("========================================");
//             log.error("❌ ERROR FETCHING ALL BUYERS");
//             log.error("========================================");
//             log.error("Error: {}", e.getMessage());
//             e.printStackTrace();
//             log.error("========================================");
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to fetch buyers");
//             response.put("error", e.getMessage());
            
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }



//     // ============================================
//     // ❌ DISABLED: DIRECT BUYER CREATION
//     // ============================================
  
//         @PostMapping
//     public ResponseEntity<?> createBuyer(@Valid @RequestBody Buyer buyer) {
//         log.warn("========================================");
//         log.warn("⚠️  DEPRECATED ENDPOINT ACCESSED");
//         log.warn("========================================");
//         log.warn("Attempted direct buyer creation");
//         log.warn("Use: POST /api/organization-admin/{adminId}/buyer");
//         log.warn("========================================");
        
//         Map<String, Object> response = new HashMap<>();
//         response.put("success", false);
//         response.put("message", "Direct buyer creation is not allowed. " +
//                                "Buyers must be created by Organization Admin via: " +
//                                "POST /api/organization-admin/{adminId}/buyer");
//         response.put("error", "DEPRECATED_ENDPOINT");
//         response.put("correctEndpoint", "POST /api/organization-admin/{adminId}/buyer");
//         response.put("documentation", "Contact your Organization Admin to create buyer companies");
        
//         return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
//     }

//      @PutMapping("/{id}")
//     public ResponseEntity<?> updateBuyer(
//             @PathVariable Long id, 
//             @Valid @RequestBody Buyer buyerReq) {
//         try {
//             log.info("========================================");
//             log.info("📥 UPDATE BUYER: {}", id);
//             log.info("========================================");
            
//             Buyer updated = buyerService.updateBuyer(id, buyerReq);
            
//             if (updated.getLocations() != null) {
//                 updated.getLocations().size();
//             }
            
//             log.info("✅ Buyer updated: {} (ID: {})", updated.getCompanyName(), updated.getId());
//             log.info("========================================");
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Buyer updated successfully");
//             response.put("data", updated);
            
//             return ResponseEntity.ok(response);
            
//         } catch (RuntimeException e) {
//             log.error("❌ Error updating buyer: {}", id, e);
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to update buyer: " + e.getMessage());
            
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
//         } catch (Exception e) {
//             log.error("❌ Unexpected error updating buyer: {}", id, e);
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Internal error: " + e.getMessage());
            
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // ============================================
//     // ✅ DELETE BUYER
//     // ============================================

//      @DeleteMapping("/{id}")
//     public ResponseEntity<?> deleteBuyer(@PathVariable Long id) {
//         try {
//             log.info("========================================");
//             log.info("📥 DELETE BUYER: {}", id);
//             log.info("========================================");
            
//             buyerService.deleteBuyer(id);
            
//             log.info("✅ Buyer deleted: ID {}", id);
//             log.info("========================================");
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Buyer deleted successfully!");
            
//             return ResponseEntity.ok(response);
            
//         } catch (RuntimeException e) {
//             log.error("❌ Error deleting buyer: {}", id, e);
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
            
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }



//     // ============================================
//     // ✅ BUYER USER LOGIN
//     // ============================================

//          @PostMapping("/login")
//     public ResponseEntity<?> loginBuyer(@RequestBody LoginRequest loginRequest) {
//         try {
//             log.info("========================================");
//             log.info("🔐 BUYER USER LOGIN: {}", loginRequest.getEmail());
//             log.info("========================================");
            
//             if (loginRequest == null || loginRequest.getEmail() == null || 
//                 loginRequest.getPassword() == null) {
                
//                 Map<String, Object> response = new HashMap<>();
//                 response.put("success", false);
//                 response.put("message", "Email and password are required");
//                 return ResponseEntity.badRequest().body(response);
//             }
            
//             LoginResponse response = loginService.authenticateUser(loginRequest);
            
//             if (!response.isSuccess()) {
//                 log.warn("❌ Login failed: {}", loginRequest.getEmail());
//                 log.info("========================================");
//                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//             }
            
//             log.info("✅ Login successful: {}", loginRequest.getEmail());
//             log.info("========================================");
//             return ResponseEntity.ok(response);
            
//         } catch (Exception e) {
//             log.error("========================================");
//             log.error("❌ LOGIN ERROR");
//             log.error("========================================");
//             log.error("Error: {}", e.getMessage());
//             e.printStackTrace();
//             log.error("========================================");
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Login failed: " + e.getMessage());
            
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }



//     // ============================================
//     // ✅ HEALTH CHECK
//     // ============================================
//     @GetMapping("/health")
//     public ResponseEntity<Map<String, Object>> healthCheck() {
//         Map<String, Object> response = new HashMap<>();
//         response.put("success", true);
//         response.put("message", "Buyer Service is running!");
//         response.put("note", "Buyers can only be created by Organization Admin");
//         response.put("timestamp", System.currentTimeMillis());
//         return ResponseEntity.ok(response);
//     }
// }


package com.itti.leadcapturing.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.itti.leadcapturing.model.Buyer;
import com.itti.leadcapturing.service.BuyerService;
import com.itti.leadcapturing.service.LoginService;
import com.itti.leadcapturing.dto.LoginRequest;
import com.itti.leadcapturing.dto.LoginResponse;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * MODIFIED:
 * - getBuyerLogoBase64: now delegates to BuyerService.getLogoBase64ForBuyer()
 *   which reads the logo from the linked OrganizationAdmin.
 * - getBuyerLogo (raw bytes): same — reads from OrganizationAdmin via FileStorageService.
 * - Removed all direct buyer logo/companyName logic.
 */
@RestController
@RequestMapping("/api/buyer")
@CrossOrigin(origins = "*")
@Slf4j
public class BuyerController {

    @Autowired
    private BuyerService buyerService;

    @Autowired
    private LoginService loginService;

    // ============================================
    // GET BUYER BY ID
    // ============================================
    @GetMapping("/{id}")
    public ResponseEntity<?> getBuyerById(@PathVariable Long id) {
        try {
            log.info("📥 GET BUYER BY ID: {}", id);
            Buyer buyer = buyerService.getBuyerById(id);
            log.info("✅ Buyer found: {}", buyer.getCompanyName());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Buyer fetched successfully");
            response.put("data", buyer);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ BUYER NOT FOUND: {}", id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Buyer not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            log.error("❌ INTERNAL ERROR FETCHING BUYER: {}", id, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch buyer");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============================================
    // GET BUYER LOGO AS BASE64
    // Logo is inherited from OrganizationAdmin who created this buyer.
    // ============================================
    @GetMapping("/{id}/logo/base64")
    public ResponseEntity<String> getBuyerLogoBase64(@PathVariable Long id) {
        try {
            log.info("📥 GET BUYER LOGO BASE64 (from OrgAdmin): {}", id);

            String logoBase64 = buyerService.getLogoBase64ForBuyer(id);

            if (logoBase64 == null || logoBase64.trim().isEmpty()) {
                log.warn("⚠️ No logo available for buyer: {}", id);
                return ResponseEntity.noContent().build(); // 204
            }

            log.info("✅ Returning logo — {} chars", logoBase64.length());

            return ResponseEntity.ok()
                    .header("Content-Type", "text/plain;charset=UTF-8")
                    .header("Cache-Control", "public, max-age=3600")
                    .body(logoBase64);

        } catch (RuntimeException e) {
            log.error("❌ Buyer not found: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ============================================
    // GET BUYER COMPANY NAME
    // Company name is inherited from OrganizationAdmin.
    // ============================================
    @GetMapping("/{id}/company-name")
    public ResponseEntity<?> getBuyerCompanyName(@PathVariable Long id) {
        try {
            log.info("📥 GET BUYER COMPANY NAME (from OrgAdmin): {}", id);
            String companyName = buyerService.getCompanyNameForBuyer(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("companyName", companyName);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // ============================================
    // GET ALL BUYERS
    // ============================================
    @GetMapping
    public ResponseEntity<?> getAllBuyers() {
        try {
            log.info("📥 GET ALL BUYERS");
            List<Buyer> buyers = buyerService.getAllBuyers();
            log.info("✅ Found {} buyers", buyers.size());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Buyers fetched successfully");
            response.put("count", buyers.size());
            response.put("data", buyers);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ ERROR FETCHING ALL BUYERS", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch buyers");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============================================
    // DIRECT BUYER CREATION — DISABLED
    // ============================================
    @PostMapping
    public ResponseEntity<?> createBuyer(@Valid @RequestBody Buyer buyer) {
        log.warn("⚠️ DEPRECATED ENDPOINT ACCESSED — direct buyer creation");
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Direct buyer creation is not allowed. " +
                               "Buyers must be created by Organization Admin via: " +
                               "POST /api/organization-admin/{adminId}/buyer");
        response.put("error", "DEPRECATED_ENDPOINT");
        response.put("correctEndpoint", "POST /api/organization-admin/{adminId}/buyer");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // ============================================
    // UPDATE BUYER
    // ============================================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBuyer(
            @PathVariable Long id,
            @Valid @RequestBody Buyer buyerReq) {
        try {
            log.info("📥 UPDATE BUYER: {}", id);
            Buyer updated = buyerService.updateBuyer(id, buyerReq);
            if (updated.getLocations() != null) updated.getLocations().size();
            log.info("✅ Buyer updated: {} (ID: {})", updated.getCompanyName(), updated.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Buyer updated successfully");
            response.put("data", updated);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error updating buyer: {}", id, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update buyer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            log.error("❌ Unexpected error updating buyer: {}", id, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Internal error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============================================
    // DELETE BUYER
    // ============================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBuyer(@PathVariable Long id) {
        try {
            log.info("📥 DELETE BUYER: {}", id);
            buyerService.deleteBuyer(id);
            log.info("✅ Buyer deleted: ID {}", id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Buyer deleted successfully!");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error deleting buyer: {}", id, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============================================
    // BUYER USER LOGIN
    // ============================================
    @PostMapping("/login")
    public ResponseEntity<?> loginBuyer(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("🔐 BUYER USER LOGIN: {}", loginRequest.getEmail());

            if (loginRequest == null || loginRequest.getEmail() == null ||
                loginRequest.getPassword() == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Email and password are required");
                return ResponseEntity.badRequest().body(response);
            }

            LoginResponse response = loginService.authenticateUser(loginRequest);

            if (!response.isSuccess()) {
                log.warn("❌ Login failed: {}", loginRequest.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            log.info("✅ Login successful: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ LOGIN ERROR: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============================================
    // HEALTH CHECK
    // ============================================
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Buyer Service is running!");
        response.put("note", "Buyers can only be created by Organization Admin. Logo is inherited from admin.");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}