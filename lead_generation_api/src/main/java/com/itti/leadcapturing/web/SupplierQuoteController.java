

package com.itti.leadcapturing.web;

import com.itti.leadcapturing.model.SupplierQuoteItem;
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
 * ✅ UPDATED: Supplier Quote Controller with GET endpoint for viewing quotes
 */
@RestController
@RequestMapping("/api/supplier-quote")
@CrossOrigin(origins = "*")
public class SupplierQuoteController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierQuoteController.class);

    @Autowired
    private SupplierQuoteService supplierQuoteService;

    // ==================== SUBMIT QUOTE ====================

    /**
     * POST /api/supplier-quote/submit
     * Submit item-level quotes for an RFQ
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitQuote(@RequestBody Map<String, Object> request) {
        logger.info("📝 [API] SUBMIT QUOTE");

        try {
            Long supplierId = Long.valueOf(request.get("supplierId").toString());
            Long rfqId = Long.valueOf(request.get("rfqId").toString());
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");

            logger.info("  Supplier ID: {}, RFQ ID: {}, Items: {}", supplierId, rfqId, items.size());

            String result = supplierQuoteService.submitItemQuote(supplierId, rfqId, items);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            logger.info("  ✅ Quote submitted successfully");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            logger.error("  ❌ Unexpected error", e);
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to submit quote");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== ✅ NEW: GET SUPPLIER QUOTES ====================

    /**
     * GET /api/supplier-quote/supplier/{supplierId}/rfq/{rfqId}
     * Get all quotes submitted by supplier for an RFQ
     * 
     * This endpoint is called when viewing submitted quotes
     */
    @GetMapping("/supplier/{supplierId}/rfq/{rfqId}")
    public ResponseEntity<?> getSubmittedQuotes(
            @PathVariable Long supplierId,
            @PathVariable Long rfqId) {
        
        logger.info("📋 [API] GET SUBMITTED QUOTES - Supplier: {}, RFQ: {}", supplierId, rfqId);

        try {
            List<SupplierQuoteItem> quotes = supplierQuoteService.getSupplierQuotes(supplierId, rfqId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", quotes.size());
            response.put("data", quotes);

            logger.info("  ✅ Found {} quotes", quotes.size());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            logger.error("  ❌ Unexpected error", e);
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch quotes");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== UPDATE QUOTE ====================

    /**
     * PUT /api/supplier-quote/{quoteItemId}
     * Update a single item quote
     */
    @PutMapping("/{quoteItemId}")
    public ResponseEntity<?> updateQuote(
            @PathVariable Long quoteItemId,
            @RequestBody Map<String, Object> updates) {
        
        logger.info("✏️ [API] UPDATE QUOTE - Quote Item ID: {}", quoteItemId);

        try {
            String result = supplierQuoteService.updateItemQuote(quoteItemId, updates);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            logger.info("  ✅ Quote updated");
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
            response.put("message", "Failed to update quote");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== DELETE QUOTE ====================

    /**
     * DELETE /api/supplier-quote/{quoteItemId}
     * Delete/withdraw a quote
     */
    @DeleteMapping("/{quoteItemId}")
    public ResponseEntity<?> deleteQuote(@PathVariable Long quoteItemId) {
        logger.info("🗑️ [API] DELETE QUOTE - Quote Item ID: {}", quoteItemId);

        try {
            String result = supplierQuoteService.deleteItemQuote(quoteItemId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            logger.info("  ✅ Quote deleted");
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
            response.put("message", "Failed to delete quote");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

