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
public class HierarchyLevelRequest {
    
    @NotBlank(message = "Level name is required")
    @Size(min = 2, max = 100, message = "Level name must be between 2 and 100 characters")
    private String levelName; // e.g., "CEO", "Manager", "Team Lead"
    
    @NotNull(message = "Level order is required")
    @Min(value = 1, message = "Level order must be at least 1")
    private Integer levelOrder; // 1 = Highest (CEO), 2 = Next level, etc.
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @NotBlank(message = "Company name is required")
    private String companyName;
}