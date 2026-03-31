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
public class HierarchyLevelUpdateRequest {
    
    @Size(min = 2, max = 100, message = "Level name must be between 2 and 100 characters")
    private String levelName;
    
    @Min(value = 1, message = "Level order must be at least 1")
    private Integer levelOrder;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}