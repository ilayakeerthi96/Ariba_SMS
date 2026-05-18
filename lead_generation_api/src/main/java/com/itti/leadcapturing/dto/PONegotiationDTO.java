
package com.itti.leadcapturing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PONegotiationDTO {

    // ===================== REQUEST: SAVE/UPDATE NEGOTIATION =====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveRequest {
        private Long rfqId;
        private Long supplierId;
        private Long finalSelectionId;
        private Long createdByUserId;

        private List<LineItemRequest> lineItems;

        private BigDecimal taxPercentage;
        private String paymentTerms;
        private String deliveryTerms;
        private String otherTerms;
        private String buyerRemarks;
        private String terms;

        private BigDecimal overallDiscountAmount;
        private BigDecimal overallDiscountPercentage;

        /**
         * NET PAYABLE = subtotalFinalized − overallDiscountAmount + totalTax
         */
        private BigDecimal netPayable;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LineItemRequest {
        private Long id;
        private Long rfqItemId;
        private Long supplierQuoteItemId;
        private Integer slNo;
        private String itemCode;
        private String itemDescription;
        private String specifications;
        private String brandMakeModel;
        private BigDecimal quantity;
        private String uom;
        private BigDecimal actualQuotedPrice;
        private BigDecimal discountPercentage;
        private BigDecimal finalizedPrice;
        private BigDecimal taxPercentage;
        private Integer deliveryDays;
        private Integer warrantyMonths;
    }

    // ===================== RESPONSE: NEGOTIATION DETAIL =====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long rfqId;
        private String rfqNumber;
        private Long supplierId;
        private String supplierName;
        private String supplierGstin;
        private String supplierContactName;
        private String supplierContactEmail;
        private String supplierContactPhone;
        private String supplierAddress;

        private Long finalSelectionId;
        private Boolean isSystemRecommended;
        private String justification;

        private List<LineItemResponse> lineItems;

        private BigDecimal subtotalQuoted;
        private BigDecimal subtotalFinalized;
        private BigDecimal totalDiscountAmount;
        private BigDecimal taxPercentage;
        private BigDecimal taxAmount;
        private BigDecimal grandTotalQuoted;
        private BigDecimal grandTotalFinalized;

        private BigDecimal overallDiscountAmount;
        private BigDecimal overallDiscountPercentage;
        private BigDecimal netPayable;

        private String paymentTerms;
        private String deliveryTerms;
        private String otherTerms;
        private String buyerRemarks;

        private String status;
        private Long purchaseOrderId;

        // ✅ Currency from buyer's location (set during buyer creation, NOT user-selectable)
        private String currencyCode;
        private String currencySymbol;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LineItemResponse {
        private Long id;
        private Long rfqItemId;
        private Long supplierQuoteItemId;
        private Integer slNo;
        private String itemCode;
        private String itemDescription;
        private String specifications;
        private String brandMakeModel;
        private BigDecimal quantity;
        private String uom;
        private BigDecimal actualQuotedPrice;
        private BigDecimal discountPercentage;
        private BigDecimal discountAmount;
        private BigDecimal finalizedPrice;
        private BigDecimal lineTotal;
        private BigDecimal taxPercentage;
        private BigDecimal taxAmount;
        private Integer deliveryDays;
        private Integer warrantyMonths;
    }
}