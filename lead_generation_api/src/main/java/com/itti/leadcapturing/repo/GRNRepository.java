

package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.GRN;
import com.itti.leadcapturing.model.GRNStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GRNRepository extends JpaRepository<GRN, Long> {

    /** Load GRN with line items eagerly. */
    @Query("SELECT DISTINCT g FROM GRN g LEFT JOIN FETCH g.lineItems WHERE g.id = :id")
    Optional<GRN> findByIdWithLineItems(@Param("id") Long id);

    /** All GRNs for a given PO. */
    @Query("SELECT g FROM GRN g WHERE g.purchaseOrderId = :poId ORDER BY g.createdAt DESC")
    List<GRN> findByPurchaseOrderId(@Param("poId") Long poId);

    /** All GRNs linked to a specific invoice. */
    @Query("SELECT g FROM GRN g WHERE g.invoiceId = :invoiceId ORDER BY g.createdAt DESC")
    List<GRN> findByInvoiceId(@Param("invoiceId") Long invoiceId);

    /** GRNs created by a specific user. */
    @Query("SELECT g FROM GRN g WHERE g.receivedByUserId = :userId ORDER BY g.createdAt DESC")
    List<GRN> findByReceivedByUserId(@Param("userId") Long userId);

    /** GRNs for a supplier (for supplier view). */
    @Query("SELECT g FROM GRN g WHERE g.supplierId = :supplierId ORDER BY g.createdAt DESC")
    List<GRN> findBySupplierId(@Param("supplierId") Long supplierId);

    /** GRNs by status. */
    @Query("SELECT g FROM GRN g WHERE g.status = :status ORDER BY g.createdAt DESC")
    List<GRN> findByStatus(@Param("status") GRNStatus status);

    /** Check if a GRN number already exists. */
    boolean existsByGrnNumber(String grnNumber);

    /** Find by GRN number. */
    Optional<GRN> findByGrnNumber(String grnNumber);

    /**
     * Check if an APPROVED GRN exists for a given PO.
     * Used before performing 3-way match — GRN must be approved first.
     */
    @Query("SELECT COUNT(g) > 0 FROM GRN g WHERE g.purchaseOrderId = :poId AND g.status = 'APPROVED'")
    boolean existsApprovedGrnForPo(@Param("poId") Long poId);

    /**
     * Find approved GRNs for a PO (for 3-way match selection).
     */
    @Query("SELECT DISTINCT g FROM GRN g LEFT JOIN FETCH g.lineItems " +
           "WHERE g.purchaseOrderId = :poId AND g.status = 'APPROVED'")
    List<GRN> findApprovedGrnsForPo(@Param("poId") Long poId);

    /**
     * Find all GRNs (any status) linked to a specific PO + Invoice combination.
     *
     * Used to track partial delivery batches — when a supplier delivers in multiple
     * shipments against the same PO and Invoice, this returns all GRN batches in order.
     *
     * Example:
     *   PO-001, Invoice INV-100 → GRN-001 (8 units) + GRN-002 (2 units)
     *   Both are returned here so the UI can show the full delivery history.
     */
    @Query("SELECT DISTINCT g FROM GRN g LEFT JOIN FETCH g.lineItems " +
           "WHERE g.purchaseOrderId = :poId AND g.invoiceId = :invoiceId " +
           "ORDER BY g.createdAt ASC")
    List<GRN> findByPurchaseOrderIdAndInvoiceId(
            @Param("poId") Long poId,
            @Param("invoiceId") Long invoiceId);

    /**
     * Count of GRNs for a PO-Invoice pair (for batch numbering in UI).
     * Useful to display "Batch 1 of 2", "Batch 2 of 2" etc.
     */
    @Query("SELECT COUNT(g) FROM GRN g " +
           "WHERE g.purchaseOrderId = :poId AND g.invoiceId = :invoiceId")
    long countByPurchaseOrderIdAndInvoiceId(
            @Param("poId") Long poId,
            @Param("invoiceId") Long invoiceId);

    /**
     * Sum of accepted quantities across ALL APPROVED/CLOSED GRNs for a PO.
     * Used to calculate how much of the PO has been received and accepted in total.
     *
     * Example: PO ordered 10. GRN-1 accepted 8 (APPROVED), GRN-2 accepted 2 (APPROVED).
     * This returns 10 — indicating the PO is fully received.
     */
    @Query("SELECT COALESCE(SUM(li.acceptedQuantity), 0) " +
           "FROM GRNLineItem li " +
           "WHERE li.grn.purchaseOrderId = :poId " +
           "AND li.poLineItemId = :poLineItemId " +
           "AND li.grn.status IN ('APPROVED', 'CLOSED')")
    java.math.BigDecimal sumTotalAcceptedQtyForPoLine(
            @Param("poId") Long poId,
            @Param("poLineItemId") Long poLineItemId);
}