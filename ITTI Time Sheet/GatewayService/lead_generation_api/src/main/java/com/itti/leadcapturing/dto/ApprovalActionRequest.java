package com.itti.leadcapturing.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalActionRequest {
    
    @NotNull(message = "RFQ ID is required")
    private Long rfqId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    private String comments;
}