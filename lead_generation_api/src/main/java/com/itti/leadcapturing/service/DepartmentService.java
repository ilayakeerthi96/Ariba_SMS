

package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.Department;
import com.itti.leadcapturing.model.Location;
import com.itti.leadcapturing.repo.DepartmentRepository;
import com.itti.leadcapturing.repo.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Transactional
    public Department createDepartment(Long locationId, Department department) {
        try {
            log.info("🔵 Creating department for location ID: {}", locationId);
            
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Location not found"));

            department.setLocation(location);
            department.setIsDeleted(false);

            Department saved = departmentRepository.save(department);
            
            log.info("✅ Department created: {} (ID: {})", saved.getDepartmentName(), saved.getId());
            return saved;
            
        } catch (Exception e) {
            log.error("❌ Error creating department", e);
            throw new RuntimeException("Failed to create department: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Department> getDepartmentsByLocation(Long locationId) {
        try {
            log.info("📥 Fetching departments for location ID: {}", locationId);
            
            List<Department> departments = departmentRepository.findByLocationId(locationId);
            
            // ✅ CRITICAL: Initialize lazy collections within transaction
            departments.forEach(department -> {
                if (department.getUsers() != null) {
                    department.getUsers().size(); // Force initialization
                    log.info("   Department '{}' has {} users", 
                             department.getDepartmentName(), 
                             department.getUsers().size());
                }
            });
            
            log.info("✅ Found {} departments", departments.size());
            return departments;
            
        } catch (Exception e) {
            log.error("❌ Error fetching departments for location ID: {}", locationId, e);
            throw new RuntimeException("Failed to fetch departments: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Department getDepartmentById(Long id) {
        try {
            log.info("📥 Fetching department ID: {}", id);
            
            Department department = departmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Department not found with ID: " + id));
            
            // ✅ Initialize lazy collections
            if (department.getUsers() != null) {
                department.getUsers().size();
            }
            
            log.info("✅ Found department: {}", department.getDepartmentName());
            return department;
            
        } catch (Exception e) {
            log.error("❌ Error fetching department ID: {}", id, e);
            throw new RuntimeException("Failed to fetch department: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Department updateDepartment(Long id, Department departmentReq) {
        try {
            log.info("🔵 Updating department ID: {}", id);
            
            Department existing = departmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Department not found"));

            // Update only provided fields
            if (departmentReq.getDepartmentName() != null && !departmentReq.getDepartmentName().trim().isEmpty()) {
                existing.setDepartmentName(departmentReq.getDepartmentName());
            }
            
            if (departmentReq.getDepartmentDescription() != null) {
                existing.setDepartmentDescription(departmentReq.getDepartmentDescription());
            }

            Department updated = departmentRepository.save(existing);
            
            // Initialize for response
            if (updated.getUsers() != null) {
                updated.getUsers().size();
            }
            
            log.info("✅ Department updated: {}", updated.getDepartmentName());
            return updated;
            
        } catch (Exception e) {
            log.error("❌ Error updating department ID: {}", id, e);
            throw new RuntimeException("Failed to update department: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteDepartment(Long id) {
        try {
            log.info("🔵 Soft deleting department ID: {}", id);
            
            Department department = departmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Department not found"));

            // Soft delete department
            department.setIsDeleted(true);
            department.setDeletedAt(LocalDateTime.now());
            departmentRepository.save(department);

            // Soft delete all users in this department
            if (department.getUsers() != null) {
                department.getUsers().forEach(user -> {
                    user.setIsDeleted(true);
                    user.setDeletedAt(LocalDateTime.now());
                });
            }
            
            log.info("✅ Department soft deleted: {}", department.getDepartmentName());
            
        } catch (Exception e) {
            log.error("❌ Error deleting department ID: {}", id, e);
            throw new RuntimeException("Failed to delete department: " + e.getMessage(), e);
        }
    }
}