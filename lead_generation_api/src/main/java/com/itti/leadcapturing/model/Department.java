
package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Department name is required")
    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "department_description")
    private String departmentDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    @JsonBackReference("location-departments")
    private Location location;

    // ✅ CRITICAL FIX: CascadeType.ALL ensures users are saved
    @OneToMany(
        mappedBy = "department", 
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @JsonManagedReference("department-users")
    private List<User> users = new ArrayList<>();

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Constructors
    public Department() {
        this.users = new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDepartmentDescription() { return departmentDescription; }
    public void setDepartmentDescription(String departmentDescription) { this.departmentDescription = departmentDescription; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public List<User> getUsers() { 
        if (users == null) {
            users = new ArrayList<>();
        }
        return users; 
    }
    
    public void setUsers(List<User> users) { 
        this.users = users != null ? users : new ArrayList<>();
    }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}