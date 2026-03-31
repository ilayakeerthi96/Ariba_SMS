package com.itti.leadcapturing.dto;

public class DepartmentResponseDTO {
    
    private Long id;
    private String departmentName;
    private String departmentDescription;

    // Constructors
    public DepartmentResponseDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDepartmentDescription() { return departmentDescription; }
    public void setDepartmentDescription(String departmentDescription) { this.departmentDescription = departmentDescription; }
}