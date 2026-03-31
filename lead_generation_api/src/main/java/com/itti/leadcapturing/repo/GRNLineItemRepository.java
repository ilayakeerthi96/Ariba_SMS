package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.GRNLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GRNLineItemRepository extends JpaRepository<GRNLineItem, Long> {

    /** All line items for a GRN. */
    @Query("SELECT li FROM GRNLineItem li WHERE li.grn.id = :grnId ORDER BY li.itemOrder ASC")
    List<GRNLineItem> findByGrnId(@Param("grnId") Long grnId);

    /** Find by PO line item ID (for cross-document matching). */
    @Query("SELECT li FROM GRNLineItem li WHERE li.poLineItemId = :poLineItemId")
    List<GRNLineItem> findByPoLineItemId(@Param("poLineItemId") Long poLineItemId);

    /**
     * Sum of accepted quantities for a PO line item across all APPROVED or CLOSED GRNs,
     * EXCLUDING a specific GRN (the one currently being matched).
     *
     * Used by 3-Way Match to determine remaining PO quantity already matched
     * in prior GRN batches, so partial deliveries are handled correctly.
     *
     * Example: PO ordered 10. GRN-1 accepted 8 (approved + matched).
     * When matching GRN-2, previouslyAccepted = 8, remaining = 2.
     * GRN-2 should only cover 2 — matching against remaining, not full 10.
     */
    @Query("SELECT COALESCE(SUM(li.acceptedQuantity), 0) " +
           "FROM GRNLineItem li " +
           "WHERE li.poLineItemId = :poLineItemId " +
           "AND li.grn.id <> :excludeGrnId " +
           "AND li.grn.status IN ('APPROVED', 'CLOSED')")
    BigDecimal sumPreviouslyAcceptedQty(
            @Param("poLineItemId") Long poLineItemId,
            @Param("excludeGrnId") Long excludeGrnId);
}