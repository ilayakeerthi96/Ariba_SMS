// package com.itti.leadcapturing.dto;

// import lombok.*;
// import jakarta.validation.constraints.*;

// // ============================================
// // SuperAdmin Registration Request
// // ============================================
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class SuperAdminRegistrationRequest {
    
//     @NotBlank(message = "Email is required")
//     @Email(message = "Invalid email format")
//     private String email;
    
//     @NotBlank(message = "Password is required")
//     @Size(min = 6, message = "Password must be at least 6 characters")
//     private String password;
    
//     @NotBlank(message = "Full name is required")
//     private String fullName;
    
//     private String phone;
    
//     @NotBlank(message = "Company name is required")
//     private String companyName;
// }


package com.itti.leadcapturing.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuperAdminRegistrationRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String organizationName; // ✅ NEW (optional at registration)
}