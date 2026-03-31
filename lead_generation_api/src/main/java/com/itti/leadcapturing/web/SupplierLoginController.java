package com.itti.leadcapturing.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.itti.leadcapturing.dto.LoginRequest;
import com.itti.leadcapturing.dto.SupplierLoginResponse;
import com.itti.leadcapturing.service.SupplierLoginService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * ✅ Supplier Login Controller
 * Endpoint: POST /leadcapture/api/supplier/login
 */
@RestController
@RequestMapping("/api/supplier")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000", "http://localhost:8080"})
@Slf4j
public class SupplierLoginController {
    
    @Autowired
    private SupplierLoginService supplierLoginService;
    
    /**
     * ✅ Supplier User Login Endpoint
     * POST: /leadcapture/api/supplier/login
     * 
     * Request Body:
     * {
     *   "email": "supplier@example.com",
     *   "password": "password123"
     * }
     * 
     * Response on Success (200 OK):
     * {
     *   "token": "eyJhbGciOiJIUzUxMiJ9...",
     *   "userId": 1,
     *   "email": "supplier@example.com",
     *   "fullName": "John Doe",
     *   "role": "ROLE_SUPPLIER",
     *   "success": true,
     *   "message": "Login successful",
     *   "department": {
     *     "id": 1,
     *     "name": "IT Department",
     *     "categoryOfProducts": "Electronics",
     *     "description": "IT Services"
     *   },
     *   "location": {
     *     "id": 1,
     *     "locationName": "Head Office",
     *     "city": "Mumbai",
     *     "state": "Maharashtra",
     *     "postalCode": "400001",
     *     "country": "IN"
     *   },
     *   "supplier": {
     *     "id": 1,
     *     "name": "ABC Suppliers Ltd",
     *     "email": "contact@abc.com",
     *     "phone": "9876543210",
     *     "website": "www.abc.com",
     *     "industrySector": "IT",
     *     "companyType": "Manufacturing"
     *   }
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<?> supplierLogin(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("========================================");
            log.info("🔐 SUPPLIER USER LOGIN REQUEST");
            log.info("========================================");
            log.info("Email: {}", loginRequest.getEmail());
            
            // Validate request
            if (loginRequest == null || loginRequest.getEmail() == null || 
                loginRequest.getPassword() == null) {
                
                log.warn("❌ Invalid login request - missing email or password");
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Email and password are required");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Authenticate supplier user
            SupplierLoginResponse response = supplierLoginService.authenticateSupplierUser(loginRequest);
            
            // Check authentication result
            if (!response.isSuccess()) {
                log.warn("========================================");
                log.warn("❌ SUPPLIER LOGIN FAILED");
                log.warn("========================================");
                log.warn("Email: {}", loginRequest.getEmail());
                log.warn("Reason: {}", response.getMessage());
                log.warn("========================================");
                
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
            }
            
            log.info("========================================");
            log.info("✅ SUPPLIER LOGIN SUCCESSFUL");
            log.info("========================================");
            log.info("User: {} ({})", response.getFullName(), response.getEmail());
            log.info("User ID: {}", response.getUserId());
            log.info("Role: {}", response.getRole());
            log.info("========================================");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("❌ SUPPLIER LOGIN CONTROLLER EXCEPTION");
            log.error("========================================");
            log.error("Exception: {}", e.getClass().getName());
            log.error("Message: {}", e.getMessage());
            e.printStackTrace();
            log.error("========================================");
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Server error: " + e.getMessage());
            
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
        }
    }
    
    /**
     * ✅ Health check endpoint (for testing)
     * GET: /leadcapture/api/supplier/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Supplier Service is running!");
        response.put("service", "Supplier Login Service");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}