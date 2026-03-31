package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.OrganizationAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationAdminRepository extends JpaRepository<OrganizationAdmin, Long> {
    
    Optional<OrganizationAdmin> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<OrganizationAdmin> findByEmailAndIsActive(String email, Boolean isActive);
    
    List<OrganizationAdmin> findByCompanyName(String companyName);
    
    List<OrganizationAdmin> findByCompanyNameAndIsActive(String companyName, Boolean isActive);
    
    List<OrganizationAdmin> findByCreatedBy(OrganizationAdmin createdBy);
    
    List<OrganizationAdmin> findByIsActive(Boolean isActive);
}