

package com.itti.leadcapturing.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuperAdminUpdateRequest {
    private String fullName;
    private String phone;
    private String password;
    private String companyName;
    private String organizationName;  // ✅ NEW
}