
package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.*;
import com.itti.leadcapturing.service.HierarchyUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hierarchy-users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class HierarchyUserController {

    private final HierarchyUserService hierarchyUserService;

    /**
     * POST /api/hierarchy-user/auth/login
     * Login hierarchy user
     */
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<HierarchyUserResponse>> loginUser(
            @Valid @RequestBody HierarchyUserLoginRequest request) {
        
        log.info("🔐 Hierarchy user login attempt: {}", request.getEmail());
        ApiResponse<HierarchyUserResponse> response = hierarchyUserService.loginUser(request);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * POST /api/hierarchy-user
     * Create hierarchy user
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HierarchyUserDetailResponse>> createHierarchyUser(
            @Valid @RequestBody HierarchyUserCreateRequest request) {
        
        log.info("📥 Creating hierarchy user: {}", request.getEmail());
        ApiResponse<HierarchyUserDetailResponse> response = 
            hierarchyUserService.createHierarchyUser(request);
        
        if (response.getSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * GET /api/hierarchy-user/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HierarchyUserDetailResponse>> getHierarchyUserById(@PathVariable Long id) {
        log.info("📥 Fetching hierarchy user: {}", id);
        ApiResponse<HierarchyUserDetailResponse> response = hierarchyUserService.getHierarchyUserById(id);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * GET /api/hierarchy-user/company/{companyName}
     */
    @GetMapping("/company/{companyName}")
    public ResponseEntity<ApiResponse<List<HierarchyUserDetailResponse>>> getUsersByCompany(
            @PathVariable String companyName) {
        
        log.info("📥 Fetching users for company: {}", companyName);
        ApiResponse<List<HierarchyUserDetailResponse>> response = 
            hierarchyUserService.getUsersByCompany(companyName);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/hierarchy-user/level/{levelId}
     */
    @GetMapping("/level/{levelId}")
    public ResponseEntity<ApiResponse<List<HierarchyUserDetailResponse>>> getUsersByHierarchyLevel(
            @PathVariable Long levelId) {
        
        log.info("📥 Fetching users for hierarchy level: {}", levelId);
        ApiResponse<List<HierarchyUserDetailResponse>> response = 
            hierarchyUserService.getUsersByHierarchyLevel(levelId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/hierarchy-user/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HierarchyUserDetailResponse>> updateHierarchyUser(
            @PathVariable Long id,
            @Valid @RequestBody HierarchyUserUpdateRequest request) {
        
        log.info("✏️ Updating hierarchy user: {}", id);
        ApiResponse<HierarchyUserDetailResponse> response = 
            hierarchyUserService.updateHierarchyUser(id, request);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * PUT /api/hierarchy-user/{id}/deactivate
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateUser(@PathVariable Long id) {
        log.info("🚫 Deactivating hierarchy user: {}", id);
        ApiResponse<String> response = hierarchyUserService.deactivateUser(id);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * PUT /api/hierarchy-user/{id}/activate
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<String>> activateUser(@PathVariable Long id) {
        log.info("✅ Activating hierarchy user: {}", id);
        ApiResponse<String> response = hierarchyUserService.activateUser(id);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * DELETE /api/hierarchy-user/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        log.info("🗑️ Deleting hierarchy user: {}", id);
        ApiResponse<String> response = hierarchyUserService.deleteUser(id);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}