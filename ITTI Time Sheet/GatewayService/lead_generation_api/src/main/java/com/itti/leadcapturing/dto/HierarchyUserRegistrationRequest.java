package com.itti.leadcapturing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Registration Request DTO for HierarchyUser (Phase 1 - Legacy)
 * Used for hierarchy/auth/register endpoint
 * Location: src/main/java/com/itti/leadcapturing/dto/HierarchyUserRegistrationRequest.java
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HierarchyUserRegistrationRequest {
    
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
}