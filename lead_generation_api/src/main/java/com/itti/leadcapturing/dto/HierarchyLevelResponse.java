package com.itti.leadcapturing.dto;

import lombok.*;
import jakarta.validation.constraints.*;

// ============================================
// HierarchyLevel Create Request
// ============================================
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HierarchyLevelResponse {
    private Long id;
    private String levelName;
    private Integer levelOrder;
    private String description;
    private String companyName;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
}