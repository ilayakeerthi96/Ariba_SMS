package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.SupplierUser;
import com.itti.leadcapturing.model.SupplierDepartment;
import com.itti.leadcapturing.model.Role;
import com.itti.leadcapturing.repo.SupplierUserRepository;
import com.itti.leadcapturing.repo.SupplierDepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SupplierUserService {

    @Autowired
    private SupplierUserRepository supplierUserRepository;

    @Autowired
    private SupplierDepartmentRepository supplierDepartmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Create a new supplier user
     * Role is automatically set to ROLE_SUPPLIER and stored as string in database
     */
    public SupplierUser createUser(SupplierUser user, Long departmentId) {
        // Validate password before encoding
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("Password cannot be null or empty");
        }

        if (supplierUserRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        SupplierDepartment department = supplierDepartmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        user.setDepartment(department);
        user.setRole(Role.ROLE_SUPPLIER); // ⭐ Set role as ROLE_SUPPLIER enum (stores as "ROLE_SUPPLIER" string in DB)
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsDeleted(false);

        return supplierUserRepository.save(user);
    }

    /**
     * Get all supplier users
     */
    public List<SupplierUser> getAllUsers() {
        return supplierUserRepository.findAll();
    }

    /**
     * Get user by ID
     */
    public SupplierUser getUser(Long id) {
        return supplierUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Update user - Email check EXCLUDES current user
     * Password update is OPTIONAL - only updates if provided and not empty
     */
    public SupplierUser updateUser(Long id, SupplierUser userReq) {
        SupplierUser existingUser = supplierUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update first name
        if (userReq.getFirstName() != null && !userReq.getFirstName().isEmpty()) {
            existingUser.setFirstName(userReq.getFirstName());
        }

        // Update last name
        if (userReq.getLastName() != null && !userReq.getLastName().isEmpty()) {
            existingUser.setLastName(userReq.getLastName());
        }

        // Email update - CRITICAL: Only check for duplicates if email is being changed
        if (userReq.getEmail() != null && !userReq.getEmail().isEmpty()) {
            if (!userReq.getEmail().equals(existingUser.getEmail())) {
                // Email is being changed - check if new email already exists
                if (supplierUserRepository.existsByEmail(userReq.getEmail())) {
                    throw new RuntimeException("Email already exists");
                }
                existingUser.setEmail(userReq.getEmail());
            }
            // If email is the same, skip the update
        }

        // Update phone
        if (userReq.getPhone() != null && !userReq.getPhone().isEmpty()) {
            existingUser.setPhone(userReq.getPhone());
        }

        // Update designation
        if (userReq.getDesignation() != null && !userReq.getDesignation().isEmpty()) {
            existingUser.setDesignation(userReq.getDesignation());
        }

        // Update employee ID
        if (userReq.getEmployeeId() != null && !userReq.getEmployeeId().isEmpty()) {
            existingUser.setEmployeeId(userReq.getEmployeeId());
        }

        // Update gender
        if (userReq.getGender() != null && !userReq.getGender().isEmpty()) {
            existingUser.setGender(userReq.getGender());
        }

        // Update date of birth
        if (userReq.getDateOfBirth() != null && !userReq.getDateOfBirth().isEmpty()) {
            existingUser.setDateOfBirth(userReq.getDateOfBirth());
        }

        // Update address line 1
        if (userReq.getAddressLine1() != null && !userReq.getAddressLine1().isEmpty()) {
            existingUser.setAddressLine1(userReq.getAddressLine1());
        }

        // Update address line 2
        if (userReq.getAddressLine2() != null) {
            existingUser.setAddressLine2(userReq.getAddressLine2());
        }

        // Update city
        if (userReq.getCity() != null && !userReq.getCity().isEmpty()) {
            existingUser.setCity(userReq.getCity());
        }

        // Update state
        if (userReq.getState() != null && !userReq.getState().isEmpty()) {
            existingUser.setState(userReq.getState());
        }

        // Update postal code
        if (userReq.getPostalCode() != null && !userReq.getPostalCode().isEmpty()) {
            existingUser.setPostalCode(userReq.getPostalCode());
        }

        // Update password ONLY if provided and not empty
        // This allows edit mode to work without requiring password
        if (userReq.getPassword() != null && !userReq.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userReq.getPassword()));
        }

        // Update role if provided (optional)
        if (userReq.getRole() != null) {
            existingUser.setRole(userReq.getRole());
        }

        return supplierUserRepository.save(existingUser);
    }

    /**
     * Delete user (soft delete)
     */
    public void deleteUser(Long id) {
        SupplierUser user = supplierUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        supplierUserRepository.save(user);
    }
}