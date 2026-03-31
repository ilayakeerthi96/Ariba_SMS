

package com.itti.leadcapturing.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationAdminResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String companyName;
    private Boolean isActive;
    private Boolean mustChangePassword; // ✅ NEW: Flag to force password change
    private String token;
    private Long createdByAdminId;
    private String createdByAdminName;
    private String createdAt;
    private String lastLogin;
}