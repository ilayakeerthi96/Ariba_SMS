// package com.itti.leadcapturing.dto;

// import lombok.*;

// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class OrganizationAdminResponse {
//     private Long id;
//     private String email;
//     private String fullName;
//     private String phone;
//     private String companyName;
//     private String organizationName;    // ✅ NEW
//     private Boolean isActive;
//     private Boolean mustChangePassword;
//     private String token;
//     private Long createdByAdminId;
//     private String createdByAdminName;
//     private String createdAt;
//     private String lastLogin;
//     private String logoUrl;             // ✅ NEW
//     private String logoBase64;          // ✅ NEW — for header display
// }

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
    private String organizationName;   // ✅ NEW
    private Boolean isActive;
    private Boolean mustChangePassword;
    private String token;
    private Long createdByAdminId;
    private String createdByAdminName;
    private String createdAt;
    private String lastLogin;
    private String logoUrl;            // ✅ NEW
    private String logoBase64;         // ✅ NEW
}