

package com.itti.leadcapturing.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HierarchyUserResponse {
    
    private Long id;
    private String email;
    private String fullName;
    private String designation;
    private String phone;
    
    // Hierarchy Level Info
    private Long hierarchyLevelId;
    private String hierarchyLevelName;
    private Integer hierarchyLevelOrder;
    
    /**
     * ✅ NEW: Multiple managers info for login response
     */
    private List<HierarchyUserDetailResponse.ManagerInfo> reportsTo;
    
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private String token;
    private String message;
    private String role; // For role-based access
}