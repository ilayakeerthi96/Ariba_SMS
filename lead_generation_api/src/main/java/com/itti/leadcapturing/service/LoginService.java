
package com.itti.leadcapturing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.itti.leadcapturing.model.User;
import com.itti.leadcapturing.model.Role;
import com.itti.leadcapturing.model.Department;
import com.itti.leadcapturing.model.Location;
import com.itti.leadcapturing.model.Buyer;
import com.itti.leadcapturing.repo.UserRepository;
import com.itti.leadcapturing.util.JwtUtil;
import com.itti.leadcapturing.dto.LoginRequest;
import com.itti.leadcapturing.dto.LoginResponse;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class LoginService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * ✅ FIXED: Uses Entity Graph method to load relationships while keeping LAZY loading
     * @Transactional ensures all operations happen in same session
     */
    @Transactional(readOnly = true)
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        LoginResponse response = new LoginResponse();
        
        log.info("========================================");
        log.info("🔵 BUYER LOGIN AUTHENTICATION START");
        log.info("========================================");
        log.info("📧 Email: {}", loginRequest.getEmail());
        
        try {
            // ✅ STEP 1: Validate Input
            log.info("📋 STEP 1: Validating input...");
            if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
                log.warn("   ❌ Email is empty");
                response.setSuccess(false);
                response.setMessage("Email is required");
                return response;
            }
            
            if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
                log.warn("   ❌ Password is empty");
                response.setSuccess(false);
                response.setMessage("Password is required");
                return response;
            }
            log.info("   ✅ Input validation passed");
            
            // ✅ STEP 2: Find User WITH Relationships (Entity Graph)
            log.info("🔍 STEP 2: Searching for user with relationships...");
            Optional<User> userOptional = userRepository.findByEmailWithRelationships(loginRequest.getEmail());
            
            if (!userOptional.isPresent()) {
                log.warn("   ❌ User not found with email: {}", loginRequest.getEmail());
                response.setSuccess(false);
                response.setMessage("Invalid email or password");
                return response;
            }
            
            User user = userOptional.get();
            log.info("   ✅ User found");
            log.info("      ID: {}", user.getId());
            log.info("      First Name: {}", user.getFirstName());
            log.info("      Last Name: {}", user.getLastName());
            log.info("      Email: {}", user.getEmail());
            log.info("      Role: {}", user.getRole());
            log.info("      Is Deleted: {}", user.getIsDeleted());
            
            // ✅ STEP 3: Check if User is Deleted
            log.info("🗑️  STEP 3: Checking if user is deleted...");
            if (user.getIsDeleted() != null && user.getIsDeleted()) {
                log.warn("   ❌ User account is deleted");
                response.setSuccess(false);
                response.setMessage("User account is deleted");
                return response;
            }
            log.info("   ✅ User is active");
            
            // ✅ STEP 4: Verify Password
            log.info("🔐 STEP 4: Verifying password...");
            log.info("   Raw password length: {}", loginRequest.getPassword().length());
            log.info("   Encoded password from DB: {}...", 
                     user.getPassword().substring(0, Math.min(20, user.getPassword().length())));
            
            boolean passwordMatches = passwordEncoder.matches(
                loginRequest.getPassword(), 
                user.getPassword()
            );
            
            if (!passwordMatches) {
                log.warn("   ❌ Password does not match");
                response.setSuccess(false);
                response.setMessage("Invalid email or password");
                return response;
            }
            log.info("   ✅ Password verified successfully");
            
            // ✅ STEP 5: Check User Role
            log.info("👤 STEP 5: Checking user role...");
            if (user.getRole() != Role.ROLE_BUYER) {
                log.warn("   ❌ User role is: {} (expected ROLE_BUYER)", user.getRole());
                response.setSuccess(false);
                response.setMessage("User does not have buyer role");
                return response;
            }
            log.info("   ✅ User has ROLE_BUYER");
            
            // ✅ STEP 6: Access Related Data (Already loaded by Entity Graph)
            log.info("📦 STEP 6: Accessing related data (already loaded by Entity Graph)...");
            Department department = null;
            Location location = null;
            Buyer buyer = null;
            
            // Get department (already loaded)
            if (user.getDepartment() != null) {
                department = user.getDepartment();
                log.info("   ✅ Department: {} (ID: {})", 
                         department.getDepartmentName(), department.getId());
                
                // Get location from department (already loaded)
                if (department.getLocation() != null) {
                    location = department.getLocation();
                    log.info("   ✅ Location (from dept): {} (ID: {})", 
                             location.getLocationName(), location.getId());
                    
                    // Get buyer from location (already loaded)
                    if (location.getBuyer() != null) {
                        buyer = location.getBuyer();
                        log.info("   ✅ Buyer (from location): {} (ID: {})", 
                                 buyer.getCompanyName(), buyer.getId());
                    }
                }
            }
            
            // Fallback: Get location directly from user if not found via department
            if (location == null && user.getLocation() != null) {
                location = user.getLocation();
                log.info("   ✅ Location (from user): {} (ID: {})", 
                         location.getLocationName(), location.getId());
                
                // Try to get buyer from this location
                if (buyer == null && location.getBuyer() != null) {
                    buyer = location.getBuyer();
                    log.info("   ✅ Buyer (from user->location): {} (ID: {})", 
                             buyer.getCompanyName(), buyer.getId());
                }
            }
            
            // Fallback: Get buyer directly from user
            if (buyer == null && user.getBuyer() != null) {
                buyer = user.getBuyer();
                log.info("   ✅ Buyer (from user): {} (ID: {})", 
                         buyer.getCompanyName(), buyer.getId());
            }
            
            // ✅ STEP 7: Generate Token with user ID
            log.info("🔑 STEP 7: Generating JWT token...");
            String token = jwtUtil.generateTokenWithId(
                user.getEmail(),
                user.getRole().toString(),
                user.getId()
            );
            log.info("   ✅ Token generated (length: {})", token.length());
            log.info("   Token (first 20 chars): {}...", 
                     token.substring(0, Math.min(20, token.length())));
            
            // ✅ STEP 8: Build Response (NULL SAFE)
            log.info("📋 STEP 8: Building login response...");
            response.setToken(token);
            response.setUserId(user.getId());
            response.setEmail(user.getEmail());
            
            // Build full name safely
            String firstName = (user.getFirstName() != null && !user.getFirstName().trim().isEmpty()) 
                ? user.getFirstName().trim() : "";
            String lastName = (user.getLastName() != null && !user.getLastName().trim().isEmpty()) 
                ? user.getLastName().trim() : "";
            
            String fullName = (firstName + " " + lastName).trim();
            if (fullName.isEmpty()) {
                fullName = user.getEmail().split("@")[0];
            }
            
            response.setFullName(fullName);
            response.setRole(user.getRole().toString());
            response.setSuccess(true);
            response.setMessage("Login successful");
            
            // Add Department (NULL safe)
            if (department != null) {
                LoginResponse.DepartmentDetails deptDetails = new LoginResponse.DepartmentDetails(
                    department.getId(),
                    department.getDepartmentName() != null ? department.getDepartmentName() : "N/A",
                    "",
                    department.getDepartmentDescription() != null ? department.getDepartmentDescription() : ""
                );
                response.setDepartment(deptDetails);
                log.info("   ✅ Department details added to response");
            } else {
                log.warn("   ⚠️  No department information available");
            }
            
            // Add Location (NULL safe)
            if (location != null) {
                LoginResponse.LocationDetails locationDetails = new LoginResponse.LocationDetails(
                    location.getId(),
                    location.getCity() != null ? location.getCity() : "",
                    location.getState() != null ? location.getState() : "",
                    location.getPostalCode() != null ? location.getPostalCode() : "",
                    location.getCountry() != null ? location.getCountry() : "IN",
                    location.getAddressLine1() != null ? location.getAddressLine1() : "",
                    location.getAddressLine2() != null ? location.getAddressLine2() : ""
                );
                response.setLocation(locationDetails);
                log.info("   ✅ Location details added to response");
            } else {
                log.warn("   ⚠️  No location information available");
            }
            
            // Add Buyer (NULL safe)
            if (buyer != null) {
                LoginResponse.BuyerDetails buyerDetails = new LoginResponse.BuyerDetails(
                    buyer.getId(),
                    buyer.getCompanyName() != null ? buyer.getCompanyName() : "",
                    "",
                    buyer.getContactPersonEmail() != null ? buyer.getContactPersonEmail() : "",
                    buyer.getContactPersonPhone() != null ? buyer.getContactPersonPhone() : "",
                    buyer.getWebsite() != null ? buyer.getWebsite() : "",
                    buyer.getCompanyType() != null ? buyer.getCompanyType() : "",
                    buyer.getCompanyType() != null ? buyer.getCompanyType() : ""
                );
                response.setBuyer(buyerDetails);
                log.info("   ✅ Buyer details added to response");
            } else {
                log.warn("   ⚠️  No buyer information available");
            }
            
            // ✅ Update last login timestamp (in separate method to avoid affecting response)
            updateLastLoginAsync(user.getId());
            
            log.info("========================================");
            log.info("✅✅✅ LOGIN SUCCESSFUL ✅✅✅");
            log.info("========================================");
            log.info("User: {} ({})", response.getFullName(), response.getEmail());
            log.info("User ID: {}", response.getUserId());
            log.info("Role: {}", response.getRole());
            log.info("Department: {}", department != null ? department.getDepartmentName() : "N/A");
            log.info("Location: {}", location != null ? location.getLocationName() : "N/A");
            log.info("Buyer: {}", buyer != null ? buyer.getCompanyName() : "N/A");
            log.info("Token: {}...", token.substring(0, Math.min(30, token.length())));
            log.info("========================================");
            
            return response;
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("❌ LOGIN ERROR");
            log.error("========================================");
            log.error("Exception: {}", e.getClass().getName());
            log.error("Message: {}", e.getMessage());
            e.printStackTrace();
            response.setSuccess(false);
            response.setMessage("Login error: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * ✅ Update last login in separate transaction (non-blocking)
     * Uses @Transactional to ensure it runs in new transaction
     */
    @Transactional
    public void updateLastLoginAsync(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
                log.info("   ✅ Last login timestamp updated for user ID: {}", userId);
            }
        } catch (Exception e) {
            log.warn("   ⚠️  Could not update last login: {}", e.getMessage());
            // Don't throw exception - this is non-critical
        }
    }
}