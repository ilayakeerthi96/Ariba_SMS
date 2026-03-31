
package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    
    @Query("SELECT l FROM Location l WHERE l.buyer.id = :buyerId AND l.isDeleted = false")
    List<Location> findByBuyerId(@Param("buyerId") Long buyerId);

    @Query("SELECT l FROM Location l WHERE l.id = :id AND l.isDeleted = false")
    Optional<Location> findById(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Location l WHERE l.id = :id AND l.isDeleted = false")
    boolean existsById(@Param("id") Long id);
}