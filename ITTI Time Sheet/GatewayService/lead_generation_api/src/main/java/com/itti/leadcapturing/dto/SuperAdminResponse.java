package com.itti.leadcapturing.dto;

import lombok.*;
import jakarta.validation.constraints.*;

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
    private Boolean isActive;
    private String token;
}