
package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.HierarchyUser;
import com.itti.leadcapturing.model.HierarchyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HierarchyUserRepository extends JpaRepository<HierarchyUser, Long> {
    Optional<HierarchyUser> findByEmail(String email);
    Optional<HierarchyUser> findByEmailAndIsActive(String email, Boolean isActive);
    boolean existsByEmail(String email);
    List<HierarchyUser> findByHierarchyLevel(HierarchyLevel hierarchyLevel);
    List<HierarchyUser> findByReportsTo(HierarchyUser reportsTo);
    List<HierarchyUser> findByCompanyNameAndIsActive(String companyName, Boolean isActive);
     List<HierarchyUser> findByHierarchyLevelAndIsActiveOrderByIdAsc(HierarchyLevel level, Boolean isActive);
    
    List<HierarchyUser> findByIsActive(Boolean isActive);
    List<HierarchyUser> findByCompanyNameAndHierarchyLevelAndIsActive(
    String companyName, 
    HierarchyLevel hierarchyLevel, 
    Boolean isActive
);
}   