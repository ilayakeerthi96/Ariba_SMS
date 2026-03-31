
package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

@Entity
@Table(name = "rfqs")
public class RFQ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "RFQ Number is required")
    @Column(name = "rfq_number", unique = true)
    private String rfqNumber;

    @NotBlank(message = "RFQ Title is required")
    @Column(name = "rfq_title")
    private String rfqTitle;

    @Column(name = "rfq_description", length = 1000)
    private String rfqDescription;

    @Column(name = "issue_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime issueDate;

    @Column(name = "due_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dueDate;

    @Column(name = "item_required_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime itemRequiredDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RFQStatus status = RFQStatus.DRAFT;

    @Column(name = "approval_required")
    private Boolean approvalRequired = false;

    @Column(name = "approval_status")
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approval_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime approvalDate;

    @Column(name = "approval_comments", length = 500)
    private String approvalComments;

    @Column(name = "cost_center_code")
    private String costCenterCode;

    @Column(name = "project_code")
    private String projectCode;

    @Column(name = "payment_terms")
    private String paymentTerms;

    @Column(name = "delivery_terms")
    private String deliveryTerms;

    @Column(name = "tax_percentage")
    private Double taxPercentage = 0.0;

    @Column(name = "justification", length = 1000)
    private String justification;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "allow_split_po")
    private Boolean allowSplitPO = false;

    @Column(name = "preferred_vendors_only")
    private Boolean preferredVendorsOnly = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_id", nullable = false)
    @JsonBackReference("buyer-rfqs")
    private Buyer buyer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
        name = "rfq_suppliers",
        joinColumns = @JoinColumn(name = "rfq_id"),
        inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    private Set<Supplier> selectedSuppliers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
        name = "rfq_approvers",
        joinColumns = @JoinColumn(name = "rfq_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> approvers = new HashSet<>();

    @OneToMany(mappedBy = "rfq", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("rfq-items")
    private List<RFQItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "rfq", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("rfq-attachments")
    private List<RFQAttachment> attachments = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime deletedAt;

    // ==================== NEW: DYNAMIC FIELDS ====================
    @Column(name = "dynamic_fields", columnDefinition = "LONGTEXT")
    private String dynamicFields;

    @Transient
    private Map<String, Object> dynamicFieldsMap;

    // ==================== CONSTRUCTORS ====================

    /**
     * Default constructor
     */
    public RFQ() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = RFQStatus.DRAFT;
        this.approvalStatus = ApprovalStatus.PENDING;
        this.isDeleted = false;
        this.approvalRequired = false;
        this.allowSplitPO = false;
        this.preferredVendorsOnly = false;
        this.priority = Priority.MEDIUM;
        this.taxPercentage = 0.0;
        this.dynamicFields = "{}";
    }

    // ==================== JPA LIFECYCLE CALLBACKS ====================

    /**
     * Called before entity is persisted
     */
    @PrePersist
    protected void onCreate() {
        System.out.println("[RFQ PrePersist] Setting creation timestamps");
        
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.issueDate == null) {
            this.issueDate = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = RFQStatus.DRAFT;
        }
        if (this.approvalStatus == null) {
            this.approvalStatus = ApprovalStatus.PENDING;
        }
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
        if (this.priority == null) {
            this.priority = Priority.MEDIUM;
        }
        if (this.taxPercentage == null) {
            this.taxPercentage = 0.0;
        }
        
        System.out.println("[RFQ PrePersist] RFQ ready: " + this.rfqTitle);
    }

    /**
     * Called before entity is updated
     */
    @PreUpdate
    protected void onUpdate() {
        System.out.println("[RFQ PreUpdate] Updating modification timestamp");
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== HELPER METHODS ====================

    /**
     * Parse ISO string date to LocalDateTime
     * Handles: 2024-01-15T10:30:00, 2024-01-15, etc.
     */
    public static LocalDateTime parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }

        try {
            // Try ISO format first (2024-01-15T10:30:00)
            if (dateString.contains("T")) {
                return LocalDateTime.parse(dateString);
            }
            // Try date-only format (2024-01-15)
            else {
                return LocalDateTime.parse(dateString + "T00:00:00");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to parse date: " + dateString);
            return null;
        }
    }

    /**
     * Get RFQ summary for logging
     */
    public String getSummary() {
        return String.format(
            "RFQ[id=%d, number=%s, title=%s, status=%s, buyer=%s]",
            this.id,
            this.rfqNumber,
            this.rfqTitle,
            this.status,
            this.buyer != null ? this.buyer.getCompanyName() : "N/A"
        );
    }

    // ==================== DYNAMIC FIELDS METHODS ====================

    /**
     * Get dynamic fields as JSON string
     */
    public String getDynamicFields() {
        return dynamicFields;
    }

    /**
     * Set dynamic fields from JSON string
     */
    public void setDynamicFields(String dynamicFields) {
        this.dynamicFields = dynamicFields;
    }

    /**
     * Get dynamic fields as Map object
     */
    public Map<String, Object> getDynamicFieldsMap() {
        if (dynamicFieldsMap == null && dynamicFields != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                dynamicFieldsMap = mapper.readValue(dynamicFields, 
                    new TypeReference<Map<String, Object>>(){});
            } catch (Exception e) {
                System.err.println("[⚠] Error parsing dynamic fields: " + e.getMessage());
                dynamicFieldsMap = new HashMap<>();
            }
        }
        return dynamicFieldsMap != null ? dynamicFieldsMap : new HashMap<>();
    }

    /**
     * Set dynamic fields from Map object
     */
    public void setDynamicFieldsMap(Map<String, Object> dynamicFieldsMap) {
        this.dynamicFieldsMap = dynamicFieldsMap;
        if (dynamicFieldsMap != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                this.dynamicFields = mapper.writeValueAsString(dynamicFieldsMap);
            } catch (Exception e) {
                System.err.println("[⚠] Error serializing dynamic fields: " + e.getMessage());
                this.dynamicFields = "{}";
            }
        }
    }

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

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getItemRequiredDate() {
        return itemRequiredDate;
    }

    public void setItemRequiredDate(LocalDateTime itemRequiredDate) {
        this.itemRequiredDate = itemRequiredDate;
    }

    public RFQStatus getStatus() {
        return status;
    }

    public void setStatus(RFQStatus status) {
        this.status = status;
    }

    public Boolean getApprovalRequired() {
        return approvalRequired;
    }

    public void setApprovalRequired(Boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getApprovalComments() {
        return approvalComments;
    }

    public void setApprovalComments(String approvalComments) {
        this.approvalComments = approvalComments;
    }

    public String getCostCenterCode() {
        return costCenterCode;
    }

    public void setCostCenterCode(String costCenterCode) {
        this.costCenterCode = costCenterCode;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getDeliveryTerms() {
        return deliveryTerms;
    }

    public void setDeliveryTerms(String deliveryTerms) {
        this.deliveryTerms = deliveryTerms;
    }

    public Double getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(Double taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Boolean getAllowSplitPO() {
        return allowSplitPO;
    }

    public void setAllowSplitPO(Boolean allowSplitPO) {
        this.allowSplitPO = allowSplitPO;
    }

    public Boolean getPreferredVendorsOnly() {
        return preferredVendorsOnly;
    }

    public void setPreferredVendorsOnly(Boolean preferredVendorsOnly) {
        this.preferredVendorsOnly = preferredVendorsOnly;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    public Set<Supplier> getSelectedSuppliers() {
        return selectedSuppliers;
    }

    public void setSelectedSuppliers(Set<Supplier> selectedSuppliers) {
        this.selectedSuppliers = selectedSuppliers;
    }

    public Set<User> getApprovers() {
        return approvers;
    }

    public void setApprovers(Set<User> approvers) {
        this.approvers = approvers;
    }

    public List<RFQItem> getItems() {
        return items;
    }

    public void setItems(List<RFQItem> items) {
        this.items = items;
    }

    public List<RFQAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<RFQAttachment> attachments) {
        this.attachments = attachments;
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

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}