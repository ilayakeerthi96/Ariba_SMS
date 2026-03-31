package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "supplier_departments")
public class SupplierDepartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Department/Category name is required")
    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "department_description")
    private String departmentDescription;

    @Column(name = "category_of_products")
    private String categoryOfProducts; // e.g., IT, Logistics, Electrical

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_location_id", nullable = false)
    @JsonBackReference("supplier-location-departments")
    private SupplierLocation location;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("supplier-department-users")
    private List<SupplierUser> users;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Constructors
    public SupplierDepartment() {}

    public SupplierDepartment(String departmentName, SupplierLocation location) {
        this.departmentName = departmentName;
        this.location = location;
        this.isDeleted = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDepartmentDescription() { return departmentDescription; }
    public void setDepartmentDescription(String departmentDescription) { this.departmentDescription = departmentDescription; }

    public String getCategoryOfProducts() { return categoryOfProducts; }
    public void setCategoryOfProducts(String categoryOfProducts) { this.categoryOfProducts = categoryOfProducts; }

    public SupplierLocation getLocation() { return location; }
    public void setLocation(SupplierLocation location) { this.location = location; }

    public List<SupplierUser> getUsers() { return users; }
    public void setUsers(List<SupplierUser> users) { this.users = users; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
