package com.itti.leadcapturing.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingApprovalResponse {
    
    private Long approvalId;
    
    // RFQ Info
    private Long rfqId;
    private String rfqNumber;
    private String rfqTitle;
    private String rfqDescription;
    private LocalDateTime rfqCreatedAt;
    
    // Current Level Info
    private Long hierarchyLevelId;
    private String hierarchyLevelName;
    private Integer hierarchyLevelOrder;
    
    // Approver Info
    private Long approverId;
    private String approverName;
    private String approverEmail;
    
    // Sequence Info
    private Integer sequenceOrder;
    private Integer totalLevels;
    
    private LocalDateTime createdAt;
}