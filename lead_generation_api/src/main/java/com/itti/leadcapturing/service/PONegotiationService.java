
package com.itti.leadcapturing.service;

import com.itti.leadcapturing.dto.PONegotiationDTO;
import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PONegotiationService {

    private static final Logger logger = LoggerFactory.getLogger(PONegotiationService.class);

    @Autowired private PONegotiationRepository negotiationRepository;
    @Autowired private RFQRepository rfqRepository;
    @Autowired private SupplierRepository supplierRepository;
    @Autowired private SupplierFinalSelectionRepository selectionRepository;
    @Autowired private SupplierQuoteItemRepository quoteItemRepository;
    @Autowired private RFQItemRepository rfqItemRepository;
    @Autowired private PurchaseOrderService purchaseOrderService;

    // ===================== INIT =====================

    @Transactional
    public PONegotiationDTO.Response initNegotiation(Long rfqId, Long supplierId, Long userId) {
        logger.info("=".repeat(70));
        logger.info("📋 [INIT NEGOTIATION] RFQ: {}, Supplier: {}", rfqId, supplierId);

        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));

        SupplierFinalSelection selection = selectionRepository.findByRfqId(rfqId)
                .orElseThrow(() -> new RuntimeException(
                        "No supplier selection found. Please select supplier first."));

        if (!selection.getSupplier().getId().equals(supplierId)) {
            throw new RuntimeException(
                    "The selected supplier does not match the finalized selection for this RFQ.");
        }

        List<SupplierQuoteItem> quoteItems = quoteItemRepository.findByRfqIdAndSupplierId(rfqId, supplierId);

        if (quoteItems.isEmpty()) {
            throw new RuntimeException(
                    "No quote items found for supplier " + supplier.getCompanyName());
        }

        // Return existing draft if present
        var existing = negotiationRepository.findLatestByRfqAndSupplier(rfqId, supplierId);
        if (existing.isPresent()
                && existing.get().getStatus() != PONegotiation.NegotiationStatus.PO_CREATED) {
            logger.info("  ♻️ Returning existing draft negotiation ID: {}", existing.get().getId());
            return toResponse(negotiationRepository
                    .findByIdWithLineItems(existing.get().getId())
                    .orElse(existing.get()));
        }

        PONegotiation negotiation = new PONegotiation();
        negotiation.setRfq(rfq);
        negotiation.setSupplier(supplier);
        negotiation.setFinalSelection(selection);
        negotiation.setCreatedByUserId(userId);
        negotiation.setStatus(PONegotiation.NegotiationStatus.DRAFT);
        negotiation.setPaymentTerms(rfq.getPaymentTerms());
        negotiation.setDeliveryTerms(rfq.getDeliveryTerms());
        negotiation.setTaxPercentage(BigDecimal.ZERO);
        negotiation.setOverallDiscountAmount(BigDecimal.ZERO);
        negotiation.setOverallDiscountPercentage(BigDecimal.ZERO);

        int slNo = 1;
        for (SupplierQuoteItem qi : quoteItems) {
            PONegotiationLineItem li = new PONegotiationLineItem();
            li.setSlNo(slNo++);
            li.setRfqItemId(qi.getRfqItem().getId());
            li.setSupplierQuoteItemId(qi.getId());
            li.setItemCode(qi.getRfqItem().getItemCode());
            li.setItemDescription(qi.getRfqItem().getItemDescription());
            li.setSpecifications(qi.getRfqItem().getSpecifications());
            li.setQuantity(qi.getQuotedQuantity() != null
                    ? qi.getQuotedQuantity() : qi.getRfqItem().getQuantity());
            li.setUom(qi.getRfqItem().getUom());
            li.setActualQuotedPrice(qi.getUnitRate());
            li.setDiscountPercentage(BigDecimal.ZERO);
            li.setFinalizedPrice(qi.getUnitRate());
            li.setTaxPercentage(BigDecimal.ZERO);
            li.setDeliveryDays(qi.getDeliveryDays());
            li.setWarrantyMonths(qi.getWarrantyMonths());

            String brand = "";
            if (qi.getBrandOffered() != null) brand = qi.getBrandOffered();
            if (qi.getMakeModel() != null)
                brand += (brand.isEmpty() ? "" : " / ") + qi.getMakeModel();
            li.setBrandMakeModel(brand.isEmpty() ? null : brand);

            li.calculatePrices();
            negotiation.addLineItem(li);
        }

        recalculateTotals(negotiation);

        PONegotiation saved = negotiationRepository.save(negotiation);

        selection.setSelectionStatus(SelectionStatus.IN_NEGOTIATION);
        selection.setPoNegotiationId(saved.getId());
        selectionRepository.save(selection);

        logger.info("  ✅ Negotiation initialized with {} items. ID: {}", quoteItems.size(), saved.getId());
        logger.info("=".repeat(70));

        return toResponse(saved);
    }

    // ===================== SAVE =====================

    @Transactional
    public PONegotiationDTO.Response saveNegotiation(Long negotiationId,
                                                      PONegotiationDTO.SaveRequest request) {
        logger.info("💾 [SAVE NEGOTIATION] ID: {}", negotiationId);

        PONegotiation negotiation = negotiationRepository.findByIdWithLineItems(negotiationId)
                .orElseThrow(() -> new RuntimeException("Negotiation not found: " + negotiationId));

        if (negotiation.getStatus() == PONegotiation.NegotiationStatus.PO_CREATED) {
            throw new RuntimeException(
                    "Cannot edit: PO has already been created from this negotiation.");
        }

        for (PONegotiationDTO.LineItemRequest liReq : request.getLineItems()) {

            PONegotiationLineItem matchedItem = null;

            if (liReq.getId() != null) {
                matchedItem = negotiation.getLineItems().stream()
                        .filter(li -> li.getId() != null && li.getId().equals(liReq.getId()))
                        .findFirst().orElse(null);
            }
            if (matchedItem == null && liReq.getSlNo() != null) {
                matchedItem = negotiation.getLineItems().stream()
                        .filter(li -> li.getSlNo() != null && li.getSlNo().equals(liReq.getSlNo()))
                        .findFirst().orElse(null);
            }

            if (matchedItem == null) {
                logger.warn("  ⚠️ No matching line item for id={}, slNo={}",
                        liReq.getId(), liReq.getSlNo());
                continue;
            }

            final PONegotiationLineItem li = matchedItem;

            if (liReq.getTaxPercentage() != null) li.setTaxPercentage(liReq.getTaxPercentage());

            if (liReq.getFinalizedPrice() != null) {
                li.setFinalizedPriceManually(liReq.getFinalizedPrice());
            } else if (liReq.getDiscountPercentage() != null) {
                li.setDiscountPercentage(liReq.getDiscountPercentage());
                li.setFinalizedPrice(null);
                li.calculatePrices();
            } else {
                li.calculatePrices();
            }
        }

        if (request.getTerms() != null)         negotiation.setPaymentTerms(request.getTerms());
        if (request.getPaymentTerms() != null)  negotiation.setPaymentTerms(request.getPaymentTerms());
        if (request.getDeliveryTerms() != null) negotiation.setDeliveryTerms(request.getDeliveryTerms());
        if (request.getOtherTerms() != null)    negotiation.setOtherTerms(request.getOtherTerms());
        if (request.getBuyerRemarks() != null)  negotiation.setBuyerRemarks(request.getBuyerRemarks());

        if (request.getOverallDiscountAmount() != null) {
            negotiation.setOverallDiscountAmount(request.getOverallDiscountAmount());
        }
        if (request.getOverallDiscountPercentage() != null) {
            negotiation.setOverallDiscountPercentage(request.getOverallDiscountPercentage());
        }

        recalculateTotals(negotiation);

        if (request.getNetPayable() != null && request.getNetPayable().compareTo(BigDecimal.ZERO) > 0) {
            negotiation.setNetPayable(request.getNetPayable());
        } else {
            BigDecimal afterDiscount = negotiation.getSubtotalFinalized()
                    .subtract(negotiation.getOverallDiscountAmount());
            negotiation.setNetPayable(
                    afterDiscount.add(negotiation.getTaxAmount()).setScale(2, RoundingMode.HALF_UP));
        }

        logger.info("  ✅ netPayable: {}", negotiation.getNetPayable());

        PONegotiation saved = negotiationRepository.save(negotiation);
        return toResponse(saved);
    }

    // ===================== FINALIZE → CREATE PO =====================

    @Transactional
    public com.itti.leadcapturing.dto.POResponseDTO createPOFromNegotiation(Long negotiationId) {
        logger.info("=".repeat(70));
        logger.info("📄 [CREATE PO FROM NEGOTIATION] Negotiation ID: {}", negotiationId);

        PONegotiation negotiation = negotiationRepository.findByIdWithLineItems(negotiationId)
                .orElseThrow(() -> new RuntimeException("Negotiation not found: " + negotiationId));

        if (negotiation.getStatus() == PONegotiation.NegotiationStatus.PO_CREATED) {
            throw new RuntimeException("PO already created from this negotiation.");
        }

        negotiation.setStatus(PONegotiation.NegotiationStatus.FINALIZED);
        negotiationRepository.save(negotiation);

        com.itti.leadcapturing.dto.POResponseDTO poDto =
                purchaseOrderService.createPOFromNegotiation(negotiation);

        negotiation.setStatus(PONegotiation.NegotiationStatus.PO_CREATED);
        negotiation.setPurchaseOrderId(poDto.getId());
        negotiationRepository.save(negotiation);

        if (negotiation.getFinalSelection() != null) {
            SupplierFinalSelection sel = negotiation.getFinalSelection();
            sel.setSelectionStatus(SelectionStatus.PO_CREATED);
            sel.setPurchaseOrderId(poDto.getId());
            selectionRepository.save(sel);
        }

        logger.info("  ✅ PO created: {} | Net Payable: {}",
                poDto.getPoNumber(), poDto.getGrandTotal());
        logger.info("=".repeat(70));

        return poDto;
    }

    // ===================== GET =====================

    @Transactional(readOnly = true)
    public PONegotiationDTO.Response getNegotiation(Long negotiationId) {
        PONegotiation negotiation = negotiationRepository.findByIdWithLineItems(negotiationId)
                .orElseThrow(() -> new RuntimeException("Negotiation not found: " + negotiationId));
        return toResponse(negotiation);
    }

    @Transactional(readOnly = true)
    public List<PONegotiationDTO.Response> getNegotiationsByRfq(Long rfqId) {
        return negotiationRepository.findByRfqId(rfqId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ===================== HELPERS =====================

    private void recalculateTotals(PONegotiation negotiation) {
        BigDecimal subtotalQuoted    = BigDecimal.ZERO;
        BigDecimal subtotalFinalized = BigDecimal.ZERO;
        BigDecimal totalDiscount     = BigDecimal.ZERO;
        BigDecimal totalTaxAmount    = BigDecimal.ZERO;

        for (PONegotiationLineItem li : negotiation.getLineItems()) {
            li.calculatePrices();
            if (li.getActualQuotedPrice() != null && li.getQuantity() != null)
                subtotalQuoted = subtotalQuoted.add(li.getActualQuotedPrice().multiply(li.getQuantity()));
            if (li.getLineTotal() != null)
                subtotalFinalized = subtotalFinalized.add(li.getLineTotal());
            if (li.getDiscountAmount() != null && li.getQuantity() != null)
                totalDiscount = totalDiscount.add(li.getDiscountAmount().multiply(li.getQuantity()));
            if (li.getTaxAmount() != null)
                totalTaxAmount = totalTaxAmount.add(li.getTaxAmount());
        }

        negotiation.setSubtotalQuoted(subtotalQuoted.setScale(2, RoundingMode.HALF_UP));
        negotiation.setSubtotalFinalized(subtotalFinalized.setScale(2, RoundingMode.HALF_UP));
        negotiation.setTotalDiscountAmount(totalDiscount.setScale(2, RoundingMode.HALF_UP));
        negotiation.setTaxAmount(totalTaxAmount.setScale(2, RoundingMode.HALF_UP));
        negotiation.setGrandTotalQuoted(subtotalQuoted.setScale(2, RoundingMode.HALF_UP));
        negotiation.setGrandTotalFinalized(
                subtotalFinalized.add(totalTaxAmount).setScale(2, RoundingMode.HALF_UP));
    }

    // ===================== DTO MAPPER =====================

    private PONegotiationDTO.Response toResponse(PONegotiation n) {
        List<PONegotiationDTO.LineItemResponse> lineItems = new ArrayList<>();
        if (n.getLineItems() != null) {
            lineItems = n.getLineItems().stream().map(li ->
                    PONegotiationDTO.LineItemResponse.builder()
                            .id(li.getId())
                            .rfqItemId(li.getRfqItemId())
                            .supplierQuoteItemId(li.getSupplierQuoteItemId())
                            .slNo(li.getSlNo())
                            .itemCode(li.getItemCode())
                            .itemDescription(li.getItemDescription())
                            .specifications(li.getSpecifications())
                            .brandMakeModel(li.getBrandMakeModel())
                            .quantity(li.getQuantity())
                            .uom(li.getUom())
                            .actualQuotedPrice(li.getActualQuotedPrice())
                            .discountPercentage(li.getDiscountPercentage())
                            .discountAmount(li.getDiscountAmount())
                            .finalizedPrice(li.getFinalizedPrice())
                            .lineTotal(li.getLineTotal())
                            .taxPercentage(li.getTaxPercentage())
                            .taxAmount(li.getTaxAmount())
                            .deliveryDays(li.getDeliveryDays())
                            .warrantyMonths(li.getWarrantyMonths())
                            .build()
            ).collect(Collectors.toList());
        }

        var builder = PONegotiationDTO.Response.builder()
                .id(n.getId())
                .rfqId(n.getRfq().getId())
                .rfqNumber(n.getRfq().getRfqNumber())
                .supplierId(n.getSupplier().getId())
                .supplierName(n.getSupplier().getCompanyName())
                .supplierGstin(n.getSupplier().getGstNumber())
                .supplierContactName(n.getSupplier().getContactPersonName())
                .supplierContactEmail(n.getSupplier().getContactPersonEmail())
                .supplierContactPhone(n.getSupplier().getContactPersonPhone())
                .lineItems(lineItems)
                .subtotalQuoted(n.getSubtotalQuoted())
                .subtotalFinalized(n.getSubtotalFinalized())
                .totalDiscountAmount(n.getTotalDiscountAmount())
                .taxPercentage(n.getTaxPercentage())
                .taxAmount(n.getTaxAmount())
                .grandTotalQuoted(n.getGrandTotalQuoted())
                .grandTotalFinalized(n.getGrandTotalFinalized())
                .overallDiscountAmount(n.getOverallDiscountAmount())
                .overallDiscountPercentage(n.getOverallDiscountPercentage())
                .netPayable(n.getNetPayable())
                .paymentTerms(n.getPaymentTerms())
                .deliveryTerms(n.getDeliveryTerms())
                .otherTerms(n.getOtherTerms())
                .buyerRemarks(n.getBuyerRemarks())
                .status(n.getStatus().name())
                .purchaseOrderId(n.getPurchaseOrderId())
                .createdAt(n.getCreatedAt())
                .updatedAt(n.getUpdatedAt());

        // ✅ Set currency from the RFQ's buyer location (same source as PO creation)
        if (n.getRfq().getLocation() != null) {
            builder.currencyCode(n.getRfq().getLocation().getCurrencyCode());
            builder.currencySymbol(n.getRfq().getLocation().getCurrencySymbol());
            logger.debug("  💱 Negotiation currency: {} {}", 
                    n.getRfq().getLocation().getCurrencyCode(),
                    n.getRfq().getLocation().getCurrencySymbol());
        } else {
            builder.currencyCode("INR");
            builder.currencySymbol("₹");
        }

        if (n.getFinalSelection() != null) {
            builder.finalSelectionId(n.getFinalSelection().getId())
                   .isSystemRecommended(n.getFinalSelection().getIsSystemRecommended())
                   .justification(n.getFinalSelection().getJustification());
        }

        String addr = n.getSupplier().getAddressLine1() != null
                ? n.getSupplier().getAddressLine1() : "";
        if (n.getSupplier().getAddressLine2() != null)
            addr += ", " + n.getSupplier().getAddressLine2();
        if (n.getSupplier().getCity() != null)
            addr += ", " + n.getSupplier().getCity();
        builder.supplierAddress(addr);

        return builder.build();
    }
}