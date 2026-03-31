

package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.*;
import com.itti.leadcapturing.service.HierarchyLevelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hierarchy-levels")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class HierarchyLevelController {

    private final HierarchyLevelService hierarchyLevelService;

    /**
     * POST /api/hierarchy-level
     * Create new hierarchy level
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HierarchyLevelResponse>> createHierarchyLevel(
            @Valid @RequestBody HierarchyLevelRequest request) {
        
        log.info("📥 Creating hierarchy level: {}", request.getLevelName());
        ApiResponse<HierarchyLevelResponse> response = 
            hierarchyLevelService.createHierarchyLevel(request);
        
        if (response.getSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * GET /api/hierarchy-level/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HierarchyLevelResponse>> getHierarchyLevelById(@PathVariable Long id) {
        log.info("📥 Fetching hierarchy level: {}", id);
        ApiResponse<HierarchyLevelResponse> response = hierarchyLevelService.getHierarchyLevelById(id);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * GET /api/hierarchy-level
     * Get all hierarchy levels
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<HierarchyLevelResponse>>> getAllHierarchyLevels() {
        log.info("📥 Fetching all hierarchy levels");
        ApiResponse<List<HierarchyLevelResponse>> response = 
            hierarchyLevelService.getAllHierarchyLevels();
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/hierarchy-level/company/{companyName}
     * Get hierarchy levels by company
     */
    @GetMapping("/company/{companyName}")
    public ResponseEntity<ApiResponse<List<HierarchyLevelResponse>>> getHierarchyLevelsByCompany(
            @PathVariable String companyName) {
        
        log.info("📥 Fetching hierarchy levels for company: {}", companyName);
        ApiResponse<List<HierarchyLevelResponse>> response = 
            hierarchyLevelService.getHierarchyLevelsByCompany(companyName);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/hierarchy-level/suggest-next-order/{companyName}
     * Suggest next level order
     */
    @GetMapping("/suggest-next-order/{companyName}")
    public ResponseEntity<ApiResponse<Integer>> suggestNextLevelOrder(@PathVariable String companyName) {
        log.info("📥 Suggesting next level order for company: {}", companyName);
        ApiResponse<Integer> response = hierarchyLevelService.suggestNextLevelOrder(companyName);
        
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/hierarchy-level/{id}
     * Update hierarchy level
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HierarchyLevelResponse>> updateHierarchyLevel(
            @PathVariable Long id,
            @Valid @RequestBody HierarchyLevelUpdateRequest request) {
        
        log.info("✏️ Updating hierarchy level: {}", id);
        ApiResponse<HierarchyLevelResponse> response = 
            hierarchyLevelService.updateHierarchyLevel(id, request);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * PUT /api/hierarchy-level/{id}/deactivate
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateHierarchyLevel(@PathVariable Long id) {
        log.info("🚫 Deactivating hierarchy level: {}", id);
        ApiResponse<String> response = hierarchyLevelService.deactivateHierarchyLevel(id);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * PUT /api/hierarchy-level/{id}/activate
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<String>> activateHierarchyLevel(@PathVariable Long id) {
        log.info("✅ Activating hierarchy level: {}", id);
        ApiResponse<String> response = hierarchyLevelService.activateHierarchyLevel(id);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * DELETE /api/hierarchy-level/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteHierarchyLevel(@PathVariable Long id) {
        log.info("🗑️ Deleting hierarchy level: {}", id);
        ApiResponse<String> response = hierarchyLevelService.deleteHierarchyLevel(id);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}