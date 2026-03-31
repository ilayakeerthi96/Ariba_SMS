
package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.User;
import com.itti.leadcapturing.model.Department;
import com.itti.leadcapturing.model.Role;
import com.itti.leadcapturing.repo.UserRepository;
import com.itti.leadcapturing.repo.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Create a new buyer user
     * Role is automatically set to ROLE_BUYER and stored as string in database
     */
    public User createUser(User user, Long departmentId) {
        // CHECK PASSWORD BEFORE ENCODING
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("Password cannot be null or empty");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        user.setDepartment(department);
        user.setRole(Role.ROLE_BUYER); // ⭐ Set role as ROLE_BUYER enum (stores as "ROLE_BUYER" string in DB)
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsDeleted(false);

        return userRepository.save(user);
    }

    /**
     * Get all buyer users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by ID
     */
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Update user - Email check EXCLUDES current user
     */
    public User updateUser(Long id, User userReq) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userReq.getFirstName() != null && !userReq.getFirstName().isEmpty()) {
            existingUser.setFirstName(userReq.getFirstName());
        }

        if (userReq.getLastName() != null && !userReq.getLastName().isEmpty()) {
            existingUser.setLastName(userReq.getLastName());
        }

        if (userReq.getEmail() != null && !userReq.getEmail().isEmpty()) {
            if (!userReq.getEmail().equals(existingUser.getEmail())) {
                if (userRepository.existsByEmail(userReq.getEmail())) {
                    throw new RuntimeException("Email already exists");
                }
                existingUser.setEmail(userReq.getEmail());
            }
        }

        if (userReq.getPhone() != null && !userReq.getPhone().isEmpty()) {
            existingUser.setPhone(userReq.getPhone());
        }

        if (userReq.getDesignation() != null && !userReq.getDesignation().isEmpty()) {
            existingUser.setDesignation(userReq.getDesignation());
        }

        if (userReq.getEmployeeId() != null && !userReq.getEmployeeId().isEmpty()) {
            existingUser.setEmployeeId(userReq.getEmployeeId());
        }

        if (userReq.getGender() != null && !userReq.getGender().isEmpty()) {
            existingUser.setGender(userReq.getGender());
        }

        if (userReq.getDateOfBirth() != null && !userReq.getDateOfBirth().isEmpty()) {
            existingUser.setDateOfBirth(userReq.getDateOfBirth());
        }

        if (userReq.getAddressLine1() != null && !userReq.getAddressLine1().isEmpty()) {
            existingUser.setAddressLine1(userReq.getAddressLine1());
        }

        if (userReq.getAddressLine2() != null) {
            existingUser.setAddressLine2(userReq.getAddressLine2());
        }

        if (userReq.getCity() != null && !userReq.getCity().isEmpty()) {
            existingUser.setCity(userReq.getCity());
        }

        if (userReq.getState() != null && !userReq.getState().isEmpty()) {
            existingUser.setState(userReq.getState());
        }

        if (userReq.getPostalCode() != null && !userReq.getPostalCode().isEmpty()) {
            existingUser.setPostalCode(userReq.getPostalCode());
        }

        // UPDATE PASSWORD ONLY IF PROVIDED
        if (userReq.getPassword() != null && !userReq.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userReq.getPassword()));
        }

        // UPDATE ROLE ONLY IF PROVIDED (Optional)
        if (userReq.getRole() != null) {
            existingUser.setRole(userReq.getRole());
        }

        return userRepository.save(existingUser);
    }

    /**
     * Delete user (soft delete)
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}