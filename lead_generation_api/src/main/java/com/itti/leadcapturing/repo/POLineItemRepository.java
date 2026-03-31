package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.POLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ✅ PO Line Item Repository
 */
@Repository
public interface POLineItemRepository extends JpaRepository<POLineItem, Long> {

    /**
     * Find all line items for a PO
     */
    @Query("SELECT li FROM POLineItem li WHERE li.purchaseOrder.id = :poId ORDER BY li.slNo ASC")
    List<POLineItem> findByPurchaseOrderId(@Param("poId") Long poId);

    /**
     * Delete all line items for a PO
     */
    @Query("DELETE FROM POLineItem li WHERE li.purchaseOrder.id = :poId")
    void deleteByPurchaseOrderId(@Param("poId") Long poId);

    /**
     * Count line items for a PO
     */
    @Query("SELECT COUNT(li) FROM POLineItem li WHERE li.purchaseOrder.id = :poId")
    Long countByPurchaseOrderId(@Param("poId") Long poId);
}