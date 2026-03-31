package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.ThreeWayMatch;
import com.itti.leadcapturing.model.ThreeWayMatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThreeWayMatchRepository extends JpaRepository<ThreeWayMatch, Long> {

    /** Load match with all line results. */
    @Query("SELECT DISTINCT m FROM ThreeWayMatch m LEFT JOIN FETCH m.lineResults WHERE m.id = :id")
    Optional<ThreeWayMatch> findByIdWithLineResults(@Param("id") Long id);

    /** All match records for an invoice (versioned history). */
    @Query("SELECT m FROM ThreeWayMatch m WHERE m.invoiceId = :invoiceId ORDER BY m.matchVersion DESC")
    List<ThreeWayMatch> findByInvoiceId(@Param("invoiceId") Long invoiceId);

    /** Latest (highest version) match record for an invoice. */
    @Query("SELECT m FROM ThreeWayMatch m WHERE m.invoiceId = :invoiceId ORDER BY m.matchVersion DESC LIMIT 1")
    Optional<ThreeWayMatch> findLatestByInvoiceId(@Param("invoiceId") Long invoiceId);

    /** All match records for a PO. */
    @Query("SELECT m FROM ThreeWayMatch m WHERE m.purchaseOrderId = :poId ORDER BY m.performedAt DESC")
    List<ThreeWayMatch> findByPurchaseOrderId(@Param("poId") Long poId);

    /** All match records for a GRN. */
    @Query("SELECT m FROM ThreeWayMatch m WHERE m.grnId = :grnId ORDER BY m.performedAt DESC")
    List<ThreeWayMatch> findByGrnId(@Param("grnId") Long grnId);

    /** Check whether a match has already been performed and is not in PENDING state. */
    @Query("SELECT COUNT(m) > 0 FROM ThreeWayMatch m " +
           "WHERE m.invoiceId = :invoiceId AND m.matchStatus <> 'PENDING'")
    boolean hasBeenMatched(@Param("invoiceId") Long invoiceId);

    /** Count of matches by status (for dashboard). */
    @Query("SELECT COUNT(m) FROM ThreeWayMatch m WHERE m.matchStatus = :status")
    Long countByStatus(@Param("status") ThreeWayMatchStatus status);

    /** Matches pending resolution (for buyer dashboard). */
    @Query("SELECT m FROM ThreeWayMatch m WHERE m.resolution = 'PENDING_RESOLUTION' ORDER BY m.performedAt ASC")
    List<ThreeWayMatch> findPendingResolution();
}