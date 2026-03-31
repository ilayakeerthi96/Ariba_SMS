// package com.itti.leadcapturing.dto;

// import lombok.*;

// /**
//  * DTO used for updating SuperAdmin profile fields.
//  * All fields are optional — only non-null fields will be updated.
//  * Logo is handled separately via multipart upload.
//  *
//  * Location: src/main/java/com/itti/leadcapturing/dto/SuperAdminUpdateRequest.java
//  */
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class SuperAdminUpdateRequest {

//     private String fullName;
//     private String phone;
//     private String password;         // optional — only update if provided
//     private String companyName;
//     private String organizationName; // ✅ NEW
// }


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