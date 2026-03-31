


package com.itti.leadcapturing.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HierarchyUserDetailResponse {
    private Long id;
    private String email;
    private String fullName;
    private String designation;
    private String phone;
    private Boolean isActive;
    private String companyName;
    
    // Hierarchy Level Info
    private Long hierarchyLevelId;
    private String hierarchyLevelName;
    private Integer hierarchyLevelOrder;
    
    /**
     * ✅ NEW: Multiple reporting managers
     */
    private List<ManagerInfo> reportsTo;
    
    // Deprecated fields (kept for backward compatibility)
    @Deprecated
    private Long reportsToId;
    @Deprecated
    private String reportsToName;
    @Deprecated
    private String reportsToDesignation;
    
    private String createdAt;
    private String updatedAt;
    private String lastLogin;
    
    /**
     * ✅ NEW: Manager Info DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ManagerInfo {
        private Long id;
        private String fullName;
        private String designation;
        private String email;
        private Integer hierarchyLevelOrder;
        private String hierarchyLevelName;
    }
}
