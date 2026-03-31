
package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.HierarchyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HierarchyLevelRepository extends JpaRepository<HierarchyLevel, Long> {
    List<HierarchyLevel> findByCompanyNameAndIsActiveOrderByLevelOrderAsc(String companyName, Boolean isActive);
    Optional<HierarchyLevel> findByLevelOrderAndCompanyName(Integer levelOrder, String companyName);
    boolean existsByLevelNameAndCompanyName(String levelName, String companyName);
    List<HierarchyLevel> findByIsActiveOrderByLevelOrderAsc(Boolean isActive);
    // In HierarchyLevelRepository.java

List<HierarchyLevel> findByCompanyNameAndIsActiveOrderByLevelOrderDesc(
    String companyName, 
    Boolean isActive
);
}