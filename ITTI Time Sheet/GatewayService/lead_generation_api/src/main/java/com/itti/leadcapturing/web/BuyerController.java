
// // package com.itti.leadcapturing.web;

// // import java.util.HashMap;
// // import java.util.List;
// // import java.util.Map;
// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.web.bind.annotation.*;
// // import org.springframework.http.HttpStatus;
// // import org.springframework.http.ResponseEntity;

// // import com.itti.leadcapturing.model.Buyer;
// // import com.itti.leadcapturing.service.BuyerService;
// // import com.itti.leadcapturing.service.LoginService;
// // import com.itti.leadcapturing.dto.LoginRequest;
// // import com.itti.leadcapturing.dto.LoginResponse;

// // import jakarta.validation.Valid;
// // import lombok.extern.slf4j.Slf4j;

// // @RestController
// // @RequestMapping("/api/buyer")
// // @CrossOrigin(origins = "*")
// // @Slf4j
// // public class BuyerController {

// //     @Autowired
// //     private BuyerService buyerService;
    
// //     @Autowired
// //     private LoginService loginService;

// //     // ============================================
// //     // ✅ CREATE BUYER (Already Working)
// //     // ============================================
// //     // @PostMapping
// //     // public ResponseEntity<?> createBuyer(@Valid @RequestBody Buyer buyer) {
// //     //     try {
// //     //         log.info("📥 Received request to create buyer: {}", buyer.getCompanyName());
            
// //     //         Buyer savedBuyer = buyerService.createBuyer(buyer);
            
// //     //         log.info("✅ Buyer created successfully: {} (ID: {})", 
// //     //                  savedBuyer.getCompanyName(), savedBuyer.getId());
            
// //     //         log.info("🔍 Returning buyer with ID: {} of type: {}", 
// //     //                  savedBuyer.getId(), 
// //     //                  savedBuyer.getId().getClass().getSimpleName());
            
// //     //         Map<String, Object> response = new HashMap<>();
// //     //         response.put("success", true);
// //     //         response.put("message", "Buyer created successfully");
// //     //         response.put("data", savedBuyer);
            
// //     //         log.info("📤 Response structure:");
// //     //         log.info("   - success: {}", response.get("success"));
// //     //         log.info("   - message: {}", response.get("message"));
// //     //         log.info("   - data.id: {}", savedBuyer.getId());
// //     //         log.info("   - data.companyName: {}", savedBuyer.getCompanyName());
            
// //     //         return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
// //     //     } catch (Exception e) {
// //     //         log.error("❌ Error creating buyer", e);
// //     //         e.printStackTrace();
            
// //     //         Map<String, Object> response = new HashMap<>();
// //     //         response.put("success", false);
// //     //         response.put("message", "Failed to create buyer");
// //     //         response.put("error", e.getMessage());
            
// //     //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
// //     //     }
// //     // }
// // @PostMapping
// // public ResponseEntity<?> createBuyer(@Valid @RequestBody Buyer buyer) {
// //     Map<String, Object> response = new HashMap<>();
// //     response.put("success", false);
// //     response.put("message", "Direct buyer creation is no longer allowed. " +
// //                            "Buyers must be created by Organization Admin via: " +
// //                            "POST /api/organization-admin/{adminId}/buyer");
// //     response.put("error", "DEPRECATED_ENDPOINT");
    
// //     return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
// // }
// //     // ============================================
// //     // ✅ FIXED: GET ALL BUYERS
// //     // ============================================
// //     @GetMapping
// //     public ResponseEntity<?> getAllBuyers() {
// //         try {
// //             log.info("📥 Received request to fetch all buyers");
            
// //             List<Buyer> buyers = buyerService.getAllBuyers();
            
// //             log.info("✅ Found {} buyers", buyers.size());
            
// //             // ✅ CRITICAL FIX: Prevent lazy loading issues
// //             // Initialize locations for each buyer to avoid LazyInitializationException
// //             buyers.forEach(buyer -> {
// //                 if (buyer.getLocations() != null) {
// //                     buyer.getLocations().size(); // Force initialization
// //                     log.info("   Buyer: {} has {} locations", 
// //                              buyer.getCompanyName(), 
// //                              buyer.getLocations().size());
// //                 }
// //             });
            
// //             Map<String, Object> response = new HashMap<>();
// //             response.put("success", true);
// //             response.put("message", "Buyers fetched successfully");
// //             response.put("count", buyers.size());
// //             response.put("data", buyers);
            
// //             return ResponseEntity.ok(response);
            
// //         } catch (Exception e) {
// //             log.error("❌ Error fetching all buyers", e);
// //             e.printStackTrace();
            
// //             Map<String, Object> response = new HashMap<>();
// //             response.put("success", false);
// //             response.put("message", "Failed to fetch buyers");
// //             response.put("error", e.getClass().getSimpleName());
// //             response.put("details", e.getMessage());
            
// //             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
// //         }
// //     }

// //     // ============================================
// //     // ✅ FIXED: GET BUYER BY ID
// //     // ============================================
// //     @GetMapping("/{id}")
// //     public ResponseEntity<?> getBuyerById(@PathVariable Long id) {
// //         try {
// //             log.info("📥 Received request to fetch buyer ID: {}", id);
            
// //             Buyer buyer = buyerService.getBuyerById(id);
            
// //             // ✅ CRITICAL FIX: Initialize lazy-loaded collections
// //             if (buyer.getLocations() != null) {
// //                 buyer.getLocations().size(); // Force initialization
// //                 log.info("   Buyer has {} locations", buyer.getLocations().size());
                
// //                 // Initialize departments for each location
// //                 buyer.getLocations().forEach(location -> {
// //                     if (location.getDepartments() != null) {
// //                         location.getDepartments().size();
// //                         log.info("     Location '{}' has {} departments", 
// //                                  location.getLocationName(), 
// //                                  location.getDepartments().size());
// //                     }
// //                 });
// //             }
            
// //             log.info("✅ Buyer found: {} (ID: {})", buyer.getCompanyName(), buyer.getId());
            
// //             Map<String, Object> response = new HashMap<>();
// //             response.put("success", true);
// //             response.put("message", "Buyer fetched successfully");
// //             response.put("data", buyer);
            
// //             return ResponseEntity.ok(response);
            
// //         } catch (RuntimeException e) {
// //             log.error("❌ Buyer not found with ID: {}", id, e);
            
// //             Map<String, Object> response = new HashMap<>();
// //             response.put("success", false);
// //             response.put("message", "Buyer not found with ID: " + id);
            
// //             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
// //         } catch (Exception e) {
// //             log.error("❌ Error fetching buyer ID: {}", id, e);
// //             e.printStackTrace();
            
// //             Map<String, Object> response = new HashMap<>();
// //             response.put("success", false);
// //             response.put("message", "Failed to fetch buyer");
// //             response.put("error", e.getClass().getSimpleName());
            
// //             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
// //         }
// //     }

// //     // ============================================
// //     // ✅ FIXED: UPDATE BUYER
// //     // ============================================
// //     @PutMapping("/{id}")
// //     public ResponseEntity<?> updateBuyer(
// //             @PathVariable Long id, 
// //             @Valid @RequestBody Buyer buyerReq) {
// //         try {
// //             log.info("📥 Received request to update buyer ID: {}", id);
// //             log.info("   Update data: companyName={}, email={}", 
// //                      buyerReq.getCompanyName(), 
// //                      buyerReq.getContactPersonEmail());
            
// //             // ✅ CRITICAL: Call service to update
// //             Buyer updated = buyerService.updateBuyer(id, buyerReq);
            
// //             // ✅ Initialize lazy collections for response
// //             if (updated.getLocations() != null) {
// //                 updated.getLocations().size();
// //             }
            
// //             log.info("✅ Buyer updated successfully: {} (ID: {})", 
// //                      updated.getCompanyName(), updated.getId());
            
// //             Map<String, Object> response = new HashMap<>();
// //             response.put("success", true);
// //             response.put("message", "Buyer updated successfully");
// //             response.put("data", updated);
            
// //             return ResponseEntity.ok(response);
            
// //         } catch (RuntimeException e) {
// //             log.error("❌ Error updating buyer ID: {}", id, e);
// //             e.printStackTrace();
            
// //             Map<String, Object> response = new HashMap<>();
// //             response.put("success", false);
// //             response.put("message", "Failed to update buyer: " + e.getMessage());
            
// //             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
// //         } catch (Exception e) {
// //             log.error("❌ Unexpected error updating buyer ID: {}", id, e);
// //             e.printStackTrace();
            
// //             Map<String, Object> response = new HashMap<>();
// //             response.put("success", false);
// //             response.put("message", "Internal error: " + e.getMessage());
// //             response.put("error", e.getClass().getSimpleName());
            
// //             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
// //         }
// //     }

// //     // ============================================
// //     // ✅ DELETE BUYER (Already Working)
// //     // ============================================
// //     @DeleteMapping("/{id}")
// //     public ResponseEntity<?> deleteBuyer(@PathVariable Long id) {
// //         try {
// //             log.info("📥 Received request to delete buyer ID: {}", id);
            
// //             buyerService.deleteBuyer(id);
            
// //             log.info("✅ Buyer deleted successfully: ID {}", id);
            
// //             Map<String, Object> response = new HashMap<>();
// //             response.put("success", true);
// //             response.put("message", "Buyer deleted successfully!");
            
// //             return ResponseEntity.ok(response);
            
// //         } catch (RuntimeException e) {
// //             log.error("❌ Error deleting buyer ID: {}", id, e);
            
// //             Map<String, Object> response = new HashMap<>();
// //             response.put("success", false);
// //             response.put("message", e.getMessage());
            
// //             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
// //         }
// //     }

// //     // ============================================
// //     // ✅ LOGIN BUYER USER
// //     // ============================================
// //     @PostMapping("/login")
// //     public ResponseEntity<?> loginBuyer(@RequestBody LoginRequest loginRequest) {
// //         try {
// //             log.info("🔐 Buyer login attempt: {}", loginRequest.getEmail());
            
// //             if (loginRequest == null || loginRequest.getEmail() == null || 
// //                 loginRequest.getPassword() == null) {
                
// //                 Map<String, Object> response = new HashMap<>();
// //                 response.put("success", false);
// //                 response.put("message", "Email and password are required");
// //                 return ResponseEntity.badRequest().body(response);
// //             }
            
// //             LoginResponse response = loginService.authenticateUser(loginRequest);
            
// //             if (!response.isSuccess()) {
// //                 log.warn("❌ Login failed for: {}", loginRequest.getEmail());
// //                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
// //             }
            
// //             log.info("✅ Login successful for: {}", loginRequest.getEmail());
// //             return ResponseEntity.ok(response);
            
// //         } catch (Exception e) {
// //             log.error("❌ Error during login", e);
            
// //             Map<String, Object> response = new HashMap<>();
// //             response.put("success", false);
// //             response.put("message", "Login failed: " + e.getMessage());
            
// //             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
// //         }
// //     }
    
// //     // ============================================
// //     // ✅ HEALTH CHECK
// //     // ============================================
// //     @GetMapping("/health")
// //     public ResponseEntity<Map<String, Object>> healthCheck() {
// //         Map<String, Object> response = new HashMap<>();
// //         response.put("success", true);
// //         response.put("message", "Buyer Service is running!");
// //         response.put("timestamp", System.currentTimeMillis());
// //         return ResponseEntity.ok(response);
// //     }
// // }


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

// /**
//  * ✅ FIXED: Buyer Controller
//  * - Direct buyer creation is DISABLED
//  * - Only Organization Admin can create buyers via /api/organization-admin/{adminId}/buyer
//  * - Buyer users can still login
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
//     // ❌ DISABLED: DIRECT BUYER CREATION
//     // ============================================
//     /**
//      * ❌ DEPRECATED - Use Organization Admin endpoint instead
//      * POST /api/organization-admin/{adminId}/buyer
//      */
//     @PostMapping
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

//     // ============================================
//     // ✅ GET ALL BUYERS - READ ONLY
//     // ============================================
//     @GetMapping
//     public ResponseEntity<?> getAllBuyers() {
//         try {
//             log.info("📥 Fetching all buyers");
            
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
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Buyers fetched successfully");
//             response.put("count", buyers.size());
//             response.put("data", buyers);
            
//             return ResponseEntity.ok(response);
            
//         } catch (Exception e) {
//             log.error("❌ Error fetching all buyers", e);
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to fetch buyers");
//             response.put("error", e.getMessage());
            
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // ============================================
//     // ✅ GET BUYER BY ID - READ ONLY
//     // ============================================
//     @GetMapping("/{id}")
//     public ResponseEntity<?> getBuyerById(@PathVariable Long id) {
//         try {
//             log.info("📥 Fetching buyer ID: {}", id);
            
//             Buyer buyer = buyerService.getBuyerById(id);
            
//             if (buyer.getLocations() != null) {
//                 buyer.getLocations().size();
//                 log.info("   Buyer has {} locations", buyer.getLocations().size());
                
//                 buyer.getLocations().forEach(location -> {
//                     if (location.getDepartments() != null) {
//                         location.getDepartments().size();
//                         log.info("     Location '{}' has {} departments", 
//                                  location.getLocationName(), 
//                                  location.getDepartments().size());
//                     }
//                 });
//             }
            
//             log.info("✅ Buyer found: {} (ID: {})", buyer.getCompanyName(), buyer.getId());
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Buyer fetched successfully");
//             response.put("data", buyer);
            
//             return ResponseEntity.ok(response);
            
//         } catch (RuntimeException e) {
//             log.error("❌ Buyer not found with ID: {}", id);
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Buyer not found with ID: " + id);
            
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
//         } catch (Exception e) {
//             log.error("❌ Error fetching buyer ID: {}", id, e);
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to fetch buyer");
//             response.put("error", e.getMessage());
            
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // ============================================
//     // ✅ UPDATE BUYER - RESTRICTED TO ORG ADMIN
//     // ============================================
//     /**
//      * ⚠️ WARNING: This should also be restricted to Organization Admin
//      * Consider moving to OrganizationAdminController
//      */
//     @PutMapping("/{id}")
//     public ResponseEntity<?> updateBuyer(
//             @PathVariable Long id, 
//             @Valid @RequestBody Buyer buyerReq) {
//         try {
//             log.info("📥 Updating buyer ID: {}", id);
//             log.info("   Update data: companyName={}, email={}", 
//                      buyerReq.getCompanyName(), 
//                      buyerReq.getContactPersonEmail());
            
//             Buyer updated = buyerService.updateBuyer(id, buyerReq);
            
//             if (updated.getLocations() != null) {
//                 updated.getLocations().size();
//             }
            
//             log.info("✅ Buyer updated successfully: {} (ID: {})", 
//                      updated.getCompanyName(), updated.getId());
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Buyer updated successfully");
//             response.put("data", updated);
            
//             return ResponseEntity.ok(response);
            
//         } catch (RuntimeException e) {
//             log.error("❌ Error updating buyer ID: {}", id, e);
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to update buyer: " + e.getMessage());
            
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
//         } catch (Exception e) {
//             log.error("❌ Unexpected error updating buyer ID: {}", id, e);
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Internal error: " + e.getMessage());
            
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // ============================================
//     // ✅ DELETE BUYER - RESTRICTED TO ORG ADMIN
//     // ============================================
//     /**
//      * ⚠️ WARNING: This should also be restricted to Organization Admin
//      * Consider moving to OrganizationAdminController
//      */
//     @DeleteMapping("/{id}")
//     public ResponseEntity<?> deleteBuyer(@PathVariable Long id) {
//         try {
//             log.info("📥 Deleting buyer ID: {}", id);
            
//             buyerService.deleteBuyer(id);
            
//             log.info("✅ Buyer deleted successfully: ID {}", id);
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Buyer deleted successfully!");
            
//             return ResponseEntity.ok(response);
            
//         } catch (RuntimeException e) {
//             log.error("❌ Error deleting buyer ID: {}", id, e);
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
            
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // ============================================
//     // ✅ BUYER USER LOGIN - ALLOWED
//     // ============================================
//     /**
//      * ✅ ALLOWED: Buyer users can login
//      * POST /api/buyer/login
//      */
//     @PostMapping("/login")
//     public ResponseEntity<?> loginBuyer(@RequestBody LoginRequest loginRequest) {
//         try {
//             log.info("🔐 Buyer user login attempt: {}", loginRequest.getEmail());
            
//             if (loginRequest == null || loginRequest.getEmail() == null || 
//                 loginRequest.getPassword() == null) {
                
//                 Map<String, Object> response = new HashMap<>();
//                 response.put("success", false);
//                 response.put("message", "Email and password are required");
//                 return ResponseEntity.badRequest().body(response);
//             }
            
//             LoginResponse response = loginService.authenticateUser(loginRequest);
            
//             if (!response.isSuccess()) {
//                 log.warn("❌ Login failed for: {}", loginRequest.getEmail());
//                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//             }
            
//             log.info("✅ Login successful for: {}", loginRequest.getEmail());
//             return ResponseEntity.ok(response);
            
//         } catch (Exception e) {
//             log.error("❌ Error during login", e);
            
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

/**
 * ✅ FIXED: Buyer Controller
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
    // ✅ GET BUYER BY ID - FIXED
    // ============================================
    @GetMapping("/{id}")
    public ResponseEntity<?> getBuyerById(@PathVariable Long id) {
        try {
            log.info("========================================");
            log.info("📥 GET BUYER BY ID: {}", id);
            log.info("========================================");
            
            Buyer buyer = buyerService.getBuyerById(id);
            
            log.info("✅ Buyer found: {}", buyer.getCompanyName());
            log.info("   Locations: {}", buyer.getLocations() != null ? buyer.getLocations().size() : 0);
            
            if (buyer.getLocations() != null) {
                buyer.getLocations().forEach(location -> {
                    log.info("     Location: {}", location.getLocationName());
                    if (location.getDepartments() != null) {
                        log.info("       Departments: {}", location.getDepartments().size());
                        location.getDepartments().forEach(dept -> {
                            log.info("         Department: {}", dept.getDepartmentName());
                            if (dept.getUsers() != null) {
                                log.info("           Users: {}", dept.getUsers().size());
                            }
                        });
                    }
                });
            }
            
            log.info("========================================");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Buyer fetched successfully");
            response.put("data", buyer);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.error("========================================");
            log.error("❌ BUYER NOT FOUND: {}", id);
            log.error("Error: {}", e.getMessage());
            log.error("========================================");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Buyer not found with ID: " + id);
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("❌ INTERNAL ERROR FETCHING BUYER: {}", id);
            log.error("========================================");
            log.error("Error Type: {}", e.getClass().getName());
            log.error("Error Message: {}", e.getMessage());
            e.printStackTrace();
            log.error("========================================");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch buyer");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============================================
    // ✅ GET ALL BUYERS - FIXED
    // ============================================
    @GetMapping
    public ResponseEntity<?> getAllBuyers() {
        try {
            log.info("========================================");
            log.info("📥 GET ALL BUYERS");
            log.info("========================================");
            
            List<Buyer> buyers = buyerService.getAllBuyers();
            
            log.info("✅ Found {} buyers", buyers.size());
            
            buyers.forEach(buyer -> {
                if (buyer.getLocations() != null) {
                    buyer.getLocations().size(); // Initialize lazy collection
                    log.info("   Buyer: {} has {} locations", 
                             buyer.getCompanyName(), 
                             buyer.getLocations().size());
                }
            });
            
            log.info("========================================");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Buyers fetched successfully");
            response.put("count", buyers.size());
            response.put("data", buyers);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("❌ ERROR FETCHING ALL BUYERS");
            log.error("========================================");
            log.error("Error: {}", e.getMessage());
            e.printStackTrace();
            log.error("========================================");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch buyers");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============================================
    // ❌ DISABLED: DIRECT BUYER CREATION
    // ============================================
    @PostMapping
    public ResponseEntity<?> createBuyer(@Valid @RequestBody Buyer buyer) {
        log.warn("========================================");
        log.warn("⚠️  DEPRECATED ENDPOINT ACCESSED");
        log.warn("========================================");
        log.warn("Attempted direct buyer creation");
        log.warn("Use: POST /api/organization-admin/{adminId}/buyer");
        log.warn("========================================");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Direct buyer creation is not allowed. " +
                               "Buyers must be created by Organization Admin via: " +
                               "POST /api/organization-admin/{adminId}/buyer");
        response.put("error", "DEPRECATED_ENDPOINT");
        response.put("correctEndpoint", "POST /api/organization-admin/{adminId}/buyer");
        response.put("documentation", "Contact your Organization Admin to create buyer companies");
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // ============================================
    // ✅ UPDATE BUYER
    // ============================================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBuyer(
            @PathVariable Long id, 
            @Valid @RequestBody Buyer buyerReq) {
        try {
            log.info("========================================");
            log.info("📥 UPDATE BUYER: {}", id);
            log.info("========================================");
            
            Buyer updated = buyerService.updateBuyer(id, buyerReq);
            
            if (updated.getLocations() != null) {
                updated.getLocations().size();
            }
            
            log.info("✅ Buyer updated: {} (ID: {})", updated.getCompanyName(), updated.getId());
            log.info("========================================");
            
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
    // ✅ DELETE BUYER
    // ============================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBuyer(@PathVariable Long id) {
        try {
            log.info("========================================");
            log.info("📥 DELETE BUYER: {}", id);
            log.info("========================================");
            
            buyerService.deleteBuyer(id);
            
            log.info("✅ Buyer deleted: ID {}", id);
            log.info("========================================");
            
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
    // ✅ BUYER USER LOGIN
    // ============================================
    @PostMapping("/login")
    public ResponseEntity<?> loginBuyer(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("========================================");
            log.info("🔐 BUYER USER LOGIN: {}", loginRequest.getEmail());
            log.info("========================================");
            
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
                log.info("========================================");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            log.info("✅ Login successful: {}", loginRequest.getEmail());
            log.info("========================================");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("❌ LOGIN ERROR");
            log.error("========================================");
            log.error("Error: {}", e.getMessage());
            e.printStackTrace();
            log.error("========================================");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ============================================
    // ✅ HEALTH CHECK
    // ============================================
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Buyer Service is running!");
        response.put("note", "Buyers can only be created by Organization Admin");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}