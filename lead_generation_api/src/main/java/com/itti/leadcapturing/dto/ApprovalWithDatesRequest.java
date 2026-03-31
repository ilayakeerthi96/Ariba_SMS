package com.itti.leadcapturing.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * ✅ NEW: Request DTO for approval with RFQ dates
 * Used by last/lowest level approver to set dates
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalWithDatesRequest {
    
    @NotNull(message = "RFQ ID is required")
    private Long rfqId;
    
    @NotNull(message = "Approver User ID is required")
    private Long approverId;
    
    private String comments;
    
    // RFQ dates (required for last level only)
    private LocalDateTime rfqDueDate;
    private LocalDateTime rfqDeliveryDate;
}