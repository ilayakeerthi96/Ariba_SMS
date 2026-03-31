package com.itti.leadcapturing.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.itti.leadcapturing.dto.LoginRequest;
import com.itti.leadcapturing.dto.LoginResponse;
import com.itti.leadcapturing.service.LoginService;

@RestController
@RequestMapping("/leadcapture/api")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000", "http://localhost:8080"})
public class LoginController {
    
    @Autowired
    private LoginService loginService;
    
    /**
     * ✅ Buyer Login Endpoint
     * POST: /leadcapture/api/buyer/login
     * 
     * Request Body:
     * {
     *   "email": "delluser@gmail.com",
     *   "password": "admin123"
     * }
     */
@PostMapping("/buyer/login")
public ResponseEntity<?> buyerLogin(@RequestBody LoginRequest loginRequest) {
    try {
        LoginResponse response = loginService.authenticateUser(loginRequest);
        
        // ✅ Returns 200 OK with success data
        if (response.isSuccess()) {
            System.out.println("📤 Returning 200 OK with success response");
            return ResponseEntity.ok(response);
        }
        
        // ✅ Returns 401 with failure data
        System.out.println("📤 Returning 401 with failure response");
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(response);
        
    } catch (Exception e) {
        System.out.println("❌ Controller exception: " + e.getMessage());
        LoginResponse errorResponse = new LoginResponse();
        errorResponse.setSuccess(false);
        errorResponse.setMessage("Server error: " + e.getMessage());
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse);
    }
}
    /**
     * ✅ Health check endpoint (for testing)
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Backend is running on port 9092");
    }
}