
// package com.itti.leadcapturing.dto;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.List;

// public class PONegotiationDTO {

//     // ===================== REQUEST: SAVE/UPDATE NEGOTIATION =====================

//     @Data
//     @NoArgsConstructor
//     @AllArgsConstructor
//     @Builder
//     public static class SaveRequest {
//         private Long rfqId;
//         private Long supplierId;
//         private Long finalSelectionId;
//         private Long createdByUserId;

//         private List<LineItemRequest> lineItems;

//         private BigDecimal taxPercentage;   // kept for header-level (optional legacy use)
//         private String paymentTerms;
//         private String deliveryTerms;
//         private String otherTerms;
//         private String buyerRemarks;
//         private String terms;               // generic terms/notes field from frontend

//         // ✅ Overall / header-level additional discount (applied on finalized subtotal)
//         private BigDecimal overallDiscountAmount;
//         private BigDecimal overallDiscountPercentage;

//         /**
//          * ✅ NET PAYABLE = subtotalFinalized − overallDiscountAmount + totalTax
//          * This is the FINAL amount that should be stored as grandTotal on the PO.
//          * If provided, this takes precedence over a recalculated grandTotal.
//          */
//         private BigDecimal netPayable;
//     }

//     @Data
//     @NoArgsConstructor
//     @AllArgsConstructor
//     @Builder
//     public static class LineItemRequest {
//         /** Database ID of the PONegotiationLineItem — used for matching in saveNegotiation() */
//         private Long id;

//         private Long rfqItemId;
//         private Long supplierQuoteItemId;
//         private Integer slNo;
//         private String itemCode;
//         private String itemDescription;
//         private String specifications;
//         private String brandMakeModel;
//         private BigDecimal quantity;
//         private String uom;

//         /** Original supplier quote price — displayed for reference, not editable */
//         private BigDecimal actualQuotedPrice;

//         /**
//          * Discount % agreed after negotiation.
//          * finalizedPrice = actualQuotedPrice - (actualQuotedPrice × discountPercentage / 100)
//          */
//         private BigDecimal discountPercentage;

//         /**
//          * Directly supply the finalized price.
//          * If both provided, finalizedPrice takes precedence.
//          */
//         private BigDecimal finalizedPrice;

//         // ✅ Per-line tax percentage (e.g. 18 for 18% GST)
//         private BigDecimal taxPercentage;

//         private Integer deliveryDays;
//         private Integer warrantyMonths;
//     }

//     // ===================== RESPONSE: NEGOTIATION DETAIL =====================

//     @Data
//     @NoArgsConstructor
//     @AllArgsConstructor
//     @Builder
//     public static class Response {
//         private Long id;
//         private Long rfqId;
//         private String rfqNumber;
//         private Long supplierId;
//         private String supplierName;
//         private String supplierGstin;
//         private String supplierContactName;
//         private String supplierContactEmail;
//         private String supplierContactPhone;
//         private String supplierAddress;

//         private Long finalSelectionId;
//         private Boolean isSystemRecommended;
//         private String justification;

//         private List<LineItemResponse> lineItems;

//         private BigDecimal subtotalQuoted;
//         private BigDecimal subtotalFinalized;    // pre-tax sum of line totals
//         private BigDecimal totalDiscountAmount;
//         private BigDecimal taxPercentage;        // header-level (legacy/display)
//         private BigDecimal taxAmount;            // ✅ total tax = sum of per-line taxAmount
//         private BigDecimal grandTotalQuoted;
//         private BigDecimal grandTotalFinalized;  // subtotalFinalized + taxAmount

//         // ✅ Overall / header-level additional discount fields
//         private BigDecimal overallDiscountAmount;
//         private BigDecimal overallDiscountPercentage;
//         /** NET PAYABLE = subtotalFinalized − overallDiscountAmount + totalTax */
//         private BigDecimal netPayable;

//         private String paymentTerms;
//         private String deliveryTerms;
//         private String otherTerms;
//         private String buyerRemarks;

//         private String status;
//         private Long purchaseOrderId;

//         private LocalDateTime createdAt;
//         private LocalDateTime updatedAt;
//     }

//     @Data
//     @NoArgsConstructor
//     @AllArgsConstructor
//     @Builder
//     public static class LineItemResponse {
//         private Long id;
//         private Long rfqItemId;
//         private Long supplierQuoteItemId;
//         private Integer slNo;
//         private String itemCode;
//         private String itemDescription;
//         private String specifications;
//         private String brandMakeModel;
//         private BigDecimal quantity;
//         private String uom;

//         /** Original quoted price for reference */
//         private BigDecimal actualQuotedPrice;

//         /** Discount % negotiated */
//         private BigDecimal discountPercentage;

//         /** Calculated discount amount */
//         private BigDecimal discountAmount;

//         /** Price after discount (per unit) */
//         private BigDecimal finalizedPrice;

//         /** Pre-tax line total (qty × finalizedPrice) */
//         private BigDecimal lineTotal;

//         // ✅ Per-line tax fields
//         /** Tax percentage for this specific line item */
//         private BigDecimal taxPercentage;

//         /** Tax amount = lineTotal × taxPercentage / 100 */
//         private BigDecimal taxAmount;

//         private Integer deliveryDays;
//         private Integer warrantyMonths;
//     }
// }

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