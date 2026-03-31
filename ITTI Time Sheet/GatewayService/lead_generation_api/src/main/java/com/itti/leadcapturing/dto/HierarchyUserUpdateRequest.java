

package com.itti.leadcapturing.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HierarchyUserUpdateRequest {
    
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;
    
    @Size(min = 2, max = 100, message = "Designation must be between 2 and 100 characters")
    private String designation;
    
    @Size(min = 10, max = 15, message = "Phone must be between 10 and 15 characters")
    private String phone;
    
    private Long hierarchyLevelId;
    
    /**
     * ✅ NEW: Multiple reporting managers support
     * If null, reporting structure won't be updated
     * If empty list, all reporting relationships will be removed
     */
    private List<Long> reportsToIds;
    
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password; // Optional password update
}
