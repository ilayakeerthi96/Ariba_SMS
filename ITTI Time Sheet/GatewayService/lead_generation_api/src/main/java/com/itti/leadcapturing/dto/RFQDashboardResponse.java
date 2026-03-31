package com.itti.leadcapturing.dto;

import java.time.LocalDateTime;

/**
 * DTO for RFQ Dashboard Response
 */
public class RFQDashboardResponse {

    private Long id;
    private String rfqNumber;
    private String rfqTitle;
    private String rfqDescription;
    private String status;
    private String approvalStatus;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private Integer suppliersCount;
    private Integer itemsCount;
    private String buyerName;

    // ==================== CONSTRUCTORS ====================

    public RFQDashboardResponse() {}

    // ==================== GETTERS AND SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getSuppliersCount() {
        return suppliersCount;
    }

    public void setSuppliersCount(Integer suppliersCount) {
        this.suppliersCount = suppliersCount;
    }

    public Integer getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(Integer itemsCount) {
        this.itemsCount = itemsCount;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    @Override
    public String toString() {
        return "RFQDashboardResponse{" +
                "id=" + id +
                ", rfqNumber='" + rfqNumber + '\'' +
                ", suppliersCount=" + suppliersCount +
                ", itemsCount=" + itemsCount +
                '}';
    }
}