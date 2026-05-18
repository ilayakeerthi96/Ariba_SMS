

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
    
//     @Transactional(readOnly = true)
//     public SupplierLoginResponse authenticateSupplierUser(LoginRequest loginRequest) {
//         SupplierLoginResponse response = new SupplierLoginResponse();
        
//         log.info("========================================");
//         log.info("🔵 SUPPLIER LOGIN AUTHENTICATION START");
//         log.info("========================================");
//         log.info("📧 Email: {}", loginRequest.getEmail());
        
//         try {
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
            
//             log.info("🔍 STEP 2: Searching for supplier user...");
//             Optional<SupplierUser> userOptional = supplierUserRepository.findByEmail(loginRequest.getEmail());
//             if (!userOptional.isPresent()) {
//                 log.warn("   ❌ Supplier user not found with email: {}", loginRequest.getEmail());
//                 response.setSuccess(false);
//                 response.setMessage("Invalid email or password");
//                 return response;
//             }
//             SupplierUser user = userOptional.get();
//             log.info("   ✅ Supplier user found — ID: {}, Email: {}, Role: {}", user.getId(), user.getEmail(), user.getRole());
            
//             log.info("🗑️  STEP 3: Checking if user is deleted...");
//             if (user.getIsDeleted() != null && user.getIsDeleted()) {
//                 log.warn("   ❌ Supplier user account is deleted");
//                 response.setSuccess(false);
//                 response.setMessage("User account is deleted");
//                 return response;
//             }
//             log.info("   ✅ User is active");
            
//             log.info("🔐 STEP 4: Verifying password...");
//             boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
//             if (!passwordMatches) {
//                 log.warn("   ❌ Password does not match");
//                 response.setSuccess(false);
//                 response.setMessage("Invalid email or password");
//                 return response;
//             }
//             log.info("   ✅ Password verified successfully");
            
//             log.info("👤 STEP 5: Checking user role...");
//             if (user.getRole() != Role.ROLE_SUPPLIER) {
//                 log.warn("   ❌ User role is: {} (expected ROLE_SUPPLIER)", user.getRole());
//                 response.setSuccess(false);
//                 response.setMessage("User does not have supplier role");
//                 return response;
//             }
//             log.info("   ✅ User has ROLE_SUPPLIER");
            
//             log.info("📦 STEP 6: Accessing related data...");
//             SupplierDepartment department = null;
//             SupplierLocation location = null;
//             Supplier supplier = null;
            
//             if (user.getDepartment() != null) {
//                 department = user.getDepartment();
//                 department.getDepartmentName();
//                 log.info("   ✅ Department: {} (ID: {})", department.getDepartmentName(), department.getId());
                
//                 if (department.getLocation() != null) {
//                     location = department.getLocation();
//                     location.getLocationName();
//                     log.info("   ✅ Location: {} (ID: {})", location.getLocationName(), location.getId());
                    
//                     if (location.getSupplier() != null) {
//                         supplier = location.getSupplier();
//                         supplier.getCompanyName();
//                         log.info("   ✅ Supplier: {} (ID: {})", supplier.getCompanyName(), supplier.getId());
//                     }
//                 }
//             }
            
//             log.info("🔑 STEP 7: Generating JWT token...");
//             String token = jwtUtil.generateTokenWithId(user.getEmail(), user.getRole().toString(), user.getId());
//             log.info("   ✅ Token generated (length: {})", token.length());
            
//             log.info("📋 STEP 8: Building login response...");
//             response.setToken(token);
//             response.setUserId(user.getId());
//             response.setEmail(user.getEmail());
            
//             String firstName = (user.getFirstName() != null && !user.getFirstName().trim().isEmpty()) ? user.getFirstName().trim() : "";
//             String lastName  = (user.getLastName()  != null && !user.getLastName().trim().isEmpty())  ? user.getLastName().trim()  : "";
//             String fullName  = (firstName + " " + lastName).trim();
//             if (fullName.isEmpty()) fullName = user.getEmail().split("@")[0];
            
//             response.setFullName(fullName);
//             response.setRole(user.getRole().toString());
//             response.setSuccess(true);
//             response.setMessage("Login successful");
            
//             if (department != null) {
//                 SupplierLoginResponse.SupplierDepartmentDetails deptDetails =
//                     new SupplierLoginResponse.SupplierDepartmentDetails(
//                         department.getId(),
//                         department.getDepartmentName() != null ? department.getDepartmentName() : "N/A",
//                         department.getCategoryOfProducts() != null ? department.getCategoryOfProducts() : "",
//                         department.getDepartmentDescription() != null ? department.getDepartmentDescription() : ""
//                     );
//                 response.setDepartment(deptDetails);
//                 log.info("   ✅ Department details added");
//             }
            
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
//                 log.info("   ✅ Location details added");
//             }
            
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
//                 log.info("   ✅ Supplier details added");
//             }
            
//             log.info("========================================");
//             log.info("✅✅✅ SUPPLIER LOGIN SUCCESSFUL ✅✅✅");
//             log.info("User: {} | ID: {} | Role: {}", response.getFullName(), response.getUserId(), response.getRole());
//             log.info("========================================");
            
//             return response;
            
//         } catch (Exception e) {
//             log.error("❌ SUPPLIER LOGIN ERROR: {} — {}", e.getClass().getName(), e.getMessage());
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
import com.itti.leadcapturing.model.ApprovalStatus;
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
            // ── STEP 1: Validate input ──────────────────────────────────────
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

            // ── STEP 2: Find supplier user ──────────────────────────────────
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

            // ── STEP 3: Check soft-delete ───────────────────────────────────
            log.info("🗑️  STEP 3: Checking if user is deleted...");
            if (user.getIsDeleted() != null && user.getIsDeleted()) {
                log.warn("   ❌ Supplier user account is deleted");
                response.setSuccess(false);
                response.setMessage("User account is deleted");
                return response;
            }
            log.info("   ✅ User is active");

            // ── STEP 4: Verify password ─────────────────────────────────────
            log.info("🔐 STEP 4: Verifying password...");
            boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
            if (!passwordMatches) {
                log.warn("   ❌ Password does not match");
                response.setSuccess(false);
                response.setMessage("Invalid email or password");
                return response;
            }
            log.info("   ✅ Password verified successfully");

            // ── STEP 5: Check role ──────────────────────────────────────────
            log.info("👤 STEP 5: Checking user role...");
            if (user.getRole() != Role.ROLE_SUPPLIER) {
                log.warn("   ❌ User role is: {} (expected ROLE_SUPPLIER)", user.getRole());
                response.setSuccess(false);
                response.setMessage("User does not have supplier role");
                return response;
            }
            log.info("   ✅ User has ROLE_SUPPLIER");

            // ── STEP 6: ✅ NEW — Check supplier approval status ─────────────
            log.info("🏢 STEP 6: Checking supplier approval status...");
            SupplierDepartment department = null;
            SupplierLocation location = null;
            Supplier supplier = null;

            if (user.getDepartment() != null) {
                department = user.getDepartment();
                department.getDepartmentName(); // Force load

                if (department.getLocation() != null) {
                    location = department.getLocation();
                    location.getLocationName(); // Force load

                    if (location.getSupplier() != null) {
                        supplier = location.getSupplier();
                        supplier.getCompanyName(); // Force load

                        log.info("   📌 Supplier: {} | Approval Status: {}",
                                supplier.getCompanyName(), supplier.getApprovalStatus());

                        // ✅ BLOCK LOGIN if supplier is not APPROVED
                        if (supplier.getApprovalStatus() == null ||
                            !ApprovalStatus.APPROVED.equals(supplier.getApprovalStatus())) {

                            String statusMsg;
                            if (supplier.getApprovalStatus() == null ||
                                ApprovalStatus.PENDING.equals(supplier.getApprovalStatus())) {
                                statusMsg = "Your company account is pending approval. " +
                                            "Please wait for administrator approval before logging in.";
                            } else if (ApprovalStatus.REJECTED.equals(supplier.getApprovalStatus())) {
                                statusMsg = "Your company account has been rejected. " +
                                            "Please contact the administrator for more information.";
                            } else {
                                // HOLD or any other status
                                statusMsg = "Your company account is currently on hold. " +
                                            "Please contact the administrator.";
                            }

                            log.warn("   ❌ Supplier login BLOCKED — Status: {}", supplier.getApprovalStatus());
                            response.setSuccess(false);
                            response.setMessage(statusMsg);
                            return response;
                        }

                        log.info("   ✅ Supplier approval status is APPROVED — login allowed");
                    } else {
                        log.warn("   ⚠️ Supplier not found for this user — blocking login for safety");
                        response.setSuccess(false);
                        response.setMessage("Supplier account not properly configured. Please contact administrator.");
                        return response;
                    }
                }
            }

            // ── STEP 7: Generate JWT token ──────────────────────────────────
            log.info("🔑 STEP 7: Generating JWT token...");
            String token = jwtUtil.generateTokenWithId(user.getEmail(), user.getRole().toString(), user.getId());
            log.info("   ✅ Token generated (length: {})", token.length());

            // ── STEP 8: Build response ──────────────────────────────────────
            log.info("📋 STEP 8: Building login response...");
            response.setToken(token);
            response.setUserId(user.getId());
            response.setEmail(user.getEmail());

            String firstName = (user.getFirstName() != null && !user.getFirstName().trim().isEmpty())
                    ? user.getFirstName().trim() : "";
            String lastName  = (user.getLastName()  != null && !user.getLastName().trim().isEmpty())
                    ? user.getLastName().trim()  : "";
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