package com.itti.leadcapturing.dto;

import java.math.BigDecimal;

public class RequirementItemDTO {
    
    private Long id;
    private String itemName;
    private String itemDescription;
    private Integer quantity;
    private BigDecimal price;

    public RequirementItemDTO() {}

    public RequirementItemDTO(Long id, String itemName, String itemDescription, Integer quantity, BigDecimal price) {
        this.id = id;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.quantity = quantity;
        this.price = price;
    }

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
}
