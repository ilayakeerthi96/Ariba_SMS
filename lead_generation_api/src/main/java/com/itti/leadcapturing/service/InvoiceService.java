

package com.itti.leadcapturing.service;
import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private final InvoiceRepository         invoiceRepository;
    private final PurchaseOrderRepository   purchaseOrderRepository;
    private final ThreeWayMatchRepository   threeWayMatchRepository;
    private final EmailService              emailService;

    // =========================================================================
    // SUPPLIER ACTIONS
    // =========================================================================

    @Transactional
    public Map<String, Object> createInvoice(Long supplierId, Long poId,
                                             Map<String, Object> invoiceData) {
        PurchaseOrder po = purchaseOrderRepository.findByIdWithDetails(poId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + poId));

        if (invoiceRepository.existsActiveInvoiceForPo(poId)) {
            throw new RuntimeException(
                    "An active invoice already exists for PO: " + po.getPoNumber());
        }

        Invoice invoice = new Invoice();
        invoice.setSupplierId(supplierId);
        invoice.setPoId(poId);
        invoice.setPoNumber(po.getPoNumber());
        invoice.setBuyerCompanyName(
                po.getBuyer() != null ? po.getBuyer().getCompanyName() : null);
        invoice.setStatus(Invoice.InvoiceStatus.DRAFT);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setInvoiceDate(LocalDateTime.now());

        // ✅ Copy currency from PO so invoice carries the buyer's location currency
        invoice.setCurrency(po.getCurrencyCode() != null ? po.getCurrencyCode() : "INR");

        if (po.getRfq() != null && po.getRfq().getCreatedByUser() != null) {
            User rfqCreator = po.getRfq().getCreatedByUser();
            invoice.setRfqId(po.getRfq().getId());
            invoice.setRfqNumber(po.getRfq().getRfqNumber());
            invoice.setRfqCreatorUserId(rfqCreator.getId());
            invoice.setRfqCreatorEmail(rfqCreator.getEmail());
            invoice.setRfqCreatorName(rfqCreator.getFirstName() + " " + rfqCreator.getLastName());
        }

        ensureOverallDiscountFromPO(po, invoiceData);
        applyInvoiceData(invoice, invoiceData);

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice {} created (DRAFT) by supplier {} for PO {}",
                saved.getInvoiceNumber(), supplierId, poId);
        return buildResponse(saved);
    }

    @Transactional
    public Map<String, Object> updateInvoice(Long supplierId, Long invoiceId,
                                             Map<String, Object> invoiceData) {
        Invoice invoice = getAndValidate(invoiceId, supplierId);

        if (invoice.getStatus() != Invoice.InvoiceStatus.DRAFT
                && invoice.getStatus() != Invoice.InvoiceStatus.REJECTED) {
            throw new RuntimeException(
                    "Invoice can only be edited in DRAFT or REJECTED status. Current: "
                            + invoice.getStatus());
        }

        applyInvoiceData(invoice, invoiceData);
        invoiceRepository.save(invoice);
        log.info("Invoice {} updated by supplier {}", invoice.getInvoiceNumber(), supplierId);
        return buildResponse(invoice);
    }

    @Transactional
    public Map<String, Object> submitInvoice(Long supplierId, Long invoiceId) {
        Invoice invoice = getAndValidate(invoiceId, supplierId);

        if (invoice.getStatus() != Invoice.InvoiceStatus.DRAFT) {
            throw new RuntimeException(
                    "Only DRAFT invoices can be submitted. Current: " + invoice.getStatus());
        }

        validateBankDetails(invoice);
        invoice.setStatus(Invoice.InvoiceStatus.SUBMITTED);
        invoiceRepository.save(invoice);

        try { emailService.sendInvoiceSubmittedToRfqCreator(invoice); }
        catch (Exception e) { log.warn("Email failed for invoice submit: {}", e.getMessage()); }

        log.info("Invoice {} submitted by supplier {}", invoice.getInvoiceNumber(), supplierId);
        return buildResponse(invoice);
    }

    @Transactional
    public Map<String, Object> resubmitInvoice(Long supplierId, Long invoiceId,
                                               String resubmitRemarks) {
        Invoice invoice = getAndValidate(invoiceId, supplierId);

        if (invoice.getStatus() == Invoice.InvoiceStatus.REJECTED_CLOSED) {
            throw new RuntimeException(
                    "This invoice is permanently closed and cannot be resubmitted.");
        }
        if (invoice.getStatus() != Invoice.InvoiceStatus.REJECTED) {
            throw new RuntimeException(
                    "Only REJECTED invoices can be resubmitted. Current: " + invoice.getStatus());
        }

        validateBankDetails(invoice);

        int newCount = invoice.getResubmitCount() == null ? 0 : invoice.getResubmitCount();
        newCount++;
        invoice.setResubmitCount(newCount);
        invoice.setResubmitRemarks(resubmitRemarks);
        invoice.setResubmittedAt(LocalDateTime.now());
        invoice.setStatus(Invoice.InvoiceStatus.SUBMITTED);
        invoiceRepository.save(invoice);

        try { emailService.sendInvoiceResubmittedToRfqCreator(invoice, resubmitRemarks); }
        catch (Exception e) { log.warn("Email failed for resubmit: {}", e.getMessage()); }

        log.info("Invoice {} resubmitted (count={}) by supplier {}",
                invoice.getInvoiceNumber(), newCount, supplierId);
        return buildResponse(invoice);
    }

    // =========================================================================
    // BUYER ACTIONS
    // =========================================================================

    @Transactional
    public Map<String, Object> approveInvoice(Long invoiceId, String approvedByName,
                                              String remarks) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

        if (invoice.getStatus() != Invoice.InvoiceStatus.SUBMITTED) {
            throw new RuntimeException(
                    "Only SUBMITTED invoices can be approved. Current: " + invoice.getStatus());
        }

        enforceThreeWayMatchPassed(invoice);

        invoice.setStatus(Invoice.InvoiceStatus.APPROVED);
        invoice.setApprovedRejectedBy(approvedByName);
        invoice.setApprovedRejectedAt(LocalDateTime.now());
        invoice.setApprovalRemarks(remarks);
        invoiceRepository.save(invoice);

        try { emailService.sendInvoiceApprovedToSupplier(invoice, approvedByName); }
        catch (Exception e) { log.warn("Email failed for approve: {}", e.getMessage()); }

        log.info("Invoice {} approved by {}", invoice.getInvoiceNumber(), approvedByName);
        return buildResponse(invoice);
    }

    @Transactional
    public Map<String, Object> requestResubmission(Long invoiceId, String buyerName,
                                                   String remarks) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

        if (invoice.getStatus() != Invoice.InvoiceStatus.SUBMITTED) {
            throw new RuntimeException(
                    "Only SUBMITTED invoices can be returned for resubmission. Current: "
                            + invoice.getStatus());
        }

        int resubmitCount = invoice.getResubmitCount() == null ? 0 : invoice.getResubmitCount();

        if (resubmitCount >= 1) {
            invoice.setStatus(Invoice.InvoiceStatus.REJECTED_CLOSED);
            invoice.setApprovalRemarks(remarks);
            invoice.setApprovedRejectedBy(buyerName);
            invoice.setApprovedRejectedAt(LocalDateTime.now());
            invoiceRepository.save(invoice);

            try { emailService.sendInvoiceRejectedPermanently(invoice, remarks); }
            catch (Exception e) { log.warn("Email failed for perm-reject: {}", e.getMessage()); }

            log.info("Invoice {} permanently closed by {} (resubmit count exceeded)",
                    invoice.getInvoiceNumber(), buyerName);
        } else {
            invoice.setStatus(Invoice.InvoiceStatus.REJECTED);
            invoice.setApprovalRemarks(remarks);
            invoice.setApprovedRejectedBy(buyerName);
            invoice.setApprovedRejectedAt(LocalDateTime.now());
            invoiceRepository.save(invoice);

            try { emailService.sendInvoiceResubmissionRequest(invoice, remarks); }
            catch (Exception e) { log.warn("Email failed for resubmission-request: {}", e.getMessage()); }

            log.info("Invoice {} returned for resubmission by {}", invoice.getInvoiceNumber(), buyerName);
        }

        return buildResponse(invoice);
    }

    @Transactional
    public Map<String, Object> markInvoicePaid(Long invoiceId, String paidByName,
                                               String paymentReference) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

        if (invoice.getStatus() != Invoice.InvoiceStatus.APPROVED) {
            throw new RuntimeException(
                    "Only APPROVED invoices can be marked as paid. Current: " + invoice.getStatus());
        }

        invoice.setStatus(Invoice.InvoiceStatus.PAID);
        invoice.setPaidBy(paidByName);
        invoice.setPaymentReference(paymentReference);
        invoice.setPaidAt(LocalDateTime.now());
        invoiceRepository.save(invoice);

        try { emailService.sendInvoiceMarkedPaid(invoice, paymentReference); }
        catch (Exception e) { log.warn("Email failed for mark-paid: {}", e.getMessage()); }

        log.info("Invoice {} marked PAID by {} (ref: {})",
                invoice.getInvoiceNumber(), paidByName, paymentReference);
        return buildResponse(invoice);
    }

    // =========================================================================
    // READ QUERIES
    // =========================================================================

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getInvoicesBySupplier(Long supplierId) {
        return invoiceRepository.findBySupplierId(supplierId)
                .stream().map(this::buildResponse).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));
        return buildResponse(invoice);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getInvoicesByRfqCreator(Long userId) {
        return invoiceRepository.findByRfqCreatorUserId(userId)
                .stream().map(this::buildResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getInvoicesByBuyerCompany(String companyName) {
        return invoiceRepository.findByBuyerCompanyName(companyName)
                .stream().map(this::buildResponse).toList();
    }

    // =========================================================================
    // ✅ FIXED: getApprovedPOsForSupplier — now includes currencyCode & currencySymbol
    // =========================================================================

    @Transactional(readOnly = true)
    public Map<String, Object> getApprovedPOsForSupplier(Long supplierId) {
        List<PurchaseOrder> pos = purchaseOrderRepository.findBySupplierIdAndApprovedStatus(supplierId);
        List<Map<String, Object>> data = pos.stream().map(po -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",               po.getId());
            m.put("poNumber",         po.getPoNumber());
            m.put("rfqNumber",        po.getRfq() != null ? po.getRfq().getRfqNumber() : null);
            m.put("rfqTitle",         po.getRfq() != null ? po.getRfq().getRfqTitle()  : null);
            m.put("grandTotal",       po.getGrandTotal());
            m.put("createdAt",        po.getCreatedAt());
            m.put("buyerCompanyName", po.getBuyer() != null ? po.getBuyer().getCompanyName() : null);

            // ✅ Currency from PO (copied from buyer's location at PO creation time)
            m.put("currencyCode",   po.getCurrencyCode()   != null ? po.getCurrencyCode()   : "INR");
            m.put("currencySymbol", po.getCurrencySymbol() != null ? po.getCurrencySymbol() : "₹");

            boolean hasInvoice = invoiceRepository.existsActiveInvoiceForPo(po.getId());
            m.put("hasInvoice", hasInvoice);
            if (hasInvoice) {
                invoiceRepository.findByPoId(po.getId()).ifPresent(inv -> {
                    m.put("invoiceId",     inv.getId());
                    m.put("invoiceNumber", inv.getInvoiceNumber());
                    m.put("invoiceStatus", inv.getStatus());
                });
            }
            return m;
        }).toList();
        return Map.of("success", true, "data", data);
    }

    // =========================================================================
    // ✅ FIXED: getPODetailsForInvoice — now includes currencyCode & currencySymbol
    // =========================================================================

    @Transactional(readOnly = true)
    public Map<String, Object> getPODetailsForInvoice(Long supplierId, Long poId) {
        PurchaseOrder po = purchaseOrderRepository.findByIdWithDetails(poId)
                .orElseThrow(() -> new RuntimeException("PO not found: " + poId));

        List<Map<String, Object>> lineItems = new ArrayList<>();
        if (po.getLineItems() != null) {
            for (POLineItem item : po.getLineItems()) {
                Map<String, Object> li = new LinkedHashMap<>();
                li.put("id",                      item.getId());
                li.put("slNo",                    item.getSlNo());
                li.put("itemCode",                item.getItemCode());
                li.put("itemDescription",         item.getItemDescription());
                li.put("itemDescriptionDetailed", item.getSpecifications());
                li.put("specifications",          item.getSpecifications());
                li.put("uom",                     item.getUom());
                li.put("poQuantity",              item.getQuantity());
                li.put("remainingQty",            item.getQuantity());
                li.put("quantity",                item.getQuantity());
                li.put("unitPrice",               item.getUnitRate());
                li.put("discountPercentage",      BigDecimal.ZERO);
                li.put("taxPercentage",           item.getTaxPercentage() != null
                        ? item.getTaxPercentage() : BigDecimal.valueOf(18));
                li.put("hsnSacCode",              "");
                li.put("lineTotal",               item.getLineTotal());
                lineItems.add(li);
            }
        }

        BigDecimal subtotal       = po.getSubtotal()   != null ? po.getSubtotal()   : BigDecimal.ZERO;
        BigDecimal taxAmount      = po.getTaxAmount()  != null ? po.getTaxAmount()  : BigDecimal.ZERO;
        BigDecimal grandTotal     = po.getGrandTotal() != null ? po.getGrandTotal() : BigDecimal.ZERO;
        BigDecimal gross          = subtotal.add(taxAmount);
        BigDecimal overallDiscount = gross.subtract(grandTotal);
        if (overallDiscount.compareTo(new BigDecimal("0.005")) < 0) {
            overallDiscount = BigDecimal.ZERO;
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id",                    po.getId());
        data.put("poNumber",              po.getPoNumber());
        data.put("rfqNumber",             po.getRfq() != null ? po.getRfq().getRfqNumber() : null);
        data.put("rfqTitle",              po.getRfq() != null ? po.getRfq().getRfqTitle()  : null);
        data.put("grandTotal",            grandTotal);
        data.put("subtotal",              subtotal);
        data.put("taxAmount",             taxAmount);
        data.put("overallDiscountAmount", overallDiscount);
        data.put("paymentTerms",          po.getPaymentTerms());
        data.put("buyerCompanyName",      po.getBuyer() != null ? po.getBuyer().getCompanyName() : null);

        // ✅ Currency from PO (copied from buyer's location at PO creation time)
        data.put("currencyCode",   po.getCurrencyCode()   != null ? po.getCurrencyCode()   : "INR");
        data.put("currencySymbol", po.getCurrencySymbol() != null ? po.getCurrencySymbol() : "₹");

        data.put("lineItems", lineItems);

        return Map.of("success", true, "data", data);
    }

    // =========================================================================
    // PRIVATE — 3-WAY MATCH ENFORCEMENT
    // =========================================================================

    private void enforceThreeWayMatchPassed(Invoice invoice) {
        Optional<ThreeWayMatch> latestMatchOpt =
                threeWayMatchRepository.findLatestByInvoiceId(invoice.getId());

        if (latestMatchOpt.isEmpty()) {
            throw new RuntimeException(
                    "Cannot approve invoice: No 3-Way Match has been performed. "
                    + "Please create a GRN, complete QA review, get it approved, "
                    + "then run the 3-Way Match before approving this invoice.");
        }

        ThreeWayMatch match = latestMatchOpt.get();
        ThreeWayMatchStatus matchStatus = match.getMatchStatus();

        boolean matchPassed = matchStatus == ThreeWayMatchStatus.MATCHED
                || matchStatus == ThreeWayMatchStatus.OVERRIDDEN_APPROVED
                || matchStatus == ThreeWayMatchStatus.PARTIAL_MATCH;

        if (!matchPassed) {
            String reason = switch (matchStatus) {
                case PENDING           -> "3-Way Match is pending — please run the match first.";
                case QUANTITY_MISMATCH -> "3-Way Match has a quantity mismatch. Resolve it first.";
                case PRICE_MISMATCH    -> "3-Way Match has a price variance. Resolve it first.";
                case ITEM_MISMATCH     -> "3-Way Match has an item mismatch. Resolve it first.";
                case EXCESS_DELIVERY   -> "3-Way Match flagged excess delivery. Resolve it first.";
                case DISPUTED          -> "3-Way Match is under dispute. Resolve the dispute first.";
                case FAILED            -> "3-Way Match has failed. The invoice must be revised and resubmitted.";
                default                -> "3-Way Match status (" + matchStatus + ") does not allow invoice approval.";
            };
            throw new RuntimeException("Cannot approve invoice: " + reason);
        }

        if (match.getResolution() == MatchResolution.PENDING_RESOLUTION) {
            throw new RuntimeException(
                    "Cannot approve invoice: The 3-Way Match mismatch is still pending resolution.");
        }

        log.info("  ✅ 3-Way Match check passed for invoice {} — Match status: {}, Resolution: {}",
                invoice.getInvoiceNumber(), matchStatus, match.getResolution());
    }

    // =========================================================================
    // PRIVATE — INVOICE DATA HELPERS
    // =========================================================================

    private void ensureOverallDiscountFromPO(PurchaseOrder po, Map<String, Object> invoiceData) {
        if (invoiceData == null) return;

        BigDecimal existingDiscount = BigDecimal.ZERO;
        if (invoiceData.containsKey("overallDiscountAmount") && invoiceData.get("overallDiscountAmount") != null) {
            try { existingDiscount = new BigDecimal(invoiceData.get("overallDiscountAmount").toString()); }
            catch (Exception ignored) {}
        }

        if (existingDiscount.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal subtotal   = po.getSubtotal()   != null ? po.getSubtotal()   : BigDecimal.ZERO;
            BigDecimal taxAmount  = po.getTaxAmount()  != null ? po.getTaxAmount()  : BigDecimal.ZERO;
            BigDecimal grandTotal = po.getGrandTotal() != null ? po.getGrandTotal() : BigDecimal.ZERO;
            BigDecimal discount   = subtotal.add(taxAmount).subtract(grandTotal);
            if (discount.compareTo(new BigDecimal("0.005")) > 0) {
                invoiceData.put("overallDiscountAmount", discount);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void applyInvoiceData(Invoice invoice, Map<String, Object> data) {
        if (data == null) return;

        BigDecimal overallDiscount = BigDecimal.ZERO;
        if (data.containsKey("overallDiscountAmount") && data.get("overallDiscountAmount") != null) {
            try { overallDiscount = new BigDecimal(data.get("overallDiscountAmount").toString()); }
            catch (Exception ignored) {}
        }

        if (data.containsKey("lineItems") && data.get("lineItems") instanceof List<?> rawList) {
            invoice.getLineItems().clear();

            int order = 1;
            BigDecimal subtotalPreTax = BigDecimal.ZERO;
            BigDecimal totalTaxAmount = BigDecimal.ZERO;

            for (Object rawItem : rawList) {
                if (!(rawItem instanceof Map)) continue;
                Map<String, Object> m = (Map<String, Object>) rawItem;

                InvoiceLineItem li = new InvoiceLineItem();
                li.setInvoice(invoice);
                li.setItemOrder(order++);

                if (m.get("id") != null)                     li.setPoLineItemId(toLong(m.get("id")));
                if (m.get("itemCode") != null)                li.setItemCode(m.get("itemCode").toString());
                if (m.get("itemDescription") != null)         li.setItemDescription(m.get("itemDescription").toString());
                if (m.get("itemDescriptionDetailed") != null) li.setItemDescriptionDetailed(m.get("itemDescriptionDetailed").toString());
                if (m.get("specifications") != null)          li.setSpecifications(m.get("specifications").toString());
                if (m.get("uom") != null)                     li.setUom(m.get("uom").toString());
                if (m.get("hsnSacCode") != null)              li.setHsnSacCode(m.get("hsnSacCode").toString());

                BigDecimal qty = null;
                if (m.get("qtyToInvoice") != null)  qty = new BigDecimal(m.get("qtyToInvoice").toString());
                else if (m.get("quantity") != null) qty = new BigDecimal(m.get("quantity").toString());
                if (qty != null) li.setQuantity(qty);

                if (m.get("unitPrice") != null) li.setUnitPrice(new BigDecimal(m.get("unitPrice").toString()));

                BigDecimal discPct = m.get("discountPercentage") != null
                        ? new BigDecimal(m.get("discountPercentage").toString()) : BigDecimal.ZERO;
                BigDecimal taxPct  = m.get("taxPercentage") != null
                        ? new BigDecimal(m.get("taxPercentage").toString()) : BigDecimal.ZERO;
                li.setDiscountPercentage(discPct);
                li.setTaxPercentage(taxPct);

                if (li.getQuantity() != null && li.getUnitPrice() != null) {
                    BigDecimal base = li.getQuantity().multiply(li.getUnitPrice());
                    if (discPct.compareTo(BigDecimal.ZERO) > 0) {
                        base = base.multiply(BigDecimal.ONE.subtract(
                                discPct.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP)));
                    }
                    BigDecimal taxAmt = base.multiply(
                            taxPct.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP));
                    li.setTaxAmount(taxAmt.setScale(2, BigDecimal.ROUND_HALF_UP));
                    li.setLineTotal(base.add(taxAmt).setScale(2, BigDecimal.ROUND_HALF_UP));
                    subtotalPreTax = subtotalPreTax.add(base);
                    totalTaxAmount = totalTaxAmount.add(taxAmt);
                } else if (m.get("lineTotal") != null) {
                    li.setLineTotal(new BigDecimal(m.get("lineTotal").toString()));
                }

                invoice.getLineItems().add(li);
            }

            invoice.setSubtotal(subtotalPreTax.setScale(2, BigDecimal.ROUND_HALF_UP));
            invoice.setTaxAmount(totalTaxAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
            BigDecimal grandTotal = subtotalPreTax.add(totalTaxAmount).subtract(overallDiscount)
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            invoice.setTotalAmount(grandTotal);
        }

        if (data.containsKey("currency"))            invoice.setCurrency((String) data.get("currency"));
        if (data.containsKey("notes"))               invoice.setNotes((String) data.get("notes"));
        if (data.containsKey("dueDate") && data.get("dueDate") != null)
            invoice.setDueDate(parseFlexibleDateTime(data.get("dueDate").toString()));
        if (data.containsKey("invoiceDate") && data.get("invoiceDate") != null)
            invoice.setInvoiceDate(parseFlexibleDateTime(data.get("invoiceDate").toString()));
        if (data.containsKey("bankName"))            invoice.setBankName((String) data.get("bankName"));
        if (data.containsKey("bankAccountName"))     invoice.setBankAccountName((String) data.get("bankAccountName"));
        if (data.containsKey("accountHolderName"))   invoice.setBankAccountName((String) data.get("accountHolderName"));
        if (data.containsKey("bankAccountNumber"))   invoice.setBankAccountNumber((String) data.get("bankAccountNumber"));
        if (data.containsKey("accountNumber"))       invoice.setBankAccountNumber((String) data.get("accountNumber"));
        if (data.containsKey("bankIfscCode"))        invoice.setBankIfscCode((String) data.get("bankIfscCode"));
        if (data.containsKey("ifscCode"))            invoice.setBankIfscCode((String) data.get("ifscCode"));
        if (data.containsKey("supplierName"))        invoice.setSupplierName((String) data.get("supplierName"));
        if (data.containsKey("supplierEmail"))       invoice.setSupplierEmail((String) data.get("supplierEmail"));
        if (data.containsKey("paymentTerms"))        invoice.setPaymentTerms((String) data.get("paymentTerms"));
        if (data.containsKey("termsAndConditions"))  invoice.setTermsAndConditions((String) data.get("termsAndConditions"));
    }

    private Map<String, Object> buildResponse(Invoice inv) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("id",                  inv.getId());
        r.put("invoiceNumber",       inv.getInvoiceNumber());
        r.put("status",              inv.getStatus());
        r.put("supplierId",          inv.getSupplierId());
        r.put("supplierName",        inv.getSupplierName());
        r.put("supplierCompanyName", inv.getSupplierName());
        r.put("supplierEmail",       inv.getSupplierEmail());
        r.put("buyerCompanyName",    inv.getBuyerCompanyName());
        r.put("poId",                inv.getPoId());
        r.put("poNumber",            inv.getPoNumber());
        r.put("rfqId",               inv.getRfqId());
        r.put("rfqNumber",           inv.getRfqNumber());
        r.put("rfqCreatorName",      inv.getRfqCreatorName());
        r.put("rfqCreatorEmail",     inv.getRfqCreatorEmail());

        BigDecimal subtotal    = inv.getSubtotal()    != null ? inv.getSubtotal()    : BigDecimal.ZERO;
        BigDecimal taxAmount   = inv.getTaxAmount()   != null ? inv.getTaxAmount()   : BigDecimal.ZERO;
        BigDecimal totalAmount = inv.getTotalAmount() != null ? inv.getTotalAmount() : BigDecimal.ZERO;
        r.put("subtotal",    subtotal);
        r.put("taxAmount",   taxAmount);
        r.put("totalAmount", totalAmount);
        r.put("grandTotal",  totalAmount);

        BigDecimal gross = subtotal.add(taxAmount);
        BigDecimal overallDiscount = gross.subtract(totalAmount);
        if (overallDiscount.compareTo(new BigDecimal("0.005")) < 0) overallDiscount = BigDecimal.ZERO;
        r.put("overallDiscountAmount", overallDiscount);

        // ✅ Currency — stored on invoice (set from PO's buyer location at creation)
        r.put("currency",     inv.getCurrency());
        r.put("currencyCode", inv.getCurrency()); // alias so frontend can use either field name

        r.put("bankName",            inv.getBankName());
        r.put("accountHolderName",   inv.getBankAccountName());
        r.put("accountNumber",       inv.getBankAccountNumber());
        r.put("ifscCode",            inv.getBankIfscCode());
        r.put("approvalRemarks",     inv.getApprovalRemarks());
        r.put("approvedRejectedBy",  inv.getApprovedRejectedBy());
        r.put("approvedRejectedAt",  inv.getApprovedRejectedAt());
        r.put("resubmitCount",       inv.getResubmitCount() == null ? 0 : inv.getResubmitCount());
        r.put("resubmitRemarks",     inv.getResubmitRemarks());
        r.put("resubmittedAt",       inv.getResubmittedAt());

        boolean canResubmit = inv.getStatus() == Invoice.InvoiceStatus.REJECTED
                && (inv.getResubmitCount() == null || inv.getResubmitCount() == 0);
        r.put("canResubmit", canResubmit);

        boolean canApprove = false;
        boolean threeWayMatchPassed = false;
        String threeWayMatchStatus = null;

        if (inv.getStatus() == Invoice.InvoiceStatus.SUBMITTED) {
            try {
                Optional<ThreeWayMatch> matchOpt =
                        threeWayMatchRepository.findLatestByInvoiceId(inv.getId());
                if (matchOpt.isPresent()) {
                    ThreeWayMatch m = matchOpt.get();
                    threeWayMatchStatus = m.getMatchStatus().name();
                    threeWayMatchPassed = m.getMatchStatus() == ThreeWayMatchStatus.MATCHED
                            || m.getMatchStatus() == ThreeWayMatchStatus.OVERRIDDEN_APPROVED
                            || m.getMatchStatus() == ThreeWayMatchStatus.PARTIAL_MATCH;
                    canApprove = threeWayMatchPassed
                            && m.getResolution() != MatchResolution.PENDING_RESOLUTION;
                }
            } catch (Exception ignored) {}
        }

        r.put("canApprove",              canApprove);
        r.put("threeWayMatchPassed",     threeWayMatchPassed);
        r.put("threeWayMatchStatus",     threeWayMatchStatus);
        r.put("canRequestResubmission",  inv.getStatus() == Invoice.InvoiceStatus.SUBMITTED);

        r.put("paymentReference",    inv.getPaymentReference());
        r.put("paidAt",              inv.getPaidAt());
        r.put("paidBy",              inv.getPaidBy());
        r.put("invoiceDate",         inv.getInvoiceDate());
        r.put("dueDate",             inv.getDueDate());
        r.put("notes",               inv.getNotes());
        r.put("paymentTerms",        inv.getPaymentTerms());
        r.put("termsAndConditions",  inv.getTermsAndConditions());
        r.put("createdAt",           inv.getCreatedAt());
        r.put("updatedAt",           inv.getUpdatedAt());

        List<Map<String, Object>> lineItems = new ArrayList<>();
        if (inv.getLineItems() != null) {
            for (InvoiceLineItem li : inv.getLineItems()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id",                      li.getId());
                m.put("poLineItemId",             li.getPoLineItemId());
                m.put("itemOrder",               li.getItemOrder());
                m.put("itemCode",                li.getItemCode());
                m.put("itemDescription",         li.getItemDescription());
                m.put("itemDescriptionDetailed", li.getItemDescriptionDetailed());
                m.put("specifications",          li.getSpecifications());
                m.put("uom",                     li.getUom());
                m.put("hsnSacCode",              li.getHsnSacCode());
                m.put("quantity",                li.getQuantity());
                m.put("unitPrice",               li.getUnitPrice());
                m.put("discountPercentage",      li.getDiscountPercentage());
                m.put("taxPercentage",           li.getTaxPercentage());
                m.put("taxAmount",               li.getTaxAmount());
                m.put("lineTotal",               li.getLineTotal());
                lineItems.add(m);
            }
        }
        r.put("lineItems", lineItems);

        return r;
    }

    private Invoice getAndValidate(Long invoiceId, Long supplierId) {
        return invoiceRepository.findByIdAndSupplierId(invoiceId, supplierId)
                .orElseThrow(() -> new RuntimeException(
                        "Invoice not found or does not belong to this supplier. id=" + invoiceId));
    }

    private void validateBankDetails(Invoice invoice) {
        if (invoice.getBankAccountNumber() == null || invoice.getBankAccountNumber().isBlank())
            throw new RuntimeException("Bank account number is required before submitting.");
        if (invoice.getBankName() == null || invoice.getBankName().isBlank())
            throw new RuntimeException("Bank name is required before submitting.");
    }

    private Long toLong(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).longValue();
        try { return Long.parseLong(val.toString()); } catch (Exception e) { return null; }
    }

    private LocalDateTime parseFlexibleDateTime(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        }
    }

    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis();
    }
}