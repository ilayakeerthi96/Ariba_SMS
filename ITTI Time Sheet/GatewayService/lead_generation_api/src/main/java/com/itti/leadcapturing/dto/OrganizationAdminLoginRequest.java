package com.itti.leadcapturing.dto;

import lombok.*;
import jakarta.validation.constraints.*;

// ============================================
// OrganizationAdmin Login Request
// ============================================
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationAdminLoginRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
}