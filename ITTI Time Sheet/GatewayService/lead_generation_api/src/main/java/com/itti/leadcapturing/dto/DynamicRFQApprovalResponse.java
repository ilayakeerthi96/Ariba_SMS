package com.itti.leadcapturing.dto;

import java.time.LocalDateTime;

/**
 * DTO for Dynamic RFQ Approval Response
 */
public class DynamicRFQApprovalResponse {

    private Long id;
    private Long rfqId;
    private String rfqNumber;
    private String rfqTitle;
    private String rfqDescription;
    private Long hierarchyLevelId;
    private String hierarchyLevelName;
    private Integer hierarchyLevelOrder;
    private String status;
    private String comments;
    private Integer sequenceOrder;
    private LocalDateTime createdAt;
    private LocalDateTime actionDate;
    private Long approverUserId;
    private String approverUserName;
    private String approverUserEmail;
    private String buyerName;

    // ==================== CONSTRUCTORS ====================
    
    public DynamicRFQApprovalResponse() {}

    // ==================== GETTERS AND SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRfqId() {
        return rfqId;
    }

    public void setRfqId(Long rfqId) {
        this.rfqId = rfqId;
    }

    public String getRfqNumber() {
        return rfqNumber;
    }

    public void setRfqNumber(String rfqNumber) {
        this.rfqNumber = rfqNumber;
    }

    public String getRfqTitle() {
        return rfqTitle;
    }

    public void setRfqTitle(String rfqTitle) {
        this.rfqTitle = rfqTitle;
    }

    public String getRfqDescription() {
        return rfqDescription;
    }

    public void setRfqDescription(String rfqDescription) {
        this.rfqDescription = rfqDescription;
    }

    public Long getHierarchyLevelId() {
        return hierarchyLevelId;
    }

    public void setHierarchyLevelId(Long hierarchyLevelId) {
        this.hierarchyLevelId = hierarchyLevelId;
    }

    public String getHierarchyLevelName() {
        return hierarchyLevelName;
    }

    public void setHierarchyLevelName(String hierarchyLevelName) {
        this.hierarchyLevelName = hierarchyLevelName;
    }

    public Integer getHierarchyLevelOrder() {
        return hierarchyLevelOrder;
    }

    public void setHierarchyLevelOrder(Integer hierarchyLevelOrder) {
        this.hierarchyLevelOrder = hierarchyLevelOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(Integer sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getActionDate() {
        return actionDate;
    }

    public void setActionDate(LocalDateTime actionDate) {
        this.actionDate = actionDate;
    }

    public Long getApproverUserId() {
        return approverUserId;
    }

    public void setApproverUserId(Long approverUserId) {
        this.approverUserId = approverUserId;
    }

    public String getApproverUserName() {
        return approverUserName;
    }

    public void setApproverUserName(String approverUserName) {
        this.approverUserName = approverUserName;
    }

    public String getApproverUserEmail() {
        return approverUserEmail;
    }

    public void setApproverUserEmail(String approverUserEmail) {
        this.approverUserEmail = approverUserEmail;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    @Override
    public String toString() {
        return "DynamicRFQApprovalResponse{" +
                "id=" + id +
                ", rfqNumber='" + rfqNumber + '\'' +
                ", hierarchyLevelName='" + hierarchyLevelName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}