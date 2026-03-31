package com.itti.leadcapturing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BuyerRequirementDTO {
    
    private Long id;
    private String title;
    private String category;
    private String description;
    private String department;
    private String requestedBy;
    private String currency;
    private BigDecimal estimatedCost;
    private LocalDate deliveryDate;
    private String deliveryLocation;
    private String status;
    private LocalDate createdDate;
    private LocalDate updatedDate;
    private List<RequirementItemDTO> requirementItems;

    public BuyerRequirementDTO() {}

    public BuyerRequirementDTO(Long id, String title, String category, String description, 
                               String department, String requestedBy, String currency, 
                               BigDecimal estimatedCost, LocalDate deliveryDate, String deliveryLocation) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.department = department;
        this.requestedBy = requestedBy;
        this.currency = currency;
        this.estimatedCost = estimatedCost;
        this.deliveryDate = deliveryDate;
        this.deliveryLocation = deliveryLocation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }
    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }
    public String getDeliveryLocation() { return deliveryLocation; }
    public void setDeliveryLocation(String deliveryLocation) { this.deliveryLocation = deliveryLocation; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
    public LocalDate getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDate updatedDate) { this.updatedDate = updatedDate; }
    public List<RequirementItemDTO> getRequirementItems() { return requirementItems; }
    public void setRequirementItems(List<RequirementItemDTO> requirementItems) { this.requirementItems = requirementItems; }
}