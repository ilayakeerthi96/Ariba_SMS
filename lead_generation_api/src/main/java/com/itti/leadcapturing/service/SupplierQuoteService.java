
package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ✅ FIXED: Supplier Quote Service with proper transaction handling
 */
@Service
public class SupplierQuoteService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierQuoteService.class);

    @Autowired
    private RFQRepository rfqRepository;

    @Autowired
    private RFQSupplierRepository rfqSupplierRepository;

    @Autowired
    private RFQItemRepository rfqItemRepository;

    @Autowired
    private SupplierQuoteItemRepository quoteItemRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    // ==================== SUBMIT ITEM-LEVEL QUOTE ====================

    // @Transactional
    // public String submitItemQuote(Long supplierId, Long rfqId, List<Map<String, Object>> itemQuotes) {
    //     logger.info("=".repeat(80));
    //     logger.info("📝 [SUBMIT ITEM QUOTES] Supplier ID: {}, RFQ ID: {}", supplierId, rfqId);
    //     logger.info("  Items: {}", itemQuotes.size());

    //     RFQ rfq = rfqRepository.findById(rfqId)
    //             .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));

    //     if (!RFQStatus.PUBLISHED.equals(rfq.getStatus())) {
    //         throw new RuntimeException("RFQ is not published. Current status: " + rfq.getStatus());
    //     }

    //     RFQSupplier rfqSupplier = rfqSupplierRepository.findByRFQAndSupplier(rfqId, supplierId)
    //             .orElseThrow(() -> new RuntimeException("Supplier not authorized for this RFQ"));

    //     logger.info("  RFQ Supplier ID: {}", rfqSupplier.getId());

    //     int processedCount = 0;
    //     BigDecimal totalQuoteAmount = BigDecimal.ZERO;

    //     for (Map<String, Object> itemQuote : itemQuotes) {
    //         Long rfqItemId = Long.valueOf(itemQuote.get("rfqItemId").toString());
            
    //         RFQItem rfqItem = rfqItemRepository.findById(rfqItemId)
    //                 .orElseThrow(() -> new RuntimeException("RFQ Item not found: " + rfqItemId));

    //         SupplierQuoteItem quoteItem = quoteItemRepository
    //                 .findByRfqItemAndSupplier(rfqItemId, supplierId)
    //                 .orElse(new SupplierQuoteItem());

    //         quoteItem.setRfqSupplier(rfqSupplier);
    //         quoteItem.setRfqItem(rfqItem);

    //         BigDecimal unitRate = new BigDecimal(itemQuote.get("unitRate").toString());
    //         quoteItem.setUnitRate(unitRate);

    //         BigDecimal quotedQuantity = itemQuote.containsKey("quotedQuantity") ?
    //                 new BigDecimal(itemQuote.get("quotedQuantity").toString()) :
    //                 rfqItem.getQuantity();
    //         quoteItem.setQuotedQuantity(quotedQuantity);

    //         BigDecimal taxPercentage = itemQuote.containsKey("taxPercentage") ?
    //                 new BigDecimal(itemQuote.get("taxPercentage").toString()) :
    //                 (rfq.getTaxPercentage() != null ? BigDecimal.valueOf(rfq.getTaxPercentage()) : BigDecimal.ZERO);
    //         quoteItem.setTaxPercentage(taxPercentage);

    //         if (itemQuote.containsKey("remarks")) {
    //             quoteItem.setRemarks(itemQuote.get("remarks").toString());
    //         }
    //         if (itemQuote.containsKey("deliveryDays")) {
    //             quoteItem.setDeliveryDays(Integer.valueOf(itemQuote.get("deliveryDays").toString()));
    //         }
    //         if (itemQuote.containsKey("warrantyMonths")) {
    //             quoteItem.setWarrantyMonths(Integer.valueOf(itemQuote.get("warrantyMonths").toString()));
    //         }
    //         if (itemQuote.containsKey("brandOffered")) {
    //             quoteItem.setBrandOffered(itemQuote.get("brandOffered").toString());
    //         }
    //         if (itemQuote.containsKey("makeModel")) {
    //             quoteItem.setMakeModel(itemQuote.get("makeModel").toString());
    //         }
    //         if (itemQuote.containsKey("countryOfOrigin")) {
    //             quoteItem.setCountryOfOrigin(itemQuote.get("countryOfOrigin").toString());
    //         }
    //         if (itemQuote.containsKey("paymentTerms")) {
    //             quoteItem.setPaymentTerms(itemQuote.get("paymentTerms").toString());
    //         }

    //         quoteItem.calculateAmounts();
    //         quoteItemRepository.save(quoteItem);
            
    //         totalQuoteAmount = totalQuoteAmount.add(quoteItem.getGrandTotal());
    //         processedCount++;

    //         logger.info("  ✅ Item {} quoted: ₹{} x {} = ₹{}",
    //                    rfqItem.getItemDescription(),
    //                    unitRate,
    //                    quotedQuantity,
    //                    quoteItem.getGrandTotal());
    //     }

    //     rfqSupplier.setStatus("RESPONDED");
    //     rfqSupplier.setRespondedAt(LocalDateTime.now());
    //     rfqSupplier.setQuoteAmount(totalQuoteAmount);
    //     rfqSupplierRepository.save(rfqSupplier);

    //     logger.info("  ✅ QUOTE SUBMITTED SUCCESSFULLY");
    //     logger.info("     Items Quoted: {}", processedCount);
    //     logger.info("     Total Amount: ₹{}", totalQuoteAmount);
    //     logger.info("=".repeat(80));

    //     return String.format("Quote submitted successfully. %d item(s) quoted. Total: ₹%s",
    //                        processedCount, totalQuoteAmount);
    // }

    @Transactional
public String submitItemQuote(Long supplierId, Long rfqId, List<Map<String, Object>> itemQuotes) {
    logger.info("=".repeat(80));
    logger.info("📝 [SUBMIT ITEM QUOTES] Supplier ID: {}, RFQ ID: {}", supplierId, rfqId);
    logger.info("  Items: {}", itemQuotes.size());

    RFQ rfq = rfqRepository.findById(rfqId)
            .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));

    if (!RFQStatus.PUBLISHED.equals(rfq.getStatus())) {
        throw new RuntimeException("RFQ is not published. Current status: " + rfq.getStatus());
    }

    RFQSupplier rfqSupplier = rfqSupplierRepository.findByRFQAndSupplier(rfqId, supplierId)
            .orElseThrow(() -> new RuntimeException("Supplier not authorized for this RFQ"));

    // ── EXPIRY GUARD ───────────────────────────────────────────────────────
    // Block item-level quote submission if the due date has passed and the
    // supplier has not already submitted a quote. This is the backend security
    // gate — the frontend hides the button, but this stops API-level bypasses.
    if (rfq.getDueDate() != null
            && LocalDateTime.now().isAfter(rfq.getDueDate())
            && !"RESPONDED".equals(rfqSupplier.getStatus())) {
        throw new RuntimeException(
            "The submission deadline for RFQ " + rfq.getRfqNumber()
            + " passed on " + rfq.getDueDate()
            + ". No further quotes can be submitted."
        );
    }
    // ── END EXPIRY GUARD ───────────────────────────────────────────────────

    logger.info("  RFQ Supplier ID: {}", rfqSupplier.getId());

    int processedCount = 0;
    java.math.BigDecimal totalQuoteAmount = java.math.BigDecimal.ZERO;

    for (Map<String, Object> itemQuote : itemQuotes) {
        Long rfqItemId = Long.valueOf(itemQuote.get("rfqItemId").toString());

        RFQItem rfqItem = rfqItemRepository.findById(rfqItemId)
                .orElseThrow(() -> new RuntimeException("RFQ Item not found: " + rfqItemId));

        SupplierQuoteItem quoteItem = quoteItemRepository
                .findByRfqItemAndSupplier(rfqItemId, supplierId)
                .orElse(new SupplierQuoteItem());

        quoteItem.setRfqSupplier(rfqSupplier);
        quoteItem.setRfqItem(rfqItem);

        java.math.BigDecimal unitRate = new java.math.BigDecimal(itemQuote.get("unitRate").toString());
        quoteItem.setUnitRate(unitRate);

        java.math.BigDecimal quotedQuantity = itemQuote.containsKey("quotedQuantity")
                ? new java.math.BigDecimal(itemQuote.get("quotedQuantity").toString())
                : rfqItem.getQuantity();
        quoteItem.setQuotedQuantity(quotedQuantity);

        java.math.BigDecimal taxPercentage = itemQuote.containsKey("taxPercentage")
                ? new java.math.BigDecimal(itemQuote.get("taxPercentage").toString())
                : (rfq.getTaxPercentage() != null
                        ? java.math.BigDecimal.valueOf(rfq.getTaxPercentage())
                        : java.math.BigDecimal.ZERO);
        quoteItem.setTaxPercentage(taxPercentage);

        if (itemQuote.containsKey("remarks"))        quoteItem.setRemarks(itemQuote.get("remarks").toString());
        if (itemQuote.containsKey("deliveryDays"))   quoteItem.setDeliveryDays(Integer.valueOf(itemQuote.get("deliveryDays").toString()));
        if (itemQuote.containsKey("warrantyMonths")) quoteItem.setWarrantyMonths(Integer.valueOf(itemQuote.get("warrantyMonths").toString()));
        if (itemQuote.containsKey("brandOffered"))   quoteItem.setBrandOffered(itemQuote.get("brandOffered").toString());
        if (itemQuote.containsKey("makeModel"))      quoteItem.setMakeModel(itemQuote.get("makeModel").toString());
        if (itemQuote.containsKey("countryOfOrigin"))quoteItem.setCountryOfOrigin(itemQuote.get("countryOfOrigin").toString());
        if (itemQuote.containsKey("paymentTerms"))   quoteItem.setPaymentTerms(itemQuote.get("paymentTerms").toString());

        quoteItem.calculateAmounts();
        quoteItemRepository.save(quoteItem);

        totalQuoteAmount = totalQuoteAmount.add(quoteItem.getGrandTotal());
        processedCount++;

        logger.info("  Item {} quoted: unitRate={} qty={} total={}",
                rfqItem.getItemDescription(), unitRate, quotedQuantity, quoteItem.getGrandTotal());
    }

    rfqSupplier.setStatus("RESPONDED");
    rfqSupplier.setRespondedAt(LocalDateTime.now());
    rfqSupplier.setQuoteAmount(totalQuoteAmount);
    rfqSupplierRepository.save(rfqSupplier);

    logger.info("  QUOTE SUBMITTED: {} items, total={}", processedCount, totalQuoteAmount);
    logger.info("=".repeat(80));

    return String.format("Quote submitted successfully. %d item(s) quoted. Total: %s",
            processedCount, totalQuoteAmount);
}

    // ==================== ✅ CRITICAL FIX: GET SUPPLIER'S SUBMITTED QUOTES ====================

    /**
     * ✅ FIXED: Get all quote items submitted by supplier for an RFQ
     * This method is called when viewing submitted quotes
     */
    @Transactional(readOnly = true)
    public List<SupplierQuoteItem> getSupplierQuotes(Long supplierId, Long rfqId) {
        logger.info("📋 [GET SUPPLIER QUOTES] Supplier ID: {}, RFQ ID: {}", supplierId, rfqId);

        try {
            // ✅ First verify RFQ exists
            RFQ rfq = rfqRepository.findById(rfqId)
                    .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));
            
            logger.info("  ✓ RFQ found: {}", rfq.getRfqNumber());

            // ✅ Verify supplier exists
            Supplier supplier = supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));
            
            logger.info("  ✓ Supplier found: {}", supplier.getCompanyName());

            // ✅ Check if supplier is associated with this RFQ
            RFQSupplier rfqSupplier = rfqSupplierRepository.findByRFQAndSupplier(rfqId, supplierId)
                    .orElseThrow(() -> new RuntimeException("Supplier not associated with this RFQ"));
            
            logger.info("  ✓ RFQSupplier found. Status: {}", rfqSupplier.getStatus());

            // ✅ Fetch quotes using the new direct query
            List<SupplierQuoteItem> quotes = quoteItemRepository.findByRfqIdAndSupplierId(rfqId, supplierId);

            logger.info("  ✅ Found {} quote items", quotes.size());
            
            // ✅ Force initialization of lazy-loaded relationships
            quotes.forEach(quote -> {
                // Touch relationships to ensure they're loaded
                if (quote.getRfqItem() != null) {
                    quote.getRfqItem().getItemDescription(); // Force load
                }
                if (quote.getRfqSupplier() != null && quote.getRfqSupplier().getSupplier() != null) {
                    quote.getRfqSupplier().getSupplier().getCompanyName(); // Force load
                }
                
                logger.info("    - Item: {} | Rate: ₹{} | Qty: {} | Total: ₹{}", 
                    quote.getRfqItem().getItemDescription(),
                    quote.getUnitRate(),
                    quote.getQuotedQuantity(),
                    quote.getGrandTotal()
                );
            });
            
            return quotes;
            
        } catch (Exception e) {
            logger.error("❌ ERROR in getSupplierQuotes: {}", e.getMessage());
            logger.error("   Exception type: {}", e.getClass().getName());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch supplier quotes: " + e.getMessage(), e);
        }
    }

    // ==================== UPDATE SINGLE ITEM QUOTE ====================

    @Transactional
    public String updateItemQuote(Long quoteItemId, Map<String, Object> updates) {
        logger.info("✏️ [UPDATE ITEM QUOTE] Quote Item ID: {}", quoteItemId);

        SupplierQuoteItem quoteItem = quoteItemRepository.findById(quoteItemId)
                .orElseThrow(() -> new RuntimeException("Quote item not found: " + quoteItemId));

        if (updates.containsKey("unitRate")) {
            quoteItem.setUnitRate(new BigDecimal(updates.get("unitRate").toString()));
        }
        if (updates.containsKey("quotedQuantity")) {
            quoteItem.setQuotedQuantity(new BigDecimal(updates.get("quotedQuantity").toString()));
        }
        if (updates.containsKey("taxPercentage")) {
            quoteItem.setTaxPercentage(new BigDecimal(updates.get("taxPercentage").toString()));
        }
        if (updates.containsKey("remarks")) {
            quoteItem.setRemarks(updates.get("remarks").toString());
        }
        if (updates.containsKey("deliveryDays")) {
            quoteItem.setDeliveryDays(Integer.valueOf(updates.get("deliveryDays").toString()));
        }
        if (updates.containsKey("warrantyMonths")) {
            quoteItem.setWarrantyMonths(Integer.valueOf(updates.get("warrantyMonths").toString()));
        }

        quoteItemRepository.save(quoteItem);

        logger.info("  ✅ Quote updated");
        return "Quote item updated successfully";
    }

    // ==================== DELETE ITEM QUOTE ====================

    @Transactional
    public String deleteItemQuote(Long quoteItemId) {
        logger.info("🗑️ [DELETE ITEM QUOTE] Quote Item ID: {}", quoteItemId);

        SupplierQuoteItem quoteItem = quoteItemRepository.findById(quoteItemId)
                .orElseThrow(() -> new RuntimeException("Quote item not found: " + quoteItemId));

        if (Boolean.TRUE.equals(quoteItem.getIsSelected())) {
            throw new RuntimeException("Cannot delete - quote has been selected by buyer");
        }

        quoteItemRepository.delete(quoteItem);

        logger.info("  ✅ Quote deleted");
        return "Quote item deleted successfully";
    }
}