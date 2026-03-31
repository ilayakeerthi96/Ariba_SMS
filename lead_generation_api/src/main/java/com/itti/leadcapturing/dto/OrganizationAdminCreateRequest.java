package com.itti.leadcapturing.dto;

import lombok.*;
import jakarta.validation.constraints.*;

// ============================================
// OrganizationAdmin Registration Request (by SuperAdmin)
// ============================================
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationAdminCreateRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;
    
    @Size(min = 10, max = 15, message = "Phone must be between 10 and 15 characters")
    private String phone;
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    
}
