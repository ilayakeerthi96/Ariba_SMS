
package com.itti.leadcapturing.service;

import com.itti.leadcapturing.dto.QuoteComparisonResponse;
import com.itti.leadcapturing.dto.QuoteComparisonResponse.*;
import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuoteComparisonService {

    private static final Logger logger = LoggerFactory.getLogger(QuoteComparisonService.class);

    @Autowired private RFQRepository rfqRepository;
    @Autowired private RFQItemRepository rfqItemRepository;
    @Autowired private RFQSupplierRepository rfqSupplierRepository;
    @Autowired private SupplierQuoteItemRepository quoteItemRepository;
    @Autowired private SupplierRepository supplierRepository;

    // ===================== CURRENCY DETERMINATION =====================

    /**
     * ✅ Per-supplier currency resolution.
     *
     * Rule (mirrors PurchaseOrderService.applyCurrency):
     *   buyer location country == supplier HQ country  →  buyer's own currency
     *   different countries                            →  USD
     *
     * @param buyerLocation  buyer's RFQ delivery location
     * @param supplier       the supplier entity
     * @return String[2] { currencyCode, currencySymbol }
     */
    private String[] resolveSupplierCurrency(Location buyerLocation, Supplier supplier) {
        if (buyerLocation == null || supplier == null) {
            return new String[]{"INR", "₹"};
        }

        String buyerCountry    = buyerLocation.getCountry();
        String supplierCountry = supplier.getCountry();

        boolean sameCountry = buyerCountry != null
                && supplierCountry != null
                && buyerCountry.trim().equalsIgnoreCase(supplierCountry.trim());

        if (sameCountry) {
            String code   = buyerLocation.getCurrencyCode()   != null ? buyerLocation.getCurrencyCode()   : "INR";
            String symbol = buyerLocation.getCurrencySymbol() != null ? buyerLocation.getCurrencySymbol() : "₹";
            logger.info("    💱 [SAME COUNTRY] buyer='{}' supplier='{}' → {} {}",
                    buyerCountry, supplierCountry, code, symbol);
            return new String[]{code, symbol};
        } else {
            logger.info("    💱 [CROSS BORDER] buyer='{}' supplier='{}' → USD $",
                    buyerCountry, supplierCountry);
            return new String[]{"USD", "$"};
        }
    }

    // ===================== MAIN METHOD =====================

    @Transactional(readOnly = true)
    public QuoteComparisonResponse getQuoteComparison(Long rfqId) {
        logger.info("=".repeat(80));
        logger.info("📊 [QUOTE COMPARISON] RFQ ID: {}", rfqId);

        try {
            RFQ rfq = rfqRepository.findById(rfqId)
                    .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));
            logger.info("✓ RFQ loaded: {} - {}", rfq.getRfqNumber(), rfq.getRfqTitle());

            QuoteComparisonResponse response = new QuoteComparisonResponse();
            response.setRfqId(rfq.getId());
            response.setRfqNumber(rfq.getRfqNumber());
            response.setRfqTitle(rfq.getRfqTitle());
            response.setRfqDescription(rfq.getRfqDescription());
            response.setDueDate(rfq.getDueDate());

            try {
                response.setBuyerName(rfq.getBuyer() != null ? rfq.getBuyer().getCompanyName() : "N/A");
            } catch (Exception e) {
                response.setBuyerName("N/A");
            }

            // Page-level currency = buyer's location currency (used as header/default)
            if (rfq.getLocation() != null) {
                response.setCurrencyCode(rfq.getLocation().getCurrencyCode());
                response.setCurrencySymbol(rfq.getLocation().getCurrencySymbol());
            } else {
                response.setCurrencyCode("INR");
                response.setCurrencySymbol("₹");
            }

            List<RFQSupplier> rfqSuppliers = rfqSupplierRepository.findByRFQAndStatus(rfqId, "RESPONDED");
            logger.info("✓ Suppliers with quotes: {}", rfqSuppliers.size());

            // ✅ Pass the RFQ location so each supplier gets its own resolved currency
            List<SupplierInfo> suppliers = buildSupplierInfoList(rfqSuppliers, rfq.getLocation());
            response.setSuppliers(suppliers);

            List<RFQItem> rfqItems = rfqItemRepository.findByRfqId(rfqId);
            List<SupplierQuoteItem> allQuoteItems = quoteItemRepository.findByRfqId(rfqId);

            List<ItemComparison> itemComparisons = buildItemComparisons(rfqItems, allQuoteItems, rfqSuppliers);
            response.setItems(itemComparisons);

            QuoteSummary summary = buildQuoteSummary(rfqItems, rfqSuppliers, allQuoteItems, itemComparisons);
            response.setSummary(summary);

            logger.info("✅ Quote comparison built successfully");
            logger.info("=".repeat(80));
            return response;

        } catch (Exception e) {
            logger.error("❌ ERROR in getQuoteComparison", e);
            throw new RuntimeException("Failed to build quote comparison: " + e.getMessage(), e);
        }
    }

    // ===================== BUILD SUPPLIER INFO LIST =====================

    /**
     * ✅ UPDATED: Resolves per-supplier currency and adds it to SupplierInfo.
     *
     * Each supplier in the comparison now carries its own currencyCode/currencySymbol
     * so the frontend can show the correct currency per column:
     *   India supplier  → INR ₹
     *   Outside supplier → USD $
     */
    private List<SupplierInfo> buildSupplierInfoList(List<RFQSupplier> rfqSuppliers,
                                                      Location buyerLocation) {
        List<SupplierInfo> result = new ArrayList<>();

        for (RFQSupplier rs : rfqSuppliers) {
            try {
                if (rs.getSupplier() == null) continue;

                Supplier supplier = rs.getSupplier();

                // ✅ Resolve currency for this specific supplier
                String[] currency = resolveSupplierCurrency(buyerLocation, supplier);

                logger.info("  Supplier: {} | country={} | currency={}",
                        supplier.getCompanyName(), supplier.getCountry(), currency[0]);

                SupplierInfo info = SupplierInfo.builder()
                        .supplierId(supplier.getId())
                        .companyName(supplier.getCompanyName())
                        .contactEmail(supplier.getContactPersonEmail())
                        .contactPhone(supplier.getContactPersonPhone())
                        .quotedAt(rs.getRespondedAt())
                        .totalQuoteAmount(rs.getQuoteAmount())
                        .status(rs.getStatus())
                        .currencyCode(currency[0])    // ✅ e.g. "INR" or "USD"
                        .currencySymbol(currency[1])  // ✅ e.g. "₹" or "$"
                        .build();

                result.add(info);
            } catch (Exception e) {
                logger.error("    ✗ Error building supplier info for RFQSupplier {}: {}", rs.getId(), e.getMessage());
            }
        }
        return result;
    }

    // ===================== BUILD ITEM COMPARISONS =====================

    private List<ItemComparison> buildItemComparisons(
            List<RFQItem> rfqItems,
            List<SupplierQuoteItem> allQuoteItems,
            List<RFQSupplier> rfqSuppliers) {

        List<ItemComparison> comparisons = new ArrayList<>();
        int slNo = 1;

        for (RFQItem item : rfqItems) {
            try {
                ItemComparison comparison = new ItemComparison();
                comparison.setSlNo(slNo++);
                comparison.setRfqItemId(item.getId());
                comparison.setItemCode(item.getItemCode());
                comparison.setItemDescription(item.getItemDescription());
                comparison.setItemDescriptionDetailed(item.getItemDescriptionDetailed());
                comparison.setSpecifications(item.getSpecifications());
                comparison.setUom(item.getUom());
                comparison.setRequiredQuantity(item.getQuantity());

                List<SupplierQuoteItem> itemQuotes = allQuoteItems.stream()
                        .filter(qi -> qi.getRfqItem() != null && qi.getRfqItem().getId().equals(item.getId()))
                        .collect(Collectors.toList());

                Map<Long, SupplierQuote> supplierQuotesMap = new HashMap<>();
                for (SupplierQuoteItem quoteItem : itemQuotes) {
                    try {
                        if (quoteItem.getRfqSupplier() == null || quoteItem.getRfqSupplier().getSupplier() == null) continue;
                        Long supplierId = quoteItem.getRfqSupplier().getSupplier().getId();
                        supplierQuotesMap.put(supplierId, buildSupplierQuote(quoteItem));
                    } catch (Exception e) {
                        logger.error("      ✗ Error processing quote item {}: {}", quoteItem.getId(), e.getMessage());
                    }
                }
                comparison.setSupplierQuotes(supplierQuotesMap);

                if (!itemQuotes.isEmpty()) {
                    comparison.setLowestBid(findLowestBid(itemQuotes));
                    comparison.setHighestBid(findHighestBid(itemQuotes));
                    Optional<SupplierQuoteItem> selected = itemQuotes.stream()
                            .filter(qi -> qi.getIsSelected() != null && qi.getIsSelected())
                            .findFirst();
                    if (selected.isPresent()) {
                        comparison.setIsAwarded(true);
                        comparison.setAwardedToSupplierId(selected.get().getRfqSupplier().getSupplier().getId());
                        comparison.setAwardedToSupplierName(selected.get().getRfqSupplier().getSupplier().getCompanyName());
                    } else {
                        comparison.setIsAwarded(false);
                    }
                }
                comparisons.add(comparison);
            } catch (Exception e) {
                logger.error("    ✗ Error processing item {}: {}", item.getId(), e.getMessage());
            }
        }
        return comparisons;
    }

    private SupplierQuote buildSupplierQuote(SupplierQuoteItem quoteItem) {
        return SupplierQuote.builder()
                .quoteItemId(quoteItem.getId())
                .unitRate(quoteItem.getUnitRate())
                .quotedQuantity(quoteItem.getQuotedQuantity())
                .totalAmount(quoteItem.getTotalAmount())
                .taxPercentage(quoteItem.getTaxPercentage())
                .taxAmount(quoteItem.getTaxAmount())
                .grandTotal(quoteItem.getGrandTotal())
                .remarks(quoteItem.getRemarks())
                .deliveryDays(quoteItem.getDeliveryDays())
                .warrantyMonths(quoteItem.getWarrantyMonths())
                .brandOffered(quoteItem.getBrandOffered())
                .makeModel(quoteItem.getMakeModel())
                .countryOfOrigin(quoteItem.getCountryOfOrigin())
                .paymentTerms(quoteItem.getPaymentTerms())
                .isSelected(quoteItem.getIsSelected())
                .selectedDate(quoteItem.getSelectedDate())
                .build();
    }

    private LowestBidInfo findLowestBid(List<SupplierQuoteItem> itemQuotes) {
        return itemQuotes.stream()
                .filter(qi -> qi.getUnitRate() != null)
                .min(Comparator.comparing(SupplierQuoteItem::getUnitRate))
                .map(qi -> LowestBidInfo.builder()
                        .supplierId(qi.getRfqSupplier().getSupplier().getId())
                        .supplierName(qi.getRfqSupplier().getSupplier().getCompanyName())
                        .unitRate(qi.getUnitRate())
                        .totalAmount(qi.getTotalAmount())
                        .build())
                .orElse(null);
    }

    private HighestBidInfo findHighestBid(List<SupplierQuoteItem> itemQuotes) {
        return itemQuotes.stream()
                .filter(qi -> qi.getUnitRate() != null)
                .max(Comparator.comparing(SupplierQuoteItem::getUnitRate))
                .map(qi -> HighestBidInfo.builder()
                        .supplierId(qi.getRfqSupplier().getSupplier().getId())
                        .supplierName(qi.getRfqSupplier().getSupplier().getCompanyName())
                        .unitRate(qi.getUnitRate())
                        .totalAmount(qi.getTotalAmount())
                        .build())
                .orElse(null);
    }

    private QuoteSummary buildQuoteSummary(
            List<RFQItem> rfqItems,
            List<RFQSupplier> rfqSuppliers,
            List<SupplierQuoteItem> allQuoteItems,
            List<ItemComparison> itemComparisons) {

        QuoteSummary summary = new QuoteSummary();
        summary.setTotalItems(rfqItems.size());
        summary.setTotalSuppliers(rfqSuppliers.size());

        long itemsQuoted = itemComparisons.stream()
                .filter(ic -> ic.getSupplierQuotes() != null && !ic.getSupplierQuotes().isEmpty())
                .count();
        summary.setItemsQuoted((int) itemsQuoted);

        long itemsAwarded = itemComparisons.stream()
                .filter(ic -> Boolean.TRUE.equals(ic.getIsAwarded()))
                .count();
        summary.setItemsAwarded((int) itemsAwarded);
        summary.setItemsPending(rfqItems.size() - (int) itemsAwarded);

        Map<Long, SupplierTotal> supplierTotals = calculateSupplierTotals(rfqSuppliers, allQuoteItems);
        summary.setSupplierTotals(supplierTotals);

        if (!supplierTotals.isEmpty()) {
            Map.Entry<Long, SupplierTotal> lowestBidder = supplierTotals.entrySet().stream()
                    .min(Comparator.comparing(e -> e.getValue().getGrandTotal()))
                    .orElse(null);
            if (lowestBidder != null) {
                summary.setLowestBidderSupplierId(lowestBidder.getKey());
                summary.setLowestBidderName(lowestBidder.getValue().getSupplierName());
                summary.setLowestTotalAmount(lowestBidder.getValue().getGrandTotal());
            }
        }
        return summary;
    }

    private Map<Long, SupplierTotal> calculateSupplierTotals(
            List<RFQSupplier> rfqSuppliers,
            List<SupplierQuoteItem> allQuoteItems) {

        Map<Long, SupplierTotal> totals = new HashMap<>();
        for (RFQSupplier rfqSupplier : rfqSuppliers) {
            try {
                if (rfqSupplier.getSupplier() == null) continue;
                Long supplierId = rfqSupplier.getSupplier().getId();

                List<SupplierQuoteItem> supplierItems = allQuoteItems.stream()
                        .filter(qi -> qi.getRfqSupplier() != null &&
                                qi.getRfqSupplier().getSupplier() != null &&
                                qi.getRfqSupplier().getSupplier().getId().equals(supplierId))
                        .collect(Collectors.toList());

                if (!supplierItems.isEmpty()) {
                    BigDecimal subtotal = supplierItems.stream()
                            .map(SupplierQuoteItem::getTotalAmount).filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal taxTotal = supplierItems.stream()
                            .map(qi -> qi.getTaxAmount() != null ? qi.getTaxAmount() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal grandTotal = supplierItems.stream()
                            .map(SupplierQuoteItem::getGrandTotal).filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    long itemsSelected = supplierItems.stream()
                            .filter(qi -> qi.getIsSelected() != null && qi.getIsSelected()).count();

                    totals.put(supplierId, SupplierTotal.builder()
                            .supplierId(supplierId)
                            .supplierName(rfqSupplier.getSupplier().getCompanyName())
                            .subtotal(subtotal).taxTotal(taxTotal).grandTotal(grandTotal)
                            .itemsQuoted(supplierItems.size()).itemsSelected((int) itemsSelected)
                            .build());
                }
            } catch (Exception e) {
                logger.error("  ✗ Error calculating totals for RFQSupplier {}: {}", rfqSupplier.getId(), e.getMessage());
            }
        }
        return totals;
    }

    @Transactional
    public String selectQuotes(Long rfqId, List<Map<String, Object>> selections, String remarks) {
        logger.info("✅ [SELECT QUOTES] RFQ ID: {}", rfqId);
        int selectedCount = 0;
        for (Map<String, Object> selection : selections) {
            try {
                Long rfqItemId  = Long.valueOf(selection.get("rfqItemId").toString());
                Long supplierId = Long.valueOf(selection.get("supplierId").toString());
                String itemRemarks = selection.get("remarks") != null ? selection.get("remarks").toString() : remarks;
                Optional<SupplierQuoteItem> quoteItemOpt = quoteItemRepository.findByRfqItemAndSupplier(rfqItemId, supplierId);
                if (quoteItemOpt.isPresent()) {
                    quoteItemOpt.get().selectQuote(itemRemarks);
                    quoteItemRepository.save(quoteItemOpt.get());
                    selectedCount++;
                }
            } catch (Exception e) {
                logger.error("  ❌ Error selecting quote", e);
            }
        }
        return selectedCount + " quote(s) selected successfully";
    }

    @Transactional
    public String unselectQuote(Long quoteItemId) {
        SupplierQuoteItem quoteItem = quoteItemRepository.findById(quoteItemId)
                .orElseThrow(() -> new RuntimeException("Quote item not found: " + quoteItemId));
        quoteItem.unselectQuote();
        quoteItemRepository.save(quoteItem);
        return "Quote unselected successfully";
    }
}