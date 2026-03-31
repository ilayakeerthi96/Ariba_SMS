

// // package com.itti.leadcapturing.service;

// // import com.itti.leadcapturing.dto.*;
// // import com.itti.leadcapturing.model.SuperAdmin;
// // import com.itti.leadcapturing.model.OrganizationAdmin;
// // import com.itti.leadcapturing.repo.SuperAdminRepository;
// // import com.itti.leadcapturing.repo.OrganizationAdminRepository;
// // import com.itti.leadcapturing.util.JwtUtil;
// // import lombok.RequiredArgsConstructor;
// // import lombok.extern.slf4j.Slf4j;
// // import org.springframework.security.crypto.password.PasswordEncoder;
// // import org.springframework.stereotype.Service;
// // import org.springframework.transaction.annotation.Transactional;
// // import org.springframework.web.multipart.MultipartFile;

// // import java.time.LocalDateTime;
// // import java.time.format.DateTimeFormatter;
// // import java.util.List;
// // import java.util.stream.Collectors;

// // /**
// //  * SuperAdmin Service — handles registration, login, update, logo upload,
// //  * and OrganizationAdmin management.
// //  *
// //  * Location: src/main/java/com/itti/leadcapturing/service/SuperAdminService.java
// //  */
// // @Service
// // @RequiredArgsConstructor
// // @Slf4j
// // public class SuperAdminService {

// //     private final SuperAdminRepository superAdminRepository;
// //     private final OrganizationAdminRepository organizationAdminRepository;
// //     private final PasswordEncoder passwordEncoder;
// //     private final JwtUtil jwtUtil;
// //     private final FileStorageService fileStorageService; // ✅ NEW

// //     private static final DateTimeFormatter DATE_FORMATTER =
// //             DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

// //     // ============================================
// //     // 1. REGISTER SUPERADMIN
// //     // ============================================
// //     @Transactional
// //     public ApiResponse<SuperAdminResponse> registerSuperAdmin(SuperAdminRegistrationRequest request) {
// //         try {
// //             log.info("🔵 Registering SuperAdmin: {}", request.getEmail());

// //             if (superAdminRepository.existsByEmail(request.getEmail())) {
// //                 log.warn("❌ Email already exists: {}", request.getEmail());
// //                 return ApiResponse.error("Registration failed", "Email already exists");
// //             }

// //             SuperAdmin superAdmin = new SuperAdmin();
// //             superAdmin.setEmail(request.getEmail());
// //             superAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
// //             superAdmin.setFullName(request.getFullName());
// //             superAdmin.setPhone(request.getPhone());
// //             superAdmin.setCompanyName(request.getCompanyName());
// //             superAdmin.setOrganizationName(request.getOrganizationName()); // ✅ NEW
// //             superAdmin.setIsActive(true);

// //             SuperAdmin savedSuperAdmin = superAdminRepository.save(superAdmin);
// //             log.info("✅ SuperAdmin registered: {}", savedSuperAdmin.getEmail());

// //             String token = jwtUtil.generateToken(savedSuperAdmin.getEmail(), "SUPER_ADMIN");
// //             SuperAdminResponse response = buildSuperAdminResponse(savedSuperAdmin, token);

// //             return ApiResponse.success("SuperAdmin registered successfully", response);

// //         } catch (Exception e) {
// //             log.error("❌ Error during SuperAdmin registration", e);
// //             return ApiResponse.error("Registration failed", e.getMessage());
// //         }
// //     }

// //     // ============================================
// //     // 2. LOGIN SUPERADMIN
// //     // ============================================
// //     @Transactional
// //     public ApiResponse<SuperAdminResponse> loginSuperAdmin(SuperAdminLoginRequest request) {
// //         try {
// //             log.info("🔐 SuperAdmin login attempt: {}", request.getEmail());

// //             SuperAdmin superAdmin = superAdminRepository.findByEmail(request.getEmail())
// //                     .orElseThrow(() -> new RuntimeException("Invalid email or password"));

// //             if (!superAdmin.getIsActive()) {
// //                 return ApiResponse.error("Login failed", "Account is inactive");
// //             }

// //             if (!passwordEncoder.matches(request.getPassword(), superAdmin.getPassword())) {
// //                 return ApiResponse.error("Login failed", "Invalid email or password");
// //             }

// //             superAdmin.setLastLogin(LocalDateTime.now());
// //             superAdminRepository.save(superAdmin);

// //             String token = jwtUtil.generateToken(superAdmin.getEmail(), "SUPER_ADMIN");
// //             log.info("✅ SuperAdmin logged in: {}", superAdmin.getEmail());

// //             return ApiResponse.success("Login successful", buildSuperAdminResponse(superAdmin, token));

// //         } catch (Exception e) {
// //             log.error("❌ Error during SuperAdmin login", e);
// //             return ApiResponse.error("Login failed", e.getMessage());
// //         }
// //     }

// //     // ============================================
// //     // 3. CREATE ORGANIZATION ADMIN
// //     // ============================================
// //     @Transactional
// //     public ApiResponse<OrganizationAdminResponse> createOrganizationAdmin(
// //             OrganizationAdminCreateRequest request) {
// //         try {
// //             log.info("🔵 Creating OrganizationAdmin: {}", request.getEmail());

// //             if (organizationAdminRepository.existsByEmail(request.getEmail())) {
// //                 return ApiResponse.error("Creation failed", "Email already exists");
// //             }

// //             OrganizationAdmin orgAdmin = new OrganizationAdmin();
// //             orgAdmin.setEmail(request.getEmail());
// //             orgAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
// //             orgAdmin.setFullName(request.getFullName());
// //             orgAdmin.setPhone(request.getPhone());
// //             orgAdmin.setCompanyName(request.getCompanyName());
// //             orgAdmin.setIsActive(true);
// //             orgAdmin.setCreatedBy(null);

// //             OrganizationAdmin saved = organizationAdminRepository.save(orgAdmin);
// //             log.info("✅ OrganizationAdmin created: {} (ID: {})", saved.getEmail(), saved.getId());

// //             return ApiResponse.success("Organization Admin created successfully",
// //                     buildOrgAdminResponse(saved));

// //         } catch (Exception e) {
// //             log.error("❌ Error creating OrganizationAdmin", e);
// //             return ApiResponse.error("Creation failed", e.getMessage());
// //         }
// //     }

// //     // ============================================
// //     // 4. GET ALL ORGANIZATION ADMINS
// //     // ============================================
// //     public ApiResponse<List<OrganizationAdminResponse>> getAllOrganizationAdmins() {
// //         try {
// //             List<OrganizationAdminResponse> responses = organizationAdminRepository.findAll()
// //                     .stream()
// //                     .map(this::buildOrgAdminResponse)
// //                     .collect(Collectors.toList());

// //             return ApiResponse.success("Organization Admins retrieved successfully", responses);

// //         } catch (Exception e) {
// //             log.error("❌ Error getting all OrganizationAdmins", e);
// //             return ApiResponse.error("Failed to retrieve admins", e.getMessage());
// //         }
// //     }

// //     // ============================================
// //     // 5. GET SUPERADMIN BY ID
// //     // ============================================
// //     public ApiResponse<SuperAdminResponse> getSuperAdminById(Long id) {
// //         try {
// //             SuperAdmin superAdmin = superAdminRepository.findById(id)
// //                     .orElseThrow(() -> new RuntimeException("SuperAdmin not found"));

// //             return ApiResponse.success("SuperAdmin retrieved successfully",
// //                     buildSuperAdminResponse(superAdmin, null));

// //         } catch (Exception e) {
// //             log.error("❌ Error getting SuperAdmin by ID: {}", id, e);
// //             return ApiResponse.error("Failed to retrieve SuperAdmin", e.getMessage());
// //         }
// //     }

// //     // ============================================
// //     // ✅ 6. UPDATE SUPERADMIN (Updated — uses new DTO)
// //     // ============================================
// //     @Transactional
// //     public ApiResponse<SuperAdminResponse> updateSuperAdmin(Long id, SuperAdminUpdateRequest request) {
// //         try {
// //             log.info("✏️ Updating SuperAdmin ID: {}", id);

// //             SuperAdmin superAdmin = superAdminRepository.findById(id)
// //                     .orElseThrow(() -> new RuntimeException("SuperAdmin not found"));

// //             // Update only non-null fields
// //             if (request.getFullName() != null && !request.getFullName().isBlank()) {
// //                 superAdmin.setFullName(request.getFullName());
// //             }
// //             if (request.getPhone() != null && !request.getPhone().isBlank()) {
// //                 superAdmin.setPhone(request.getPhone());
// //             }
// //             if (request.getCompanyName() != null && !request.getCompanyName().isBlank()) {
// //                 superAdmin.setCompanyName(request.getCompanyName());
// //             }
// //             if (request.getOrganizationName() != null && !request.getOrganizationName().isBlank()) {
// //                 superAdmin.setOrganizationName(request.getOrganizationName()); // ✅ NEW
// //             }
// //             if (request.getPassword() != null && !request.getPassword().isBlank()) {
// //                 superAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
// //             }

// //             SuperAdmin updated = superAdminRepository.save(superAdmin);
// //             log.info("✅ SuperAdmin updated: {}", updated.getEmail());

// //             return ApiResponse.success("SuperAdmin updated successfully",
// //                     buildSuperAdminResponse(updated, null));

// //         } catch (Exception e) {
// //             log.error("❌ Error updating SuperAdmin: {}", id, e);
// //             return ApiResponse.error("Failed to update SuperAdmin", e.getMessage());
// //         }
// //     }

// //     // ============================================
// //     // ✅ 7. UPLOAD / UPDATE LOGO  (NEW)
// //     // ============================================
// //     @Transactional
// //     public ApiResponse<SuperAdminResponse> uploadLogo(Long id, MultipartFile logoFile) {
// //         try {
// //             log.info("🖼️ Uploading logo for SuperAdmin ID: {}", id);

// //             SuperAdmin superAdmin = superAdminRepository.findById(id)
// //                     .orElseThrow(() -> new RuntimeException("SuperAdmin not found with ID: " + id));

// //             // Delete the old logo file from disk if it exists
// //             if (superAdmin.getLogoUrl() != null) {
// //                 fileStorageService.deleteLogo(superAdmin.getLogoUrl());
// //                 log.info("🗑️ Old logo removed for SuperAdmin ID: {}", id);
// //             }

// //             // Store the new logo and get its URL
// //             String newLogoUrl = fileStorageService.storeLogo(logoFile, id);
// //             superAdmin.setLogoUrl(newLogoUrl);

// //             SuperAdmin updated = superAdminRepository.save(superAdmin);
// //             log.info("✅ Logo uploaded successfully for SuperAdmin ID: {} → {}", id, newLogoUrl);

// //             return ApiResponse.success("Logo uploaded successfully",
// //                     buildSuperAdminResponse(updated, null));

// //         } catch (IllegalArgumentException e) {
// //             log.warn("⚠️ Logo upload validation failed: {}", e.getMessage());
// //             return ApiResponse.error("Logo upload failed", e.getMessage());

// //         } catch (Exception e) {
// //             log.error("❌ Error uploading logo for SuperAdmin ID: {}", id, e);
// //             return ApiResponse.error("Logo upload failed", e.getMessage());
// //         }
// //     }

// //     // ============================================
// //     // BUILD RESPONSE HELPERS
// //     // ============================================
// //     private SuperAdminResponse buildSuperAdminResponse(SuperAdmin superAdmin, String token) {
// //         return SuperAdminResponse.builder()
// //                 .id(superAdmin.getId())
// //                 .email(superAdmin.getEmail())
// //                 .fullName(superAdmin.getFullName())
// //                 .phone(superAdmin.getPhone())
// //                 .companyName(superAdmin.getCompanyName())
// //                 .organizationName(superAdmin.getOrganizationName()) // ✅ NEW
// //                 .logoUrl(superAdmin.getLogoUrl())                    // ✅ NEW
// //                 .isActive(superAdmin.getIsActive())
// //                 .token(token)
// //                 .build();
// //     }

// //     private OrganizationAdminResponse buildOrgAdminResponse(OrganizationAdmin orgAdmin) {
// //         return OrganizationAdminResponse.builder()
// //                 .id(orgAdmin.getId())
// //                 .email(orgAdmin.getEmail())
// //                 .fullName(orgAdmin.getFullName())
// //                 .phone(orgAdmin.getPhone())
// //                 .companyName(orgAdmin.getCompanyName())
// //                 .isActive(orgAdmin.getIsActive())
// //                 .createdAt(orgAdmin.getCreatedAt() != null ?
// //                         orgAdmin.getCreatedAt().format(DATE_FORMATTER) : null)
// //                 .lastLogin(orgAdmin.getLastLogin() != null ?
// //                         orgAdmin.getLastLogin().format(DATE_FORMATTER) : null)
// //                 .build();
// //     }
// // }


// package com.itti.leadcapturing.service;

// import com.itti.leadcapturing.dto.*;
// import com.itti.leadcapturing.model.SuperAdmin;
// import com.itti.leadcapturing.model.OrganizationAdmin;
// import com.itti.leadcapturing.repo.SuperAdminRepository;
// import com.itti.leadcapturing.repo.OrganizationAdminRepository;
// import com.itti.leadcapturing.util.JwtUtil;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.multipart.MultipartFile;

// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.Base64;
// import java.util.List;
// import java.util.stream.Collectors;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class SuperAdminService {

//     private final SuperAdminRepository superAdminRepository;
//     private final OrganizationAdminRepository organizationAdminRepository;
//     private final PasswordEncoder passwordEncoder;
//     private final JwtUtil jwtUtil;
//     private final FileStorageService fileStorageService;

//     private static final DateTimeFormatter DATE_FORMATTER =
//             DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//     // ============================================
//     // 1. REGISTER SUPERADMIN
//     // ============================================
//     @Transactional
//     public ApiResponse<SuperAdminResponse> registerSuperAdmin(SuperAdminRegistrationRequest request) {
//         try {
//             log.info("🔵 Registering SuperAdmin: {}", request.getEmail());

//             if (superAdminRepository.existsByEmail(request.getEmail())) {
//                 return ApiResponse.error("Registration failed", "Email already exists");
//             }

//             SuperAdmin superAdmin = new SuperAdmin();
//             superAdmin.setEmail(request.getEmail());
//             superAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
//             superAdmin.setFullName(request.getFullName());
//             superAdmin.setPhone(request.getPhone());
//             superAdmin.setCompanyName(request.getCompanyName());
//             superAdmin.setOrganizationName(request.getOrganizationName());
//             superAdmin.setIsActive(true);

//             SuperAdmin saved = superAdminRepository.save(superAdmin);
//             String token = jwtUtil.generateToken(saved.getEmail(), "SUPER_ADMIN");

//             return ApiResponse.success("SuperAdmin registered successfully",
//                     buildSuperAdminResponse(saved, token));

//         } catch (Exception e) {
//             log.error("❌ Error during SuperAdmin registration", e);
//             return ApiResponse.error("Registration failed", e.getMessage());
//         }
//     }

//     // ============================================
//     // 2. LOGIN SUPERADMIN
//     // ============================================
//     @Transactional
//     public ApiResponse<SuperAdminResponse> loginSuperAdmin(SuperAdminLoginRequest request) {
//         try {
//             log.info("🔐 SuperAdmin login attempt: {}", request.getEmail());

//             SuperAdmin superAdmin = superAdminRepository.findByEmail(request.getEmail())
//                     .orElseThrow(() -> new RuntimeException("Invalid email or password"));

//             if (!superAdmin.getIsActive()) {
//                 return ApiResponse.error("Login failed", "Account is inactive");
//             }
//             if (!passwordEncoder.matches(request.getPassword(), superAdmin.getPassword())) {
//                 return ApiResponse.error("Login failed", "Invalid email or password");
//             }

//             superAdmin.setLastLogin(LocalDateTime.now());
//             superAdminRepository.save(superAdmin);

//             String token = jwtUtil.generateToken(superAdmin.getEmail(), "SUPER_ADMIN");
//             log.info("✅ SuperAdmin logged in: {}", superAdmin.getEmail());

//             return ApiResponse.success("Login successful",
//                     buildSuperAdminResponse(superAdmin, token));

//         } catch (Exception e) {
//             log.error("❌ Error during SuperAdmin login", e);
//             return ApiResponse.error("Login failed", e.getMessage());
//         }
//     }

//     // ============================================
//     // 3. CREATE ORGANIZATION ADMIN
//     // ============================================
//     @Transactional
//     public ApiResponse<OrganizationAdminResponse> createOrganizationAdmin(
//             OrganizationAdminCreateRequest request) {
//         try {
//             if (organizationAdminRepository.existsByEmail(request.getEmail())) {
//                 return ApiResponse.error("Creation failed", "Email already exists");
//             }

//             OrganizationAdmin orgAdmin = new OrganizationAdmin();
//             orgAdmin.setEmail(request.getEmail());
//             orgAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
//             orgAdmin.setFullName(request.getFullName());
//             orgAdmin.setPhone(request.getPhone());
//             orgAdmin.setCompanyName(request.getCompanyName());
//             orgAdmin.setIsActive(true);
//             orgAdmin.setCreatedBy(null);

//             OrganizationAdmin saved = organizationAdminRepository.save(orgAdmin);
//             return ApiResponse.success("Organization Admin created successfully",
//                     buildOrgAdminResponse(saved));

//         } catch (Exception e) {
//             log.error("❌ Error creating OrganizationAdmin", e);
//             return ApiResponse.error("Creation failed", e.getMessage());
//         }
//     }

//     // ============================================
//     // 4. GET ALL ORGANIZATION ADMINS
//     // ============================================
//     public ApiResponse<List<OrganizationAdminResponse>> getAllOrganizationAdmins() {
//         try {
//             List<OrganizationAdminResponse> responses = organizationAdminRepository.findAll()
//                     .stream()
//                     .map(this::buildOrgAdminResponse)
//                     .collect(Collectors.toList());
//             return ApiResponse.success("Organization Admins retrieved successfully", responses);
//         } catch (Exception e) {
//             return ApiResponse.error("Failed to retrieve admins", e.getMessage());
//         }
//     }

//     // ============================================
//     // 5. GET SUPERADMIN BY ID
//     // ============================================
//     public ApiResponse<SuperAdminResponse> getSuperAdminById(Long id) {
//         try {
//             SuperAdmin superAdmin = superAdminRepository.findById(id)
//                     .orElseThrow(() -> new RuntimeException("SuperAdmin not found"));
//             return ApiResponse.success("SuperAdmin retrieved successfully",
//                     buildSuperAdminResponse(superAdmin, null));
//         } catch (Exception e) {
//             return ApiResponse.error("Failed to retrieve SuperAdmin", e.getMessage());
//         }
//     }

//     // ============================================
//     // 6. UPDATE SUPERADMIN
//     // ============================================
//     @Transactional
//     public ApiResponse<SuperAdminResponse> updateSuperAdmin(Long id, SuperAdminUpdateRequest request) {
//         try {
//             log.info("✏️ Updating SuperAdmin ID: {}", id);

//             SuperAdmin superAdmin = superAdminRepository.findById(id)
//                     .orElseThrow(() -> new RuntimeException("SuperAdmin not found"));

//             if (request.getFullName() != null && !request.getFullName().isBlank())
//                 superAdmin.setFullName(request.getFullName());
//             if (request.getPhone() != null && !request.getPhone().isBlank())
//                 superAdmin.setPhone(request.getPhone());
//             if (request.getCompanyName() != null && !request.getCompanyName().isBlank())
//                 superAdmin.setCompanyName(request.getCompanyName());
//             if (request.getOrganizationName() != null && !request.getOrganizationName().isBlank())
//                 superAdmin.setOrganizationName(request.getOrganizationName());
//             if (request.getPassword() != null && !request.getPassword().isBlank())
//                 superAdmin.setPassword(passwordEncoder.encode(request.getPassword()));

//             SuperAdmin updated = superAdminRepository.save(superAdmin);
//             log.info("✅ SuperAdmin updated: {}", updated.getEmail());

//             return ApiResponse.success("SuperAdmin updated successfully",
//                     buildSuperAdminResponse(updated, null));

//         } catch (Exception e) {
//             log.error("❌ Error updating SuperAdmin: {}", id, e);
//             return ApiResponse.error("Failed to update SuperAdmin", e.getMessage());
//         }
//     }

//     // ============================================
//     // 7. UPLOAD LOGO FOR SUPERADMIN
//     // ============================================
//     @Transactional
//     public ApiResponse<SuperAdminResponse> uploadLogo(Long id, MultipartFile logoFile) {
//         try {
//             log.info("🖼️ Uploading logo for SuperAdmin ID: {}", id);

//             SuperAdmin superAdmin = superAdminRepository.findById(id)
//                     .orElseThrow(() -> new RuntimeException("SuperAdmin not found with ID: " + id));

//             if (superAdmin.getLogoUrl() != null) {
//                 fileStorageService.deleteLogo(superAdmin.getLogoUrl());
//             }

//             String newLogoUrl = fileStorageService.storeLogo(logoFile, id);
//             superAdmin.setLogoUrl(newLogoUrl);

//             SuperAdmin updated = superAdminRepository.save(superAdmin);
//             log.info("✅ Logo uploaded for SuperAdmin ID: {}", id);

//             return ApiResponse.success("Logo uploaded successfully",
//                     buildSuperAdminResponse(updated, null));

//         } catch (IllegalArgumentException e) {
//             return ApiResponse.error("Logo upload failed", e.getMessage());
//         } catch (Exception e) {
//             log.error("❌ Error uploading logo for SuperAdmin ID: {}", id, e);
//             return ApiResponse.error("Logo upload failed", e.getMessage());
//         }
//     }

//     // ============================================
//     // 8. GET LOGO (base64) FOR SUPERADMIN
//     // ============================================
//     public ApiResponse<String> getSuperAdminLogo(Long id) {
//         try {
//             SuperAdmin superAdmin = superAdminRepository.findById(id)
//                     .orElseThrow(() -> new RuntimeException("SuperAdmin not found"));

//             if (superAdmin.getLogoUrl() == null) {
//                 return ApiResponse.success("No logo found", null);
//             }

//             byte[] logoBytes = fileStorageService.readLogoAsBytes(superAdmin.getLogoUrl());
//             if (logoBytes == null) {
//                 return ApiResponse.success("No logo found", null);
//             }

//             String mimeType = getMimeType(superAdmin.getLogoUrl());
//             String base64 = "data:" + mimeType + ";base64," +
//                     Base64.getEncoder().encodeToString(logoBytes);

//             return ApiResponse.success("Logo retrieved", base64);

//         } catch (Exception e) {
//             log.error("❌ Error getting SuperAdmin logo: {}", id, e);
//             return ApiResponse.error("Failed to get logo", e.getMessage());
//         }
//     }

//     // ============================================
//     // BUILD RESPONSE HELPERS
//     // ============================================
//     private SuperAdminResponse buildSuperAdminResponse(SuperAdmin superAdmin, String token) {
//         String logoBase64 = null;
//         if (superAdmin.getLogoUrl() != null) {
//             try {
//                 byte[] bytes = fileStorageService.readLogoAsBytes(superAdmin.getLogoUrl());
//                 if (bytes != null) {
//                     String mime = getMimeType(superAdmin.getLogoUrl());
//                     logoBase64 = "data:" + mime + ";base64," +
//                             Base64.getEncoder().encodeToString(bytes);
//                 }
//             } catch (Exception e) {
//                 log.warn("⚠️ Could not read logo for SuperAdmin {}", superAdmin.getId());
//             }
//         }

//         return SuperAdminResponse.builder()
//                 .id(superAdmin.getId())
//                 .email(superAdmin.getEmail())
//                 .fullName(superAdmin.getFullName())
//                 .phone(superAdmin.getPhone())
//                 .companyName(superAdmin.getCompanyName())
//                 .organizationName(superAdmin.getOrganizationName())
//                 .logoUrl(superAdmin.getLogoUrl())
//                 .logoBase64(logoBase64)
//                 .isActive(superAdmin.getIsActive())
//                 .token(token)
//                 .build();
//     }

//     private OrganizationAdminResponse buildOrgAdminResponse(OrganizationAdmin orgAdmin) {
//         String logoBase64 = null;
//         if (orgAdmin.getLogoUrl() != null) {
//             try {
//                 byte[] bytes = fileStorageService.readLogoAsBytes(orgAdmin.getLogoUrl());
//                 if (bytes != null) {
//                     String mime = getMimeType(orgAdmin.getLogoUrl());
//                     logoBase64 = "data:" + mime + ";base64," +
//                             Base64.getEncoder().encodeToString(bytes);
//                 }
//             } catch (Exception e) {
//                 log.warn("⚠️ Could not read logo for OrgAdmin {}", orgAdmin.getId());
//             }
//         }

//         return OrganizationAdminResponse.builder()
//                 .id(orgAdmin.getId())
//                 .email(orgAdmin.getEmail())
//                 .fullName(orgAdmin.getFullName())
//                 .phone(orgAdmin.getPhone())
//                 .companyName(orgAdmin.getCompanyName())
//                 .isActive(orgAdmin.getIsActive())
//                 .logoUrl(orgAdmin.getLogoUrl())
//                 .logoBase64(logoBase64)
//                 .createdAt(orgAdmin.getCreatedAt() != null ?
//                         orgAdmin.getCreatedAt().format(DATE_FORMATTER) : null)
//                 .lastLogin(orgAdmin.getLastLogin() != null ?
//                         orgAdmin.getLastLogin().format(DATE_FORMATTER) : null)
//                 .build();
//     }

//     private String getMimeType(String logoUrl) {
//         if (logoUrl == null) return "image/jpeg";
//         String lower = logoUrl.toLowerCase();
//         if (lower.endsWith(".png")) return "image/png";
//         if (lower.endsWith(".gif")) return "image/gif";
//         if (lower.endsWith(".svg")) return "image/svg+xml";
//         if (lower.endsWith(".webp")) return "image/webp";
//         return "image/jpeg";
//     }
// }


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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuperAdminService {

    private final SuperAdminRepository superAdminRepository;
    private final OrganizationAdminRepository organizationAdminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final FileStorageService fileStorageService;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ============================================
    // 1. REGISTER SUPERADMIN
    // ============================================
    @Transactional
    public ApiResponse<SuperAdminResponse> registerSuperAdmin(SuperAdminRegistrationRequest request) {
        try {
            log.info("🔵 Registering SuperAdmin: {}", request.getEmail());

            if (superAdminRepository.existsByEmail(request.getEmail())) {
                return ApiResponse.error("Registration failed", "Email already exists");
            }

            SuperAdmin superAdmin = new SuperAdmin();
            superAdmin.setEmail(request.getEmail());
            superAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
            superAdmin.setFullName(request.getFullName());
            superAdmin.setPhone(request.getPhone());
            superAdmin.setCompanyName(request.getCompanyName());
            superAdmin.setOrganizationName(request.getOrganizationName());
            superAdmin.setIsActive(true);

            SuperAdmin saved = superAdminRepository.save(superAdmin);
            String token = jwtUtil.generateToken(saved.getEmail(), "SUPER_ADMIN");

            return ApiResponse.success("SuperAdmin registered successfully",
                    buildSuperAdminResponse(saved, token));

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

            SuperAdmin superAdmin = superAdminRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid email or password"));

            if (!superAdmin.getIsActive()) {
                return ApiResponse.error("Login failed", "Account is inactive");
            }
            if (!passwordEncoder.matches(request.getPassword(), superAdmin.getPassword())) {
                return ApiResponse.error("Login failed", "Invalid email or password");
            }

            superAdmin.setLastLogin(LocalDateTime.now());
            superAdminRepository.save(superAdmin);

            String token = jwtUtil.generateToken(superAdmin.getEmail(), "SUPER_ADMIN");
            log.info("✅ SuperAdmin logged in: {}", superAdmin.getEmail());

            return ApiResponse.success("Login successful",
                    buildSuperAdminResponse(superAdmin, token));

        } catch (Exception e) {
            log.error("❌ Error during SuperAdmin login", e);
            return ApiResponse.error("Login failed", e.getMessage());
        }
    }

    // ============================================
    // 3. CREATE ORGANIZATION ADMIN (called from SuperAdmin)
    // ============================================
    @Transactional
    public ApiResponse<OrganizationAdminResponse> createOrganizationAdmin(
            OrganizationAdminCreateRequest request) {
        try {
            if (organizationAdminRepository.existsByEmail(request.getEmail())) {
                return ApiResponse.error("Creation failed", "Email already exists");
            }

            OrganizationAdmin orgAdmin = new OrganizationAdmin();
            orgAdmin.setEmail(request.getEmail());
            orgAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
            orgAdmin.setFullName(request.getFullName());
            orgAdmin.setPhone(request.getPhone());
            orgAdmin.setCompanyName(request.getCompanyName());
            orgAdmin.setIsActive(true);
            orgAdmin.setMustChangePassword(true);
            orgAdmin.setCreatedBy(null);

            OrganizationAdmin saved = organizationAdminRepository.save(orgAdmin);
            return ApiResponse.success("Organization Admin created successfully",
                    buildOrgAdminResponse(saved));

        } catch (Exception e) {
            log.error("❌ Error creating OrganizationAdmin", e);
            return ApiResponse.error("Creation failed", e.getMessage());
        }
    }

    // ============================================
    // 4. GET ALL ORGANIZATION ADMINS
    // ============================================
    public ApiResponse<List<OrganizationAdminResponse>> getAllOrganizationAdmins() {
        try {
            List<OrganizationAdminResponse> responses = organizationAdminRepository.findAll()
                    .stream()
                    .map(this::buildOrgAdminResponse)
                    .collect(Collectors.toList());
            return ApiResponse.success("Organization Admins retrieved successfully", responses);
        } catch (Exception e) {
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
            return ApiResponse.success("SuperAdmin retrieved successfully",
                    buildSuperAdminResponse(superAdmin, null));
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve SuperAdmin", e.getMessage());
        }
    }

    // ============================================
    // 6. UPDATE SUPERADMIN
    // ============================================
    @Transactional
    public ApiResponse<SuperAdminResponse> updateSuperAdmin(Long id, SuperAdminUpdateRequest request) {
        try {
            log.info("✏️ Updating SuperAdmin ID: {}", id);

            SuperAdmin superAdmin = superAdminRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("SuperAdmin not found"));

            if (request.getFullName() != null && !request.getFullName().isBlank())
                superAdmin.setFullName(request.getFullName());
            if (request.getPhone() != null && !request.getPhone().isBlank())
                superAdmin.setPhone(request.getPhone());
            if (request.getCompanyName() != null && !request.getCompanyName().isBlank())
                superAdmin.setCompanyName(request.getCompanyName());
            if (request.getOrganizationName() != null && !request.getOrganizationName().isBlank())
                superAdmin.setOrganizationName(request.getOrganizationName());
            if (request.getPassword() != null && !request.getPassword().isBlank())
                superAdmin.setPassword(passwordEncoder.encode(request.getPassword()));

            SuperAdmin updated = superAdminRepository.save(superAdmin);
            log.info("✅ SuperAdmin updated: {}", updated.getEmail());

            return ApiResponse.success("SuperAdmin updated successfully",
                    buildSuperAdminResponse(updated, null));

        } catch (Exception e) {
            log.error("❌ Error updating SuperAdmin: {}", id, e);
            return ApiResponse.error("Failed to update SuperAdmin", e.getMessage());
        }
    }

    // ============================================
    // 7. UPLOAD LOGO FOR SUPERADMIN
    // ============================================
    @Transactional
    public ApiResponse<SuperAdminResponse> uploadLogo(Long id, MultipartFile logoFile) {
        try {
            log.info("🖼️ Uploading logo for SuperAdmin ID: {}", id);

            SuperAdmin superAdmin = superAdminRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("SuperAdmin not found with ID: " + id));

            if (superAdmin.getLogoUrl() != null) {
                fileStorageService.deleteLogo(superAdmin.getLogoUrl());
            }

            String newLogoUrl = fileStorageService.storeLogo(logoFile, id);
            superAdmin.setLogoUrl(newLogoUrl);

            SuperAdmin updated = superAdminRepository.save(superAdmin);
            log.info("✅ Logo uploaded for SuperAdmin ID: {}", id);

            return ApiResponse.success("Logo uploaded successfully",
                    buildSuperAdminResponse(updated, null));

        } catch (IllegalArgumentException e) {
            return ApiResponse.error("Logo upload failed", e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error uploading logo for SuperAdmin ID: {}", id, e);
            return ApiResponse.error("Logo upload failed", e.getMessage());
        }
    }

    // ============================================
    // 8. GET LOGO (base64) FOR SUPERADMIN
    // ============================================
    public ApiResponse<String> getSuperAdminLogo(Long id) {
        try {
            SuperAdmin superAdmin = superAdminRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("SuperAdmin not found"));

            if (superAdmin.getLogoUrl() == null) {
                return ApiResponse.success("No logo found", null);
            }

            byte[] logoBytes = fileStorageService.readLogoAsBytes(superAdmin.getLogoUrl());
            if (logoBytes == null) {
                return ApiResponse.success("No logo found", null);
            }

            String mimeType = getMimeType(superAdmin.getLogoUrl());
            String base64 = "data:" + mimeType + ";base64," +
                    Base64.getEncoder().encodeToString(logoBytes);

            return ApiResponse.success("Logo retrieved", base64);

        } catch (Exception e) {
            log.error("❌ Error getting SuperAdmin logo: {}", id, e);
            return ApiResponse.error("Failed to get logo", e.getMessage());
        }
    }

    // ============================================
    // BUILD RESPONSE HELPERS
    // ============================================
    private SuperAdminResponse buildSuperAdminResponse(SuperAdmin superAdmin, String token) {
        String logoBase64 = null;
        if (superAdmin.getLogoUrl() != null) {
            try {
                byte[] bytes = fileStorageService.readLogoAsBytes(superAdmin.getLogoUrl());
                if (bytes != null) {
                    String mime = getMimeType(superAdmin.getLogoUrl());
                    logoBase64 = "data:" + mime + ";base64," +
                            Base64.getEncoder().encodeToString(bytes);
                }
            } catch (Exception e) {
                log.warn("⚠️ Could not read logo for SuperAdmin {}", superAdmin.getId());
            }
        }

        return SuperAdminResponse.builder()
                .id(superAdmin.getId())
                .email(superAdmin.getEmail())
                .fullName(superAdmin.getFullName())
                .phone(superAdmin.getPhone())
                .companyName(superAdmin.getCompanyName())
                .organizationName(superAdmin.getOrganizationName())
                .logoUrl(superAdmin.getLogoUrl())
                .logoBase64(logoBase64)
                .isActive(superAdmin.getIsActive())
                .token(token)
                .build();
    }

    private OrganizationAdminResponse buildOrgAdminResponse(OrganizationAdmin orgAdmin) {
        String logoBase64 = null;
        if (orgAdmin.getLogoUrl() != null) {
            try {
                byte[] bytes = fileStorageService.readLogoAsBytes(orgAdmin.getLogoUrl());
                if (bytes != null) {
                    String mime = getMimeType(orgAdmin.getLogoUrl());
                    logoBase64 = "data:" + mime + ";base64," +
                            Base64.getEncoder().encodeToString(bytes);
                }
            } catch (Exception e) {
                log.warn("⚠️ Could not read logo for OrgAdmin {}", orgAdmin.getId());
            }
        }

        return OrganizationAdminResponse.builder()
                .id(orgAdmin.getId())
                .email(orgAdmin.getEmail())
                .fullName(orgAdmin.getFullName())
                .phone(orgAdmin.getPhone())
                .companyName(orgAdmin.getCompanyName())
                .isActive(orgAdmin.getIsActive())
                .logoUrl(orgAdmin.getLogoUrl())
                .logoBase64(logoBase64)
                .createdAt(orgAdmin.getCreatedAt() != null ?
                        orgAdmin.getCreatedAt().format(DATE_FORMATTER) : null)
                .lastLogin(orgAdmin.getLastLogin() != null ?
                        orgAdmin.getLastLogin().format(DATE_FORMATTER) : null)
                .build();
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