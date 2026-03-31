package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.RFQItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ✅ REPOSITORY: RFQ Item
 * Handles RFQ item database operations
 */
@Repository
public interface RFQItemRepository extends JpaRepository<RFQItem, Long> {

    /**
     * Find all items for an RFQ ordered by item_order
     * This is the main method used for fetching items
     */
    @Query("SELECT i FROM RFQItem i WHERE i.rfq.id = :rfqId ORDER BY i.itemOrder ASC")
    List<RFQItem> findByRfqId(@Param("rfqId") Long rfqId);

    /**
     * ✅ ADDED: Find all items for an RFQ ordered by item_order (explicit method name)
     * This is an alias for findByRfqId() for better code readability
     */
    @Query("SELECT i FROM RFQItem i WHERE i.rfq.id = :rfqId ORDER BY i.itemOrder ASC")
    List<RFQItem> findByRfqIdOrderByItemOrder(@Param("rfqId") Long rfqId);

    /**
     * Find an item by RFQ ID and item code
     * Useful for checking if item code already exists in RFQ
     */
    @Query("SELECT i FROM RFQItem i WHERE i.rfq.id = :rfqId AND i.itemCode = :itemCode")
    Optional<RFQItem> findByRfqIdAndItemCode(@Param("rfqId") Long rfqId, @Param("itemCode") String itemCode);

    /**
     * Count items for an RFQ
     */
    @Query("SELECT COUNT(i) FROM RFQItem i WHERE i.rfq.id = :rfqId")
    Long countByRfqId(@Param("rfqId") Long rfqId);

    /**
     * Find item by ID with RFQ loaded (for performance)
     */
    @Query("SELECT i FROM RFQItem i LEFT JOIN FETCH i.rfq WHERE i.id = :itemId")
    Optional<RFQItem> findByIdWithRfq(@Param("itemId") Long itemId);

    /**
     * Delete all items for an RFQ
     * Used when deleting an RFQ
     */
    @Query("DELETE FROM RFQItem i WHERE i.rfq.id = :rfqId")
    void deleteByRfqId(@Param("rfqId") Long rfqId);

    /**
     * Get max item order for an RFQ
     * Used when adding new items to determine the next order number
     */
    @Query("SELECT MAX(i.itemOrder) FROM RFQItem i WHERE i.rfq.id = :rfqId")
    Integer getMaxItemOrder(@Param("rfqId") Long rfqId);
}