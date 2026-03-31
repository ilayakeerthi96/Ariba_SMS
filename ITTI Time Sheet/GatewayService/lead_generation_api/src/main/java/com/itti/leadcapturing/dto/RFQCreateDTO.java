package com.itti.leadcapturing.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;
import com.itti.leadcapturing.model.RFQStatus;
import com.itti.leadcapturing.model.ApprovalStatus;
import com.itti.leadcapturing.model.Priority;

public class RFQCreateDTO {
    
    private String rfqTitle;
    private String rfqDescription;
    private LocalDateTime dueDate;
    private LocalDateTime itemRequiredDate;
    private String costCenterCode;
    private String projectCode;
    private String paymentTerms;
    private String deliveryTerms;
    private Double taxPercentage;
    private String justification;
    private Priority priority;
    private Boolean approvalRequired;
    private Boolean allowSplitPO;
    private Boolean preferredVendorsOnly;

    // Constructors
    public RFQCreateDTO() {}

    // Getters and Setters
    public String getRfqTitle() { return rfqTitle; }
    public void setRfqTitle(String rfqTitle) { this.rfqTitle = rfqTitle; }

    public String getRfqDescription() { return rfqDescription; }
    public void setRfqDescription(String rfqDescription) { this.rfqDescription = rfqDescription; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getItemRequiredDate() { return itemRequiredDate; }
    public void setItemRequiredDate(LocalDateTime itemRequiredDate) { this.itemRequiredDate = itemRequiredDate; }

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

    public Boolean getApprovalRequired() { return approvalRequired; }
    public void setApprovalRequired(Boolean approvalRequired) { this.approvalRequired = approvalRequired; }

    public Boolean getAllowSplitPO() { return allowSplitPO; }
    public void setAllowSplitPO(Boolean allowSplitPO) { this.allowSplitPO = allowSplitPO; }

    public Boolean getPreferredVendorsOnly() { return preferredVendorsOnly; }
    public void setPreferredVendorsOnly(Boolean preferredVendorsOnly) { this.preferredVendorsOnly = preferredVendorsOnly; }
}