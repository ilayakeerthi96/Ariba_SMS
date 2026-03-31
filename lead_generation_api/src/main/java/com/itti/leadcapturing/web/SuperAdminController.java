
// // package com.itti.leadcapturing.web;

// // import com.itti.leadcapturing.dto.*;
// // import com.itti.leadcapturing.service.SuperAdminService;
// // import jakarta.validation.Valid;
// // import lombok.RequiredArgsConstructor;
// // import lombok.extern.slf4j.Slf4j;
// // import org.springframework.http.HttpStatus;
// // import org.springframework.http.ResponseEntity;
// // import org.springframework.web.bind.annotation.*;

// // import java.util.List;

// // /**
// //  * ✅ UPDATED: SuperAdmin REST Controller
// //  * Now focuses on creating OrganizationAdmins
// //  * Base URL: /api/superadmin
// //  * 
// //  * Location: src/main/java/com/itti/leadcapturing/web/SuperAdminController.java
// //  */
// // @RestController
// // @RequestMapping("/api/superadmin")
// // @RequiredArgsConstructor
// // @Slf4j
// // @CrossOrigin(origins = "*", maxAge = 3600)
// // public class SuperAdminController {

// //     private final SuperAdminService superAdminService;

// //     // ============================================
// //     // 1. REGISTER SUPERADMIN
// //     // ============================================
// //     /**
// //      * Register a new SuperAdmin (Initial Setup)
// //      * POST /api/superadmin/auth/register
// //      */
// //     @PostMapping("/auth/register")
// //     public ResponseEntity<ApiResponse<SuperAdminResponse>> register(
// //             @Valid @RequestBody SuperAdminRegistrationRequest request) {
        
// //         log.info("📥 SuperAdmin registration request: {}", request.getEmail());
// //         ApiResponse<SuperAdminResponse> response = superAdminService.registerSuperAdmin(request);
        
// //         if (response.getSuccess()) {
// //             return ResponseEntity.status(HttpStatus.CREATED).body(response);
// //         } else {
// //             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
// //         }
// //     }

// //     // ============================================
// //     // 2. LOGIN SUPERADMIN
// //     // ============================================
// //     /**
// //      * Login SuperAdmin
// //      * POST /api/superadmin/auth/login
// //      */
// //     @PostMapping("/auth/login")
// //     public ResponseEntity<ApiResponse<SuperAdminResponse>> login(
// //             @Valid @RequestBody SuperAdminLoginRequest request) {
        
// //         log.info("🔐 SuperAdmin login attempt: {}", request.getEmail());
// //         ApiResponse<SuperAdminResponse> response = superAdminService.loginSuperAdmin(request);
        
// //         if (response.getSuccess()) {
// //             return ResponseEntity.ok(response);
// //         } else {
// //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
// //         }
// //     }

// //     // ============================================
// //     // 🆕 3. CREATE ORGANIZATION ADMIN
// //     // ============================================
// //     /**
// //      * Create Organization Admin (Main SuperAdmin Function)
// //      * POST /api/superadmin/create-org-admin
// //      */
// //     @PostMapping("/create-org-admin")
// //     public ResponseEntity<ApiResponse<OrganizationAdminResponse>> createOrganizationAdmin(
// //             @Valid @RequestBody OrganizationAdminCreateRequest request) {
        
// //         log.info("📥 SuperAdmin creating OrganizationAdmin: {}", request.getEmail());
// //         ApiResponse<OrganizationAdminResponse> response = 
// //             superAdminService.createOrganizationAdmin(request);
        
// //         if (response.getSuccess()) {
// //             return ResponseEntity.status(HttpStatus.CREATED).body(response);
// //         } else {
// //             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
// //         }
// //     }

// //     // ============================================
// //     // 🆕 4. GET ALL ORGANIZATION ADMINS
// //     // ============================================
// //     /**
// //      * Get all Organization Admins (for SuperAdmin dashboard)
// //      * GET /api/superadmin/org-admins
// //      */
// //     @GetMapping("/org-admins")
// //     public ResponseEntity<ApiResponse<List<OrganizationAdminResponse>>> getAllOrganizationAdmins() {
// //         log.info("📥 Fetching all OrganizationAdmins");
// //         ApiResponse<List<OrganizationAdminResponse>> response = 
// //             superAdminService.getAllOrganizationAdmins();
        
// //         return ResponseEntity.ok(response);
// //     }

// //     // ============================================
// //     // 5. GET SUPERADMIN BY ID
// //     // ============================================
// //     /**
// //      * Get SuperAdmin by ID
// //      * GET /api/superadmin/{id}
// //      */
// //     @GetMapping("/{id}")
// //     public ResponseEntity<ApiResponse<SuperAdminResponse>> getSuperAdminById(@PathVariable Long id) {
// //         log.info("📥 Fetching SuperAdmin: {}", id);
// //         ApiResponse<SuperAdminResponse> response = superAdminService.getSuperAdminById(id);
        
// //         if (response.getSuccess()) {
// //             return ResponseEntity.ok(response);
// //         } else {
// //             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
// //         }
// //     }

// //     // ============================================
// //     // 6. UPDATE SUPERADMIN
// //     // ============================================
// //     /**
// //      * Update SuperAdmin
// //      * PUT /api/superadmin/{id}
// //      */
// //     @PutMapping("/{id}")
// //     public ResponseEntity<ApiResponse<SuperAdminResponse>> updateSuperAdmin(
// //             @PathVariable Long id,
// //             @Valid @RequestBody SuperAdminRegistrationRequest request) {
        
// //         log.info("✏️ Updating SuperAdmin: {}", id);
// //         ApiResponse<SuperAdminResponse> response = 
// //             superAdminService.updateSuperAdmin(id, request);
        
// //         if (response.getSuccess()) {
// //             return ResponseEntity.ok(response);
// //         } else {
// //             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
// //         }
// //     }

// //     // ============================================
// //     // 7. HEALTH CHECK
// //     // ============================================
// //     /**
// //      * Health check endpoint
// //      * GET /api/superadmin/health
// //      */
// //     @GetMapping("/health")
// //     public ResponseEntity<String> healthCheck() {
// //         return ResponseEntity.ok("✅ SuperAdmin Service is running!");
// //     }
// // }


// package com.itti.leadcapturing.web;

// import com.itti.leadcapturing.dto.*;
// import com.itti.leadcapturing.service.SuperAdminService;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;

// import java.util.List;

// /**
//  * SuperAdmin REST Controller
//  * Base URL: /leadcapture/api/superadmin
//  *
//  * Location: src/main/java/com/itti/leadcapturing/web/SuperAdminController.java
//  */
// @RestController
// @RequestMapping("/api/superadmin")
// @RequiredArgsConstructor
// @Slf4j
// @CrossOrigin(origins = "*", maxAge = 3600)
// public class SuperAdminController {

//     private final SuperAdminService superAdminService;

//     // ============================================
//     // 1. REGISTER SUPERADMIN
//     // POST /api/superadmin/auth/register
//     // ============================================
//     @PostMapping("/auth/register")
//     public ResponseEntity<ApiResponse<SuperAdminResponse>> register(
//             @Valid @RequestBody SuperAdminRegistrationRequest request) {

//         log.info("📥 SuperAdmin registration: {}", request.getEmail());
//         ApiResponse<SuperAdminResponse> response = superAdminService.registerSuperAdmin(request);

//         return response.getSuccess()
//                 ? ResponseEntity.status(HttpStatus.CREATED).body(response)
//                 : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//     }

//     // ============================================
//     // 2. LOGIN SUPERADMIN
//     // POST /api/superadmin/auth/login
//     // ============================================
//     @PostMapping("/auth/login")
//     public ResponseEntity<ApiResponse<SuperAdminResponse>> login(
//             @Valid @RequestBody SuperAdminLoginRequest request) {

//         log.info("🔐 SuperAdmin login: {}", request.getEmail());
//         ApiResponse<SuperAdminResponse> response = superAdminService.loginSuperAdmin(request);

//         return response.getSuccess()
//                 ? ResponseEntity.ok(response)
//                 : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//     }

//     // ============================================
//     // 3. CREATE ORGANIZATION ADMIN
//     // POST /api/superadmin/create-org-admin
//     // ============================================
//     @PostMapping("/create-org-admin")
//     public ResponseEntity<ApiResponse<OrganizationAdminResponse>> createOrganizationAdmin(
//             @Valid @RequestBody OrganizationAdminCreateRequest request) {

//         log.info("📥 Creating OrganizationAdmin: {}", request.getEmail());
//         ApiResponse<OrganizationAdminResponse> response =
//                 superAdminService.createOrganizationAdmin(request);

//         return response.getSuccess()
//                 ? ResponseEntity.status(HttpStatus.CREATED).body(response)
//                 : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//     }

//     // ============================================
//     // 4. GET ALL ORGANIZATION ADMINS
//     // GET /api/superadmin/org-admins
//     // ============================================
//     @GetMapping("/org-admins")
//     public ResponseEntity<ApiResponse<List<OrganizationAdminResponse>>> getAllOrganizationAdmins() {
//         log.info("📥 Fetching all OrganizationAdmins");
//         return ResponseEntity.ok(superAdminService.getAllOrganizationAdmins());
//     }

//     // ============================================
//     // 5. GET SUPERADMIN BY ID
//     // GET /api/superadmin/{id}
//     // ============================================
//     @GetMapping("/{id}")
//     public ResponseEntity<ApiResponse<SuperAdminResponse>> getSuperAdminById(@PathVariable Long id) {
//         log.info("📥 Fetching SuperAdmin: {}", id);
//         ApiResponse<SuperAdminResponse> response = superAdminService.getSuperAdminById(id);

//         return response.getSuccess()
//                 ? ResponseEntity.ok(response)
//                 : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//     }

//     // ============================================
//     // ✅ 6. UPDATE SUPERADMIN (profile fields)
//     // PUT /api/superadmin/{id}
//     // ============================================
//     @PutMapping("/{id}")
//     public ResponseEntity<ApiResponse<SuperAdminResponse>> updateSuperAdmin(
//             @PathVariable Long id,
//             @RequestBody SuperAdminUpdateRequest request) {

//         log.info("✏️ Updating SuperAdmin: {}", id);
//         ApiResponse<SuperAdminResponse> response =
//                 superAdminService.updateSuperAdmin(id, request);

//         return response.getSuccess()
//                 ? ResponseEntity.ok(response)
//                 : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//     }

//     // ============================================
//     // ✅ 7. UPLOAD LOGO  (NEW)
//     // POST /api/superadmin/{id}/upload-logo
//     // Content-Type: multipart/form-data
//     // ============================================
//     @PostMapping(value = "/{id}/upload-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//     public ResponseEntity<ApiResponse<SuperAdminResponse>> uploadLogo(
//             @PathVariable Long id,
//             @RequestParam("logo") MultipartFile logoFile) {

//         log.info("🖼️ Logo upload request for SuperAdmin ID: {} | file: {} | size: {} bytes",
//                 id, logoFile.getOriginalFilename(), logoFile.getSize());

//         ApiResponse<SuperAdminResponse> response = superAdminService.uploadLogo(id, logoFile);

//         return response.getSuccess()
//                 ? ResponseEntity.ok(response)
//                 : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//     }

//     // Add to SuperAdminController

// // ✅ 7. UPLOAD LOGO
// // POST /api/superadmin/{id}/upload-logo
// @PostMapping(value = "/{id}/upload-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
// public ResponseEntity<ApiResponse<SuperAdminResponse>> uploadLogo(
//         @PathVariable Long id,
//         @RequestParam("logo") MultipartFile logoFile) {

//     log.info("🖼️ Logo upload for SuperAdmin ID: {}", id);
//     ApiResponse<SuperAdminResponse> response = superAdminService.uploadLogo(id, logoFile);

//     return response.getSuccess()
//             ? ResponseEntity.ok(response)
//             : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
// }

// // ✅ 8. GET LOGO (base64)
// // GET /api/superadmin/{id}/logo
// @GetMapping("/{id}/logo")
// public ResponseEntity<ApiResponse<String>> getSuperAdminLogo(@PathVariable Long id) {
//     log.info("🖼️ Get logo for SuperAdmin ID: {}", id);
//     ApiResponse<String> response = superAdminService.getSuperAdminLogo(id);

//     return response.getSuccess()
//             ? ResponseEntity.ok(response)
//             : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
// }

//     // ============================================
//     // 8. HEALTH CHECK
//     // GET /api/superadmin/health
//     // ============================================
//     @GetMapping("/health")
//     public ResponseEntity<String> healthCheck() {
//         return ResponseEntity.ok("✅ SuperAdmin Service is running!");
//     }
// }


package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.*;
import com.itti.leadcapturing.service.SuperAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/superadmin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    // 1. REGISTER
    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<SuperAdminResponse>> register(
            @Valid @RequestBody SuperAdminRegistrationRequest request) {
        log.info("📥 SuperAdmin registration: {}", request.getEmail());
        ApiResponse<SuperAdminResponse> response = superAdminService.registerSuperAdmin(request);
        return response.getSuccess()
                ? ResponseEntity.status(HttpStatus.CREATED).body(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 2. LOGIN
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<SuperAdminResponse>> login(
            @Valid @RequestBody SuperAdminLoginRequest request) {
        log.info("🔐 SuperAdmin login: {}", request.getEmail());
        ApiResponse<SuperAdminResponse> response = superAdminService.loginSuperAdmin(request);
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 3. CREATE ORG ADMIN
    @PostMapping("/create-org-admin")
    public ResponseEntity<ApiResponse<OrganizationAdminResponse>> createOrganizationAdmin(
            @Valid @RequestBody OrganizationAdminCreateRequest request) {
        log.info("📥 Creating OrganizationAdmin: {}", request.getEmail());
        ApiResponse<OrganizationAdminResponse> response =
                superAdminService.createOrganizationAdmin(request);
        return response.getSuccess()
                ? ResponseEntity.status(HttpStatus.CREATED).body(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 4. GET ALL ORG ADMINS
    @GetMapping("/org-admins")
    public ResponseEntity<ApiResponse<List<OrganizationAdminResponse>>> getAllOrganizationAdmins() {
        log.info("📥 Fetching all OrganizationAdmins");
        return ResponseEntity.ok(superAdminService.getAllOrganizationAdmins());
    }

    // 5. GET SUPERADMIN BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SuperAdminResponse>> getSuperAdminById(
            @PathVariable Long id) {
        log.info("📥 Fetching SuperAdmin: {}", id);
        ApiResponse<SuperAdminResponse> response = superAdminService.getSuperAdminById(id);
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 6. UPDATE SUPERADMIN
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SuperAdminResponse>> updateSuperAdmin(
            @PathVariable Long id,
            @RequestBody SuperAdminUpdateRequest request) {
        log.info("✏️ Updating SuperAdmin: {}", id);
        ApiResponse<SuperAdminResponse> response =
                superAdminService.updateSuperAdmin(id, request);
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 7. UPLOAD LOGO
    @PostMapping(value = "/{id}/upload-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SuperAdminResponse>> uploadLogo(
            @PathVariable Long id,
            @RequestParam("logo") MultipartFile logoFile) {
        log.info("🖼️ Logo upload for SuperAdmin ID: {} | file: {} | size: {} bytes",
                id, logoFile.getOriginalFilename(), logoFile.getSize());
        ApiResponse<SuperAdminResponse> response = superAdminService.uploadLogo(id, logoFile);
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 8. GET LOGO (base64)
    @GetMapping("/{id}/logo")
    public ResponseEntity<ApiResponse<String>> getSuperAdminLogo(@PathVariable Long id) {
        log.info("🖼️ Get logo for SuperAdmin ID: {}", id);
        ApiResponse<String> response = superAdminService.getSuperAdminLogo(id);
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 9. HEALTH CHECK
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("✅ SuperAdmin Service is running!");
    }
}