package com.itti.leadcapturing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Supplier Final Selection (with/without system recommendation)
 */
public class SupplierFinalSelectionDTO {

    // ===================== REQUEST =====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        /** The RFQ for which supplier is being selected */
        private Long rfqId;

        /** The supplier being selected */
        private Long supplierId;

        /** Was this supplier the system-recommended one? */
        private Boolean isSystemRecommended;

        /**
         * MANDATORY if isSystemRecommended = false.
         * Why the creator is choosing a non-recommended supplier.
         */
        private String justification;

        /** Optional remarks for any selection */
        private String remarks;

        /** User ID of the RFQ creator making the selection */
        private Long selectedByUserId;

        /** User's full name for audit trail */
        private String selectedByUserName;
    }

    // ===================== RESPONSE =====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long rfqId;
        private String rfqNumber;
        private String rfqTitle;

        private Long supplierId;
        private String supplierName;
        private String supplierEmail;
        private String supplierPhone;

        private Boolean isSystemRecommended;
        private Integer systemRecommendedRank;
        private BigDecimal systemFinalScore;

        private String justification;
        private String remarks;

        private Long selectedByUserId;
        private String selectedByUserName;

        private String selectionStatus;
        private LocalDateTime selectedAt;

        private Long poNegotiationId;
        private Long purchaseOrderId;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}