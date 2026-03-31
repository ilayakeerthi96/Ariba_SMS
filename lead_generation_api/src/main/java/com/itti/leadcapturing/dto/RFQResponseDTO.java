package com.itti.leadcapturing.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import com.itti.leadcapturing.model.RFQStatus;
import com.itti.leadcapturing.model.ApprovalStatus;
import com.itti.leadcapturing.model.Priority;

/**
 * ✅ FIXED: Now includes actual items and suppliers arrays
 */
public class RFQResponseDTO {
    
    private Long id;
    private String rfqNumber;
    private String rfqTitle;
    private String rfqDescription;
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private LocalDateTime itemRequiredDate;
    private RFQStatus status;
    private ApprovalStatus approvalStatus;
    private Boolean approvalRequired;
    private Long approvedBy;
    private LocalDateTime approvalDate;
    private String approvalComments;
    private String costCenterCode;
    private String projectCode;
    private String paymentTerms;
    private String deliveryTerms;
    private Double taxPercentage;
    private String justification;
    private Priority priority;
    private Boolean allowSplitPO;
    private Boolean preferredVendorsOnly;
    private BuyerDto buyer;
    private LocationDto location;
    private UserDto createdByUser;
    
    // ==================== ✅ CRITICAL FIX ====================
    /**
     * ACTUAL items array (not just count)
     */
    private List<RFQItemDTO> items = new ArrayList<>();
    
    /**
     * ACTUAL suppliers array (not just count)
     */
    private List<SupplierDto> selectedSuppliers = new ArrayList<>();
    
    // Counts (for backward compatibility)
    private Integer itemsCount;
    private Integer attachmentsCount;
    private Integer suppliersCount;
    private Integer approversCount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public RFQResponseDTO() {}

    // ==================== GETTERS AND SETTERS ====================
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRfqNumber() { return rfqNumber; }
    public void setRfqNumber(String rfqNumber) { this.rfqNumber = rfqNumber; }

    public String getRfqTitle() { return rfqTitle; }
    public void setRfqTitle(String rfqTitle) { this.rfqTitle = rfqTitle; }

    public String getRfqDescription() { return rfqDescription; }
    public void setRfqDescription(String rfqDescription) { this.rfqDescription = rfqDescription; }

    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime issueDate) { this.issueDate = issueDate; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getItemRequiredDate() { return itemRequiredDate; }
    public void setItemRequiredDate(LocalDateTime itemRequiredDate) { this.itemRequiredDate = itemRequiredDate; }

    public RFQStatus getStatus() { return status; }
    public void setStatus(RFQStatus status) { this.status = status; }

    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }

    public Boolean getApprovalRequired() { return approvalRequired; }
    public void setApprovalRequired(Boolean approvalRequired) { this.approvalRequired = approvalRequired; }

    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }

    public String getApprovalComments() { return approvalComments; }
    public void setApprovalComments(String approvalComments) { this.approvalComments = approvalComments; }

    public String getCostCenterCode() { return costCenterCode; }
    public void setCostCenterCode(String costCenterCode) { this.costCenterCode = costCenterCode; }

    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public String getDeliveryTerms() { return deliveryTerms; }
    public void setDeliveryTerms(String deliveryTerms) { this.deliveryTerms = deliveryTerms; }

    public Double getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(Double taxPercentage) { this.taxPercentage = taxPercentage; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Boolean getAllowSplitPO() { return allowSplitPO; }
    public void setAllowSplitPO(Boolean allowSplitPO) { this.allowSplitPO = allowSplitPO; }

    public Boolean getPreferredVendorsOnly() { return preferredVendorsOnly; }
    public void setPreferredVendorsOnly(Boolean preferredVendorsOnly) { this.preferredVendorsOnly = preferredVendorsOnly; }

    public BuyerDto getBuyer() { return buyer; }
    public void setBuyer(BuyerDto buyer) { this.buyer = buyer; }

    public LocationDto getLocation() { return location; }
    public void setLocation(LocationDto location) { this.location = location; }

    public UserDto getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(UserDto createdByUser) { this.createdByUser = createdByUser; }

    // ✅ CRITICAL: Items array getter/setter
    public List<RFQItemDTO> getItems() { return items; }
    public void setItems(List<RFQItemDTO> items) { 
        this.items = items;
        this.itemsCount = items != null ? items.size() : 0;
    }

    // ✅ CRITICAL: Suppliers array getter/setter
    public List<SupplierDto> getSelectedSuppliers() { return selectedSuppliers; }
    public void setSelectedSuppliers(List<SupplierDto> selectedSuppliers) { 
        this.selectedSuppliers = selectedSuppliers;
        this.suppliersCount = selectedSuppliers != null ? selectedSuppliers.size() : 0;
    }

    public Integer getItemsCount() { return itemsCount; }
    public void setItemsCount(Integer itemsCount) { this.itemsCount = itemsCount; }

    public Integer getAttachmentsCount() { return attachmentsCount; }
    public void setAttachmentsCount(Integer attachmentsCount) { this.attachmentsCount = attachmentsCount; }

    public Integer getSuppliersCount() { return suppliersCount; }
    public void setSuppliersCount(Integer suppliersCount) { this.suppliersCount = suppliersCount; }

    public Integer getApproversCount() { return approversCount; }
    public void setApproversCount(Integer approversCount) { this.approversCount = approversCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}