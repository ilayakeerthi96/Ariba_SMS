
package com.itti.leadcapturing.service;

import com.itti.leadcapturing.dto.*;
import com.itti.leadcapturing.model.SuperAdmin;
import com.itti.leadcapturing.model.OrganizationAdmin;
import com.itti.leadcapturing.repo.SuperAdminRepository;
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

/**
 * ✅ UPDATED: SuperAdmin Service
 * SuperAdmin can only create OrganizationAdmins
 * Location: src/main/java/com/itti/leadcapturing/service/SuperAdminService.java
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SuperAdminService {

    private final SuperAdminRepository superAdminRepository;
    private final OrganizationAdminRepository organizationAdminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ============================================
    // 1. REGISTER SUPERADMIN (Initial Setup Only)
    // ============================================
    @Transactional
    public ApiResponse<SuperAdminResponse> registerSuperAdmin(SuperAdminRegistrationRequest request) {
        try {
            log.info("🔵 Registering SuperAdmin: {}", request.getEmail());
            
            // Check if email already exists
            if (superAdminRepository.existsByEmail(request.getEmail())) {
                log.warn("❌ Email already exists: {}", request.getEmail());
                return ApiResponse.error("Registration failed", "Email already exists");
            }

            // Create new SuperAdmin
            SuperAdmin superAdmin = new SuperAdmin();
            superAdmin.setEmail(request.getEmail());
            superAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
            superAdmin.setFullName(request.getFullName());
            superAdmin.setPhone(request.getPhone());
            superAdmin.setCompanyName(request.getCompanyName());
            superAdmin.setIsActive(true);

            // Save SuperAdmin
            SuperAdmin savedSuperAdmin = superAdminRepository.save(superAdmin);
            log.info("✅ SuperAdmin registered: {}", savedSuperAdmin.getEmail());

            // Generate JWT token
            String token = jwtUtil.generateToken(savedSuperAdmin.getEmail(), "SUPER_ADMIN");

            // Build response
            SuperAdminResponse response = buildSuperAdminResponse(savedSuperAdmin, token);
            
            return ApiResponse.success("SuperAdmin registered successfully", response);

        } catch (Exception e) {
            log.error("❌ Error during SuperAdmin registration", e);
            return ApiResponse.error("Registration failed", e.getMessage());
        }
    }

    // ============================================
    // 2. LOGIN SUPERADMIN
    // ============================================
    @Transactional
    public ApiResponse<SuperAdminResponse> loginSuperAdmin(SuperAdminLoginRequest request) {
        try {
            log.info("🔐 SuperAdmin login attempt: {}", request.getEmail());

            // Find SuperAdmin by email
            SuperAdmin superAdmin = superAdminRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid email or password"));

            // Check if SuperAdmin is active
            if (!superAdmin.getIsActive()) {
                log.warn("❌ Account inactive: {}", request.getEmail());
                return ApiResponse.error("Login failed", "Account is inactive");
            }

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), superAdmin.getPassword())) {
                log.warn("❌ Invalid password: {}", request.getEmail());
                return ApiResponse.error("Login failed", "Invalid email or password");
            }

            // Update last login timestamp
            superAdmin.setLastLogin(LocalDateTime.now());
            superAdminRepository.save(superAdmin);

            // Generate JWT token
            String token = jwtUtil.generateToken(superAdmin.getEmail(), "SUPER_ADMIN");

            log.info("✅ SuperAdmin logged in: {}", superAdmin.getEmail());

            // Build response
            SuperAdminResponse response = buildSuperAdminResponse(superAdmin, token);
            
            return ApiResponse.success("Login successful", response);

        } catch (Exception e) {
            log.error("❌ Error during SuperAdmin login", e);
            return ApiResponse.error("Login failed", e.getMessage());
        }
    }

    // ============================================
    // 🆕 3. CREATE ORGANIZATION ADMIN (Main Function)
    // ============================================
    @Transactional
    public ApiResponse<OrganizationAdminResponse> createOrganizationAdmin(
            OrganizationAdminCreateRequest request) {
        try {
            log.info("🔵 SuperAdmin creating OrganizationAdmin: {} for company: {}", 
                     request.getEmail(), request.getCompanyName());
            
            // Check if email already exists
            if (organizationAdminRepository.existsByEmail(request.getEmail())) {
                log.warn("❌ Email already exists: {}", request.getEmail());
                return ApiResponse.error("Creation failed", "Email already exists");
            }

            // Create new OrganizationAdmin
            OrganizationAdmin orgAdmin = new OrganizationAdmin();
            orgAdmin.setEmail(request.getEmail());
            orgAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
            orgAdmin.setFullName(request.getFullName());
            orgAdmin.setPhone(request.getPhone());
            orgAdmin.setCompanyName(request.getCompanyName());
            orgAdmin.setIsActive(true);
            orgAdmin.setCreatedBy(null); // Created by SuperAdmin

            // Save
            OrganizationAdmin savedOrgAdmin = organizationAdminRepository.save(orgAdmin);
            log.info("✅ OrganizationAdmin created by SuperAdmin: {} (ID: {})", 
                     savedOrgAdmin.getEmail(), savedOrgAdmin.getId());

            OrganizationAdminResponse response = buildOrgAdminResponse(savedOrgAdmin);
            return ApiResponse.success("Organization Admin created successfully", response);

        } catch (Exception e) {
            log.error("❌ Error creating OrganizationAdmin", e);
            return ApiResponse.error("Creation failed", e.getMessage());
        }
    }

    // ============================================
    // 🆕 4. GET ALL ORGANIZATION ADMINS (for SuperAdmin dashboard)
    // ============================================
    public ApiResponse<List<OrganizationAdminResponse>> getAllOrganizationAdmins() {
        try {
            List<OrganizationAdmin> orgAdmins = organizationAdminRepository.findAll();
            
            List<OrganizationAdminResponse> responses = orgAdmins.stream()
                    .map(this::buildOrgAdminResponse)
                    .collect(Collectors.toList());

            return ApiResponse.success("Organization Admins retrieved successfully", responses);

        } catch (Exception e) {
            log.error("❌ Error getting all OrganizationAdmins", e);
            return ApiResponse.error("Failed to retrieve admins", e.getMessage());
        }
    }

    // ============================================
    // 5. GET SUPERADMIN BY ID
    // ============================================
    public ApiResponse<SuperAdminResponse> getSuperAdminById(Long id) {
        try {
            SuperAdmin superAdmin = superAdminRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("SuperAdmin not found"));

            SuperAdminResponse response = buildSuperAdminResponse(superAdmin, null);
            return ApiResponse.success("SuperAdmin retrieved successfully", response);

        } catch (Exception e) {
            log.error("❌ Error getting SuperAdmin by ID: {}", id, e);
            return ApiResponse.error("Failed to retrieve SuperAdmin", e.getMessage());
        }
    }

    // ============================================
    // 6. UPDATE SUPERADMIN
    // ============================================
    @Transactional
    public ApiResponse<SuperAdminResponse> updateSuperAdmin(Long id, SuperAdminRegistrationRequest request) {
        try {
            SuperAdmin superAdmin = superAdminRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("SuperAdmin not found"));

            // Update fields
            if (request.getFullName() != null) {
                superAdmin.setFullName(request.getFullName());
            }
            if (request.getPhone() != null) {
                superAdmin.setPhone(request.getPhone());
            }
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                superAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            SuperAdmin updatedSuperAdmin = superAdminRepository.save(superAdmin);
            log.info("✅ SuperAdmin updated: {}", updatedSuperAdmin.getEmail());

            SuperAdminResponse response = buildSuperAdminResponse(updatedSuperAdmin, null);
            return ApiResponse.success("SuperAdmin updated successfully", response);

        } catch (Exception e) {
            log.error("❌ Error updating SuperAdmin: {}", id, e);
            return ApiResponse.error("Failed to update SuperAdmin", e.getMessage());
        }
    }

    // ============================================
    // BUILD RESPONSE HELPERS
    // ============================================
    private SuperAdminResponse buildSuperAdminResponse(SuperAdmin superAdmin, String token) {
        return SuperAdminResponse.builder()
                .id(superAdmin.getId())
                .email(superAdmin.getEmail())
                .fullName(superAdmin.getFullName())
                .phone(superAdmin.getPhone())
                .companyName(superAdmin.getCompanyName())
                .isActive(superAdmin.getIsActive())
                .token(token)
                .build();
    }

    private OrganizationAdminResponse buildOrgAdminResponse(OrganizationAdmin orgAdmin) {
        return OrganizationAdminResponse.builder()
                .id(orgAdmin.getId())
                .email(orgAdmin.getEmail())
                .fullName(orgAdmin.getFullName())
                .phone(orgAdmin.getPhone())
                .companyName(orgAdmin.getCompanyName())
                .isActive(orgAdmin.getIsActive())
                .createdAt(orgAdmin.getCreatedAt() != null ? 
                          orgAdmin.getCreatedAt().format(DATE_FORMATTER) : null)
                .lastLogin(orgAdmin.getLastLogin() != null ? 
                          orgAdmin.getLastLogin().format(DATE_FORMATTER) : null)
                .build();
    }
}