
package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.*;
import com.itti.leadcapturing.service.SuperAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ✅ UPDATED: SuperAdmin REST Controller
 * Now focuses on creating OrganizationAdmins
 * Base URL: /api/superadmin
 * 
 * Location: src/main/java/com/itti/leadcapturing/web/SuperAdminController.java
 */
@RestController
@RequestMapping("/api/superadmin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    // ============================================
    // 1. REGISTER SUPERADMIN
    // ============================================
    /**
     * Register a new SuperAdmin (Initial Setup)
     * POST /api/superadmin/auth/register
     */
    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<SuperAdminResponse>> register(
            @Valid @RequestBody SuperAdminRegistrationRequest request) {
        
        log.info("📥 SuperAdmin registration request: {}", request.getEmail());
        ApiResponse<SuperAdminResponse> response = superAdminService.registerSuperAdmin(request);
        
        if (response.getSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ============================================
    // 2. LOGIN SUPERADMIN
    // ============================================
    /**
     * Login SuperAdmin
     * POST /api/superadmin/auth/login
     */
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<SuperAdminResponse>> login(
            @Valid @RequestBody SuperAdminLoginRequest request) {
        
        log.info("🔐 SuperAdmin login attempt: {}", request.getEmail());
        ApiResponse<SuperAdminResponse> response = superAdminService.loginSuperAdmin(request);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // ============================================
    // 🆕 3. CREATE ORGANIZATION ADMIN
    // ============================================
    /**
     * Create Organization Admin (Main SuperAdmin Function)
     * POST /api/superadmin/create-org-admin
     */
    @PostMapping("/create-org-admin")
    public ResponseEntity<ApiResponse<OrganizationAdminResponse>> createOrganizationAdmin(
            @Valid @RequestBody OrganizationAdminCreateRequest request) {
        
        log.info("📥 SuperAdmin creating OrganizationAdmin: {}", request.getEmail());
        ApiResponse<OrganizationAdminResponse> response = 
            superAdminService.createOrganizationAdmin(request);
        
        if (response.getSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ============================================
    // 🆕 4. GET ALL ORGANIZATION ADMINS
    // ============================================
    /**
     * Get all Organization Admins (for SuperAdmin dashboard)
     * GET /api/superadmin/org-admins
     */
    @GetMapping("/org-admins")
    public ResponseEntity<ApiResponse<List<OrganizationAdminResponse>>> getAllOrganizationAdmins() {
        log.info("📥 Fetching all OrganizationAdmins");
        ApiResponse<List<OrganizationAdminResponse>> response = 
            superAdminService.getAllOrganizationAdmins();
        
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 5. GET SUPERADMIN BY ID
    // ============================================
    /**
     * Get SuperAdmin by ID
     * GET /api/superadmin/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SuperAdminResponse>> getSuperAdminById(@PathVariable Long id) {
        log.info("📥 Fetching SuperAdmin: {}", id);
        ApiResponse<SuperAdminResponse> response = superAdminService.getSuperAdminById(id);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // ============================================
    // 6. UPDATE SUPERADMIN
    // ============================================
    /**
     * Update SuperAdmin
     * PUT /api/superadmin/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SuperAdminResponse>> updateSuperAdmin(
            @PathVariable Long id,
            @Valid @RequestBody SuperAdminRegistrationRequest request) {
        
        log.info("✏️ Updating SuperAdmin: {}", id);
        ApiResponse<SuperAdminResponse> response = 
            superAdminService.updateSuperAdmin(id, request);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // ============================================
    // 7. HEALTH CHECK
    // ============================================
    /**
     * Health check endpoint
     * GET /api/superadmin/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("✅ SuperAdmin Service is running!");
    }
}