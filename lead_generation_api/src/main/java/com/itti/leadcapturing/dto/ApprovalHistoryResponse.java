package com.itti.leadcapturing.dto;

import lombok.*;
import com.itti.leadcapturing.model.ApprovalActionStatus;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalHistoryResponse {
    
    private Long id;
    private Long rfqId;
    private String rfqNumber;
    private String rfqTitle;
    
    // Hierarchy Level Info
    private Long hierarchyLevelId;
    private String hierarchyLevelName;
    private Integer hierarchyLevelOrder;
    
    // Approver Info
    private ApproverInfo approver;
    
    // Approval Status
    private ApprovalActionStatus status;
    private String comments;
    private LocalDateTime actionDate;
    private Integer sequenceOrder;
    private LocalDateTime createdAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApproverInfo {
        private Long id;
        private String fullName;
        private String email;
        private String designation;
    }
}