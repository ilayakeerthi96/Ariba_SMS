

package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * ✅ UPDATED: OrganizationAdmin Entity with mustChangePassword flag
 */
@Entity
@Table(name = "organization_admin")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(length = 15)
    private String phone;

    @Column(nullable = false, length = 100)
    private String companyName;

    @Column(nullable = false)
    private Boolean isActive = true;

    /**
     * ✅ NEW: Flag to force password change on first login
     * Default: true (must change password)
     */
    @Column(nullable = false)
    private Boolean mustChangePassword = true;

    /**
     * ✅ Track who created this admin
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_admin_id")
    private OrganizationAdmin createdBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime lastLogin;

    @Column
    private LocalDateTime passwordChangedAt;
}