package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.SupplierQuoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ✅ COMPLETELY FIXED: Repository with proper eager loading to prevent lazy initialization errors
 */
@Repository
public interface SupplierQuoteItemRepository extends JpaRepository<SupplierQuoteItem, Long> {

    /**
     * ✅ CRITICAL FIX: Find by Supplier ID and RFQ ID with ALL necessary joins
     * This prevents the 500 error "could not initialize proxy - no session"
     */
    @Query("SELECT DISTINCT qi FROM SupplierQuoteItem qi " +
           "LEFT JOIN FETCH qi.rfqSupplier rs " +
           "LEFT JOIN FETCH rs.supplier s " +
           "LEFT JOIN FETCH rs.rfq r " +
           "LEFT JOIN FETCH qi.rfqItem i " +
           "WHERE r.id = :rfqId " +
           "AND s.id = :supplierId " +
           "ORDER BY i.itemOrder ASC")
    List<SupplierQuoteItem> findByRfqIdAndSupplierId(
        @Param("rfqId") Long rfqId,
        @Param("supplierId") Long supplierId
    );

    /**
     * ✅ Find all quote items for a specific RFQ
     */
    @Query("SELECT DISTINCT qi FROM SupplierQuoteItem qi " +
           "LEFT JOIN FETCH qi.rfqSupplier rs " +
           "LEFT JOIN FETCH rs.supplier s " +
           "LEFT JOIN FETCH rs.rfq r " +
           "LEFT JOIN FETCH qi.rfqItem i " +
           "WHERE r.id = :rfqId " +
           "ORDER BY i.itemOrder ASC, s.companyName ASC")
    List<SupplierQuoteItem> findByRfqId(@Param("rfqId") Long rfqId);

    /**
     * ✅ Find by RFQ Supplier ID
     */
    @Query("SELECT DISTINCT qi FROM SupplierQuoteItem qi " +
           "LEFT JOIN FETCH qi.rfqSupplier rs " +
           "LEFT JOIN FETCH rs.supplier s " +
           "LEFT JOIN FETCH qi.rfqItem i " +
           "WHERE rs.id = :rfqSupplierId " +
           "ORDER BY i.itemOrder ASC")
    List<SupplierQuoteItem> findByRfqSupplierId(@Param("rfqSupplierId") Long rfqSupplierId);

    /**
     * ✅ Find quote item by RFQ Item and Supplier
     */
    @Query("SELECT qi FROM SupplierQuoteItem qi " +
           "LEFT JOIN FETCH qi.rfqSupplier rs " +
           "LEFT JOIN FETCH rs.supplier s " +
           "LEFT JOIN FETCH qi.rfqItem i " +
           "WHERE i.id = :rfqItemId " +
           "AND s.id = :supplierId")
    Optional<SupplierQuoteItem> findByRfqItemAndSupplier(
        @Param("rfqItemId") Long rfqItemId,
        @Param("supplierId") Long supplierId
    );

    /**
     * ✅ Find all selected quote items
     */
    @Query("SELECT DISTINCT qi FROM SupplierQuoteItem qi " +
           "LEFT JOIN FETCH qi.rfqSupplier rs " +
           "LEFT JOIN FETCH rs.supplier s " +
           "LEFT JOIN FETCH rs.rfq r " +
           "LEFT JOIN FETCH qi.rfqItem i " +
           "WHERE r.id = :rfqId " +
           "AND qi.isSelected = true " +
           "ORDER BY i.itemOrder ASC")
    List<SupplierQuoteItem> findSelectedQuotesByRfqId(@Param("rfqId") Long rfqId);

    /**
     * Count methods
     */
    @Query("SELECT COUNT(DISTINCT qi) FROM SupplierQuoteItem qi " +
           "JOIN qi.rfqSupplier rs " +
           "JOIN rs.rfq r " +
           "WHERE r.id = :rfqId")
    Long countByRfqId(@Param("rfqId") Long rfqId);

    @Query("SELECT COUNT(DISTINCT qi) FROM SupplierQuoteItem qi " +
           "JOIN qi.rfqSupplier rs " +
           "JOIN rs.rfq r " +
           "WHERE r.id = :rfqId AND qi.isSelected = true")
    Long countSelectedByRfqId(@Param("rfqId") Long rfqId);

    @Query("DELETE FROM SupplierQuoteItem qi WHERE qi.rfqSupplier.id = :rfqSupplierId")
    void deleteByRfqSupplierId(@Param("rfqSupplierId") Long rfqSupplierId);

    @Query("SELECT CASE WHEN COUNT(DISTINCT qi) >= :totalItems THEN true ELSE false END " +
           "FROM SupplierQuoteItem qi " +
           "WHERE qi.rfqSupplier.id = :rfqSupplierId")
    Boolean hasQuotedAllItems(@Param("rfqSupplierId") Long rfqSupplierId, @Param("totalItems") Long totalItems);
}