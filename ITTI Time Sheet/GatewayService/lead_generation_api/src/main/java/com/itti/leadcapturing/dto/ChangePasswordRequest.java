
// package com.itti.leadcapturing.dto;

// import lombok.*;
// import jakarta.validation.constraints.*;

// /**
//  * DTO for changing password
//  * Used by OrganizationAdmin and HierarchyUser
//  */
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class ChangePasswordRequest {
    
//     @NotBlank(message = "Current password is required")
//     private String currentPassword;
    
//     @NotBlank(message = "New password is required")
//     @Size(min = 6, message = "New password must be at least 6 characters")
//     private String newPassword;
    
//     @NotBlank(message = "Confirm password is required")
//     private String confirmPassword;
// }

package com.itti.leadcapturing.dto;

import lombok.*;
import jakarta.validation.constraints.*;

/**
 * DTO for password change requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {
    
    @NotBlank(message = "Current password is required")
    private String currentPassword;
    
    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 6 characters")
    private String newPassword;
    
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}