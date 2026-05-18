
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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationAdminService {

    private final OrganizationAdminRepository organizationAdminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final FileStorageService fileStorageService;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            
            // ============================================================
// ADD THESE TWO METHODS to OrganizationAdminService.java
// (paste alongside other methods, before the closing brace)
// ============================================================

    @Transactional
    public ApiResponse<String> updateOrgAdminTheme(Long id, String theme) {
        try {
            log.info("🎨 Updating theme for OrgAdmin ID: {} → {}", id, theme);
            OrganizationAdmin admin = organizationAdminRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("OrgAdmin not found"));
            admin.setTheme(theme);
            organizationAdminRepository.save(admin);
            log.info("✅ OrgAdmin theme updated: {}", theme);
            return ApiResponse.success("Theme updated successfully", theme);
        } catch (Exception e) {
            log.error("❌ Error updating theme for OrgAdmin: {}", id, e);
            return ApiResponse.error("Theme update failed", e.getMessage());
        }
    }

    public ApiResponse<String> getOrgAdminTheme(Long id) {
        try {
            OrganizationAdmin admin = organizationAdminRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("OrgAdmin not found"));
            String theme = admin.getTheme() != null ? admin.getTheme() : "default";
            return ApiResponse.success("Theme retrieved", theme);
        } catch (Exception e) {
            return ApiResponse.error("Failed to get theme", e.getMessage());
        }
    }

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
                OrganizationAdmin creator = organizationAdminRepository
                        .findById(createdByAdminId).orElse(null);
                admin.setCreatedBy(creator);
            }

            OrganizationAdmin savedAdmin = organizationAdminRepository.save(admin);
            log.info("✅ OrganizationAdmin created: {} (ID: {})",
                    savedAdmin.getEmail(), savedAdmin.getId());

            return ApiResponse.success("Organization Admin created successfully",
                    buildAdminResponse(savedAdmin, null));

        } catch (Exception e) {
            log.error("❌ Error creating OrganizationAdmin", e);
            return ApiResponse.error("Creation failed", e.getMessage());
        }
    }

    // ============================================
    // GET ADMIN ENTITY DIRECTLY (used by BuyerService)
    // ============================================
    public OrganizationAdmin getOrganizationAdminByIdDirect(Long id) {
        log.info("📥 Fetching Organization Admin by ID: {}", id);
        return organizationAdminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Organization Admin not found with ID: " + id));
    }

    // ============================================
    // 2. LOGIN ORGANIZATION ADMIN
    // ============================================
    @Transactional
    public ApiResponse<OrganizationAdminResponse> loginOrganizationAdmin(
            OrganizationAdminLoginRequest request) {
        try {
            log.info("=".repeat(80));
            log.info("🔐 OrganizationAdmin login attempt: {}", request.getEmail());

            OrganizationAdmin admin = organizationAdminRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid email or password"));

            log.info("  [✓] Found admin: {} (ID: {})", admin.getFullName(), admin.getId());
            log.info("  [✓] Company: {}", admin.getCompanyName());
            log.info("  [✓] Is Active: {}", admin.getIsActive());

            if (!admin.getIsActive()) {
                log.warn("❌ Account inactive: {}", request.getEmail());
                return ApiResponse.error("Login failed", "Account is inactive");
            }

            if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                log.warn("❌ Invalid password: {}", request.getEmail());
                return ApiResponse.error("Login failed", "Invalid email or password");
            }

            admin.setLastLogin(LocalDateTime.now());
            organizationAdminRepository.save(admin);

            String token = jwtUtil.generateTokenWithId(
                    admin.getEmail(), "ORGANIZATION_ADMIN", admin.getId());

            log.info("  [✓] Token generated");
            log.info("  [✓] Must change password: {}", admin.getMustChangePassword());

            OrganizationAdminResponse response = buildAdminResponse(admin, token);

            log.info("=".repeat(80));
            log.info("✅ LOGIN SUCCESSFUL — ID: {}, Email: {}", response.getId(), response.getEmail());
            log.info("=".repeat(80));

            return ApiResponse.success("Login successful", response);

        } catch (Exception e) {
            log.error("❌ Login error", e);
            return ApiResponse.error("Login failed", e.getMessage());
        }
    }

    // ============================================
    // 3. CHANGE PASSWORD
    // ============================================
    @Transactional
    public ApiResponse<String> changePassword(Long adminId, ChangePasswordRequest request) {
        try {
            log.info("🔐 Change password request for admin ID: {}", adminId);

            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ApiResponse.error("Password change failed",
                        "New password and confirm password do not match");
            }

            OrganizationAdmin admin = organizationAdminRepository.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Organization Admin not found"));

            if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
                return ApiResponse.error("Password change failed", "Current password is incorrect");
            }

            if (passwordEncoder.matches(request.getNewPassword(), admin.getPassword())) {
                return ApiResponse.error("Password change failed",
                        "New password must be different from current password");
            }

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
            return ApiResponse.success("Organization Admin retrieved successfully",
                    buildAdminResponse(admin, null));
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

            if (request.getFullName() != null && !request.getFullName().isEmpty())
                admin.setFullName(request.getFullName());
            if (request.getPhone() != null && !request.getPhone().isEmpty())
                admin.setPhone(request.getPhone());
            if (request.getCompanyName() != null && !request.getCompanyName().isEmpty())
                admin.setCompanyName(request.getCompanyName());
            if (request.getOrganizationName() != null && !request.getOrganizationName().isEmpty())
                admin.setOrganizationName(request.getOrganizationName());
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                admin.setPassword(passwordEncoder.encode(request.getPassword()));
                admin.setPasswordChangedAt(LocalDateTime.now());
            }

            OrganizationAdmin updatedAdmin = organizationAdminRepository.save(admin);
            log.info("✅ OrganizationAdmin updated: {}", updatedAdmin.getEmail());

            return ApiResponse.success("Admin updated successfully",
                    buildAdminResponse(updatedAdmin, null));

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
    // 11. UPLOAD LOGO
    // ============================================
    @Transactional
    public ApiResponse<OrganizationAdminResponse> uploadLogo(Long id, MultipartFile logoFile) {
        try {
            log.info("🖼️ Uploading logo for OrganizationAdmin ID: {}", id);

            OrganizationAdmin admin = organizationAdminRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException(
                            "Organization Admin not found with ID: " + id));

            if (admin.getLogoUrl() != null) {
                fileStorageService.deleteLogo(admin.getLogoUrl());
            }

            String newLogoUrl = fileStorageService.storeLogo(logoFile, id);
            admin.setLogoUrl(newLogoUrl);

            OrganizationAdmin updated = organizationAdminRepository.save(admin);
            log.info("✅ Logo uploaded for OrgAdmin ID: {}", id);

            return ApiResponse.success("Logo uploaded successfully",
                    buildAdminResponse(updated, null));

        } catch (IllegalArgumentException e) {
            return ApiResponse.error("Logo upload failed", e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error uploading logo for OrgAdmin ID: {}", id, e);
            return ApiResponse.error("Logo upload failed", e.getMessage());
        }
    }

    // ============================================
    // 12. GET LOGO (base64)
    // ============================================
    public ApiResponse<String> getOrganizationAdminLogo(Long id) {
        try {
            OrganizationAdmin admin = organizationAdminRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Organization Admin not found"));

            if (admin.getLogoUrl() == null) {
                return ApiResponse.success("No logo found", null);
            }

            byte[] logoBytes = fileStorageService.readLogoAsBytes(admin.getLogoUrl());
            if (logoBytes == null) {
                return ApiResponse.success("No logo found", null);
            }

            String mimeType = getMimeType(admin.getLogoUrl());
            String base64 = "data:" + mimeType + ";base64," +
                    Base64.getEncoder().encodeToString(logoBytes);

            return ApiResponse.success("Logo retrieved", base64);

        } catch (Exception e) {
            log.error("❌ Error getting OrgAdmin logo: {}", id, e);
            return ApiResponse.error("Failed to get logo", e.getMessage());
        }
    }

    // ============================================
    // BUILD RESPONSE HELPER
    // ============================================
    private OrganizationAdminResponse buildAdminResponse(OrganizationAdmin admin, String token) {
        String logoBase64 = null;
        if (admin.getLogoUrl() != null) {
            try {
                byte[] bytes = fileStorageService.readLogoAsBytes(admin.getLogoUrl());
                if (bytes != null) {
                    String mime = getMimeType(admin.getLogoUrl());
                    logoBase64 = "data:" + mime + ";base64," +
                            Base64.getEncoder().encodeToString(bytes);
                }
            } catch (Exception e) {
                log.warn("⚠️ Could not read logo for OrgAdmin {}", admin.getId());
            }
        }

        OrganizationAdminResponse.OrganizationAdminResponseBuilder builder =
                OrganizationAdminResponse.builder()
                        .id(admin.getId())
                        .email(admin.getEmail())
                        .fullName(admin.getFullName())
                        .phone(admin.getPhone())
                        .companyName(admin.getCompanyName())
                        .organizationName(admin.getOrganizationName())
                        .isActive(admin.getIsActive())
                        .mustChangePassword(admin.getMustChangePassword())
                        .token(token)
                        .logoUrl(admin.getLogoUrl())
                        .logoBase64(logoBase64)
                                                .theme(admin.getTheme() != null ? admin.getTheme() : "default")
                        .createdAt(admin.getCreatedAt() != null ?
                                admin.getCreatedAt().format(DATE_FORMATTER) : null)
                        .lastLogin(admin.getLastLogin() != null ?
                                admin.getLastLogin().format(DATE_FORMATTER) : null);

        if (admin.getCreatedBy() != null) {
            builder.createdByAdminId(admin.getCreatedBy().getId())
                    .createdByAdminName(admin.getCreatedBy().getFullName());
        }

        return builder.build();
    }

    private String getMimeType(String logoUrl) {
        if (logoUrl == null) return "image/jpeg";
        String lower = logoUrl.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }
}