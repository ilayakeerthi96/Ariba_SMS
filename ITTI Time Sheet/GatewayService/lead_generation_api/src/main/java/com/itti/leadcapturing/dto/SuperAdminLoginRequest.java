package com.itti.leadcapturing.dto;

import lombok.*;
import jakarta.validation.constraints.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuperAdminLoginRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
}