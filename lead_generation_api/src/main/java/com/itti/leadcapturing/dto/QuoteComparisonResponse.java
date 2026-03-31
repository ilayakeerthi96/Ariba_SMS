// package com.itti.leadcapturing.dto;

// import lombok.*;
// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Map;

// /**
//  * ✅ DTO: Quote Comparison Response
//  * Main response for quote comparison view
//  */
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class QuoteComparisonResponse {
    
//     // RFQ Info
//     private Long rfqId;
//     private String rfqNumber;
//     private String rfqTitle;
//     private String rfqDescription;
//     private LocalDateTime dueDate;
//     private String buyerName;

//     private String currencyCode;
// private String currencySymbol;
    
//     // Suppliers who submitted quotes
//     private List<SupplierInfo> suppliers;
    
//     // Item-wise comparison
//     private List<ItemComparison> items;
    
//     // Summary
//     private QuoteSummary summary;
    
//     // ==================== NESTED CLASSES ====================
    
//     /**
//      * Supplier information
//      */
//     @Data
//     @NoArgsConstructor
//     @AllArgsConstructor
//     @Builder
//     public static class SupplierInfo {
//         private Long supplierId;
//         private String companyName;
//         private String contactEmail;
//         private String contactPhone;
//         private LocalDateTime quotedAt;
//         private BigDecimal totalQuoteAmount;
//         private String status;
//     }
    
//     /**
//      * Item-wise comparison (one row per item)
//      */
//     @Data
//     @NoArgsConstructor
//     @AllArgsConstructor
//     @Builder
//     public static class ItemComparison {
//         private Integer slNo;
//         private Long rfqItemId;
//         private String itemCode;
//         private String itemDescription;
//         private String specifications;
//         private String uom;
//         private BigDecimal requiredQuantity;
        
//         // Supplier quotes for this item (key = supplierId)
//         private Map<Long, SupplierQuote> supplierQuotes;
        
//         // Lowest/Highest bids
//         private LowestBidInfo lowestBid;
//         private HighestBidInfo highestBid;
        
//         // Selection status
//         private Boolean isAwarded;
//         private Long awardedToSupplierId;
//         private String awardedToSupplierName;
//     }
    
//     /**
//      * Supplier's quote for a specific item
//      */
//     @Data
//     @NoArgsConstructor
//     @AllArgsConstructor
//     @Builder
//     public static class SupplierQuote {
//         private Long quoteItemId;
//         private BigDecimal unitRate;
//         private BigDecimal quotedQuantity;
//         private BigDecimal totalAmount;
//         private BigDecimal taxPercentage;
//         private BigDecimal taxAmount;
//         private BigDecimal grandTotal;
//         private String remarks;
//         private Integer deliveryDays;
//         private Integer warrantyMonths;
//         private String brandOffered;
//         private String makeModel;
//         private String countryOfOrigin;
//         private String paymentTerms;
//         private Boolean isSelected;
//         private LocalDateTime selectedDate;
//     }
    
//     /**
//      * Lowest bid information
//      */
//     @Data
//     @NoArgsConstructor
//     @AllArgsConstructor
//     @Builder
//     public static class LowestBidInfo {
//         private Long supplierId;
//         private String supplierName;
//         private BigDecimal unitRate;
//         private BigDecimal totalAmount;
//     }
    
//     /**
//      * Highest bid information
//      */
//     @Data
//     @NoArgsConstructor
//     @AllArgsConstructor
//     @Builder
//     public static class HighestBidInfo {
//         private Long supplierId;
//         private String supplierName;
//         private BigDecimal unitRate;
//         private BigDecimal totalAmount;
//     }
    
//     /**
//      * Overall quote summary
//      */
//     @Data
//     @NoArgsConstructor
//     @AllArgsConstructor
//     @Builder
//     public static class QuoteSummary {
//         private Integer totalItems;
//         private Integer totalSuppliers;
//         private Integer itemsQuoted;
//         private Integer itemsAwarded;
//         private Integer itemsPending;
        
//         // Supplier-wise totals
//         private Map<Long, SupplierTotal> supplierTotals;
        
//         // Overall lowest bidder
//         private Long lowestBidderSupplierId;
//         private String lowestBidderName;
//         private BigDecimal lowestTotalAmount;
//     }
    
//     /**
//      * Supplier total amounts
//      */
//     @Data
//     @NoArgsConstructor
//     @AllArgsConstructor
//     @Builder
//     public static class SupplierTotal {
//         private Long supplierId;
//         private String supplierName;
//         private BigDecimal subtotal;
//         private BigDecimal taxTotal;
//         private BigDecimal grandTotal;
//         private Integer itemsQuoted;
//         private Integer itemsSelected;
//     }
// }

// /**
//  * ✅ DTO: Quote Submission Request (from supplier)
//  */
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// class QuoteSubmissionRequest {
//     private Long rfqId;
//     private Long supplierId;
//     private List<QuoteItemRequest> items;
//     private String generalRemarks;
//     private String paymentTerms;
//     private Integer overallDeliveryDays;
// }

// /**
//  * ✅ DTO: Quote Item Request (item-level pricing)
//  */
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// class QuoteItemRequest {
//     private Long rfqItemId;
//     private BigDecimal unitRate;
//     private BigDecimal quotedQuantity;
//     private BigDecimal taxPercentage;
//     private String remarks;
//     private Integer deliveryDays;
//     private Integer warrantyMonths;
//     private String brandOffered;
//     private String makeModel;
//     private String countryOfOrigin;
//     private String paymentTerms;
// }

// /**
//  * ✅ DTO: Quote Selection Request (buyer selects winning quotes)
//  */
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// class QuoteSelectionRequest {
//     private Long rfqId;
//     private List<SelectedQuote> selections;
//     private String selectionRemarks;
// }

// /**
//  * ✅ DTO: Selected Quote
//  */
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// class SelectedQuote {
//     private Long rfqItemId;
//     private Long supplierId;
//     private String remarks;
// }



package com.itti.leadcapturing.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteComparisonResponse {

    // RFQ Info
    private Long rfqId;
    private String rfqNumber;
    private String rfqTitle;
    private String rfqDescription;
    private LocalDateTime dueDate;
    private String buyerName;

    // Buyer's location currency (kept for backward compat — used as page-level default)
    private String currencyCode;
    private String currencySymbol;

    // Suppliers who submitted quotes
    private List<SupplierInfo> suppliers;

    // Item-wise comparison
    private List<ItemComparison> items;

    // Summary
    private QuoteSummary summary;

    // ==================== NESTED CLASSES ====================

    /**
     * Supplier information — now includes per-supplier resolved currency.
     *
     * Currency resolution rule (same as PurchaseOrderService.applyCurrency):
     *   buyer location country == supplier HQ country  →  buyer's own currency (INR, EUR, etc.)
     *   different countries                            →  USD
     *
     * This means:
     *   India buyer + India supplier   → currencyCode = INR
     *   India buyer + Singapore supplier → currencyCode = USD
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SupplierInfo {
        private Long supplierId;
        private String companyName;
        private String contactEmail;
        private String contactPhone;
        private LocalDateTime quotedAt;
        private BigDecimal totalQuoteAmount;
        private String status;

        // ✅ Per-supplier resolved currency (set by QuoteComparisonService.buildSupplierInfoList)
        private String currencyCode;    // e.g. "INR" or "USD"
        private String currencySymbol;  // e.g. "₹" or "$"
    }

    /**
     * Item-wise comparison (one row per item)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemComparison {
        private Integer slNo;
        private Long rfqItemId;
        private String itemCode;
        private String itemDescription;
        private String itemDescriptionDetailed;
        private String specifications;
        private String uom;
        private BigDecimal requiredQuantity;

        // Supplier quotes for this item (key = supplierId)
        private Map<Long, SupplierQuote> supplierQuotes;

        // Lowest/Highest bids
        private LowestBidInfo lowestBid;
        private HighestBidInfo highestBid;

        // Selection status
        private Boolean isAwarded;
        private Long awardedToSupplierId;
        private String awardedToSupplierName;
    }

    /**
     * Supplier's quote for a specific item
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SupplierQuote {
        private Long quoteItemId;
        private BigDecimal unitRate;
        private BigDecimal quotedQuantity;
        private BigDecimal totalAmount;
        private BigDecimal taxPercentage;
        private BigDecimal taxAmount;
        private BigDecimal grandTotal;
        private String remarks;
        private Integer deliveryDays;
        private Integer warrantyMonths;
        private String brandOffered;
        private String makeModel;
        private String countryOfOrigin;
        private String paymentTerms;
        private Boolean isSelected;
        private LocalDateTime selectedDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LowestBidInfo {
        private Long supplierId;
        private String supplierName;
        private BigDecimal unitRate;
        private BigDecimal totalAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HighestBidInfo {
        private Long supplierId;
        private String supplierName;
        private BigDecimal unitRate;
        private BigDecimal totalAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuoteSummary {
        private Integer totalItems;
        private Integer totalSuppliers;
        private Integer itemsQuoted;
        private Integer itemsAwarded;
        private Integer itemsPending;
        private Map<Long, SupplierTotal> supplierTotals;
        private Long lowestBidderSupplierId;
        private String lowestBidderName;
        private BigDecimal lowestTotalAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SupplierTotal {
        private Long supplierId;
        private String supplierName;
        private BigDecimal subtotal;
        private BigDecimal taxTotal;
        private BigDecimal grandTotal;
        private Integer itemsQuoted;
        private Integer itemsSelected;
    }
}