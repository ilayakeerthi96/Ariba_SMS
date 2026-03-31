

package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.RFQItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RFQItemRepository extends JpaRepository<RFQItem, Long> {

    @Query("SELECT i FROM RFQItem i WHERE i.rfq.id = :rfqId ORDER BY i.itemOrder ASC")
    List<RFQItem> findByRfqId(@Param("rfqId") Long rfqId);

    @Query("SELECT i FROM RFQItem i WHERE i.rfq.id = :rfqId AND i.itemCode = :itemCode")
    Optional<RFQItem> findByRfqIdAndItemCode(@Param("rfqId") Long rfqId, @Param("itemCode") String itemCode);
}