package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "requisitions")
public class Requisition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Requisition title is required")
    @Column(name = "title")
    private String title;

    @NotBlank(message = "Category is required")
    @Column(name = "category")
    private String category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Department ID is required")
    @ManyToOne(fetch = FetchType.EAGER)  // ⬅️ CHANGED: EAGER to avoid lazy loading issues
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @NotNull(message = "Requested by user ID is required")
    @ManyToOne(fetch = FetchType.EAGER)  // ⬅️ CHANGED: EAGER to avoid lazy loading issues
    @JoinColumn(name = "requested_by_id", nullable = false)
    private User requestedBy;

    @NotBlank(message = "Currency is required")
    @Column(name = "currency")
    private String currency;

    @NotNull(message = "Estimated cost is required")
    @Min(value = 0, message = "Estimated cost cannot be negative")
    @Column(name = "estimated_cost", precision = 15, scale = 2)
    private BigDecimal estimatedCost;

    @NotNull(message = "Delivery date is required")
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @NotBlank(message = "Delivery location is required")
    @Column(name = "delivery_location")
    private String deliveryLocation;

    @OneToMany(mappedBy = "requisition", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("requisition-items")
    private List<RequisitionItem> items;

    @Column(name = "status", columnDefinition = "VARCHAR(50) DEFAULT 'DRAFT'")
    private String status = "DRAFT";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "DRAFT";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Requisition() {}

    public Requisition(String title, String category, Department department, User requestedBy,
                       String currency, BigDecimal estimatedCost, LocalDate deliveryDate, String deliveryLocation) {
        this.title = title;
        this.category = category;
        this.department = department;
        this.requestedBy = requestedBy;
        this.currency = currency;
        this.estimatedCost = estimatedCost;
        this.deliveryDate = deliveryDate;
        this.deliveryLocation = deliveryLocation;
        this.status = "DRAFT";
        this.isDeleted = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public User getRequestedBy() { return requestedBy; }
    public void setRequestedBy(User requestedBy) { this.requestedBy = requestedBy; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }

    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getDeliveryLocation() { return deliveryLocation; }
    public void setDeliveryLocation(String deliveryLocation) { this.deliveryLocation = deliveryLocation; }

    public List<RequisitionItem> getItems() { return items; }
    public void setItems(List<RequisitionItem> items) { this.items = items; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}