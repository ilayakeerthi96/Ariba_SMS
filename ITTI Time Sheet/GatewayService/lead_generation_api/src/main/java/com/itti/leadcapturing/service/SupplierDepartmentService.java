
package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.SupplierDepartment;
import com.itti.leadcapturing.model.SupplierUser;
import com.itti.leadcapturing.model.SupplierLocation;
import com.itti.leadcapturing.repo.SupplierDepartmentRepository;
import com.itti.leadcapturing.repo.SupplierUserRepository;
import com.itti.leadcapturing.repo.SupplierLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SupplierDepartmentService {

    @Autowired
    private SupplierDepartmentRepository departmentRepository;

    @Autowired
    private SupplierUserRepository userRepository;

    @Autowired
    private SupplierLocationRepository locationRepository;

    @Transactional
    public SupplierDepartment createDepartment(Long locationId, SupplierDepartment department) {
        try {
            log.info("🔵 Creating supplier department for location ID: {}", locationId);
            
            SupplierLocation location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            
            department.setLocation(location);
            department.setIsDeleted(false);
            
            SupplierDepartment saved = departmentRepository.save(department);
            
            log.info("✅ Supplier department created: {} (ID: {})", saved.getDepartmentName(), saved.getId());
            return saved;
            
        } catch (Exception e) {
            log.error("❌ Error creating supplier department", e);
            throw new RuntimeException("Failed to create department: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public SupplierDepartment getDepartment(Long id) {
        try {
            log.info("📥 Fetching supplier department ID: {}", id);
            
            SupplierDepartment department = departmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Department not found with ID: " + id));
            
            // ✅ CRITICAL: Initialize lazy collections within transaction
            if (department.getUsers() != null) {
                department.getUsers().size(); // Force initialization
            }
            
            log.info("✅ Found supplier department: {}", department.getDepartmentName());
            return department;
            
        } catch (Exception e) {
            log.error("❌ Error fetching supplier department ID: {}", id, e);
            throw new RuntimeException("Failed to fetch department: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<SupplierDepartment> getDepartmentsByLocation(Long locationId) {
        try {
            log.info("📥 Fetching supplier departments for location ID: {}", locationId);
            
            List<SupplierDepartment> departments = departmentRepository.findByLocationId(locationId);
            
            // ✅ CRITICAL: Initialize lazy collections within transaction
            departments.forEach(department -> {
                if (department.getUsers() != null) {
                    department.getUsers().size();
                    log.info("   Department '{}' has {} users", 
                             department.getDepartmentName(), 
                             department.getUsers().size());
                }
            });
            
            log.info("✅ Found {} supplier departments", departments.size());
            return departments;
            
        } catch (Exception e) {
            log.error("❌ Error fetching supplier departments for location ID: {}", locationId, e);
            throw new RuntimeException("Failed to fetch departments: " + e.getMessage(), e);
        }
    }

    @Transactional
    public SupplierDepartment updateDepartment(Long id, SupplierDepartment deptReq) {
        try {
            log.info("🔵 Updating supplier department ID: {}", id);
            
            SupplierDepartment existingDept = departmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Department not found"));

            // Update only provided fields
            if (deptReq.getDepartmentName() != null && !deptReq.getDepartmentName().trim().isEmpty()) {
                existingDept.setDepartmentName(deptReq.getDepartmentName());
            }
            if (deptReq.getDepartmentDescription() != null) {
                existingDept.setDepartmentDescription(deptReq.getDepartmentDescription());
            }
            if (deptReq.getCategoryOfProducts() != null && !deptReq.getCategoryOfProducts().trim().isEmpty()) {
                existingDept.setCategoryOfProducts(deptReq.getCategoryOfProducts());
            }

            SupplierDepartment updated = departmentRepository.save(existingDept);
            
            // Initialize for response
            if (updated.getUsers() != null) {
                updated.getUsers().size();
            }
            
            log.info("✅ Supplier department updated: {}", updated.getDepartmentName());
            return updated;
            
        } catch (Exception e) {
            log.error("❌ Error updating supplier department ID: {}", id, e);
            throw new RuntimeException("Failed to update department: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteDepartment(Long id) {
        try {
            log.info("🔵 Soft deleting supplier department ID: {}", id);
            
            SupplierDepartment department = departmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Department not found"));

            // Soft delete all users under this department
            if (department.getUsers() != null && !department.getUsers().isEmpty()) {
                for (SupplierUser user : department.getUsers()) {
                    user.setIsDeleted(true);
                    user.setDeletedAt(LocalDateTime.now());
                    userRepository.save(user);
                }
            }

            // Soft delete the department itself
            department.setIsDeleted(true);
            department.setDeletedAt(LocalDateTime.now());
            departmentRepository.save(department);
            
            log.info("✅ Supplier department soft deleted: {}", department.getDepartmentName());
            
        } catch (Exception e) {
            log.error("❌ Error deleting supplier department ID: {}", id, e);
            throw new RuntimeException("Failed to delete department: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void hardDeleteDepartment(Long id) {
        SupplierDepartment department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        // Hard delete all users
        if (department.getUsers() != null && !department.getUsers().isEmpty()) {
            userRepository.deleteAll(department.getUsers());
        }

        // Hard delete the department
        departmentRepository.delete(department);
    }
}