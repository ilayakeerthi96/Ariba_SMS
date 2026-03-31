

package com.itti.leadcapturing.service;

import com.itti.leadcapturing.dto.*;
import com.itti.leadcapturing.model.HierarchyUser;
import com.itti.leadcapturing.model.HierarchyLevel;
import com.itti.leadcapturing.repo.HierarchyUserRepository;
import com.itti.leadcapturing.repo.HierarchyLevelRepository;
import com.itti.leadcapturing.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HierarchyUserService {

    private final HierarchyUserRepository hierarchyUserRepository;
    private final HierarchyLevelRepository hierarchyLevelRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ============================================
    // ✅ COMPLETELY FIXED: LOGIN WITH HIERARCHY LEVEL DATA
    // ============================================
    @Transactional
    public ApiResponse<HierarchyUserResponse> loginUser(HierarchyUserLoginRequest request) {
        try {
            log.info("=".repeat(80));
            log.info("🔐 Hierarchy user login attempt: {}", request.getEmail());

            // Step 1: Find user by email
            HierarchyUser user = hierarchyUserRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid email or password"));

            log.info("  [✓] Found user: {} (ID: {})", user.getFullName(), user.getId());
            log.info("  [✓] Designation: {}", user.getDesignation());
            log.info("  [✓] Company: {}", user.getCompanyName());
            log.info("  [✓] Is Active: {}", user.getIsActive());

            // ✅ CRITICAL: Check hierarchy level
            if (user.getHierarchyLevel() == null) {
                log.error("❌ User {} has NO hierarchy level assigned!", user.getEmail());
                return ApiResponse.error("Login failed", 
                    "Your account is not assigned to any hierarchy level. Please contact your administrator.");
            }

            log.info("  [✓] Hierarchy Level: {} (ID: {}, Order: {})", 
                     user.getHierarchyLevel().getLevelName(),
                     user.getHierarchyLevel().getId(),
                     user.getHierarchyLevel().getLevelOrder());

            // Step 2: Check if active
            if (!user.getIsActive()) {
                log.warn("❌ Login failed: User account is inactive - {}", request.getEmail());
                return ApiResponse.error("Login failed", "Account is inactive. Please contact your administrator.");
            }

            // Step 3: Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("❌ Login failed: Invalid password for email - {}", request.getEmail());
                return ApiResponse.error("Login failed", "Invalid email or password");
            }

            log.info("  [✓] Password verified");

            // Step 4: Update last login
            user.setLastLogin(LocalDateTime.now());
            hierarchyUserRepository.save(user);

            log.info("  [✓] Last login updated");

            // Step 5: Determine user role
            String role = determineUserRole(user);
            log.info("  [✓] Role determined: {}", role);

            // Step 6: ✅ FIXED: Generate token WITH user ID
            String token = jwtUtil.generateTokenWithId(
                user.getEmail(), 
                role, 
                user.getId() // ✅ CRITICAL: Pass user ID
            );

            log.info("  [✓] Token generated with user ID: {}", user.getId());

            // Step 7: ✅ COMPLETELY FIXED: Build response with COMPLETE hierarchy level data
            HierarchyUserResponse response = buildLoginResponse(user, token, role);

            log.info("=".repeat(80));
            log.info("✅ LOGIN SUCCESSFUL - RESPONSE DETAILS:");
            log.info("  User ID: {}", response.getId());
            log.info("  Full Name: {}", response.getFullName());
            log.info("  Email: {}", response.getEmail());
            log.info("  Role: {}", response.getRole());
            log.info("  Hierarchy Level ID: {}", response.getHierarchyLevelId());
            log.info("  Hierarchy Level Name: {}", response.getHierarchyLevelName());
            log.info("  Hierarchy Level Order: {}", response.getHierarchyLevelOrder());
            log.info("  Token: {}...", token.substring(0, Math.min(20, token.length())));
            
            // ✅ CRITICAL: Verify hierarchyLevel object exists in response
            if (response.getHierarchyLevelId() == null) {
                log.error("❌ WARNING: hierarchyLevelId is NULL in response!");
            }
            
            log.info("=".repeat(80));
            
            return ApiResponse.success("Login successful", response);

        } catch (Exception e) {
            log.error("❌ Error during hierarchy user login", e);
            e.printStackTrace();
            return ApiResponse.error("Login failed", e.getMessage());
        }
    }

    private String determineUserRole(HierarchyUser user) {
        if (user.getHierarchyLevel() == null) {
            return "USER";
        }
        
        String levelName = user.getHierarchyLevel().getLevelName().toUpperCase();
        Integer levelOrder = user.getHierarchyLevel().getLevelOrder();
        
        if (levelOrder <= 10) return "CEO";
        else if (levelOrder <= 20) return "COO";
        else if (levelName.contains("MANAGER")) return "MANAGER";
        else if (levelName.contains("PROCUREMENT")) return "PROCUREMENT";
        else if (levelName.contains("FINANCE")) return "FINANCE";
        else if (levelName.contains("ADMIN")) return "ADMIN";
        
        return "USER";
    }

    // ✅ COMPLETELY FIXED: Build login response with COMPLETE hierarchy level data
    private HierarchyUserResponse buildLoginResponse(HierarchyUser user, String token, String role) {
        log.info("  [BUILD RESPONSE] Building login response for: {} (ID: {})", 
                 user.getFullName(), user.getId());

        HierarchyUserResponse.HierarchyUserResponseBuilder builder = HierarchyUserResponse.builder()
                .id(user.getId())                           // ✅ CRITICAL: User's own ID
                .email(user.getEmail())                     // ✅ User's own email
                .fullName(user.getFullName())               // ✅ User's own name
                .designation(user.getDesignation())         // ✅ User's own designation
                .phone(user.getPhone())                     // ✅ User's own phone
                .isActive(user.getIsActive())               // ✅ User's own status
                .createdAt(user.getCreatedAt())             // ✅ User's own created date
                .lastLogin(user.getLastLogin())             // ✅ User's own last login
                .token(token)                               // ✅ Token for THIS user
                .role(role)                                 // ✅ User's own role
                .message("Login successful");

        // ✅ CRITICAL FIX: Add COMPLETE hierarchy level info
        if (user.getHierarchyLevel() != null) {
            HierarchyLevel level = user.getHierarchyLevel();
            
            builder.hierarchyLevelId(level.getId())
                   .hierarchyLevelName(level.getLevelName())
                   .hierarchyLevelOrder(level.getLevelOrder());
            
            log.info("  [BUILD RESPONSE] ✅ Hierarchy level data added:");
            log.info("    - hierarchyLevelId: {}", level.getId());
            log.info("    - hierarchyLevelName: {}", level.getLevelName());
            log.info("    - hierarchyLevelOrder: {}", level.getLevelOrder());
            log.info("    - companyName: {}", level.getCompanyName());
        } else {
            log.error("  [BUILD RESPONSE] ❌ WARNING: User has NO hierarchy level!");
        }

        // Add managers info
        if (user.getReportsTo() != null && !user.getReportsTo().isEmpty()) {
            List<HierarchyUserDetailResponse.ManagerInfo> managersInfo = user.getReportsTo().stream()
                    .map(this::buildManagerInfo)
                    .collect(Collectors.toList());
            builder.reportsTo(managersInfo);
        }

        HierarchyUserResponse response = builder.build();

        log.info("  [BUILD RESPONSE] Response created:");
        log.info("    - ID: {}", response.getId());
        log.info("    - Email: {}", response.getEmail());
        log.info("    - Full Name: {}", response.getFullName());
        log.info("    - Role: {}", response.getRole());
        log.info("    - Hierarchy Level ID: {}", response.getHierarchyLevelId());
        log.info("    - Hierarchy Level Name: {}", response.getHierarchyLevelName());
        log.info("    - Hierarchy Level Order: {}", response.getHierarchyLevelOrder());

        return response;
    }

    // ============================================
    // ✅ COMPLETELY FIXED: CREATE USER
    // ============================================
    @Transactional
    public ApiResponse<HierarchyUserDetailResponse> createHierarchyUser(HierarchyUserCreateRequest request) {
        try {
            log.info("🔵 Creating hierarchy user: {} for company: {}", request.getEmail(), request.getCompanyName());
            log.info("   Request data - Level ID: {}, Manager IDs: {}", 
                     request.getHierarchyLevelId(), request.getReportsToIds());
            
            // Validate email
            if (hierarchyUserRepository.existsByEmail(request.getEmail())) {
                log.warn("❌ Email already exists: {}", request.getEmail());
                return ApiResponse.error("Creation failed", "Email already exists");
            }

            // Get hierarchy level
            HierarchyLevel hierarchyLevel = hierarchyLevelRepository.findById(request.getHierarchyLevelId())
                    .orElseThrow(() -> new RuntimeException("Hierarchy level not found with ID: " + request.getHierarchyLevelId()));
            
            log.info("✅ Found hierarchy level: {} (Order: {})", hierarchyLevel.getLevelName(), hierarchyLevel.getLevelOrder());

            // Validate company match
            if (!hierarchyLevel.getCompanyName().equals(request.getCompanyName())) {
                log.warn("❌ Hierarchy level company mismatch");
                return ApiResponse.error("Creation failed", "Hierarchy level does not belong to this company");
            }

            // Create new user
            HierarchyUser user = new HierarchyUser();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFullName(request.getFullName());
            user.setDesignation(request.getDesignation());
            user.setPhone(request.getPhone());
            user.setHierarchyLevel(hierarchyLevel);
            user.setCompanyName(request.getCompanyName());
            user.setIsActive(true);
            user.setIsDeleted(false);
            user.setReportsTo(new HashSet<>());

            // ✅ STEP 1: Save user FIRST without managers
            HierarchyUser savedUser = hierarchyUserRepository.save(user);
            log.info("✅ User saved with ID: {}", savedUser.getId());

            // ✅ STEP 2: Now fetch and set managers
            if (request.getReportsToIds() != null && !request.getReportsToIds().isEmpty()) {
                log.info("🔵 Processing {} managers", request.getReportsToIds().size());
                
                for (Long managerId : request.getReportsToIds()) {
                    log.info("   🔍 Fetching manager with ID: {}", managerId);
                    
                    HierarchyUser manager = hierarchyUserRepository.findById(managerId)
                            .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + managerId));
                    
                    log.info("   ✅ Found manager: {} (Level: {})", 
                             manager.getFullName(), 
                             manager.getHierarchyLevel() != null ? manager.getHierarchyLevel().getLevelOrder() : "N/A");
                    
                    // Validate manager is at higher level
                    if (manager.getHierarchyLevel() != null && savedUser.getHierarchyLevel() != null) {
                        if (manager.getHierarchyLevel().getLevelOrder() >= savedUser.getHierarchyLevel().getLevelOrder()) {
                            log.warn("❌ Manager validation failed");
                            return ApiResponse.error("Creation failed", 
                                "Manager '" + manager.getFullName() + "' must be at a higher hierarchy level");
                        }
                    }
                    
                    savedUser.getReportsTo().add(manager);
                    log.info("   ✅ Added manager to set");
                }
                
                // Set primary manager for backward compatibility
                if (!savedUser.getReportsTo().isEmpty()) {
                    savedUser.setReportsToPrimary(savedUser.getReportsTo().iterator().next());
                    log.info("✅ Set primary manager: {}", savedUser.getReportsToPrimary().getFullName());
                }
                
                // ✅ STEP 3: Save again with managers
                savedUser = hierarchyUserRepository.save(savedUser);
                log.info("✅ User saved with {} managers", savedUser.getReportsTo().size());
            }

            // ✅ STEP 4: Fetch fresh from database
            savedUser = hierarchyUserRepository.findById(savedUser.getId())
                    .orElseThrow(() -> new RuntimeException("Failed to fetch saved user"));
            
            log.info("✅ Final check - User has {} managers assigned", savedUser.getReportsTo().size());

            HierarchyUserDetailResponse response = buildUserDetailResponse(savedUser);
            return ApiResponse.success("Hierarchy user created successfully", response);

        } catch (Exception e) {
            log.error("❌ Error creating hierarchy user", e);
            e.printStackTrace();
            return ApiResponse.error("Creation failed", "Internal error: " + e.getMessage());
        }
    }

    // ============================================
    // GET BY ID
    // ============================================
    @Transactional(readOnly = true)
    public ApiResponse<HierarchyUserDetailResponse> getHierarchyUserById(Long id) {
        try {
            HierarchyUser user = hierarchyUserRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getReportsTo() != null) {
                user.getReportsTo().size();
            }

            HierarchyUserDetailResponse response = buildUserDetailResponse(user);
            return ApiResponse.success("User retrieved successfully", response);

        } catch (Exception e) {
            log.error("Error getting user by ID: {}", id, e);
            return ApiResponse.error("Failed to retrieve user", e.getMessage());
        }
    }

    // ============================================
    // GET BY COMPANY
    // ============================================
    @Transactional(readOnly = true)
    public ApiResponse<List<HierarchyUserDetailResponse>> getUsersByCompany(String companyName) {
        try {
            log.info("📥 Fetching users for company: {}", companyName);
            
            List<HierarchyUser> users = hierarchyUserRepository.findByCompanyNameAndIsActive(companyName, true);
            
            log.info("✅ Found {} users", users.size());
            
            List<HierarchyUserDetailResponse> responses = new ArrayList<>();
            
            for (HierarchyUser user : users) {
                if (user.getReportsTo() != null) {
                    user.getReportsTo().size();
                    log.info("   User: {} has {} managers", user.getFullName(), user.getReportsTo().size());
                }
                
                HierarchyUserDetailResponse response = buildUserDetailResponse(user);
                responses.add(response);
            }

            return ApiResponse.success("Users retrieved successfully", responses);

        } catch (Exception e) {
            log.error("❌ Error getting users by company: {}", companyName, e);
            e.printStackTrace();
            return ApiResponse.error("Failed to retrieve users", e.getMessage());
        }
    }

    // ============================================
    // GET BY LEVEL
    // ============================================
    @Transactional(readOnly = true)
    public ApiResponse<List<HierarchyUserDetailResponse>> getUsersByHierarchyLevel(Long levelId) {
        try {
            HierarchyLevel level = hierarchyLevelRepository.findById(levelId)
                    .orElseThrow(() -> new RuntimeException("Hierarchy level not found"));
            
            List<HierarchyUser> users = hierarchyUserRepository.findByHierarchyLevel(level);
            
            List<HierarchyUserDetailResponse> responses = users.stream()
                    .map(user -> {
                        if (user.getReportsTo() != null) user.getReportsTo().size();
                        return buildUserDetailResponse(user);
                    })
                    .collect(Collectors.toList());

            return ApiResponse.success("Users retrieved successfully", responses);

        } catch (Exception e) {
            log.error("Error getting users by hierarchy level: {}", levelId, e);
            return ApiResponse.error("Failed to retrieve users", e.getMessage());
        }
    }

    // ============================================
    // UPDATE USER
    // ============================================
    @Transactional
    public ApiResponse<HierarchyUserDetailResponse> updateHierarchyUser(Long id, HierarchyUserUpdateRequest request) {
        try {
            log.info("🔵 Updating user ID: {}", id);
            log.info("   Update request - Manager IDs: {}", request.getReportsToIds());
            
            HierarchyUser user = hierarchyUserRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (request.getFullName() != null && !request.getFullName().isEmpty()) {
                user.setFullName(request.getFullName());
            }

            if (request.getDesignation() != null && !request.getDesignation().isEmpty()) {
                user.setDesignation(request.getDesignation());
            }

            if (request.getPhone() != null) {
                user.setPhone(request.getPhone());
            }

            if (request.getHierarchyLevelId() != null) {
                HierarchyLevel newLevel = hierarchyLevelRepository.findById(request.getHierarchyLevelId())
                        .orElseThrow(() -> new RuntimeException("Hierarchy level not found"));
                user.setHierarchyLevel(newLevel);
            }

            if (request.getReportsToIds() != null) {
                log.info("🔵 Updating managers - Current: {}, New: {}", 
                         user.getReportsTo().size(), request.getReportsToIds().size());
                
                user.getReportsTo().clear();
                
                if (!request.getReportsToIds().isEmpty()) {
                    for (Long managerId : request.getReportsToIds()) {
                        log.info("   🔍 Fetching manager ID: {}", managerId);
                        
                        HierarchyUser manager = hierarchyUserRepository.findById(managerId)
                                .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + managerId));
                        
                        log.info("   ✅ Found manager: {}", manager.getFullName());
                        
                        if (manager.getHierarchyLevel() != null && user.getHierarchyLevel() != null) {
                            if (manager.getHierarchyLevel().getLevelOrder() >= user.getHierarchyLevel().getLevelOrder()) {
                                return ApiResponse.error("Update failed", 
                                    "Manager '" + manager.getFullName() + "' must be at a higher hierarchy level");
                            }
                        }
                        
                        user.getReportsTo().add(manager);
                    }
                    
                    if (!user.getReportsTo().isEmpty()) {
                        user.setReportsToPrimary(user.getReportsTo().iterator().next());
                    } else {
                        user.setReportsToPrimary(null);
                    }
                } else {
                    user.setReportsToPrimary(null);
                }
                
                log.info("✅ Managers updated - New count: {}", user.getReportsTo().size());
            }

            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            HierarchyUser updatedUser = hierarchyUserRepository.save(user);
            
            updatedUser = hierarchyUserRepository.findById(updatedUser.getId())
                    .orElseThrow(() -> new RuntimeException("Failed to fetch updated user"));
            
            log.info("✅ User updated successfully with {} managers", updatedUser.getReportsTo().size());

            HierarchyUserDetailResponse response = buildUserDetailResponse(updatedUser);
            return ApiResponse.success("User updated successfully", response);

        } catch (Exception e) {
            log.error("❌ Error updating user: {}", id, e);
            e.printStackTrace();
            return ApiResponse.error("Update failed", "Internal error: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<String> deactivateUser(Long id) {
        try {
            HierarchyUser user = hierarchyUserRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setIsActive(false);
            hierarchyUserRepository.save(user);
            return ApiResponse.success("User deactivated successfully", null);
        } catch (Exception e) {
            log.error("Error deactivating user: {}", id, e);
            return ApiResponse.error("Failed to deactivate user", e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<String> activateUser(Long id) {
        try {
            HierarchyUser user = hierarchyUserRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setIsActive(true);
            hierarchyUserRepository.save(user);
            return ApiResponse.success("User activated successfully", null);
        } catch (Exception e) {
            log.error("Error activating user: {}", id, e);
            return ApiResponse.error("Failed to activate user", e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<String> deleteUser(Long id) {
        try {
            HierarchyUser user = hierarchyUserRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setIsDeleted(true);
            user.setIsActive(false);
            hierarchyUserRepository.save(user);
            return ApiResponse.success("User deleted successfully", null);
        } catch (Exception e) {
            log.error("Error deleting user: {}", id, e);
            return ApiResponse.error("Failed to delete user", e.getMessage());
        }
    }

    private HierarchyUserDetailResponse buildUserDetailResponse(HierarchyUser user) {
        HierarchyUserDetailResponse.HierarchyUserDetailResponseBuilder builder = HierarchyUserDetailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .designation(user.getDesignation())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .companyName(user.getCompanyName())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().format(DATE_FORMATTER) : null)
                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().format(DATE_FORMATTER) : null)
                .lastLogin(user.getLastLogin() != null ? user.getLastLogin().format(DATE_FORMATTER) : null);

        if (user.getHierarchyLevel() != null) {
            builder.hierarchyLevelId(user.getHierarchyLevel().getId())
                   .hierarchyLevelName(user.getHierarchyLevel().getLevelName())
                   .hierarchyLevelOrder(user.getHierarchyLevel().getLevelOrder());
        }

        if (user.getReportsTo() != null && !user.getReportsTo().isEmpty()) {
            List<HierarchyUserDetailResponse.ManagerInfo> managersInfo = user.getReportsTo().stream()
                    .map(this::buildManagerInfo)
                    .collect(Collectors.toList());
            builder.reportsTo(managersInfo);
            
            HierarchyUser primaryManager = user.getReportsToPrimary() != null 
                ? user.getReportsToPrimary() 
                : user.getReportsTo().iterator().next();
            
            builder.reportsToId(primaryManager.getId())
                   .reportsToName(primaryManager.getFullName())
                   .reportsToDesignation(primaryManager.getDesignation());
        } else {
            builder.reportsTo(new ArrayList<>());
        }

        return builder.build();
    }

    private HierarchyUserDetailResponse.ManagerInfo buildManagerInfo(HierarchyUser manager) {
        HierarchyUserDetailResponse.ManagerInfo.ManagerInfoBuilder builder = 
            HierarchyUserDetailResponse.ManagerInfo.builder()
                .id(manager.getId())
                .fullName(manager.getFullName())
                .designation(manager.getDesignation())
                .email(manager.getEmail());
        
        if (manager.getHierarchyLevel() != null) {
            builder.hierarchyLevelOrder(manager.getHierarchyLevel().getLevelOrder())
                   .hierarchyLevelName(manager.getHierarchyLevel().getLevelName());
        }
        
        return builder.build();
    }
}