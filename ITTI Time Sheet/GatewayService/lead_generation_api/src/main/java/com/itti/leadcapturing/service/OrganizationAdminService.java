
package com.itti.leadcapturing.service;

import com.itti.leadcapturing.dto.*;
import com.itti.leadcapturing.model.OrganizationAdmin;
import com.itti.leadcapturing.repo.OrganizationAdminRepository;
import com.itti.leadcapturing.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationAdminService {

    private final OrganizationAdminRepository organizationAdminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ============================================
    // 1. CREATE ORGANIZATION ADMIN
    // ============================================
    @Transactional
    public ApiResponse<OrganizationAdminResponse> createOrganizationAdmin(
            OrganizationAdminCreateRequest request, 
            Long createdByAdminId) {
        try {
            log.info("🔵 Creating OrganizationAdmin: {} for company: {}", 
                     request.getEmail(), request.getCompanyName());
            
            if (organizationAdminRepository.existsByEmail(request.getEmail())) {
                log.warn("❌ Email already exists: {}", request.getEmail());
                return ApiResponse.error("Creation failed", "Email already exists");
            }

            OrganizationAdmin admin = new OrganizationAdmin();
            admin.setEmail(request.getEmail());
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
            admin.setFullName(request.getFullName());
            admin.setPhone(request.getPhone());
            admin.setCompanyName(request.getCompanyName());
            admin.setIsActive(true);
            admin.setMustChangePassword(true);

            if (createdByAdminId != null) {
                OrganizationAdmin creator = organizationAdminRepository.findById(createdByAdminId)
                        .orElse(null);
                admin.setCreatedBy(creator);
            }

            OrganizationAdmin savedAdmin = organizationAdminRepository.save(admin);
            log.info("✅ OrganizationAdmin created: {} (ID: {})", 
                     savedAdmin.getEmail(), savedAdmin.getId());

            OrganizationAdminResponse response = buildAdminResponse(savedAdmin, null);
            return ApiResponse.success("Organization Admin created successfully", response);

        } catch (Exception e) {
            log.error("❌ Error creating OrganizationAdmin", e);
            return ApiResponse.error("Creation failed", e.getMessage());
        }
    }


    /**
 * ✅ NEW: Get Organization Admin by ID (returns entity directly, not wrapped)
 * Used by buyer creation to get the admin entity
 */
public OrganizationAdmin getOrganizationAdminByIdDirect(Long id) {
    log.info("📥 Fetching Organization Admin by ID: {}", id);
    
    return organizationAdminRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Organization Admin not found with ID: " + id));
}
    // ============================================
    // 2. ✅ FIXED: LOGIN ORGANIZATION ADMIN
    // ============================================
    @Transactional
    public ApiResponse<OrganizationAdminResponse> loginOrganizationAdmin(
            OrganizationAdminLoginRequest request) {
        try {
            log.info("=".repeat(80));
            log.info("🔐 OrganizationAdmin login attempt: {}", request.getEmail());

            // Step 1: Find user by email
            OrganizationAdmin admin = organizationAdminRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid email or password"));

            log.info("  [✓] Found admin: {} (ID: {})", admin.getFullName(), admin.getId());
            log.info("  [✓] Company: {}", admin.getCompanyName());
            log.info("  [✓] Is Active: {}", admin.getIsActive());

            // Step 2: Check if active
            if (!admin.getIsActive()) {
                log.warn("❌ Account inactive: {}", request.getEmail());
                return ApiResponse.error("Login failed", "Account is inactive");
            }

            // Step 3: Verify password
            if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                log.warn("❌ Invalid password: {}", request.getEmail());
                return ApiResponse.error("Login failed", "Invalid email or password");
            }

            log.info("  [✓] Password verified");

            // Step 4: Update last login
            admin.setLastLogin(LocalDateTime.now());
            organizationAdminRepository.save(admin);

            log.info("  [✓] Last login updated");

            // Step 5: Generate JWT token with user ID
            String token = jwtUtil.generateTokenWithId(
                admin.getEmail(), 
                "ORGANIZATION_ADMIN", 
                admin.getId()
            );

            log.info("  [✓] Token generated");
            log.info("  [✓] Must change password: {}", admin.getMustChangePassword());

            // Step 6: Build response with CORRECT user data
            OrganizationAdminResponse response = buildAdminResponse(admin, token);

            log.info("=".repeat(80));
            log.info("✅ LOGIN SUCCESSFUL");
            log.info("  User ID: {}", response.getId());
            log.info("  Full Name: {}", response.getFullName());
            log.info("  Email: {}", response.getEmail());
            log.info("  Company: {}", response.getCompanyName());
            log.info("  Token: {}...", token.substring(0, Math.min(20, token.length())));
            log.info("=".repeat(80));

            return ApiResponse.success("Login successful", response);

        } catch (Exception e) {
            log.error("❌ Login error", e);
            e.printStackTrace();
            return ApiResponse.error("Login failed", e.getMessage());
        }
    }

    // ============================================
    // 3. ✅ CHANGE PASSWORD
    // ============================================
    @Transactional
    public ApiResponse<String> changePassword(Long adminId, ChangePasswordRequest request) {
        try {
            log.info("🔐 Change password request for admin ID: {}", adminId);

            // Validate new password and confirm password match
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                log.warn("❌ Passwords do not match for admin ID: {}", adminId);
                return ApiResponse.error("Password change failed", "New password and confirm password do not match");
            }

            // Find admin
            OrganizationAdmin admin = organizationAdminRepository.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Organization Admin not found"));

            // Verify current password
            if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
                log.warn("❌ Invalid current password for admin: {}", admin.getEmail());
                return ApiResponse.error("Password change failed", "Current password is incorrect");
            }

            // Check if new password is same as old password
            if (passwordEncoder.matches(request.getNewPassword(), admin.getPassword())) {
                log.warn("❌ New password same as old for admin: {}", admin.getEmail());
                return ApiResponse.error("Password change failed", "New password must be different from current password");
            }

            // Update password
            admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
            admin.setMustChangePassword(false);
            admin.setPasswordChangedAt(LocalDateTime.now());
            organizationAdminRepository.save(admin);

            log.info("✅ Password changed successfully for: {}", admin.getEmail());
            return ApiResponse.success("Password changed successfully", null);

        } catch (Exception e) {
            log.error("❌ Error changing password for admin ID: {}", adminId, e);
            return ApiResponse.error("Password change failed", e.getMessage());
        }
    }

    // ============================================
    // 4. GET BY ID
    // ============================================
    public ApiResponse<OrganizationAdminResponse> getOrganizationAdminById(Long id) {
        try {
            log.info("📥 Fetching OrganizationAdmin by ID: {}", id);
            
            OrganizationAdmin admin = organizationAdminRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Organization Admin not found"));

            log.info("  [✓] Found: {} (ID: {})", admin.getFullName(), admin.getId());

            OrganizationAdminResponse response = buildAdminResponse(admin, null);
            return ApiResponse.success("Organization Admin retrieved successfully", response);

        } catch (Exception e) {
            log.error("❌ Error getting admin by ID: {}", id, e);
            return ApiResponse.error("Failed to retrieve admin", e.getMessage());
        }
    }

    // ============================================
    // 5. GET BY COMPANY
    // ============================================
    public ApiResponse<List<OrganizationAdminResponse>> getAdminsByCompany(String companyName) {
        try {
            List<OrganizationAdmin> admins = organizationAdminRepository
                    .findByCompanyNameAndIsActive(companyName, true);
            
            List<OrganizationAdminResponse> responses = admins.stream()
                    .map(admin -> buildAdminResponse(admin, null))
                    .collect(Collectors.toList());

            return ApiResponse.success("Admins retrieved successfully", responses);

        } catch (Exception e) {
            log.error("❌ Error getting admins by company: {}", companyName, e);
            return ApiResponse.error("Failed to retrieve admins", e.getMessage());
        }
    }

    // ============================================
    // 6. GET ALL
    // ============================================
    public ApiResponse<List<OrganizationAdminResponse>> getAllOrganizationAdmins() {
        try {
            List<OrganizationAdmin> admins = organizationAdminRepository.findAll();
            
            List<OrganizationAdminResponse> responses = admins.stream()
                    .map(admin -> buildAdminResponse(admin, null))
                    .collect(Collectors.toList());

            return ApiResponse.success("All admins retrieved successfully", responses);

        } catch (Exception e) {
            log.error("❌ Error getting all admins", e);
            return ApiResponse.error("Failed to retrieve admins", e.getMessage());
        }
    }

    // ============================================
    // 7. UPDATE
    // ============================================
    @Transactional
    public ApiResponse<OrganizationAdminResponse> updateOrganizationAdmin(
            Long id, 
            OrganizationAdminUpdateRequest request) {
        try {
            OrganizationAdmin admin = organizationAdminRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Organization Admin not found"));

            if (request.getFullName() != null && !request.getFullName().isEmpty()) {
                admin.setFullName(request.getFullName());
            }

            if (request.getPhone() != null) {
                admin.setPhone(request.getPhone());
            }

            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                admin.setPassword(passwordEncoder.encode(request.getPassword()));
                admin.setPasswordChangedAt(LocalDateTime.now());
            }

            OrganizationAdmin updatedAdmin = organizationAdminRepository.save(admin);
            log.info("✅ OrganizationAdmin updated: {}", updatedAdmin.getEmail());

            OrganizationAdminResponse response = buildAdminResponse(updatedAdmin, null);
            return ApiResponse.success("Admin updated successfully", response);

        } catch (Exception e) {
            log.error("❌ Error updating admin: {}", id, e);
            return ApiResponse.error("Update failed", e.getMessage());
        }
    }

    // ============================================
    // 8. DEACTIVATE
    // ============================================
    @Transactional
    public ApiResponse<String> deactivateOrganizationAdmin(Long id) {
        try {
            OrganizationAdmin admin = organizationAdminRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Organization Admin not found"));

            admin.setIsActive(false);
            organizationAdminRepository.save(admin);

            log.info("✅ OrganizationAdmin deactivated: {}", admin.getEmail());
            return ApiResponse.success("Admin deactivated successfully", null);

        } catch (Exception e) {
            log.error("❌ Error deactivating admin: {}", id, e);
            return ApiResponse.error("Failed to deactivate admin", e.getMessage());
        }
    }

    // ============================================
    // 9. ACTIVATE
    // ============================================
    @Transactional
    public ApiResponse<String> activateOrganizationAdmin(Long id) {
        try {
            OrganizationAdmin admin = organizationAdminRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Organization Admin not found"));

            admin.setIsActive(true);
            organizationAdminRepository.save(admin);

            log.info("✅ OrganizationAdmin activated: {}", admin.getEmail());
            return ApiResponse.success("Admin activated successfully", null);

        } catch (Exception e) {
            log.error("❌ Error activating admin: {}", id, e);
            return ApiResponse.error("Failed to activate admin", e.getMessage());
        }
    }

    // ============================================
    // 10. DELETE
    // ============================================
    @Transactional
    public ApiResponse<String> deleteOrganizationAdmin(Long id) {
        try {
            OrganizationAdmin admin = organizationAdminRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Organization Admin not found"));

            organizationAdminRepository.delete(admin);

            log.info("✅ OrganizationAdmin deleted: {}", admin.getEmail());
            return ApiResponse.success("Admin deleted successfully", null);

        } catch (Exception e) {
            log.error("❌ Error deleting admin: {}", id, e);
            return ApiResponse.error("Failed to delete admin", e.getMessage());
        }
    }

    // ============================================
    // ✅ FIXED: BUILD RESPONSE - Returns CORRECT user data
    // ============================================
    private OrganizationAdminResponse buildAdminResponse(OrganizationAdmin admin, String token) {
        log.info("  [BUILD RESPONSE] Building response for: {} (ID: {})", 
                 admin.getFullName(), admin.getId());

        OrganizationAdminResponse.OrganizationAdminResponseBuilder builder = 
            OrganizationAdminResponse.builder()
                .id(admin.getId())                          // ✅ CRITICAL: User's own ID
                .email(admin.getEmail())                    // ✅ User's own email
                .fullName(admin.getFullName())              // ✅ User's own name
                .phone(admin.getPhone())                    // ✅ User's own phone
                .companyName(admin.getCompanyName())        // ✅ User's own company
                .isActive(admin.getIsActive())              // ✅ User's own status
                .mustChangePassword(admin.getMustChangePassword()) // ✅ User's own flag
                .token(token)                               // ✅ Token for THIS user
                .createdAt(admin.getCreatedAt() != null ? 
                          admin.getCreatedAt().format(DATE_FORMATTER) : null)
                .lastLogin(admin.getLastLogin() != null ? 
                          admin.getLastLogin().format(DATE_FORMATTER) : null);

        // Add creator info if exists
        if (admin.getCreatedBy() != null) {
            builder.createdByAdminId(admin.getCreatedBy().getId())
                   .createdByAdminName(admin.getCreatedBy().getFullName());
        }

        OrganizationAdminResponse response = builder.build();

        log.info("  [BUILD RESPONSE] Response created:");
        log.info("    - ID: {}", response.getId());
        log.info("    - Email: {}", response.getEmail());
        log.info("    - Full Name: {}", response.getFullName());
        log.info("    - Company: {}", response.getCompanyName());

        return response;
    }
}