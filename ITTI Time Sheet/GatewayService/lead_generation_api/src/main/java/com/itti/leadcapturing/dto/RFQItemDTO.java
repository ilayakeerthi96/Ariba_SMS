// ==================== FILE: src/main/java/com/itti/leadcapturing/dto/RFQItemDTO.java ====================

package com.itti.leadcapturing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object for RFQ Item
 * Includes dynamic fields for different company types
 */
public class RFQItemDTO {
    
    private Long id;
    private String itemCode;
    private String itemDescription;
    private BigDecimal quantity;
    private String uom;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private LocalDateTime itemRequiredDate;
    private String itemDescriptionDetailed;
    private String specifications;
    private Integer itemOrder;

    // ==================== DYNAMIC FIELDS ====================
    /**
     * Dynamic fields specific to company type
     * Example: {"software": "Windows Server", "version": "2024.1"}
     */
    private Map<String, Object> dynamicFields;

    /**
     * Company type this item was created for
     * Values: "IT", "Manufacturing", "Trading", "Services", "Distribution", "Retail", "Others"
     */
    private String companyType;

    // ==================== AUDIT FIELDS ====================
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==================== CONSTRUCTORS ====================

    public RFQItemDTO() {
    }

    public RFQItemDTO(String itemCode, String itemDescription, BigDecimal quantity, String uom) {
        this.itemCode = itemCode;
        this.itemDescription = itemDescription;
        this.quantity = quantity;
        this.uom = uom;
    }

    public RFQItemDTO(String itemCode, String itemDescription, BigDecimal quantity, String uom, 
                      String companyType, Map<String, Object> dynamicFields) {
        this.itemCode = itemCode;
        this.itemDescription = itemDescription;
        this.quantity = quantity;
        this.uom = uom;
        this.companyType = companyType;
        this.dynamicFields = dynamicFields;
    }

    // ==================== GETTERS AND SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public LocalDateTime getItemRequiredDate() {
        return itemRequiredDate;
    }

    public void setItemRequiredDate(LocalDateTime itemRequiredDate) {
        this.itemRequiredDate = itemRequiredDate;
    }

    public String getItemDescriptionDetailed() {
        return itemDescriptionDetailed;
    }

    public void setItemDescriptionDetailed(String itemDescriptionDetailed) {
        this.itemDescriptionDetailed = itemDescriptionDetailed;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public Integer getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(Integer itemOrder) {
        this.itemOrder = itemOrder;
    }

    public Map<String, Object> getDynamicFields() {
        return dynamicFields;
    }

    public void setDynamicFields(Map<String, Object> dynamicFields) {
        this.dynamicFields = dynamicFields;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Get a specific dynamic field value
     */
    public Object getDynamicFieldValue(String fieldName) {
        if (this.dynamicFields == null) {
            return null;
        }
        return this.dynamicFields.getOrDefault(fieldName, null);
    }

    /**
     * Check if item has dynamic fields
     */
    public boolean hasDynamicFields() {
        return this.dynamicFields != null && !this.dynamicFields.isEmpty();
    }

    @Override
    public String toString() {
        return "RFQItemDTO{" +
                "id=" + id +
                ", itemCode='" + itemCode + '\'' +
                ", itemDescription='" + itemDescription + '\'' +
                ", quantity=" + quantity +
                ", uom='" + uom + '\'' +
                ", companyType='" + companyType + '\'' +
                ", hasDynamicFields=" + hasDynamicFields() +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}