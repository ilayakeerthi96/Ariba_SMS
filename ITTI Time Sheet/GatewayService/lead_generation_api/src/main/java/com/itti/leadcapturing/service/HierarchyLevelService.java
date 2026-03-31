
package com.itti.leadcapturing.service;

import com.itti.leadcapturing.dto.*;
import com.itti.leadcapturing.model.HierarchyLevel;
import com.itti.leadcapturing.repo.HierarchyLevelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ✅ UPDATED: Supports level ordering with increments of 10
 * ✅ UPDATED: Soft delete only (no hard delete)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HierarchyLevelService {

    private final HierarchyLevelRepository hierarchyLevelRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * ✅ UPDATED: Suggest next level order (increments of 10)
     */
    public ApiResponse<Integer> suggestNextLevelOrder(String companyName) {
        try {
            List<HierarchyLevel> existingLevels = hierarchyLevelRepository
                    .findByCompanyNameAndIsActiveOrderByLevelOrderAsc(companyName, true);
            
            if (existingLevels.isEmpty()) {
                return ApiResponse.success("Next level order suggested", 10);
            }
            
            // Get the highest level order and add 10
            Integer maxOrder = existingLevels.stream()
                    .map(HierarchyLevel::getLevelOrder)
                    .max(Integer::compareTo)
                    .orElse(0);
            
            return ApiResponse.success("Next level order suggested", maxOrder + 10);
            
        } catch (Exception e) {
            log.error("Error suggesting next level order", e);
            return ApiResponse.error("Failed to suggest level order", e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<HierarchyLevelResponse> createHierarchyLevel(HierarchyLevelRequest request) {
        try {
            log.info("Creating hierarchy level: {} (Order: {}) for company: {}", 
                     request.getLevelName(), request.getLevelOrder(), request.getCompanyName());
            
            // Check if level name already exists for this company
            if (hierarchyLevelRepository.existsByLevelNameAndCompanyName(request.getLevelName(), request.getCompanyName())) {
                log.warn("Level name already exists: {} for company: {}", request.getLevelName(), request.getCompanyName());
                return ApiResponse.error("Creation failed", "Level name already exists for this company");
            }

            // ✅ UPDATED: Check if level order already exists (now allowing gaps like 10, 15, 20)
            if (hierarchyLevelRepository.findByLevelOrderAndCompanyName(request.getLevelOrder(), request.getCompanyName()).isPresent()) {
                log.warn("Level order already exists: {} for company: {}", request.getLevelOrder(), request.getCompanyName());
                return ApiResponse.error("Creation failed", 
                    "Level order " + request.getLevelOrder() + " already exists. Try a different number (e.g., " + 
                    (request.getLevelOrder() + 5) + " or " + (request.getLevelOrder() - 5) + ")");
            }

            // Create new hierarchy level
            HierarchyLevel hierarchyLevel = new HierarchyLevel();
            hierarchyLevel.setLevelName(request.getLevelName());
            hierarchyLevel.setLevelOrder(request.getLevelOrder());
            hierarchyLevel.setDescription(request.getDescription());
            hierarchyLevel.setCompanyName(request.getCompanyName());
            hierarchyLevel.setIsActive(true);
            hierarchyLevel.setIsDeleted(false);

            // Save hierarchy level
            HierarchyLevel savedLevel = hierarchyLevelRepository.save(hierarchyLevel);
            log.info("Hierarchy level created successfully: {} (Order: {})", savedLevel.getLevelName(), savedLevel.getLevelOrder());

            // Build response
            HierarchyLevelResponse response = buildHierarchyLevelResponse(savedLevel);
            return ApiResponse.success("Hierarchy level created successfully", response);

        } catch (Exception e) {
            log.error("Error creating hierarchy level", e);
            return ApiResponse.error("Creation failed", e.getMessage());
        }
    }

    public ApiResponse<HierarchyLevelResponse> getHierarchyLevelById(Long id) {
        try {
            HierarchyLevel hierarchyLevel = hierarchyLevelRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Hierarchy level not found"));

            HierarchyLevelResponse response = buildHierarchyLevelResponse(hierarchyLevel);
            return ApiResponse.success("Hierarchy level retrieved successfully", response);

        } catch (Exception e) {
            log.error("Error getting hierarchy level by ID: {}", id, e);
            return ApiResponse.error("Failed to retrieve hierarchy level", e.getMessage());
        }
    }

    public ApiResponse<List<HierarchyLevelResponse>> getAllHierarchyLevels() {
        try {
            List<HierarchyLevel> hierarchyLevels = hierarchyLevelRepository.findByIsActiveOrderByLevelOrderAsc(true);
            
            List<HierarchyLevelResponse> responses = hierarchyLevels.stream()
                    .map(this::buildHierarchyLevelResponse)
                    .collect(Collectors.toList());

            return ApiResponse.success("Hierarchy levels retrieved successfully", responses);

        } catch (Exception e) {
            log.error("Error getting all hierarchy levels", e);
            return ApiResponse.error("Failed to retrieve hierarchy levels", e.getMessage());
        }
    }

    public ApiResponse<List<HierarchyLevelResponse>> getHierarchyLevelsByCompany(String companyName) {
        try {
            log.info("Fetching hierarchy levels for company: {}", companyName);
            
            List<HierarchyLevel> hierarchyLevels = hierarchyLevelRepository
                    .findByCompanyNameAndIsActiveOrderByLevelOrderAsc(companyName, true);
            
            List<HierarchyLevelResponse> responses = hierarchyLevels.stream()
                    .map(this::buildHierarchyLevelResponse)
                    .collect(Collectors.toList());

            log.info("Found {} hierarchy levels for company: {}", responses.size(), companyName);
            return ApiResponse.success("Hierarchy levels retrieved successfully", responses);

        } catch (Exception e) {
            log.error("Error getting hierarchy levels by company: {}", companyName, e);
            return ApiResponse.error("Failed to retrieve hierarchy levels", e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<HierarchyLevelResponse> updateHierarchyLevel(Long id, HierarchyLevelUpdateRequest request) {
        try {
            log.info("Updating hierarchy level with ID: {}", id);
            
            HierarchyLevel hierarchyLevel = hierarchyLevelRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Hierarchy level not found"));

            // Update fields if provided
            if (request.getLevelName() != null && !request.getLevelName().isEmpty()) {
                // Check if new level name already exists for this company (excluding current level)
                if (hierarchyLevelRepository.existsByLevelNameAndCompanyName(request.getLevelName(), hierarchyLevel.getCompanyName())) {
                    HierarchyLevel existingLevel = hierarchyLevelRepository
                            .findByCompanyNameAndIsActiveOrderByLevelOrderAsc(hierarchyLevel.getCompanyName(), true)
                            .stream()
                            .filter(l -> l.getLevelName().equals(request.getLevelName()) && !l.getId().equals(id))
                            .findFirst()
                            .orElse(null);
                    
                    if (existingLevel != null) {
                        return ApiResponse.error("Update failed", "Level name already exists for this company");
                    }
                }
                hierarchyLevel.setLevelName(request.getLevelName());
            }

            if (request.getLevelOrder() != null) {
                // Check if new level order already exists for this company (excluding current level)
                hierarchyLevelRepository.findByLevelOrderAndCompanyName(request.getLevelOrder(), hierarchyLevel.getCompanyName())
                        .ifPresent(existingLevel -> {
                            if (!existingLevel.getId().equals(id)) {
                                throw new RuntimeException("Level order already exists for this company");
                            }
                        });
                hierarchyLevel.setLevelOrder(request.getLevelOrder());
            }

            if (request.getDescription() != null) {
                hierarchyLevel.setDescription(request.getDescription());
            }

            // Save updated hierarchy level
            HierarchyLevel updatedLevel = hierarchyLevelRepository.save(hierarchyLevel);
            log.info("Hierarchy level updated successfully: {}", updatedLevel.getLevelName());

            HierarchyLevelResponse response = buildHierarchyLevelResponse(updatedLevel);
            return ApiResponse.success("Hierarchy level updated successfully", response);

        } catch (Exception e) {
            log.error("Error updating hierarchy level: {}", id, e);
            return ApiResponse.error("Update failed", e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<String> deactivateHierarchyLevel(Long id) {
        try {
            HierarchyLevel hierarchyLevel = hierarchyLevelRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Hierarchy level not found"));

            hierarchyLevel.setIsActive(false);
            hierarchyLevelRepository.save(hierarchyLevel);

            log.info("Hierarchy level deactivated: {}", hierarchyLevel.getLevelName());
            return ApiResponse.success("Hierarchy level deactivated successfully", null);

        } catch (Exception e) {
            log.error("Error deactivating hierarchy level: {}", id, e);
            return ApiResponse.error("Failed to deactivate hierarchy level", e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<String> activateHierarchyLevel(Long id) {
        try {
            HierarchyLevel hierarchyLevel = hierarchyLevelRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Hierarchy level not found"));

            hierarchyLevel.setIsActive(true);
            hierarchyLevelRepository.save(hierarchyLevel);

            log.info("Hierarchy level activated: {}", hierarchyLevel.getLevelName());
            return ApiResponse.success("Hierarchy level activated successfully", null);

        } catch (Exception e) {
            log.error("Error activating hierarchy level: {}", id, e);
            return ApiResponse.error("Failed to activate hierarchy level", e.getMessage());
        }
    }

    /**
     * ✅ UPDATED: Soft delete only (marks isDeleted = true)
     */
    @Transactional
    public ApiResponse<String> deleteHierarchyLevel(Long id) {
        try {
            HierarchyLevel hierarchyLevel = hierarchyLevelRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Hierarchy level not found"));

            // Soft delete
            hierarchyLevel.setIsDeleted(true);
            hierarchyLevel.setIsActive(false);
            hierarchyLevelRepository.save(hierarchyLevel);

            log.info("Hierarchy level soft deleted: {}", hierarchyLevel.getLevelName());
            return ApiResponse.success("Hierarchy level deleted successfully", null);

        } catch (Exception e) {
            log.error("Error deleting hierarchy level: {}", id, e);
            return ApiResponse.error("Failed to delete hierarchy level", e.getMessage());
        }
    }

    /**
     * ✅ Note: Reorder is less relevant with level order gaps (10, 20, 30)
     * but kept for backward compatibility
     */
    @Transactional
    public ApiResponse<List<HierarchyLevelResponse>> reorderHierarchyLevels(String companyName, List<Long> levelIds) {
        try {
            log.info("Reordering hierarchy levels for company: {}", companyName);
            
            // Validate all level IDs exist and belong to the company
            for (int i = 0; i < levelIds.size(); i++) {
                Long levelId = levelIds.get(i);
                HierarchyLevel level = hierarchyLevelRepository.findById(levelId)
                        .orElseThrow(() -> new RuntimeException("Hierarchy level not found: " + levelId));
                
                if (!level.getCompanyName().equals(companyName)) {
                    throw new RuntimeException("Level does not belong to company: " + levelId);
                }
                
                // Update level order (10-based with gaps)
                level.setLevelOrder((i + 1) * 10);
                hierarchyLevelRepository.save(level);
            }

            // Fetch updated levels
            List<HierarchyLevel> updatedLevels = hierarchyLevelRepository
                    .findByCompanyNameAndIsActiveOrderByLevelOrderAsc(companyName, true);
            
            List<HierarchyLevelResponse> responses = updatedLevels.stream()
                    .map(this::buildHierarchyLevelResponse)
                    .collect(Collectors.toList());

            log.info("Hierarchy levels reordered successfully for company: {}", companyName);
            return ApiResponse.success("Hierarchy levels reordered successfully", responses);

        } catch (Exception e) {
            log.error("Error reordering hierarchy levels for company: {}", companyName, e);
            return ApiResponse.error("Failed to reorder hierarchy levels", e.getMessage());
        }
    }

    private HierarchyLevelResponse buildHierarchyLevelResponse(HierarchyLevel hierarchyLevel) {
        return HierarchyLevelResponse.builder()
                .id(hierarchyLevel.getId())
                .levelName(hierarchyLevel.getLevelName())
                .levelOrder(hierarchyLevel.getLevelOrder())
                .description(hierarchyLevel.getDescription())
                .companyName(hierarchyLevel.getCompanyName())
                .isActive(hierarchyLevel.getIsActive())
                .createdAt(hierarchyLevel.getCreatedAt() != null ? 
                          hierarchyLevel.getCreatedAt().format(DATE_FORMATTER) : null)
                .updatedAt(hierarchyLevel.getUpdatedAt() != null ? 
                          hierarchyLevel.getUpdatedAt().format(DATE_FORMATTER) : null)
                .build();
    }
}