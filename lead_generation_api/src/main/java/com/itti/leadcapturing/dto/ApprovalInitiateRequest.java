package com.itti.leadcapturing.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalInitiateRequest {
    
    @NotNull(message = "RFQ ID is required")
    private Long rfqId;
    
    @NotNull(message = "Creator User ID is required")
    private Long creatorUserId;
}