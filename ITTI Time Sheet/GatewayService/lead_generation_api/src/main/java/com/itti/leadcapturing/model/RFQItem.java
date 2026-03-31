// ==================== FILE: src/main/java/com/itti/leadcapturing/model/RFQItem.java ====================

package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Type;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.Map;

@Entity
@Table(name = "rfq_items")
public class RFQItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_code")
    private String itemCode;

    @Column(name = "item_description")
    private String itemDescription;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "uom")
    private String uom;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "line_total")
    private BigDecimal lineTotal;

    @Column(name = "item_required_date")
    private LocalDateTime itemRequiredDate;

    @Column(name = "item_description_detailed", length = 1000)
    private String itemDescriptionDetailed;

    @Column(name = "specifications")
    private String specifications;

    @Column(name = "item_order")
    private Integer itemOrder;

    // ==================== DYNAMIC FIELDS ====================
    /**
     * Stores dynamic fields as JSON
     * Example: {"software": "Windows Server", "version": "2024.1", "processor": "Intel Xeon"}
     */
    @Column(name = "dynamic_fields", columnDefinition = "JSON")
    private String dynamicFields;

    /**
     * Stores the company type this item was created for
     * Example: "IT", "Manufacturing", "Trading", etc.
     */
    @Column(name = "company_type")
    private String companyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    @JsonBackReference("rfq-items")
    private RFQ rfq;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== CONSTRUCTORS ====================

    public RFQItem() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public RFQItem(String itemCode, String itemDescription, BigDecimal quantity, String uom) {
        this.itemCode = itemCode;
        this.itemDescription = itemDescription;
        this.quantity = quantity;
        this.uom = uom;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public RFQItem(String itemCode, String itemDescription, BigDecimal quantity, String uom, String companyType) {
        this.itemCode = itemCode;
        this.itemDescription = itemDescription;
        this.quantity = quantity;
        this.uom = uom;
        this.companyType = companyType;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== JPA LIFECYCLE CALLBACKS ====================

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== HELPER METHODS ====================

    /**
     * Convert dynamic fields JSON string to Map
     * Useful for processing individual fields
     */
    public Map<String, Object> getDynamicFieldsAsMap() {
        if (this.dynamicFields == null || this.dynamicFields.isEmpty()) {
            return new java.util.HashMap<>();
        }
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(this.dynamicFields, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to parse dynamicFields JSON: " + e.getMessage());
            return new java.util.HashMap<>();
        }
    }

    /**
     * Set dynamic fields from Map
     * Converts Map to JSON string for storage
     */
    public void setDynamicFieldsFromMap(Map<String, Object> fieldsMap) {
        if (fieldsMap == null || fieldsMap.isEmpty()) {
            this.dynamicFields = null;
            return;
        }
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            this.dynamicFields = mapper.writeValueAsString(fieldsMap);
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to convert Map to JSON: " + e.getMessage());
            this.dynamicFields = null;
        }
    }

    /**
     * Get a specific dynamic field value
     */
    public Object getDynamicFieldValue(String fieldName) {
        Map<String, Object> fieldsMap = getDynamicFieldsAsMap();
        return fieldsMap.getOrDefault(fieldName, null);
    }

    /**
     * Set a specific dynamic field value
     */
    public void setDynamicFieldValue(String fieldName, Object value) {
        Map<String, Object> fieldsMap = getDynamicFieldsAsMap();
        fieldsMap.put(fieldName, value);
        setDynamicFieldsFromMap(fieldsMap);
    }

    /**
     * Get summary of item with dynamic fields
     */
    public String getSummary() {
        return String.format(
            "RFQItem[id=%d, description=%s, quantity=%s, companyType=%s, hasDynamicFields=%b]",
            this.id,
            this.itemDescription,
            this.quantity,
            this.companyType,
            this.dynamicFields != null && !this.dynamicFields.isEmpty()
        );
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

    public String getDynamicFields() {
        return dynamicFields;
    }

    public void setDynamicFields(String dynamicFields) {
        this.dynamicFields = dynamicFields;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public RFQ getRfq() {
        return rfq;
    }

    public void setRfq(RFQ rfq) {
        this.rfq = rfq;
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
}