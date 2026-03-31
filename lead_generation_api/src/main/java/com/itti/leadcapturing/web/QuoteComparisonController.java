// package com.itti.leadcapturing.web;

// import com.itti.leadcapturing.dto.QuoteComparisonResponse;
// import com.itti.leadcapturing.service.QuoteComparisonService;
// import com.itti.leadcapturing.service.SupplierQuoteService;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.http.HttpStatus;
// import org.springframework.web.bind.annotation.*;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// /**
//  * ✅ CONTROLLER: Quote Comparison & Selection
//  * Handles quote comparison view and selection APIs
//  */
// @RestController
// @RequestMapping("/api/quote-comparison")
// @CrossOrigin(origins = "*")
// public class QuoteComparisonController {

//     private static final Logger logger = LoggerFactory.getLogger(QuoteComparisonController.class);

//     @Autowired
//     private QuoteComparisonService quoteComparisonService;

//     @Autowired
//     private SupplierQuoteService supplierQuoteService;

//     // ==================== GET QUOTE COMPARISON ====================

//     /**
//      * GET /api/quote-comparison/rfq/{rfqId}
//      * Get complete quote comparison for an RFQ
//      * 
//      * Returns:
//      * - All suppliers who submitted quotes
//      * - Item-wise comparison with all suppliers' rates
//      * - Lowest/highest bids
//      * - Summary statistics
//      */
//     @GetMapping("/rfq/{rfqId}")
//     public ResponseEntity<?> getQuoteComparison(@PathVariable Long rfqId) {
//         logger.info("📊 [API] GET Quote Comparison - RFQ ID: {}", rfqId);

//         try {
//             QuoteComparisonResponse comparison = quoteComparisonService.getQuoteComparison(rfqId);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("data", comparison);

//             logger.info("  ✅ Comparison retrieved successfully");
//             return ResponseEntity.ok(response);

//         } catch (RuntimeException e) {
//             logger.error("  ❌ Error: {}", e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());

//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Unexpected error", e);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to fetch quote comparison");

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // ==================== SELECT QUOTES ====================

//     /**
//      * POST /api/quote-comparison/select
//      * Select winning quotes for items
//      * 
//      * Request Body:
//      * {
//      *   "rfqId": 1,
//      *   "selections": [
//      *     {"rfqItemId": 1, "supplierId": 5, "remarks": "Best price"},
//      *     {"rfqItemId": 2, "supplierId": 3, "remarks": "Quality + delivery"}
//      *   ],
//      *   "remarks": "Selected based on price and quality"
//      * }
//      */
//     @PostMapping("/select")
//     public ResponseEntity<?> selectQuotes(@RequestBody Map<String, Object> request) {
//         logger.info("✅ [API] SELECT QUOTES");

//         try {
//             Long rfqId = Long.valueOf(request.get("rfqId").toString());
//             @SuppressWarnings("unchecked")
//             List<Map<String, Object>> selections = (List<Map<String, Object>>) request.get("selections");
//             String remarks = request.get("remarks") != null ? request.get("remarks").toString() : "";

//             logger.info("  RFQ ID: {}, Selections: {}", rfqId, selections.size());

//             String result = quoteComparisonService.selectQuotes(rfqId, selections, remarks);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", result);

//             logger.info("  ✅ Selection successful");
//             return ResponseEntity.ok(response);

//         } catch (RuntimeException e) {
//             logger.error("  ❌ Error: {}", e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());

//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Unexpected error", e);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to select quotes");

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // ==================== UNSELECT QUOTE ====================

//     /**
//      * DELETE /api/quote-comparison/unselect/{quoteItemId}
//      * Remove selection from a quote item
//      */
//     @DeleteMapping("/unselect/{quoteItemId}")
//     public ResponseEntity<?> unselectQuote(@PathVariable Long quoteItemId) {
//         logger.info("❌ [API] UNSELECT QUOTE - Quote Item ID: {}", quoteItemId);

//         try {
//             String result = quoteComparisonService.unselectQuote(quoteItemId);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", result);

//             logger.info("  ✅ Unselected successfully");
//             return ResponseEntity.ok(response);

//         } catch (RuntimeException e) {
//             logger.error("  ❌ Error: {}", e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());

//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Unexpected error", e);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to unselect quote");

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // ==================== GET SELECTED QUOTES ====================

//     /**
//      * GET /api/quote-comparison/rfq/{rfqId}/selected
//      * Get only selected/awarded quotes for an RFQ
//      */
//     @GetMapping("/rfq/{rfqId}/selected")
//     public ResponseEntity<?> getSelectedQuotes(@PathVariable Long rfqId) {
//         logger.info("🏆 [API] GET SELECTED QUOTES - RFQ ID: {}", rfqId);

//         try {
//             QuoteComparisonResponse comparison = quoteComparisonService.getQuoteComparison(rfqId);
            
//             // Filter to show only awarded items
//             List<QuoteComparisonResponse.ItemComparison> awardedItems = comparison.getItems().stream()
//                     .filter(item -> Boolean.TRUE.equals(item.getIsAwarded()))
//                     .toList();

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("totalAwarded", awardedItems.size());
//             response.put("data", awardedItems);

//             logger.info("  ✅ Found {} awarded items", awardedItems.size());
//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Error: {}", e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to fetch selected quotes");

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // ==================== GET QUOTE STATISTICS ====================

//     /**
//      * GET /api/quote-comparison/rfq/{rfqId}/statistics
//      * Get quote statistics summary
//      */
//     @GetMapping("/rfq/{rfqId}/statistics")
//     public ResponseEntity<?> getQuoteStatistics(@PathVariable Long rfqId) {
//         logger.info("📈 [API] GET QUOTE STATISTICS - RFQ ID: {}", rfqId);

//         try {
//             QuoteComparisonResponse comparison = quoteComparisonService.getQuoteComparison(rfqId);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("summary", comparison.getSummary());
//             response.put("suppliers", comparison.getSuppliers());

//             logger.info("  ✅ Statistics retrieved");
//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Error: {}", e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to fetch statistics");

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // ==================== EXPORT COMPARISON (PLACEHOLDER) ====================

//     /**
//      * GET /api/quote-comparison/rfq/{rfqId}/export
//      * Export comparison to Excel
//      * 
//      * TODO: Implement Excel export using Apache POI
//      */
//     @GetMapping("/rfq/{rfqId}/export")
//     public ResponseEntity<?> exportComparison(@PathVariable Long rfqId) {
//         logger.info("📥 [API] EXPORT COMPARISON - RFQ ID: {}", rfqId);

//         Map<String, Object> response = new HashMap<>();
//         response.put("success", false);
//         response.put("message", "Export feature coming soon");

//         return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(response);
//     }
// }

package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.QuoteComparisonResponse;
import com.itti.leadcapturing.service.QuoteComparisonService;
import com.itti.leadcapturing.service.SupplierQuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ✅ CONTROLLER: Quote Comparison & Selection
 * Handles quote comparison view and selection APIs
 */
@RestController
@RequestMapping("/api/quote-comparison")
@CrossOrigin(origins = "*")
public class QuoteComparisonController {

    private static final Logger logger = LoggerFactory.getLogger(QuoteComparisonController.class);

    @Autowired
    private QuoteComparisonService quoteComparisonService;

    @Autowired
    private SupplierQuoteService supplierQuoteService;

    // ==================== GET QUOTE COMPARISON ====================

    /**
     * GET /api/quote-comparison/rfq/{rfqId}
     * Get complete quote comparison for an RFQ
     * 
     * Returns:
     * - All suppliers who submitted quotes
     * - Item-wise comparison with all suppliers' rates
     * - Lowest/highest bids
     * - Summary statistics
     */
    @GetMapping("/rfq/{rfqId}")
    public ResponseEntity<?> getQuoteComparison(@PathVariable Long rfqId) {
        logger.info("📊 [API] GET Quote Comparison - RFQ ID: {}", rfqId);

        try {
            QuoteComparisonResponse comparison = quoteComparisonService.getQuoteComparison(rfqId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", comparison);

            logger.info("  ✅ Comparison retrieved successfully");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            logger.error("  ❌ Unexpected error", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch quote comparison");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== SELECT QUOTES ====================

    /**
     * POST /api/quote-comparison/select
     * Select winning quotes for items
     * 
     * Request Body:
     * {
     *   "rfqId": 1,
     *   "selections": [
     *     {"rfqItemId": 1, "supplierId": 5, "remarks": "Best price"},
     *     {"rfqItemId": 2, "supplierId": 3, "remarks": "Quality + delivery"}
     *   ],
     *   "remarks": "Selected based on price and quality"
     * }
     */
    @PostMapping("/select")
    public ResponseEntity<?> selectQuotes(@RequestBody Map<String, Object> request) {
        logger.info("✅ [API] SELECT QUOTES");

        try {
            Long rfqId = Long.valueOf(request.get("rfqId").toString());
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> selections = (List<Map<String, Object>>) request.get("selections");
            String remarks = request.get("remarks") != null ? request.get("remarks").toString() : "";

            logger.info("  RFQ ID: {}, Selections: {}", rfqId, selections.size());

            String result = quoteComparisonService.selectQuotes(rfqId, selections, remarks);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            logger.info("  ✅ Selection successful");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            logger.error("  ❌ Unexpected error", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to select quotes");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== UNSELECT QUOTE ====================

    /**
     * DELETE /api/quote-comparison/unselect/{quoteItemId}
     * Remove selection from a quote item
     */
    @DeleteMapping("/unselect/{quoteItemId}")
    public ResponseEntity<?> unselectQuote(@PathVariable Long quoteItemId) {
        logger.info("❌ [API] UNSELECT QUOTE - Quote Item ID: {}", quoteItemId);

        try {
            String result = quoteComparisonService.unselectQuote(quoteItemId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            logger.info("  ✅ Unselected successfully");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            logger.error("  ❌ Unexpected error", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to unselect quote");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== GET SELECTED QUOTES ====================

    /**
     * GET /api/quote-comparison/rfq/{rfqId}/selected
     * Get only selected/awarded quotes for an RFQ
     */
    @GetMapping("/rfq/{rfqId}/selected")
    public ResponseEntity<?> getSelectedQuotes(@PathVariable Long rfqId) {
        logger.info("🏆 [API] GET SELECTED QUOTES - RFQ ID: {}", rfqId);

        try {
            QuoteComparisonResponse comparison = quoteComparisonService.getQuoteComparison(rfqId);
            
            // Filter to show only awarded items
            List<QuoteComparisonResponse.ItemComparison> awardedItems = comparison.getItems().stream()
                    .filter(item -> Boolean.TRUE.equals(item.getIsAwarded()))
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalAwarded", awardedItems.size());
            response.put("data", awardedItems);

            logger.info("  ✅ Found {} awarded items", awardedItems.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch selected quotes");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== GET QUOTE STATISTICS ====================

    /**
     * GET /api/quote-comparison/rfq/{rfqId}/statistics
     * Get quote statistics summary
     */
    @GetMapping("/rfq/{rfqId}/statistics")
    public ResponseEntity<?> getQuoteStatistics(@PathVariable Long rfqId) {
        logger.info("📈 [API] GET QUOTE STATISTICS - RFQ ID: {}", rfqId);

        try {
            QuoteComparisonResponse comparison = quoteComparisonService.getQuoteComparison(rfqId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("summary", comparison.getSummary());
            response.put("suppliers", comparison.getSuppliers());

            logger.info("  ✅ Statistics retrieved");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch statistics");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== EXPORT COMPARISON (PLACEHOLDER) ====================

    /**
     * GET /api/quote-comparison/rfq/{rfqId}/export
     * Export comparison to Excel
     * 
     * TODO: Implement Excel export using Apache POI
     */
    @GetMapping("/rfq/{rfqId}/export")
    public ResponseEntity<?> exportComparison(@PathVariable Long rfqId) {
        logger.info("📥 [API] EXPORT COMPARISON - RFQ ID: {}", rfqId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Export feature coming soon");

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(response);
    }
}