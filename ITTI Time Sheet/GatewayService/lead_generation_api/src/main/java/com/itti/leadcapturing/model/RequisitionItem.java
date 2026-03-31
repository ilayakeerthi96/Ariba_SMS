package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.math.BigDecimal;

@Entity
@Table(name = "requisition_items")
public class RequisitionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name is required")
    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_description")
    private String itemDescription;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity")
    private Integer quantity;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_id", nullable = false)
    @JsonBackReference("requisition-items")
    private Requisition requisition;

    // Constructors
    public RequisitionItem() {}

    public RequisitionItem(String itemName, String itemDescription, Integer quantity, BigDecimal price) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Requisition getRequisition() { return requisition; }
    public void setRequisition(Requisition requisition) { this.requisition = requisition; }
}