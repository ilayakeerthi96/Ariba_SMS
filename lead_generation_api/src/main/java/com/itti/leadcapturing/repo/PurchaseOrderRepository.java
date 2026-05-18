// package com.itti.leadcapturing.repo;

// import com.itti.leadcapturing.model.PurchaseOrder;
// import com.itti.leadcapturing.model.POStatus;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import java.util.List;
// import java.util.Optional;

// /**
//  * ✅ Purchase Order Repository
//  */
// @Repository
// public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

//     // ==================== FIND METHODS ====================

//     /**
//      * Find all non-deleted POs
//      */
//     @Query("SELECT po FROM PurchaseOrder po WHERE po.isDeleted = false ORDER BY po.createdAt DESC")
//     List<PurchaseOrder> findAll();

//     /**
//      * Find PO by ID (non-deleted only)
//      */
//     @Query("SELECT po FROM PurchaseOrder po WHERE po.id = :id AND po.isDeleted = false")
//     Optional<PurchaseOrder> findById(@Param("id") Long id);

//     /**
//      * Find PO by PO Number
//      */
//     @Query("SELECT po FROM PurchaseOrder po WHERE po.poNumber = :poNumber AND po.isDeleted = false")
//     Optional<PurchaseOrder> findByPoNumber(@Param("poNumber") String poNumber);

//     /**
//      * Find all POs for a buyer
//      */
//     @Query("SELECT po FROM PurchaseOrder po WHERE po.buyer.id = :buyerId AND po.isDeleted = false ORDER BY po.createdAt DESC")
//     List<PurchaseOrder> findByBuyerId(@Param("buyerId") Long buyerId);

//     /**
//      * Find all POs for a supplier
//      */
//     @Query("SELECT po FROM PurchaseOrder po WHERE po.supplier.id = :supplierId AND po.isDeleted = false ORDER BY po.createdAt DESC")
//     List<PurchaseOrder> findBySupplierId(@Param("supplierId") Long supplierId);

//     /**
//      * Find all POs for an RFQ
//      */
//     @Query("SELECT po FROM PurchaseOrder po WHERE po.rfq.id = :rfqId AND po.isDeleted = false ORDER BY po.createdAt DESC")
//     List<PurchaseOrder> findByRfqId(@Param("rfqId") Long rfqId);

    
//     /**
//      * Find POs by status
//      */
//     @Query("SELECT po FROM PurchaseOrder po WHERE po.status = :status AND po.isDeleted = false ORDER BY po.createdAt DESC")
//     List<PurchaseOrder> findByStatus(@Param("status") POStatus status);

//     /**
//      * Find POs by buyer and status
//      */
//     @Query("SELECT po FROM PurchaseOrder po WHERE po.buyer.id = :buyerId AND po.status = :status AND po.isDeleted = false ORDER BY po.createdAt DESC")
//     List<PurchaseOrder> findByBuyerIdAndStatus(@Param("buyerId") Long buyerId, @Param("status") POStatus status);

//     /**
//      * Find POs created by a user
//      */
//     @Query("SELECT po FROM PurchaseOrder po WHERE po.createdByUserId = :userId AND po.isDeleted = false ORDER BY po.createdAt DESC")
//     List<PurchaseOrder> findByCreatedByUserId(@Param("userId") Long userId);

//     // ==================== COUNT METHODS ====================

//     /**
//      * Count all POs
//      */
//     @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.isDeleted = false")
//     Long countAll();

//     /**
//      * Count POs by buyer
//      */
//     @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.buyer.id = :buyerId AND po.isDeleted = false")
//     Long countByBuyerId(@Param("buyerId") Long buyerId);

//     /**
//      * Count POs by status
//      */
//     @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.status = :status AND po.isDeleted = false")
//     Long countByStatus(@Param("status") POStatus status);

//     /**
//      * Count POs by buyer and status
//      */
//     @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.buyer.id = :buyerId AND po.status = :status AND po.isDeleted = false")
//     Long countByBuyerIdAndStatus(@Param("buyerId") Long buyerId, @Param("status") POStatus status);

//     // ==================== EAGER LOADING ====================

//     /**
//      * Find PO with all details (line items, buyer, supplier, etc.)
//      */
//     @Query("SELECT DISTINCT po FROM PurchaseOrder po " +
//            "LEFT JOIN FETCH po.lineItems " +
//            "LEFT JOIN FETCH po.buyer " +
//            "LEFT JOIN FETCH po.supplier " +
//            "LEFT JOIN FETCH po.buyerLocation " +
//            "LEFT JOIN FETCH po.deliveryLocation " +
//            "LEFT JOIN FETCH po.rfq " +
//            "WHERE po.id = :id AND po.isDeleted = false")
//     Optional<PurchaseOrder> findByIdWithDetails(@Param("id") Long id);

//     // ==================== CHECK EXISTS ====================

//     /**
//      * Check if PO number already exists
//      */
//     @Query("SELECT CASE WHEN COUNT(po) > 0 THEN true ELSE false END FROM PurchaseOrder po WHERE po.poNumber = :poNumber AND po.isDeleted = false")
//     Boolean existsByPoNumber(@Param("poNumber") String poNumber);

//     @Query("SELECT po FROM PurchaseOrder po " +
//        "WHERE (po.buyer.organizationCompanyName = :companyName " +
//        "   OR po.buyer.companyName = :companyName) " +
//        "AND (po.isDeleted = false OR po.isDeleted IS NULL) " +
//        "ORDER BY po.createdAt DESC")
// List<PurchaseOrder> findByCompanyName(@Param("companyName") String companyName);

// /**
//  * Find all APPROVED Purchase Orders for a specific supplier
//  * Used by supplier dashboard PO tab and invoice creation
//  * GET /api/invoice/supplier/{supplierId}/approved-pos
//  */
// @Query("SELECT po FROM PurchaseOrder po " +
//        "WHERE po.supplier.id = :supplierId " +
//        "AND po.approvalStatus = com.itti.leadcapturing.model.ApprovalStatus.APPROVED " +
//        "AND (po.isDeleted = false OR po.isDeleted IS NULL) " +
//        "ORDER BY po.createdAt DESC")
// List<PurchaseOrder> findBySupplierIdAndApprovedStatus(@Param("supplierId") Long supplierId);


// }

package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.PurchaseOrder;
import com.itti.leadcapturing.model.POStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ✅ Purchase Order Repository
 */
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    // ==================== FIND METHODS ====================

    /**
     * Find all non-deleted POs
     */
    @Query("SELECT po FROM PurchaseOrder po WHERE po.isDeleted = false ORDER BY po.createdAt DESC")
    List<PurchaseOrder> findAllActive();

    /**
     * Find PO by ID (non-deleted only)
     */
    @Query("SELECT po FROM PurchaseOrder po WHERE po.id = :id AND po.isDeleted = false")
    Optional<PurchaseOrder> findById(@Param("id") Long id);

    /**
     * Find PO by PO Number
     */
    @Query("SELECT po FROM PurchaseOrder po WHERE po.poNumber = :poNumber AND po.isDeleted = false")
    Optional<PurchaseOrder> findByPoNumber(@Param("poNumber") String poNumber);

    /**
     * Find all POs for a buyer
     */
    @Query("SELECT po FROM PurchaseOrder po WHERE po.buyer.id = :buyerId AND po.isDeleted = false ORDER BY po.createdAt DESC")
    List<PurchaseOrder> findByBuyerId(@Param("buyerId") Long buyerId);

    /**
     * Find all POs for a supplier
     */
    @Query("SELECT po FROM PurchaseOrder po WHERE po.supplier.id = :supplierId AND po.isDeleted = false ORDER BY po.createdAt DESC")
    List<PurchaseOrder> findBySupplierId(@Param("supplierId") Long supplierId);

    /**
     * Find all POs for an RFQ — used by RFQReportService to load PO sheet
     */
    @Query("SELECT po FROM PurchaseOrder po WHERE po.rfq.id = :rfqId AND (po.isDeleted = false OR po.isDeleted IS NULL) ORDER BY po.createdAt DESC")
    List<PurchaseOrder> findByRfqId(@Param("rfqId") Long rfqId);

    /**
     * Find POs by status
     */
    @Query("SELECT po FROM PurchaseOrder po WHERE po.status = :status AND po.isDeleted = false ORDER BY po.createdAt DESC")
    List<PurchaseOrder> findByStatus(@Param("status") POStatus status);

    /**
     * Find POs by buyer and status
     */
    @Query("SELECT po FROM PurchaseOrder po WHERE po.buyer.id = :buyerId AND po.status = :status AND po.isDeleted = false ORDER BY po.createdAt DESC")
    List<PurchaseOrder> findByBuyerIdAndStatus(@Param("buyerId") Long buyerId, @Param("status") POStatus status);

    /**
     * Find POs created by a user
     */
    @Query("SELECT po FROM PurchaseOrder po WHERE po.createdByUserId = :userId AND po.isDeleted = false ORDER BY po.createdAt DESC")
    List<PurchaseOrder> findByCreatedByUserId(@Param("userId") Long userId);

    // ==================== COUNT METHODS ====================

    /**
     * Count all POs
     */
    @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.isDeleted = false")
    Long countAll();

    /**
     * Count POs by buyer
     */
    @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.buyer.id = :buyerId AND po.isDeleted = false")
    Long countByBuyerId(@Param("buyerId") Long buyerId);

    /**
     * Count POs by status
     */
    @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.status = :status AND po.isDeleted = false")
    Long countByStatus(@Param("status") POStatus status);

    /**
     * Count POs by buyer and status
     */
    @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.buyer.id = :buyerId AND po.status = :status AND po.isDeleted = false")
    Long countByBuyerIdAndStatus(@Param("buyerId") Long buyerId, @Param("status") POStatus status);

    // ==================== EAGER LOADING ====================

    /**
     * Find PO with all details (line items, buyer, supplier, etc.)
     */
    @Query("SELECT DISTINCT po FROM PurchaseOrder po " +
           "LEFT JOIN FETCH po.lineItems " +
           "LEFT JOIN FETCH po.buyer " +
           "LEFT JOIN FETCH po.supplier " +
           "LEFT JOIN FETCH po.buyerLocation " +
           "LEFT JOIN FETCH po.deliveryLocation " +
           "LEFT JOIN FETCH po.rfq " +
           "WHERE po.id = :id AND po.isDeleted = false")
    Optional<PurchaseOrder> findByIdWithDetails(@Param("id") Long id);

    // ==================== CHECK EXISTS ====================

    /**
     * Check if PO number already exists
     */
    @Query("SELECT CASE WHEN COUNT(po) > 0 THEN true ELSE false END FROM PurchaseOrder po WHERE po.poNumber = :poNumber AND po.isDeleted = false")
    Boolean existsByPoNumber(@Param("poNumber") String poNumber);

    /**
     * Find POs by buyer company name (supports both field variants)
     */
    @Query("SELECT po FROM PurchaseOrder po " +
           "WHERE (po.buyer.organizationCompanyName = :companyName " +
           "   OR po.buyer.companyName = :companyName) " +
           "AND (po.isDeleted = false OR po.isDeleted IS NULL) " +
           "ORDER BY po.createdAt DESC")
    List<PurchaseOrder> findByCompanyName(@Param("companyName") String companyName);

    /**
     * Find all APPROVED Purchase Orders for a specific supplier.
     * Used by supplier dashboard PO tab and invoice creation.
     * GET /api/invoice/supplier/{supplierId}/approved-pos
     */
    @Query("SELECT po FROM PurchaseOrder po " +
           "WHERE po.supplier.id = :supplierId " +
           "AND po.approvalStatus = com.itti.leadcapturing.model.ApprovalStatus.APPROVED " +
           "AND (po.isDeleted = false OR po.isDeleted IS NULL) " +
           "ORDER BY po.createdAt DESC")
    List<PurchaseOrder> findBySupplierIdAndApprovedStatus(@Param("supplierId") Long supplierId);
}