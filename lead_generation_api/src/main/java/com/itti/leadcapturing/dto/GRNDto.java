

package com.itti.leadcapturing.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTOs for GRN (Goods Receipt Note) — two-phase creation flow.
 *
 * PHASE 1 — CreateRequest  : receiver fills orderedQty + receivedQty only.
 * PHASE 2 — QAReviewRequest: QA team fills defectiveQty + rejectedQty + qaRemarks per line.
 */
public class GRNDto {

    // =========================================================================
    // PHASE 1 — GRN CREATE REQUEST
    // Only orderedQuantity + receivedQuantity per line.
    // No acceptance/rejection at this stage.
    // =========================================================================

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateRequest {

        private Long purchaseOrderId;           // required
        private Long invoiceId;                 // optional — link invoice at creation or later

        // Delivery metadata
        private LocalDateTime receivedDate;
        private String deliveryChallanNumber;
        private String lrNumber;
        private String transporterName;
        private String vehicleNumber;
        private String deliveryLocation;

        // Receiver info
        private Long receivedByUserId;          // required
        private String receivedByName;

        private String remarks;
        private String internalNotes;

        private List<LineItemCreateRequest> lineItems; // required — one per PO line
    }

    /**
     * Phase 1 line item — only what the receiver physically counts at the dock.
     */
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LineItemCreateRequest {

        private Long poLineItemId;              // required — references PO line
        private Integer itemOrder;

        // Pre-filled from PO (frontend sends, backend re-validates from PO)
        private String itemCode;
        private String itemDescription;
        private String specifications;
        private String brandMakeModel;
        private String uom;
        private BigDecimal orderedQuantity;     // from PO
        private BigDecimal poUnitRate;          // from PO

        // Receiver fills ONLY this:
        private BigDecimal receivedQuantity;    // how many physically arrived

        private String remarks;                 // optional line remark
    }

    // =========================================================================
    // PHASE 2 — QA REVIEW REQUEST
    // QA team fills defective/rejected quantities + remarks per line.
    // Submitted after GRN is in SUBMITTED status (moves it to QA_REVIEW,
    // then on completion to APPROVED).
    // =========================================================================

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class QAReviewRequest {

        private Long grnId;                     // required

        private String inspectedByName;         // QA inspector name
        private String qaOverallRemarks;        // overall QA remarks (optional)

        private List<QALineItemRequest> lineItems; // one entry per line being reviewed
    }

    /**
     * Phase 2 line item — QA team fills defective/rejected qty + reason.
     */
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class QALineItemRequest {

        private Long grnLineItemId;             // required — ID of GRNLineItem

        /**
         * How many units the QA team found defective / damaged.
         * Zero = all units are good.
         */
        private BigDecimal defectiveQuantity;

        /**
         * Formally rejected quantity.
         * If null, defaults to defectiveQuantity.
         * Must be ≤ receivedQuantity.
         */
        private BigDecimal rejectedQuantity;

        /**
         * Mandatory when rejectedQuantity > 0.
         * Describes reason for rejection (damaged, wrong spec, etc.)
         */
        private String qaRemarks;

        /**
         * Override itemCondition if QA wants to explicitly set WRONG_ITEM etc.
         * Optional — system will auto-derive from quantities if not provided.
         */
        private String itemCondition;
    }

    // =========================================================================
    // GRN RESPONSE — returned for all read/write operations
    // =========================================================================

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {

        private Long id;
        private String grnNumber;
        private LocalDateTime receivedDate;

        // References
        private Long purchaseOrderId;
        private String poNumber;
        private Long invoiceId;
        private String invoiceNumber;

        // Supplier
        private Long supplierId;
        private String supplierName;

        // Delivery metadata
        private String deliveryChallanNumber;
        private String lrNumber;
        private String transporterName;
        private String vehicleNumber;
        private String deliveryLocation;

        // Receiver info
        private Long receivedByUserId;
        private String receivedByName;

        // QA info
        private String inspectedByName;
        private String qaOverallRemarks;

        // Status
        private String status;
        private String approvedByName;
        private LocalDateTime approvedAt;

        // Remarks
        private String remarks;
        private String internalNotes;

        // Totals
        private BigDecimal totalOrderedValue;
        private BigDecimal totalReceivedValue;
        private BigDecimal totalRejectedValue;

        // Derived flags
        private Boolean hasRejections;
        private Boolean isFullyReceived;
        private Boolean qaCompleted;            // true when QA has been submitted

        // What the buyer can do next (for UI)
        private List<String> availableActions;

        private List<LineItemResponse> lineItems;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LineItemResponse {

        private Long id;
        private Long poLineItemId;
        private Integer itemOrder;

        private String itemCode;
        private String itemDescription;
        private String specifications;
        private String brandMakeModel;
        private String uom;

        // PO reference quantities
        private BigDecimal orderedQuantity;
        private BigDecimal poUnitRate;
        private BigDecimal poLineTotal;

        // Phase 1 — received
        private BigDecimal receivedQuantity;

        // Phase 2 — QA results
        private BigDecimal defectiveQuantity;
        private BigDecimal rejectedQuantity;
        private BigDecimal acceptedQuantity;
        private String qaRemarks;

        // Computed values
        private BigDecimal acceptedValue;
        private BigDecimal rejectedValue;

        // Derived shortfalls/excesses
        private BigDecimal shortDeliveryQuantity;   // orderedQty - receivedQty (if > 0)
        private BigDecimal excessQuantity;          // receivedQty - orderedQty (if > 0)

        private String itemCondition;
        private String remarks;
    }

    // =========================================================================
    // 3-WAY MATCH REQUEST
    // =========================================================================

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PerformMatchRequest {

        private Long purchaseOrderId;           // required
        private Long grnId;                     // required — must be APPROVED GRN
        private Long invoiceId;                 // required

        /** Tolerance % (defaults to 2.00 if not supplied). */
        private BigDecimal tolerancePercentage;

        private Long performedByUserId;
        private String performedByName;
    }

    // =========================================================================
    // 3-WAY MATCH RESOLUTION REQUEST
    // =========================================================================

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ResolveMatchRequest {

        private Long matchId;
        private String resolution;              // MatchResolution enum name
        private String resolutionRemarks;       // mandatory for any mismatch resolution

        private Long resolvedByUserId;
        private String resolvedByName;

        /**
         * For PARTIAL_PAYMENT_APPROVED: the exact amount to authorise.
         * Leave null for other resolutions.
         */
        private BigDecimal approvedPaymentAmount;
    }

    // =========================================================================
    // 3-WAY MATCH RESPONSE
    // =========================================================================

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MatchResponse {

        private Long id;
        private Integer matchVersion;

        private Long purchaseOrderId;
        private String poNumber;
        private Long grnId;
        private String grnNumber;
        private Long invoiceId;
        private String invoiceNumber;

        private BigDecimal tolerancePercentage;

        private String matchStatus;
        private String matchSummary;

        private BigDecimal poTotalValue;
        private BigDecimal grnAcceptedValue;
        private BigDecimal invoiceTotalValue;
        private BigDecimal varianceAmount;
        private BigDecimal variancePercentage;

        private Boolean hasQuantityMismatch;
        private Boolean hasPriceMismatch;
        private Boolean hasItemMismatch;
        private Boolean hasExcessDelivery;
        private Integer totalMatchedLines;
        private Integer totalMismatchLines;

        private String resolution;
        private String resolutionRemarks;
        private String resolvedByName;
        private LocalDateTime resolvedAt;
        private BigDecimal approvedPaymentAmount;

        private String performedByName;
        private LocalDateTime performedAt;
        private LocalDateTime updatedAt;

        private List<LineMatchResponse> lineResults;
        private List<String> availableResolutions;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LineMatchResponse {

        private Long id;
        private Long poLineItemId;
        private Long grnLineItemId;
        private Long invoiceLineItemId;
        private Integer itemOrder;

        private String itemCode;
        private String itemDescription;
        private String uom;

        private BigDecimal poOrderedQuantity;
        private BigDecimal grnAcceptedQuantity;
        private BigDecimal invoiceQuantity;
        private BigDecimal quantityVariance;
        private BigDecimal quantityVariancePct;

        private BigDecimal poUnitRate;
        private BigDecimal invoiceUnitPrice;
        private BigDecimal priceVariance;
        private BigDecimal priceVariancePct;

        private BigDecimal poLineValue;
        private BigDecimal grnAcceptedValue;
        private BigDecimal invoiceLineValue;
        private BigDecimal valueVariance;

        private String lineMatchStatus;
        private Boolean withinTolerance;
        private String mismatchNotes;
    }
}