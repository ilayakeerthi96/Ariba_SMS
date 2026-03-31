

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

/**
 * ✅ UPDATED: Using levelOrder increments of 10 (10, 20, 30...)
 * ✅ UPDATED: Soft delete with isDeleted flag
 */
@Entity
@Table(name = "hierarchy_levels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE hierarchy_levels SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class HierarchyLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String levelName;

    /**
     * ✅ NEW: Level order uses increments of 10 (10, 20, 30, 40...)
     * This allows inserting levels in between (e.g., 15 between 10 and 20)
     */
    @Column(nullable = false)
    private Integer levelOrder; // 10 = CEO, 20 = COO, 30 = Manager, etc.

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Boolean isActive = true;

    /**
     * ✅ NEW: Soft delete flag
     */
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
}