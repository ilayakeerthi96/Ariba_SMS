
package com.itti.leadcapturing.service;

import com.itti.leadcapturing.dto.POResponseDTO;
import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderService.class);

    @Autowired private PurchaseOrderRepository poRepository;
    @Autowired private POLineItemRepository lineItemRepository;
    @Autowired private RFQRepository rfqRepository;
    @Autowired private SupplierRepository supplierRepository;
    @Autowired private BuyerRepository buyerRepository;
    @Autowired private LocationRepository locationRepository;
    @Autowired private SupplierQuoteItemRepository quoteItemRepository;
    @Autowired private RFQItemRepository rfqItemRepository;

    // ===================== CONVERT TO DTO =====================

    public POResponseDTO convertToDTO(PurchaseOrder po) {
        POResponseDTO dto = new POResponseDTO();

        dto.setId(po.getId());
        dto.setPoNumber(po.getPoNumber());
        dto.setPoDate(po.getPoDate());
        dto.setReferenceQuoteNo(po.getReferenceQuoteNo());
        dto.setCreatedByUserId(po.getCreatedByUserId());

        if (po.getBuyer() != null) {
            dto.setBuyerId(po.getBuyer().getId());
            dto.setBuyerName(po.getBuyer().getCompanyName());
            dto.setBuyerEmail(po.getBuyer().getContactPersonEmail());
            dto.setBuyerGstin(po.getBuyer().getGstNumber());
            dto.setBuyerPan(po.getBuyer().getPanNumber());
            dto.setBuyerContactName(po.getBuyer().getContactPersonName());
            dto.setBuyerContactPhone(po.getBuyer().getContactPersonPhone());
        }
        if (po.getBuyerLocation() != null) {
            dto.setBuyerLocationId(po.getBuyerLocation().getId());
            dto.setBuyerLocationName(po.getBuyerLocation().getLocationName());
            String addr = po.getBuyerLocation().getAddressLine1() != null ? po.getBuyerLocation().getAddressLine1() : "";
            if (po.getBuyerLocation().getCity() != null) addr += ", " + po.getBuyerLocation().getCity();
            if (po.getBuyerLocation().getState() != null) addr += ", " + po.getBuyerLocation().getState();
            dto.setBuyerLocationAddress(addr);

            // ✅ NEW: Copy currency from buyer's location
            dto.setCurrencyCode(po.getBuyerLocation().getCurrencyCode());
            dto.setCurrencySymbol(po.getBuyerLocation().getCurrencySymbol());
        } else {
            // Fallback: use currency stored directly on PO
            dto.setCurrencyCode(po.getCurrencyCode());
            dto.setCurrencySymbol(po.getCurrencySymbol());
        }

        if (po.getDeliveryLocation() != null) {
            dto.setDeliveryLocationId(po.getDeliveryLocation().getId());
            dto.setDeliveryLocationName(po.getDeliveryLocation().getLocationName());
            String dAddr = po.getDeliveryLocation().getAddressLine1() != null ? po.getDeliveryLocation().getAddressLine1() : "";
            if (po.getDeliveryLocation().getCity() != null) dAddr += ", " + po.getDeliveryLocation().getCity();
            if (po.getDeliveryLocation().getState() != null) dAddr += ", " + po.getDeliveryLocation().getState();
            dto.setDeliveryLocationAddress(dAddr);
        }
        if (po.getSupplier() != null) {
            dto.setSupplierId(po.getSupplier().getId());
            dto.setSupplierName(po.getSupplier().getCompanyName());
            dto.setSupplierGstin(po.getSupplier().getGstNumber());
            dto.setSupplierContactName(po.getSupplier().getContactPersonName());
            dto.setSupplierContactEmail(po.getSupplier().getContactPersonEmail());
            dto.setSupplierContactPhone(po.getSupplier().getContactPersonPhone());
            dto.setSupplierCity(po.getSupplier().getCity());
            dto.setSupplierState(po.getSupplier().getState());
            String addr = po.getSupplier().getAddressLine1() != null ? po.getSupplier().getAddressLine1() : "";
            if (po.getSupplier().getAddressLine2() != null) addr += ", " + po.getSupplier().getAddressLine2();
            dto.setSupplierAddress(addr);
        }
        if (po.getRfq() != null) {
            dto.setRfqId(po.getRfq().getId());
            dto.setRfqNumber(po.getRfq().getRfqNumber());
            dto.setRfqTitle(po.getRfq().getRfqTitle());
        }

        BigDecimal totalTaxAmount = BigDecimal.ZERO;
        if (po.getLineItems() != null) {
            List<POResponseDTO.POLineItemDTO> itemDTOs = new ArrayList<>();
            for (POLineItem li : po.getLineItems()) {
                POResponseDTO.POLineItemDTO liDto = new POResponseDTO.POLineItemDTO();
                liDto.setId(li.getId());
                liDto.setSlNo(li.getSlNo());
                liDto.setItemCode(li.getItemCode());
                liDto.setItemDescription(li.getItemDescription());
                liDto.setSpecifications(li.getSpecifications());
                liDto.setBrandMakeModel(li.getBrandMakeModel());
                liDto.setQuantity(li.getQuantity());
                liDto.setUom(li.getUom());
                liDto.setUnitRate(li.getUnitRate());
                liDto.setLineTotal(li.getLineTotal());
                liDto.setDeliveryDays(li.getDeliveryDays());
                liDto.setWarrantyMonths(li.getWarrantyMonths());
                liDto.setOriginalQuotedPrice(li.getOriginalQuotedPrice());
                liDto.setDiscountPercentage(li.getDiscountPercentage() != null ? li.getDiscountPercentage() : BigDecimal.ZERO);
                liDto.setTaxPercentage(li.getTaxPercentage() != null ? li.getTaxPercentage() : BigDecimal.ZERO);
                liDto.setTaxAmount(li.getTaxAmount() != null ? li.getTaxAmount() : BigDecimal.ZERO);
                BigDecimal ltWithTax = (li.getLineTotal() != null ? li.getLineTotal() : BigDecimal.ZERO)
                        .add(li.getTaxAmount() != null ? li.getTaxAmount() : BigDecimal.ZERO);
                liDto.setLineTotalWithTax(ltWithTax.setScale(2, RoundingMode.HALF_UP));
                if (li.getTaxAmount() != null) {
                    totalTaxAmount = totalTaxAmount.add(li.getTaxAmount());
                }
                itemDTOs.add(liDto);
            }
            dto.setLineItems(itemDTOs);
        } else {
            dto.setLineItems(new ArrayList<>());
        }

        dto.setSubtotal(po.getSubtotal());
        dto.setTaxPercentage(po.getTaxPercentage());
        dto.setTaxAmount(po.getTaxAmount());
        dto.setTotalTaxAmount(totalTaxAmount.setScale(2, RoundingMode.HALF_UP));
        dto.setGrandTotal(po.getGrandTotal());
        dto.setAmountInWords(po.getAmountInWords());
        dto.setPaymentTerms(po.getPaymentTerms());
        dto.setDeliveryTerms(po.getDeliveryTerms());
        dto.setOtherTerms(po.getOtherTerms());
        dto.setDispatchedThrough(po.getDispatchedThrough());
        dto.setModeOfPayment(po.getModeOfPayment());
        dto.setDestination(po.getDestination());
        dto.setBuyerRemarks(po.getBuyerRemarks());
        dto.setInternalNotes(po.getInternalNotes());
        dto.setStatus(po.getStatus());
        dto.setApprovalRequired(po.getApprovalRequired());
        dto.setApprovalStatus(po.getApprovalStatus());
        dto.setApprovedBy(po.getApprovedBy());
        dto.setApprovalDate(po.getApprovalDate());
        dto.setApprovedByName(po.getApprovedByName());
        dto.setApprovedByDesignation(po.getApprovedByDesignation());
        dto.setCreatedAt(po.getCreatedAt());
        dto.setUpdatedAt(po.getUpdatedAt());

        return dto;
    }

    // ===================== CREATE PO (from quote items — original flow) =====================

    @Transactional
    public POResponseDTO createPurchaseOrder(
            Long rfqId, Long supplierId, String buyerRemarks,
            List<Long> selectedQuoteItemIds, Long userId) {

        logger.info("=".repeat(60));
        logger.info("📝 [CREATE PO] RFQ: {}, Supplier: {}, Items: {}", rfqId, supplierId, selectedQuoteItemIds.size());

        try {
            RFQ rfq = rfqRepository.findById(rfqId)
                    .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));
            Supplier supplier = supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));
            Buyer buyer = rfq.getBuyer();
            if (buyer == null) throw new RuntimeException("RFQ has no buyer");
            Location buyerLocation = rfq.getLocation();
            if (buyerLocation == null) throw new RuntimeException("RFQ has no location");

            PurchaseOrder po = new PurchaseOrder();
            po.setPoNumber(generatePONumber(buyer));
            po.setPoDate(LocalDateTime.now());
            po.setReferenceQuoteNo(rfq.getRfqNumber());
            po.setBuyer(buyer);
            po.setBuyerLocation(buyerLocation);
            po.setDeliveryLocation(buyerLocation);
            po.setSupplier(supplier);
            po.setRfq(rfq);
            po.setCreatedByUserId(userId);
            po.setBuyerRemarks(buyerRemarks);
            po.setStatus(POStatus.DRAFT);
            po.setPaymentTerms(rfq.getPaymentTerms());
            po.setDeliveryTerms(rfq.getDeliveryTerms());
            po.setTaxPercentage(rfq.getTaxPercentage() != null ?
                    BigDecimal.valueOf(rfq.getTaxPercentage()) : BigDecimal.ZERO);

            // ✅ NEW: Copy currency from buyer's location
            po.setCurrencyCode(buyerLocation.getCurrencyCode());
            po.setCurrencySymbol(buyerLocation.getCurrencySymbol());

            int slNo = 1;
            for (Long quoteItemId : selectedQuoteItemIds) {
                SupplierQuoteItem quoteItem = quoteItemRepository.findById(quoteItemId)
                        .orElseThrow(() -> new RuntimeException("Quote item not found: " + quoteItemId));
                POLineItem lineItem = buildLineItemFromQuote(quoteItem, slNo++);
                po.addLineItem(lineItem);
            }

            po.calculateTotals();
            po.setAmountInWords(convertAmountToWords(po.getGrandTotal()));

            PurchaseOrder saved = poRepository.save(po);
            PurchaseOrder withDetails = poRepository.findByIdWithDetails(saved.getId())
                    .orElseThrow(() -> new RuntimeException("Could not reload PO after save"));

            logger.info("  ✅ PO Created: {} | Total: {}{}", saved.getPoNumber(),
                    buyerLocation.getCurrencySymbol(), saved.getGrandTotal());
            return convertToDTO(withDetails);

        } catch (Exception e) {
            logger.error("❌ ERROR creating PO: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create PO: " + e.getMessage(), e);
        }
    }

    // ===================== ✅ CREATE PO FROM NEGOTIATION =====================

    @Transactional
    public POResponseDTO createPOFromNegotiation(PONegotiation negotiation) {
        logger.info("=".repeat(60));
        logger.info("📝 [CREATE PO FROM NEGOTIATION] Negotiation ID: {}", negotiation.getId());

        try {
            RFQ rfq = negotiation.getRfq();
            Supplier supplier = negotiation.getSupplier();
            Buyer buyer = rfq.getBuyer();
            if (buyer == null) throw new RuntimeException("RFQ has no buyer");
            Location buyerLocation = rfq.getLocation();
            if (buyerLocation == null) throw new RuntimeException("RFQ has no location");

            PurchaseOrder po = new PurchaseOrder();
            po.setPoNumber(generatePONumber(buyer));
            po.setPoDate(LocalDateTime.now());
            po.setReferenceQuoteNo(rfq.getRfqNumber());
            po.setBuyer(buyer);
            po.setBuyerLocation(buyerLocation);
            po.setDeliveryLocation(buyerLocation);
            po.setSupplier(supplier);
            po.setRfq(rfq);
            po.setCreatedByUserId(negotiation.getCreatedByUserId());
            po.setStatus(POStatus.DRAFT);
            po.setPaymentTerms(negotiation.getPaymentTerms());
            po.setDeliveryTerms(negotiation.getDeliveryTerms());
            po.setOtherTerms(negotiation.getOtherTerms());
            po.setBuyerRemarks(negotiation.getBuyerRemarks());
            po.setTaxPercentage(BigDecimal.ZERO);

            // ✅ NEW: Copy currency from buyer's location
            po.setCurrencyCode(buyerLocation.getCurrencyCode());
            po.setCurrencySymbol(buyerLocation.getCurrencySymbol());

            if (negotiation.getFinalSelection() != null &&
                !Boolean.TRUE.equals(negotiation.getFinalSelection().getIsSystemRecommended())) {
                po.setInternalNotes("Supplier selected over system recommendation. Justification: " +
                        negotiation.getFinalSelection().getJustification());
            }

            for (PONegotiationLineItem nli : negotiation.getLineItems()) {
                POLineItem lineItem = new POLineItem();
                lineItem.setSlNo(nli.getSlNo());
                lineItem.setItemCode(nli.getItemCode());
                lineItem.setItemDescription(nli.getItemDescription());
                lineItem.setSpecifications(nli.getSpecifications());
                lineItem.setBrandMakeModel(nli.getBrandMakeModel());
                lineItem.setQuantity(nli.getQuantity());
                lineItem.setUom(nli.getUom());
                lineItem.setUnitRate(nli.getFinalizedPrice());
                lineItem.setOriginalQuotedPrice(nli.getActualQuotedPrice());
                lineItem.setDiscountPercentage(
                    nli.getDiscountPercentage() != null ? nli.getDiscountPercentage() : BigDecimal.ZERO);
                lineItem.setTaxPercentage(
                    nli.getTaxPercentage() != null ? nli.getTaxPercentage() : BigDecimal.ZERO);
                lineItem.calculateLineTotal();
                lineItem.setDeliveryDays(nli.getDeliveryDays());
                lineItem.setWarrantyMonths(nli.getWarrantyMonths());

                if (nli.getRfqItemId() != null) {
                    rfqItemRepository.findById(nli.getRfqItemId()).ifPresent(lineItem::setRfqItem);
                }
                if (nli.getSupplierQuoteItemId() != null) {
                    quoteItemRepository.findById(nli.getSupplierQuoteItemId())
                            .ifPresent(lineItem::setSupplierQuoteItem);
                }
                po.addLineItem(lineItem);

                logger.info("  ✓ Item {}: {} @ {}{} (orig {}{}, disc {}%, tax {}%)",
                        nli.getSlNo(), nli.getItemDescription(),
                        buyerLocation.getCurrencySymbol(), nli.getFinalizedPrice(),
                        buyerLocation.getCurrencySymbol(), nli.getActualQuotedPrice(),
                        nli.getDiscountPercentage(), nli.getTaxPercentage());
            }

            calculateTotalsWithPerLineTax(po);

            BigDecimal overallDiscount = (negotiation.getOverallDiscountAmount() != null)
                    ? negotiation.getOverallDiscountAmount() : BigDecimal.ZERO;

            if (overallDiscount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal adjustedSubtotal = po.getSubtotal().subtract(overallDiscount);
                BigDecimal netPayable = adjustedSubtotal.add(po.getTaxAmount())
                        .setScale(2, RoundingMode.HALF_UP);
                po.setGrandTotal(netPayable);
                logger.info("  ✅ Overall discount {} applied. Net Payable: {}{}",
                        overallDiscount, buyerLocation.getCurrencySymbol(), netPayable);
            }

            if (negotiation.getNetPayable() != null
                    && negotiation.getNetPayable().compareTo(BigDecimal.ZERO) > 0
                    && negotiation.getNetPayable().compareTo(po.getGrandTotal()) != 0) {
                logger.info("  ℹ️ Using negotiation netPayable {}{} (calculated: {}{})",
                        buyerLocation.getCurrencySymbol(), negotiation.getNetPayable(),
                        buyerLocation.getCurrencySymbol(), po.getGrandTotal());
                po.setGrandTotal(negotiation.getNetPayable());
            }

            po.setAmountInWords(convertAmountToWords(po.getGrandTotal()));

            PurchaseOrder saved = poRepository.save(po);
            PurchaseOrder withDetails = poRepository.findByIdWithDetails(saved.getId())
                    .orElseThrow(() -> new RuntimeException("Could not reload PO after save"));

            logger.info("  ✅ PO Created: {} | Currency: {} | Subtotal: {} | Tax: {} | Grand Total: {}",
                    saved.getPoNumber(), buyerLocation.getCurrencyCode(),
                    saved.getSubtotal(), saved.getTaxAmount(), saved.getGrandTotal());
            logger.info("=".repeat(60));

            return convertToDTO(withDetails);

        } catch (Exception e) {
            logger.error("❌ ERROR creating PO from negotiation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create PO from negotiation: " + e.getMessage(), e);
        }
    }

    private void calculateTotalsWithPerLineTax(PurchaseOrder po) {
        BigDecimal subtotal  = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;
        for (POLineItem li : po.getLineItems()) {
            if (li.getLineTotal() != null)  subtotal  = subtotal.add(li.getLineTotal());
            if (li.getTaxAmount() != null)  taxAmount = taxAmount.add(li.getTaxAmount());
        }
        po.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        po.setTaxAmount(taxAmount.setScale(2, RoundingMode.HALF_UP));
        po.setGrandTotal(subtotal.add(taxAmount).setScale(2, RoundingMode.HALF_UP));
    }

    // ===================== UPDATE PO =====================

    @Transactional
    public POResponseDTO updatePurchaseOrder(Long poId, Map<String, Object> updates) {
        logger.info("✏️ [UPDATE PO] ID: {}", poId);
        PurchaseOrder po = poRepository.findByIdWithDetails(poId)
                .orElseThrow(() -> new RuntimeException("PO not found: " + poId));
        if (!POStatus.DRAFT.equals(po.getStatus())) {
            throw new RuntimeException("Only DRAFT POs can be edited. Current status: " + po.getStatus());
        }
        if (updates.containsKey("buyerRemarks"))    po.setBuyerRemarks((String) updates.get("buyerRemarks"));
        if (updates.containsKey("paymentTerms"))    po.setPaymentTerms((String) updates.get("paymentTerms"));
        if (updates.containsKey("deliveryTerms"))   po.setDeliveryTerms((String) updates.get("deliveryTerms"));
        if (updates.containsKey("otherTerms"))      po.setOtherTerms((String) updates.get("otherTerms"));
        if (updates.containsKey("internalNotes"))   po.setInternalNotes((String) updates.get("internalNotes"));
        if (updates.containsKey("dispatchedThrough")) po.setDispatchedThrough((String) updates.get("dispatchedThrough"));
        if (updates.containsKey("modeOfPayment"))   po.setModeOfPayment((String) updates.get("modeOfPayment"));
        if (updates.containsKey("destination"))     po.setDestination((String) updates.get("destination"));
        PurchaseOrder updated = poRepository.save(po);
        logger.info("  ✅ PO updated");
        return convertToDTO(updated);
    }

    // ===================== DELETE PO =====================

    @Transactional
    public void deletePurchaseOrder(Long poId) {
        logger.info("🗑️ [DELETE PO] ID: {}", poId);
        PurchaseOrder po = poRepository.findById(poId)
                .orElseThrow(() -> new RuntimeException("PO not found: " + poId));
        if (!POStatus.DRAFT.equals(po.getStatus())) {
            throw new RuntimeException("Only DRAFT POs can be deleted. Current status: " + po.getStatus());
        }
        po.setIsDeleted(true);
        po.setDeletedAt(LocalDateTime.now());
        poRepository.save(po);
        logger.info("  ✅ PO soft-deleted");
    }

    // ===================== GET METHODS =====================

    @Transactional(readOnly = true)
    public POResponseDTO getPOById(Long poId) {
        PurchaseOrder po = poRepository.findByIdWithDetails(poId)
                .orElseThrow(() -> new RuntimeException("PO not found: " + poId));
        return convertToDTO(po);
    }

    @Transactional(readOnly = true)
    public List<POResponseDTO> getPOsByBuyer(Long buyerId) {
        return poRepository.findByBuyerId(buyerId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<POResponseDTO> getPOsByRFQ(Long rfqId) {
        return poRepository.findByRfqId(rfqId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<POResponseDTO> getAllPOs() {
        return poRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<POResponseDTO> getPOsByCompanyName(String companyName) {
        return poRepository.findByCompanyName(companyName).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<POResponseDTO> getPOsBySupplier(Long supplierId) {
        return poRepository.findBySupplierIdAndApprovedStatus(supplierId).stream()
                .map(this::convertToDTO).collect(Collectors.toList());
    }

    // ===================== HELPER METHODS =====================

    private POLineItem buildLineItemFromQuote(SupplierQuoteItem quoteItem, int slNo) {
        POLineItem lineItem = new POLineItem();
        lineItem.setSlNo(slNo);
        lineItem.setSupplierQuoteItem(quoteItem);
        RFQItem rfqItem = quoteItem.getRfqItem();
        lineItem.setRfqItem(rfqItem);
        lineItem.setItemCode(rfqItem.getItemCode());
        lineItem.setItemDescription(rfqItem.getItemDescription());
        lineItem.setSpecifications(rfqItem.getSpecifications());
        String brandInfo = "";
        if (quoteItem.getBrandOffered() != null) brandInfo = quoteItem.getBrandOffered();
        if (quoteItem.getMakeModel() != null)
            brandInfo += (brandInfo.isEmpty() ? "" : " / ") + quoteItem.getMakeModel();
        lineItem.setBrandMakeModel(brandInfo.isEmpty() ? null : brandInfo);
        lineItem.setQuantity(quoteItem.getQuotedQuantity());
        lineItem.setUom(rfqItem.getUom());
        lineItem.setUnitRate(quoteItem.getUnitRate());
        lineItem.setOriginalQuotedPrice(quoteItem.getUnitRate());
        lineItem.setDiscountPercentage(BigDecimal.ZERO);
        lineItem.setTaxPercentage(BigDecimal.ZERO);
        lineItem.calculateLineTotal();
        lineItem.setDeliveryDays(quoteItem.getDeliveryDays());
        lineItem.setWarrantyMonths(quoteItem.getWarrantyMonths());
        return lineItem;
    }

    private String generatePONumber(Buyer buyer) {
        String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy"));
        String companyCode = buyer.getCompanyName()
                .substring(0, Math.min(4, buyer.getCompanyName().length()))
                .toUpperCase().replaceAll("\\s+", "");
        Long count = poRepository.countByBuyerId(buyer.getId());
        String sequence = String.format("%03d", (count + 1));
        return companyCode + "/PO/" + year + "/" + sequence;
    }

    public String convertAmountToWords(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) return "Zero Only";
        long units = amount.longValue();
        int cents = amount.remainder(BigDecimal.ONE).multiply(new BigDecimal(100)).intValue();
        String words = inWords(units);
        if (cents > 0) words += " and " + inWords(cents) + " Cents";
        return words + " Only";
    }

    private static final String[] ones = {
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen"
    };
    private static final String[] tens = {
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    private String inWords(long n) {
        if (n == 0) return "Zero";
        if (n < 0) return "Negative " + inWords(-n);
        StringBuilder result = new StringBuilder();
        if (n >= 10000000) { result.append(inWords(n / 10000000)).append(" Crore "); n %= 10000000; }
        if (n >= 100000)   { result.append(inWords(n / 100000)).append(" Lakh "); n %= 100000; }
        if (n >= 1000)     { result.append(inWords(n / 1000)).append(" Thousand "); n %= 1000; }
        if (n >= 100)      { result.append(ones[(int)(n / 100)]).append(" Hundred "); n %= 100; }
        if (n >= 20) {
            result.append(tens[(int)(n / 10)]);
            n %= 10;
            if (n > 0) result.append(" ").append(ones[(int)n]);
        } else if (n > 0) {
            result.append(ones[(int)n]);
        }
        return result.toString().trim();
    }
}