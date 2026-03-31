package com.itti.leadcapturing.dto;

public class SupplierDepartmentResponseDTO {
    
    private Long id;
    private String departmentName;
    private String departmentDescription;
    private String categoryOfProducts;

    // Constructors
    public SupplierDepartmentResponseDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDepartmentDescription() { return departmentDescription; }
    public void setDepartmentDescription(String departmentDescription) { this.departmentDescription = departmentDescription; }

    public String getCategoryOfProducts() { return categoryOfProducts; }
    public void setCategoryOfProducts(String categoryOfProducts) { this.categoryOfProducts = categoryOfProducts; }
}
