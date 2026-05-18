
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

        // ============================================================
// ADD THESE TWO ENDPOINTS to SuperAdminController.java
// ============================================================

    // SAVE THEME
    @PutMapping("/{id}/theme")
    public ResponseEntity<ApiResponse<String>> updateTheme(
            @PathVariable Long id,
            @RequestBody ThemeUpdateRequest request) {
        log.info("🎨 Theme update for SuperAdmin ID: {}", id);
        ApiResponse<String> response = superAdminService.updateSuperAdminTheme(id, request.getTheme());
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // GET THEME
    @GetMapping("/{id}/theme")
    public ResponseEntity<ApiResponse<String>> getTheme(@PathVariable Long id) {
        log.info("🎨 Get theme for SuperAdmin ID: {}", id);
        ApiResponse<String> response = superAdminService.getSuperAdminTheme(id);
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


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