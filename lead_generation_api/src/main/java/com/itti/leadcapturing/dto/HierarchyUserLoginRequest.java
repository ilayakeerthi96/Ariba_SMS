package com.itti.leadcapturing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Login Request DTO for HierarchyUser
 * Location: src/main/java/com/itti/leadcapturing/dto/HierarchyUserLoginRequest.java
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HierarchyUserLoginRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
}