// package com.itti.leadcapturing.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import com.itti.leadcapturing.model.SupplierUser;
// import com.itti.leadcapturing.model.Role;
// import com.itti.leadcapturing.model.SupplierDepartment;
// import com.itti.leadcapturing.model.SupplierLocation;
// import com.itti.leadcapturing.model.Supplier;
// import com.itti.leadcapturing.repo.SupplierUserRepository;
// import com.itti.leadcapturing.util.JwtUtil;
// import com.itti.leadcapturing.dto.LoginRequest;
// import com.itti.leadcapturing.dto.SupplierLoginResponse;
// import lombok.extern.slf4j.Slf4j;

// import java.time.LocalDateTime;
// import java.util.Optional;

// @Service
// @Slf4j
// public class SupplierLoginService {
    
//     @Autowired
//     private SupplierUserRepository supplierUserRepository;
    
//     @Autowired
//     private PasswordEncoder passwordEncoder;
    
//     @Autowired
//     private JwtUtil jwtUtil;
    
//     /**
//      * ✅ Supplier User Authentication with Entity Graph
//      * @Transactional ensures all operations happen in same session
//      */
//     @Transactional(readOnly = true)
//     public SupplierLoginResponse authenticateSupplierUser(LoginRequest loginRequest) {
//         SupplierLoginResponse response = new SupplierLoginResponse();
        
//         log.info("========================================");
//         log.info("🔵 SUPPLIER LOGIN AUTHENTICATION START");
//         log.info("========================================");
//         log.info("📧 Email: {}", loginRequest.getEmail());
        
//         try {
//             // ✅ STEP 1: Validate Input
//             log.info("📋 STEP 1: Validating input...");
//             if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
//                 log.warn("   ❌ Email is empty");
//                 response.setSuccess(false);
//                 response.setMessage("Email is required");
//                 return response;
//             }
            
//             if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
//                 log.warn("   ❌ Password is empty");
//                 response.setSuccess(false);
//                 response.setMessage("Password is required");
//                 return response;
//             }
//             log.info("   ✅ Input validation passed");
            
//             // ✅ STEP 2: Find Supplier User
//             log.info("🔍 STEP 2: Searching for supplier user...");
//             Optional<SupplierUser> userOptional = supplierUserRepository.findByEmail(loginRequest.getEmail());
            
//             if (!userOptional.isPresent()) {
//                 log.warn("   ❌ Supplier user not found with email: {}", loginRequest.getEmail());
//                 response.setSuccess(false);
//                 response.setMessage("Invalid email or password");
//                 return response;
//             }
            
//             SupplierUser user = userOptional.get();
//             log.info("   ✅ Supplier user found");
//             log.info("      ID: {}", user.getId());
//             log.info("      First Name: {}", user.getFirstName());
//             log.info("      Last Name: {}", user.getLastName());
//             log.info("      Email: {}", user.getEmail());
//             log.info("      Role: {}", user.getRole());
//             log.info("      Is Deleted: {}", user.getIsDeleted());
            
//             // ✅ STEP 3: Check if User is Deleted
//             log.info("🗑️  STEP 3: Checking if user is deleted...");
//             if (user.getIsDeleted() != null && user.getIsDeleted()) {
//                 log.warn("   ❌ Supplier user account is deleted");
//                 response.setSuccess(false);
//                 response.setMessage("User account is deleted");
//                 return response;
//             }
//             log.info("   ✅ User is active");
            
//             // ✅ STEP 4: Verify Password
//             log.info("🔐 STEP 4: Verifying password...");
//             log.info("   Raw password length: {}", loginRequest.getPassword().length());
//             log.info("   Encoded password from DB: {}...", 
//                      user.getPassword().substring(0, Math.min(20, user.getPassword().length())));
            
//             boolean passwordMatches = passwordEncoder.matches(
//                 loginRequest.getPassword(), 
//                 user.getPassword()
//             );
            
//             if (!passwordMatches) {
//                 log.warn("   ❌ Password does not match");
//                 response.setSuccess(false);
//                 response.setMessage("Invalid email or password");
//                 return response;
//             }
//             log.info("   ✅ Password verified successfully");
            
//             // ✅ STEP 5: Check User Role
//             log.info("👤 STEP 5: Checking user role...");
//             if (user.getRole() != Role.ROLE_SUPPLIER) {
//                 log.warn("   ❌ User role is: {} (expected ROLE_SUPPLIER)", user.getRole());
//                 response.setSuccess(false);
//                 response.setMessage("User does not have supplier role");
//                 return response;
//             }
//             log.info("   ✅ User has ROLE_SUPPLIER");
            
//             // ✅ STEP 6: Access Related Data (Load within transaction)
//             log.info("📦 STEP 6: Accessing related data...");
//             SupplierDepartment department = null;
//             SupplierLocation location = null;
//             Supplier supplier = null;
            
//             // Get department
//             if (user.getDepartment() != null) {
//                 department = user.getDepartment();
//                 department.getDepartmentName(); // Initialize
//                 log.info("   ✅ Department: {} (ID: {})", 
//                          department.getDepartmentName(), department.getId());
                
//                 // Get location from department
//                 if (department.getLocation() != null) {
//                     location = department.getLocation();
//                     location.getLocationName(); // Initialize
//                     log.info("   ✅ Location: {} (ID: {})", 
//                              location.getLocationName(), location.getId());
                    
//                     // Get supplier from location
//                     if (location.getSupplier() != null) {
//                         supplier = location.getSupplier();
//                         supplier.getCompanyName(); // Initialize
//                         log.info("   ✅ Supplier: {} (ID: {})", 
//                                  supplier.getCompanyName(), supplier.getId());
//                     }
//                 }
//             }
            
//             // ✅ STEP 7: Generate Token with user ID
//             log.info("🔑 STEP 7: Generating JWT token...");
//             String token = jwtUtil.generateTokenWithId(
//                 user.getEmail(),
//                 user.getRole().toString(),
//                 user.getId()
//             );
//             log.info("   ✅ Token generated (length: {})", token.length());
//             log.info("   Token (first 20 chars): {}...", 
//                      token.substring(0, Math.min(20, token.length())));
            
//             // ✅ STEP 8: Build Response (NULL SAFE)
//             log.info("📋 STEP 8: Building login response...");
//             response.setToken(token);
//             response.setUserId(user.getId());
//             response.setEmail(user.getEmail());
            
//             // Build full name safely
//             String firstName = (user.getFirstName() != null && !user.getFirstName().trim().isEmpty()) 
//                 ? user.getFirstName().trim() : "";
//             String lastName = (user.getLastName() != null && !user.getLastName().trim().isEmpty()) 
//                 ? user.getLastName().trim() : "";
            
//             String fullName = (firstName + " " + lastName).trim();
//             if (fullName.isEmpty()) {
//                 fullName = user.getEmail().split("@")[0];
//             }
            
//             response.setFullName(fullName);
//             response.setRole(user.getRole().toString());
//             response.setSuccess(true);
//             response.setMessage("Login successful");
            
//             // Add Department (NULL safe)
//             if (department != null) {
//                 SupplierLoginResponse.SupplierDepartmentDetails deptDetails = 
//                     new SupplierLoginResponse.SupplierDepartmentDetails(
//                         department.getId(),
//                         department.getDepartmentName() != null ? department.getDepartmentName() : "N/A",
//                         department.getCategoryOfProducts() != null ? department.getCategoryOfProducts() : "",
//                         department.getDepartmentDescription() != null ? department.getDepartmentDescription() : ""
//                     );
//                 response.setDepartment(deptDetails);
//                 log.info("   ✅ Department details added to response");
//             } else {
//                 log.warn("   ⚠️  No department information available");
//             }
            
//             // Add Location (NULL safe)
//             if (location != null) {
//                 SupplierLoginResponse.SupplierLocationDetails locationDetails = 
//                     new SupplierLoginResponse.SupplierLocationDetails(
//                         location.getId(),
//                         location.getLocationName() != null ? location.getLocationName() : "",
//                         location.getCity() != null ? location.getCity() : "",
//                         location.getState() != null ? location.getState() : "",
//                         location.getPostalCode() != null ? location.getPostalCode() : "",
//                         location.getCountry() != null ? location.getCountry() : "IN",
//                         location.getAddressLine1() != null ? location.getAddressLine1() : "",
//                         location.getAddressLine2() != null ? location.getAddressLine2() : ""
//                     );
//                 response.setLocation(locationDetails);
//                 log.info("   ✅ Location details added to response");
//             } else {
//                 log.warn("   ⚠️  No location information available");
//             }
            
//             // Add Supplier (NULL safe)
//             if (supplier != null) {
//                 SupplierLoginResponse.SupplierDetails supplierDetails = 
//                     new SupplierLoginResponse.SupplierDetails(
//                         supplier.getId(),
//                         supplier.getCompanyName() != null ? supplier.getCompanyName() : "",
//                         "",
//                         supplier.getContactPersonEmail() != null ? supplier.getContactPersonEmail() : "",
//                         supplier.getContactPersonPhone() != null ? supplier.getContactPersonPhone() : "",
//                         supplier.getWebsite() != null ? supplier.getWebsite() : "",
//                         supplier.getIndustrySector() != null ? supplier.getIndustrySector() : "",
//                         supplier.getCompanyType() != null ? supplier.getCompanyType() : ""
//                     );
//                 response.setSupplier(supplierDetails);
//                 log.info("   ✅ Supplier details added to response");
//             } else {
//                 log.warn("   ⚠️  No supplier information available");
//             }
            
//             log.info("========================================");
//             log.info("✅✅✅ SUPPLIER LOGIN SUCCESSFUL ✅✅✅");
//             log.info("========================================");
//             log.info("User: {} ({})", response.getFullName(), response.getEmail());
//             log.info("User ID: {}", response.getUserId());
//             log.info("Role: {}", response.getRole());
//             log.info("Department: {}", department != null ? department.getDepartmentName() : "N/A");
//             log.info("Location: {}", location != null ? location.getLocationName() : "N/A");
//             log.info("Supplier: {}", supplier != null ? supplier.getCompanyName() : "N/A");
//             log.info("Token: {}...", token.substring(0, Math.min(30, token.length())));
//             log.info("========================================");
            
//             return response;
            
//         } catch (Exception e) {
//             log.error("========================================");
//             log.error("❌ SUPPLIER LOGIN ERROR");
//             log.error("========================================");
//             log.error("Exception: {}", e.getClass().getName());
//             log.error("Message: {}", e.getMessage());
//             e.printStackTrace();
//             response.setSuccess(false);
//             response.setMessage("Login error: " + e.getMessage());
//             return response;
//         }
//     }
// }

package com.itti.leadcapturing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.itti.leadcapturing.model.SupplierUser;
import com.itti.leadcapturing.model.Role;
import com.itti.leadcapturing.model.SupplierDepartment;
import com.itti.leadcapturing.model.SupplierLocation;
import com.itti.leadcapturing.model.Supplier;
import com.itti.leadcapturing.repo.SupplierUserRepository;
import com.itti.leadcapturing.util.JwtUtil;
import com.itti.leadcapturing.dto.LoginRequest;
import com.itti.leadcapturing.dto.SupplierLoginResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Service
@Slf4j
public class SupplierLoginService {
    
    @Autowired
    private SupplierUserRepository supplierUserRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Transactional(readOnly = true)
    public SupplierLoginResponse authenticateSupplierUser(LoginRequest loginRequest) {
        SupplierLoginResponse response = new SupplierLoginResponse();
        
        log.info("========================================");
        log.info("🔵 SUPPLIER LOGIN AUTHENTICATION START");
        log.info("========================================");
        log.info("📧 Email: {}", loginRequest.getEmail());
        
        try {
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
            
            log.info("🔍 STEP 2: Searching for supplier user...");
            Optional<SupplierUser> userOptional = supplierUserRepository.findByEmail(loginRequest.getEmail());
            if (!userOptional.isPresent()) {
                log.warn("   ❌ Supplier user not found with email: {}", loginRequest.getEmail());
                response.setSuccess(false);
                response.setMessage("Invalid email or password");
                return response;
            }
            SupplierUser user = userOptional.get();
            log.info("   ✅ Supplier user found — ID: {}, Email: {}, Role: {}", user.getId(), user.getEmail(), user.getRole());
            
            log.info("🗑️  STEP 3: Checking if user is deleted...");
            if (user.getIsDeleted() != null && user.getIsDeleted()) {
                log.warn("   ❌ Supplier user account is deleted");
                response.setSuccess(false);
                response.setMessage("User account is deleted");
                return response;
            }
            log.info("   ✅ User is active");
            
            log.info("🔐 STEP 4: Verifying password...");
            boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
            if (!passwordMatches) {
                log.warn("   ❌ Password does not match");
                response.setSuccess(false);
                response.setMessage("Invalid email or password");
                return response;
            }
            log.info("   ✅ Password verified successfully");
            
            log.info("👤 STEP 5: Checking user role...");
            if (user.getRole() != Role.ROLE_SUPPLIER) {
                log.warn("   ❌ User role is: {} (expected ROLE_SUPPLIER)", user.getRole());
                response.setSuccess(false);
                response.setMessage("User does not have supplier role");
                return response;
            }
            log.info("   ✅ User has ROLE_SUPPLIER");
            
            log.info("📦 STEP 6: Accessing related data...");
            SupplierDepartment department = null;
            SupplierLocation location = null;
            Supplier supplier = null;
            
            if (user.getDepartment() != null) {
                department = user.getDepartment();
                department.getDepartmentName();
                log.info("   ✅ Department: {} (ID: {})", department.getDepartmentName(), department.getId());
                
                if (department.getLocation() != null) {
                    location = department.getLocation();
                    location.getLocationName();
                    log.info("   ✅ Location: {} (ID: {})", location.getLocationName(), location.getId());
                    
                    if (location.getSupplier() != null) {
                        supplier = location.getSupplier();
                        supplier.getCompanyName();
                        log.info("   ✅ Supplier: {} (ID: {})", supplier.getCompanyName(), supplier.getId());
                    }
                }
            }
            
            log.info("🔑 STEP 7: Generating JWT token...");
            String token = jwtUtil.generateTokenWithId(user.getEmail(), user.getRole().toString(), user.getId());
            log.info("   ✅ Token generated (length: {})", token.length());
            
            log.info("📋 STEP 8: Building login response...");
            response.setToken(token);
            response.setUserId(user.getId());
            response.setEmail(user.getEmail());
            
            String firstName = (user.getFirstName() != null && !user.getFirstName().trim().isEmpty()) ? user.getFirstName().trim() : "";
            String lastName  = (user.getLastName()  != null && !user.getLastName().trim().isEmpty())  ? user.getLastName().trim()  : "";
            String fullName  = (firstName + " " + lastName).trim();
            if (fullName.isEmpty()) fullName = user.getEmail().split("@")[0];
            
            response.setFullName(fullName);
            response.setRole(user.getRole().toString());
            response.setSuccess(true);
            response.setMessage("Login successful");
            
            if (department != null) {
                SupplierLoginResponse.SupplierDepartmentDetails deptDetails =
                    new SupplierLoginResponse.SupplierDepartmentDetails(
                        department.getId(),
                        department.getDepartmentName() != null ? department.getDepartmentName() : "N/A",
                        department.getCategoryOfProducts() != null ? department.getCategoryOfProducts() : "",
                        department.getDepartmentDescription() != null ? department.getDepartmentDescription() : ""
                    );
                response.setDepartment(deptDetails);
                log.info("   ✅ Department details added");
            }
            
            if (location != null) {
                SupplierLoginResponse.SupplierLocationDetails locationDetails =
                    new SupplierLoginResponse.SupplierLocationDetails(
                        location.getId(),
                        location.getLocationName() != null ? location.getLocationName() : "",
                        location.getCity() != null ? location.getCity() : "",
                        location.getState() != null ? location.getState() : "",
                        location.getPostalCode() != null ? location.getPostalCode() : "",
                        location.getCountry() != null ? location.getCountry() : "IN",
                        location.getAddressLine1() != null ? location.getAddressLine1() : "",
                        location.getAddressLine2() != null ? location.getAddressLine2() : ""
                    );
                response.setLocation(locationDetails);
                log.info("   ✅ Location details added");
            }
            
            if (supplier != null) {
                SupplierLoginResponse.SupplierDetails supplierDetails =
                    new SupplierLoginResponse.SupplierDetails(
                        supplier.getId(),
                        supplier.getCompanyName() != null ? supplier.getCompanyName() : "",
                        "",
                        supplier.getContactPersonEmail() != null ? supplier.getContactPersonEmail() : "",
                        supplier.getContactPersonPhone() != null ? supplier.getContactPersonPhone() : "",
                        supplier.getWebsite() != null ? supplier.getWebsite() : "",
                        supplier.getIndustrySector() != null ? supplier.getIndustrySector() : "",
                        supplier.getCompanyType() != null ? supplier.getCompanyType() : ""
                    );
                response.setSupplier(supplierDetails);
                log.info("   ✅ Supplier details added");
            }
            
            log.info("========================================");
            log.info("✅✅✅ SUPPLIER LOGIN SUCCESSFUL ✅✅✅");
            log.info("User: {} | ID: {} | Role: {}", response.getFullName(), response.getUserId(), response.getRole());
            log.info("========================================");
            
            return response;
            
        } catch (Exception e) {
            log.error("❌ SUPPLIER LOGIN ERROR: {} — {}", e.getClass().getName(), e.getMessage());
            e.printStackTrace();
            response.setSuccess(false);
            response.setMessage("Login error: " + e.getMessage());
            return response;
        }
    }
}