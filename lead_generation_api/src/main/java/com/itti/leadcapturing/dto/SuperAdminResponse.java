

package com.itti.leadcapturing.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuperAdminResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String companyName;
    private String organizationName;  // ✅ NEW
    private Boolean isActive;
    private String token;
    private String logoUrl;           // ✅ NEW
    private String logoBase64;        // ✅ NEW
     private String theme;
}