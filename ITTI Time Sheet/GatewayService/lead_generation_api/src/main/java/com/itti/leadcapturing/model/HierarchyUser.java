


package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * ✅ FIXED: Multiple reporting managers support with proper cascade handling
 */
@Entity
@Table(name = "hierarchy_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE hierarchy_users SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class HierarchyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(length = 100)
    private String designation;

    @Column(length = 15)
    private String phone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hierarchy_level_id")
    private HierarchyLevel hierarchyLevel;

    /**
     * ✅ FIXED: Multiple reporting managers (Many-to-Many relationship)
     * CascadeType.PERSIST and CascadeType.MERGE removed to prevent issues
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_reporting_structure",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "manager_id")
    )
    private Set<HierarchyUser> reportsTo = new HashSet<>();

    /**
     * ✅ DEPRECATED but kept for backward compatibility
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reports_to_id")
    @Deprecated
    private HierarchyUser reportsToPrimary;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Column(length = 100)
    private String companyName;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime lastLogin;
    
    // ✅ FIXED: Helper methods without triggering cascades
    public void addManager(HierarchyUser manager) {
        if (this.reportsTo == null) {
            this.reportsTo = new HashSet<>();
        }
        this.reportsTo.add(manager);
    }
    
    public void removeManager(HierarchyUser manager) {
        if (this.reportsTo != null) {
            this.reportsTo.remove(manager);
        }
    }
    
    public void clearManagers() {
        if (this.reportsTo != null) {
            this.reportsTo.clear();
        }
    }
    
    /**
     * ✅ CRITICAL: Override equals and hashCode to prevent circular reference issues
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HierarchyUser)) return false;
        HierarchyUser that = (HierarchyUser) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}