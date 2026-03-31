

package com.itti.leadcapturing.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HierarchyUserCreateRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;
    
    @NotBlank(message = "Designation is required")
    @Size(min = 2, max = 100, message = "Designation must be between 2 and 100 characters")
    private String designation;
    
    @Size(min = 10, max = 15, message = "Phone must be between 10 and 15 characters")
    private String phone;
    
    @NotNull(message = "Hierarchy level ID is required")
    private Long hierarchyLevelId;
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    /**
     * ✅ NEW: Multiple reporting managers support
     * List of manager IDs this user reports to
     */
    private List<Long> reportsToIds;
}