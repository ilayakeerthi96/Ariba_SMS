// package com.itti.leadcapturing.repo;

// import com.itti.leadcapturing.model.Invoice;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import java.util.List;
// import java.util.Optional;

// @Repository
// public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

//     // ── Supplier queries ──────────────────────────────────────────────────────

//     /**
//      * All invoices belonging to a supplier (any status) — lineItems eagerly loaded.
//      */
//     @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.lineItems WHERE i.supplierId = :supplierId ORDER BY i.createdAt DESC")
//     List<Invoice> findBySupplierId(@Param("supplierId") Long supplierId);

//     /**
//      * Single invoice belonging to a specific supplier — lineItems eagerly loaded.
//      */
//     @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.lineItems WHERE i.id = :id AND i.supplierId = :supplierId")
//     Optional<Invoice> findByIdAndSupplierId(@Param("id") Long id, @Param("supplierId") Long supplierId);

//     /**
//      * Invoices for a supplier that are in APPROVED status.
//      */
//     @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.lineItems WHERE i.supplierId = :supplierId AND i.status = 'APPROVED'")
//     List<Invoice> findApprovedInvoicesBySupplierId(@Param("supplierId") Long supplierId);

//     // ── Buyer / company queries ───────────────────────────────────────────────

//     /**
//      * All non-DRAFT invoices for a buyer company — lineItems eagerly loaded.
//      */
//     @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.lineItems WHERE i.buyerCompanyName = :companyName AND i.status <> 'DRAFT' ORDER BY i.createdAt DESC")
//     List<Invoice> findByBuyerCompanyName(@Param("companyName") String companyName);

//     /**
//      * All invoices routed to a specific RFQ creator — lineItems eagerly loaded.
//      */
//     @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.lineItems WHERE i.rfqCreatorUserId = :userId AND i.status <> 'DRAFT' ORDER BY i.createdAt DESC")
//     List<Invoice> findByRfqCreatorUserId(@Param("userId") Long userId);

//     // ── PO-level queries ──────────────────────────────────────────────────────

//     /**
//      * Check whether an invoice already exists for a given PO (prevent duplicates).
//      */
//     @Query("SELECT COUNT(i) > 0 FROM Invoice i WHERE i.poId = :poId AND i.status NOT IN ('REJECTED_CLOSED')")
//     boolean existsActiveInvoiceForPo(@Param("poId") Long poId);
    

//     /**
//      * Find invoice by PO id (for status lookups).
//      */
//     Optional<Invoice> findByPoId(Long poId);

//     // ── Invoice number ────────────────────────────────────────────────────────

//     Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
// }

package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // ── Supplier queries ──────────────────────────────────────────────────────

    /**
     * All invoices belonging to a supplier (any status) — lineItems eagerly loaded.
     */
    @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.lineItems WHERE i.supplierId = :supplierId ORDER BY i.createdAt DESC")
    List<Invoice> findBySupplierId(@Param("supplierId") Long supplierId);

    /**
     * Single invoice belonging to a specific supplier — lineItems eagerly loaded.
     */
    @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.lineItems WHERE i.id = :id AND i.supplierId = :supplierId")
    Optional<Invoice> findByIdAndSupplierId(@Param("id") Long id, @Param("supplierId") Long supplierId);

    /**
     * Invoices for a supplier that are in APPROVED status.
     */
    @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.lineItems WHERE i.supplierId = :supplierId AND i.status = 'APPROVED'")
    List<Invoice> findApprovedInvoicesBySupplierId(@Param("supplierId") Long supplierId);

    // ── Buyer / company queries ───────────────────────────────────────────────

    /**
     * All non-DRAFT invoices for a buyer company — lineItems eagerly loaded.
     */
    @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.lineItems WHERE i.buyerCompanyName = :companyName AND i.status <> 'DRAFT' ORDER BY i.createdAt DESC")
    List<Invoice> findByBuyerCompanyName(@Param("companyName") String companyName);

    /**
     * All invoices routed to a specific RFQ creator — lineItems eagerly loaded.
     */
    @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.lineItems WHERE i.rfqCreatorUserId = :userId AND i.status <> 'DRAFT' ORDER BY i.createdAt DESC")
    List<Invoice> findByRfqCreatorUserId(@Param("userId") Long userId);

    // ── PO-level queries ──────────────────────────────────────────────────────

    /**
     * Check whether an invoice already exists for a given PO (prevent duplicates).
     */
    @Query("SELECT COUNT(i) > 0 FROM Invoice i WHERE i.poId = :poId AND i.status NOT IN ('REJECTED_CLOSED')")
    boolean existsActiveInvoiceForPo(@Param("poId") Long poId);

    /**
     * All Invoices linked to a given PO ID — used by RFQReportService for the Invoice sheet.
     * Returns a List so callers can safely call isEmpty() / get(0).
     */
    @Query("SELECT i FROM Invoice i WHERE i.poId = :poId ORDER BY i.createdAt DESC")
    List<Invoice> findByPoId(@Param("poId") Long poId);

    // ── Invoice number ────────────────────────────────────────────────────────

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}