

package com.itti.leadcapturing.service;

import com.itti.leadcapturing.dto.GRNDto;
import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ThreeWayMatchService — the core reconciliation engine.
 *
 * HOW IT WORKS
 * ────────────
 * For each PO line item the engine compares across three documents:
 *
 *   PO          → what was ORDERED (quantity + agreed unit rate)
 *   GRN         → what was RECEIVED & ACCEPTED (accepted quantity)
 *   Invoice     → what the supplier is BILLING for (quantity + unit price)
 *
 * PARTIAL DELIVERY SUPPORT (FIXED)
 * ──────────────────────────────────
 * If a PO has multiple GRN batches (e.g. 8 received first, then 2 later),
 * each GRN is matched independently against its own effective invoice line qty.
 *
 * The engine calculates:
 *   previouslyAcceptedQty  = sum of acceptedQty from all OTHER APPROVED/CLOSED GRNs
 *                            for the same PO line item (excluding current GRN)
 *   remainingPoQty         = poOrderedQty - previouslyAcceptedQty
 *   effectiveInvoiceQty    = invoiceQty   - previouslyAcceptedQty   ← KEY FIX
 *
 * Example (Ordered 10, Invoice 10):
 *   GRN-1 batch (8 units):
 *     previouslyAccepted = 0
 *     effectiveInvoiceQty = 10 - 0 = 10
 *     grnAccepted = 8  →  qty mismatch (8 vs 10, 20% > tolerance)
 *     Buyer resolves with PARTIAL_PAYMENT_APPROVED / ACCEPTED_WITH_VARIANCE
 *
 *   GRN-2 batch (2 units):
 *     previouslyAccepted = 8
 *     effectiveInvoiceQty = 10 - 8 = 2
 *     grnAccepted = 2  →  MATCHED (2 vs 2) ✅
 *
 * Match fails (QTY_EXCESS) only when: grnAcceptedQty > remainingPoQty
 *
 * INVOICE STATUS RULES
 * ────────────────────
 *   SUBMITTED → First-time match. If MATCHED, auto-moves invoice to APPROVED.
 *   APPROVED  → Re-match (e.g., second GRN batch). Invoice stays APPROVED, remarks updated.
 *   Any other status → blocked.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ThreeWayMatchService {

    private final ThreeWayMatchRepository   matchRepository;
    private final GRNRepository             grnRepository;
    private final InvoiceRepository         invoiceRepository;
    private final PurchaseOrderRepository   poRepository;
    private final GRNLineItemRepository     grnLineItemRepository;
    private final GRNService                grnService;

    private static final BigDecimal DEFAULT_TOLERANCE = new BigDecimal("2.00");
    private static final BigDecimal HUNDRED           = new BigDecimal("100");

    // =========================================================================
    // PERFORM 3-WAY MATCH
    // =========================================================================

    @Transactional
    public GRNDto.MatchResponse performMatch(GRNDto.PerformMatchRequest request) {
        log.info("=".repeat(60));
        log.info("🔍 [3-WAY MATCH] PO: {}, GRN: {}, Invoice: {}",
                request.getPurchaseOrderId(), request.getGrnId(), request.getInvoiceId());

        // ── Load & validate documents ─────────────────────────────────────────
        PurchaseOrder po = poRepository.findByIdWithDetails(request.getPurchaseOrderId())
                .orElseThrow(() -> new RuntimeException("PO not found: " + request.getPurchaseOrderId()));

        GRN grn = grnRepository.findByIdWithLineItems(request.getGrnId())
                .orElseThrow(() -> new RuntimeException("GRN not found: " + request.getGrnId()));

        // ✅ GRN must be APPROVED
        if (grn.getStatus() != GRNStatus.APPROVED) {
            throw new RuntimeException(
                "GRN must be in APPROVED status before matching. Current: " + grn.getStatus());
        }
        if (!grn.getPurchaseOrderId().equals(request.getPurchaseOrderId())) {
            throw new RuntimeException("GRN does not belong to this PO.");
        }

        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + request.getInvoiceId()));

        // ✅ Allow BOTH SUBMITTED and APPROVED invoices
        //    SUBMITTED = first-time match  (e.g., GRN-1 batch)
        //    APPROVED  = re-match          (e.g., GRN-2 batch after partial delivery)
        boolean isFirstMatch = invoice.getStatus() == Invoice.InvoiceStatus.SUBMITTED;
        boolean isReMatch    = invoice.getStatus() == Invoice.InvoiceStatus.APPROVED;

        if (!isFirstMatch && !isReMatch) {
            throw new RuntimeException(
                "3-Way Match can only be performed on SUBMITTED or APPROVED invoices. Current: "
                + invoice.getStatus()
                + ". DRAFT, PAID, REJECTED invoices cannot be matched.");
        }

        if (!invoice.getPoId().equals(request.getPurchaseOrderId())) {
            throw new RuntimeException("Invoice PO does not match the provided PO.");
        }

        // ── Determine version (re-match increments version) ──────────────────
        int version = matchRepository.findLatestByInvoiceId(invoice.getId())
                .map(m -> m.getMatchVersion() + 1).orElse(1);

        BigDecimal tolerance = request.getTolerancePercentage() != null
                ? request.getTolerancePercentage() : DEFAULT_TOLERANCE;

        // ── Build match record ────────────────────────────────────────────────
        ThreeWayMatch match = new ThreeWayMatch();
        match.setPurchaseOrderId(po.getId());
        match.setPoNumber(po.getPoNumber());
        match.setGrnId(grn.getId());
        match.setGrnNumber(grn.getGrnNumber());
        match.setInvoiceId(invoice.getId());
        match.setInvoiceNumber(invoice.getInvoiceNumber());
        match.setTolerancePercentage(tolerance);
        match.setMatchVersion(version);
        match.setPerformedByUserId(request.getPerformedByUserId());
        match.setPerformedByName(request.getPerformedByName());
        match.setPerformedAt(LocalDateTime.now());

        // ── Per-line matching (partial delivery aware) ────────────────────────
        List<MatchLineResult> lineResults = matchLines(po, grn, invoice, tolerance);
        for (MatchLineResult r : lineResults) match.addLineResult(r);

        // ── Compute effective invoice total from line results ─────────────────
        // invoiceLineValue in each MatchLineResult already uses effectiveInvoiceQty × price
        // so summing gives the effective total for THIS batch only
        BigDecimal effectiveInvoiceTotal = lineResults.stream()
                .filter(r -> r.getInvoiceLineValue() != null)
                .map(MatchLineResult::getInvoiceLineValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ── Financial totals ──────────────────────────────────────────────────
        match.setPoTotalValue(po.getGrandTotal());
        match.setGrnAcceptedValue(grn.getTotalReceivedValue());
        match.setInvoiceTotalValue(effectiveInvoiceTotal);  // effective for this batch

        // ── Aggregate mismatch flags ──────────────────────────────────────────
        boolean qtyMismatch   = lineResults.stream().anyMatch(r ->
                r.getLineMatchStatus() == MatchLineResult.LineMatchStatus.QTY_SHORT);
        boolean priceMismatch = lineResults.stream().anyMatch(r ->
                r.getLineMatchStatus() == MatchLineResult.LineMatchStatus.PRICE_VARIANCE);
        boolean itemMismatch  = lineResults.stream().anyMatch(r ->
                r.getLineMatchStatus() == MatchLineResult.LineMatchStatus.NOT_IN_PO
                        || r.getLineMatchStatus() == MatchLineResult.LineMatchStatus.NOT_RECEIVED);
        boolean excessDel     = lineResults.stream().anyMatch(r ->
                r.getLineMatchStatus() == MatchLineResult.LineMatchStatus.QTY_EXCESS);

        long matchedCount  = lineResults.stream().filter(r ->
                r.getLineMatchStatus() == MatchLineResult.LineMatchStatus.MATCHED
                || r.getLineMatchStatus() == MatchLineResult.LineMatchStatus.PARTIALLY_MATCHED).count();
        long mismatchCount = lineResults.size() - matchedCount;

        match.setHasQuantityMismatch(qtyMismatch);
        match.setHasPriceMismatch(priceMismatch);
        match.setHasItemMismatch(itemMismatch);
        match.setHasExcessDelivery(excessDel);
        match.setTotalMatchedLines((int) matchedCount);
        match.setTotalMismatchLines((int) mismatchCount);

        // ── Overall variance (effective invoice total vs GRN accepted value) ──
        BigDecimal grnVal   = grn.getTotalReceivedValue() != null ? grn.getTotalReceivedValue() : BigDecimal.ZERO;
        BigDecimal variance = effectiveInvoiceTotal.subtract(grnVal);
        match.setVarianceAmount(variance.setScale(2, RoundingMode.HALF_UP));
        if (grnVal.compareTo(BigDecimal.ZERO) > 0) {
            match.setVariancePercentage(variance.multiply(HUNDRED)
                    .divide(grnVal, 4, RoundingMode.HALF_UP));
        }

        // ── Overall match status ──────────────────────────────────────────────
        ThreeWayMatchStatus overallStatus;
        if (!qtyMismatch && !priceMismatch && !itemMismatch && !excessDel) {
            overallStatus = ThreeWayMatchStatus.MATCHED;
        } else if (qtyMismatch && !priceMismatch && !itemMismatch) {
            overallStatus = ThreeWayMatchStatus.QUANTITY_MISMATCH;
        } else if (priceMismatch && !qtyMismatch && !itemMismatch) {
            overallStatus = ThreeWayMatchStatus.PRICE_MISMATCH;
        } else if (itemMismatch) {
            overallStatus = ThreeWayMatchStatus.ITEM_MISMATCH;
        } else if (excessDel && !qtyMismatch && !priceMismatch) {
            overallStatus = ThreeWayMatchStatus.EXCESS_DELIVERY;
        } else {
            overallStatus = ThreeWayMatchStatus.PARTIAL_MATCH;
        }

        match.setMatchStatus(overallStatus);
        match.setMatchSummary(buildSummary(overallStatus, (int) matchedCount, (int) mismatchCount,
                variance, tolerance));

        // ── Auto-actions ──────────────────────────────────────────────────────
        if (overallStatus == ThreeWayMatchStatus.MATCHED) {
            if (isFirstMatch) {
                // SUBMITTED invoice → matched → auto-approve
                invoice.setStatus(Invoice.InvoiceStatus.APPROVED);
                invoice.setApprovalRemarks("Auto-approved via 3-Way Match (v" + version
                        + "). All lines matched within tolerance.");
                invoice.setApprovedRejectedBy(request.getPerformedByName());
                invoice.setApprovedRejectedAt(LocalDateTime.now());
                invoiceRepository.save(invoice);
                log.info("  ✅ FULL MATCH (first) — Invoice {} auto-approved", invoice.getInvoiceNumber());
            } else {
                // APPROVED invoice → re-match (second GRN batch) → already approved, update remarks
                invoice.setApprovalRemarks("Re-verified via 3-Way Match (v" + version
                        + "). Remaining delivery batch matched. Full order now complete.");
                invoiceRepository.save(invoice);
                log.info("  ✅ FULL MATCH (re-match v{}) — Invoice {} remaining batch verified",
                        version, invoice.getInvoiceNumber());
            }
            match.setResolution(MatchResolution.ACCEPTED_WITH_VARIANCE);
            match.setApprovedPaymentAmount(effectiveInvoiceTotal);
        } else {
            // Mismatch → pending resolution
            match.setResolution(MatchResolution.PENDING_RESOLUTION);
            log.warn("  ⚠️ MISMATCH ({}) — Invoice {} pending resolution. Effective variance: ₹{}",
                    overallStatus, invoice.getInvoiceNumber(), variance);
        }

        ThreeWayMatch saved = matchRepository.save(match);

        log.info("  📊 Match v{} — Matched: {}/{} lines | Status: {} | GRN: {} | IsReMatch: {}",
                version, matchedCount, lineResults.size(), overallStatus,
                grn.getGrnNumber(), isReMatch);
        log.info("=".repeat(60));

        return toResponse(saved);
    }

    // =========================================================================
    // RESOLVE MISMATCH
    // =========================================================================

    @Transactional
    public GRNDto.MatchResponse resolveMatch(GRNDto.ResolveMatchRequest request) {
        log.info("⚖️ [RESOLVE MATCH] ID: {}, Resolution: {}", request.getMatchId(), request.getResolution());

        ThreeWayMatch match = matchRepository.findByIdWithLineResults(request.getMatchId())
                .orElseThrow(() -> new RuntimeException("3-Way Match record not found: " + request.getMatchId()));

        if (match.getMatchStatus() == ThreeWayMatchStatus.MATCHED) {
            throw new RuntimeException("This match is already fully matched — no resolution needed.");
        }
        if (match.getResolution() != MatchResolution.PENDING_RESOLUTION) {
            throw new RuntimeException("This match has already been resolved: " + match.getResolution());
        }
        if (request.getResolutionRemarks() == null || request.getResolutionRemarks().isBlank()) {
            throw new RuntimeException("Resolution remarks are mandatory when resolving a mismatch.");
        }

        MatchResolution resolution;
        try {
            resolution = MatchResolution.valueOf(request.getResolution());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid resolution: " + request.getResolution()
                    + ". Valid values: " + java.util.Arrays.toString(MatchResolution.values()));
        }

        match.setResolution(resolution);
        match.setResolutionRemarks(request.getResolutionRemarks());
        match.setResolvedByUserId(request.getResolvedByUserId());
        match.setResolvedByName(request.getResolvedByName());
        match.setResolvedAt(LocalDateTime.now());

        Invoice invoice = invoiceRepository.findById(match.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        switch (resolution) {

            case ACCEPTED_WITH_VARIANCE -> {
                match.setMatchStatus(ThreeWayMatchStatus.OVERRIDDEN_APPROVED);
                BigDecimal approvedAmount = request.getApprovedPaymentAmount() != null
                        ? request.getApprovedPaymentAmount() : invoice.getTotalAmount();
                match.setApprovedPaymentAmount(approvedAmount);
                invoice.setStatus(Invoice.InvoiceStatus.APPROVED);
                invoice.setApprovalRemarks("Approved with variance. Justification: " + request.getResolutionRemarks());
                invoice.setApprovedRejectedBy(request.getResolvedByName());
                invoice.setApprovedRejectedAt(LocalDateTime.now());
                invoiceRepository.save(invoice);
                log.info("  ⚠️ Invoice {} approved with variance override by {}",
                        invoice.getInvoiceNumber(), request.getResolvedByName());
            }

            case REVISED_INVOICE_REQUESTED -> {
                invoice.setStatus(Invoice.InvoiceStatus.REJECTED);
                invoice.setApprovalRemarks("Revised invoice requested after 3-Way Match mismatch. "
                        + request.getResolutionRemarks());
                invoice.setApprovedRejectedBy(request.getResolvedByName());
                invoice.setApprovedRejectedAt(LocalDateTime.now());
                invoiceRepository.save(invoice);
                log.info("  📩 Revised invoice requested for {}", invoice.getInvoiceNumber());
            }

            case PARTIAL_PAYMENT_APPROVED -> {
                if (request.getApprovedPaymentAmount() == null) {
                    throw new RuntimeException("approvedPaymentAmount is required for PARTIAL_PAYMENT_APPROVED.");
                }
                match.setApprovedPaymentAmount(request.getApprovedPaymentAmount());
                match.setMatchStatus(ThreeWayMatchStatus.PARTIAL_MATCH);
                invoice.setStatus(Invoice.InvoiceStatus.APPROVED);
                invoice.setApprovalRemarks("Partial payment approved: ₹" + request.getApprovedPaymentAmount()
                        + ". Reason: " + request.getResolutionRemarks());
                invoice.setApprovedRejectedBy(request.getResolvedByName());
                invoice.setApprovedRejectedAt(LocalDateTime.now());
                invoiceRepository.save(invoice);
                log.info("  💰 Partial payment ₹{} approved for {}",
                        request.getApprovedPaymentAmount(), invoice.getInvoiceNumber());
            }

            case CREDIT_NOTE_REQUESTED -> {
                match.setMatchStatus(ThreeWayMatchStatus.PARTIAL_MATCH);
                BigDecimal payableAmount = match.getGrnAcceptedValue();
                match.setApprovedPaymentAmount(payableAmount);
                invoice.setStatus(Invoice.InvoiceStatus.APPROVED);
                invoice.setApprovalRemarks("Approved for GRN-accepted value ₹" + payableAmount
                        + ". Credit note requested from supplier. " + request.getResolutionRemarks());
                invoice.setApprovedRejectedBy(request.getResolvedByName());
                invoice.setApprovedRejectedAt(LocalDateTime.now());
                invoiceRepository.save(invoice);
                log.info("  📄 Credit note requested; invoice {} approved for ₹{}",
                        invoice.getInvoiceNumber(), payableAmount);
            }

            case DEBIT_NOTE_RAISED -> {
                match.setMatchStatus(ThreeWayMatchStatus.OVERRIDDEN_APPROVED);
                BigDecimal debitAmount = match.getVarianceAmount();
                match.setApprovedPaymentAmount(match.getGrnAcceptedValue());
                invoice.setStatus(Invoice.InvoiceStatus.APPROVED);
                invoice.setApprovalRemarks("Approved for GRN-accepted value. Debit note raised for ₹" + debitAmount
                        + ". " + request.getResolutionRemarks());
                invoice.setApprovedRejectedBy(request.getResolvedByName());
                invoice.setApprovedRejectedAt(LocalDateTime.now());
                invoiceRepository.save(invoice);
                log.info("  🧾 Debit note raised ₹{} for invoice {}", debitAmount, invoice.getInvoiceNumber());
            }

            case DISPUTED -> {
                match.setMatchStatus(ThreeWayMatchStatus.DISPUTED);
                log.warn("  ⚖️ Invoice {} formally disputed", invoice.getInvoiceNumber());
            }

            case CANCELLED -> {
                match.setMatchStatus(ThreeWayMatchStatus.FAILED);
                invoice.setStatus(Invoice.InvoiceStatus.REJECTED_CLOSED);
                invoice.setApprovalRemarks("Invoice cancelled after 3-Way Match failure. "
                        + request.getResolutionRemarks());
                invoice.setApprovedRejectedBy(request.getResolvedByName());
                invoice.setApprovedRejectedAt(LocalDateTime.now());
                invoiceRepository.save(invoice);
                log.info("  ❌ Invoice {} cancelled", invoice.getInvoiceNumber());
            }

            default -> throw new RuntimeException("Unhandled resolution: " + resolution);
        }

        ThreeWayMatch saved = matchRepository.save(match);
        log.info("  ✅ Match {} resolved: {}", saved.getId(), resolution);
        return toResponse(saved);
    }

    // =========================================================================
    // READ METHODS
    // =========================================================================

    @Transactional(readOnly = true)
    public GRNDto.MatchResponse getMatchById(Long matchId) {
        ThreeWayMatch match = matchRepository.findByIdWithLineResults(matchId)
                .orElseThrow(() -> new RuntimeException("3-Way Match not found: " + matchId));
        return toResponse(match);
    }

    @Transactional(readOnly = true)
    public List<GRNDto.MatchResponse> getMatchesByInvoice(Long invoiceId) {
        return matchRepository.findByInvoiceId(invoiceId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GRNDto.MatchResponse> getMatchesByPO(Long poId) {
        return matchRepository.findByPurchaseOrderId(poId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<GRNDto.MatchResponse> getLatestMatch(Long invoiceId) {
        return matchRepository.findLatestByInvoiceId(invoiceId).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<GRNDto.MatchResponse> getPendingResolutions() {
        return matchRepository.findPendingResolution().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // =========================================================================
    // CORE MATCHING ENGINE — PARTIAL DELIVERY AWARE (FIXED)
    // =========================================================================

    private List<MatchLineResult> matchLines(PurchaseOrder po, GRN grn, Invoice invoice,
                                              BigDecimal tolerance) {
        List<MatchLineResult> results = new ArrayList<>();

        List<POLineItem>      poLines      = po.getLineItems();
        List<GRNLineItem>     grnLines     = grn.getLineItems();
        List<InvoiceLineItem> invoiceLines = invoice.getLineItems();

        for (POLineItem poLine : poLines) {
            MatchLineResult result = new MatchLineResult();
            result.setPoLineItemId(poLine.getId());
            result.setItemOrder(poLine.getSlNo());
            result.setItemCode(poLine.getItemCode());
            result.setItemDescription(poLine.getItemDescription());
            result.setUom(poLine.getUom());

            BigDecimal poQty  = safeQty(poLine.getQuantity());
            BigDecimal poRate = safeAmt(poLine.getUnitRate());
            result.setPoOrderedQuantity(poQty);
            result.setPoUnitRate(poRate);
            result.setPoLineValue(poQty.multiply(poRate).setScale(2, RoundingMode.HALF_UP));

            // ── Partial delivery: calculate previously accepted & remaining ────
            // previouslyAccepted = sum of acceptedQty from all OTHER APPROVED/CLOSED GRNs
            BigDecimal previouslyAccepted = grnLineItemRepository
                    .sumPreviouslyAcceptedQty(poLine.getId(), grn.getId());
            BigDecimal remainingPoQty = poQty.subtract(previouslyAccepted).max(BigDecimal.ZERO);

            log.debug("  Line {} [{}]: poQty={} | previouslyAccepted={} | remainingPoQty={}",
                    poLine.getSlNo(), poLine.getItemDescription(),
                    poQty, previouslyAccepted, remainingPoQty);

            // ── Find matching GRN and Invoice lines ───────────────────────────
            GRNLineItem grnLine = grnLines.stream()
                    .filter(g -> g.getPoLineItemId() != null && g.getPoLineItemId().equals(poLine.getId()))
                    .findFirst().orElse(null);

            InvoiceLineItem invLine = invoiceLines.stream()
                    .filter(i -> i.getPoLineItemId() != null && i.getPoLineItemId().equals(poLine.getId()))
                    .findFirst()
                    .or(() -> invoiceLines.stream()
                            .filter(i -> i.getItemOrder() != null && i.getItemOrder().equals(poLine.getSlNo()))
                            .findFirst())
                    .orElse(null);

            if (grnLine == null) {
                result.setGrnAcceptedQuantity(BigDecimal.ZERO);
                result.setGrnAcceptedValue(BigDecimal.ZERO);
                result.setLineMatchStatus(MatchLineResult.LineMatchStatus.NOT_RECEIVED);
                result.setWithinTolerance(false);
                result.setMismatchNotes("Item not found in GRN — goods may not have arrived.");
                results.add(result);
                continue;
            }

            if (invLine == null) {
                result.setGrnAcceptedQuantity(safeQty(grnLine.getAcceptedQuantity()));
                result.setGrnAcceptedValue(safeAmt(grnLine.getAcceptedValue()));
                result.setLineMatchStatus(MatchLineResult.LineMatchStatus.NOT_RECEIVED);
                result.setWithinTolerance(false);
                result.setMismatchNotes("Item not found in Invoice — invoice may be incomplete.");
                results.add(result);
                continue;
            }

            result.setGrnLineItemId(grnLine.getId());
            result.setInvoiceLineItemId(invLine.getId());

            BigDecimal grnAccQty  = safeQty(grnLine.getAcceptedQuantity());
            BigDecimal fullInvQty = safeQty(invLine.getQuantity()); // full invoice qty (for logging only)
            BigDecimal invPrice   = safeAmt(invLine.getUnitPrice());

            // ── KEY FIX: Effective invoice qty for partial delivery ────────────
            // For re-match (second GRN batch), subtract what was already covered by
            // previous approved GRNs from the full invoice quantity.
            //
            // Example — Ordered 10, Invoice 10:
            //   GRN-1 (8 units): previouslyAccepted=0, effectiveInvoiceQty=10-0=10
            //                    grnAccepted=8 vs effective=10 → 20% mismatch → buyer resolves
            //   GRN-2 (2 units): previouslyAccepted=8, effectiveInvoiceQty=10-8=2
            //                    grnAccepted=2 vs effective=2 → MATCHED ✅
            BigDecimal effectiveInvoiceQty = fullInvQty.subtract(previouslyAccepted).max(BigDecimal.ZERO);

            result.setGrnAcceptedQuantity(grnAccQty);
            result.setInvoiceQuantity(effectiveInvoiceQty);   // effective qty for this match batch
            result.setPoUnitRate(poRate);
            result.setInvoiceUnitPrice(invPrice);

            // ── Quantity variance: GRN accepted vs EFFECTIVE invoice qty ──────
            BigDecimal qtyVariance = grnAccQty.subtract(effectiveInvoiceQty);
            result.setQuantityVariance(qtyVariance.setScale(3, RoundingMode.HALF_UP));
            BigDecimal qtyVariancePct = effectiveInvoiceQty.compareTo(BigDecimal.ZERO) > 0
                    ? qtyVariance.abs().multiply(HUNDRED).divide(effectiveInvoiceQty, 4, RoundingMode.HALF_UP)
                    : (grnAccQty.compareTo(BigDecimal.ZERO) > 0 ? new BigDecimal("100.0000") : BigDecimal.ZERO);
            result.setQuantityVariancePct(qtyVariancePct);

            // ── Price variance: invoice price vs PO rate ──────────────────────
            BigDecimal priceVariance = invPrice.subtract(poRate);
            result.setPriceVariance(priceVariance.setScale(2, RoundingMode.HALF_UP));
            BigDecimal priceVariancePct = poRate.compareTo(BigDecimal.ZERO) > 0
                    ? priceVariance.abs().multiply(HUNDRED).divide(poRate, 4, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            result.setPriceVariancePct(priceVariancePct);

            // ── Values (use effectiveInvoiceQty for this batch's invoice value) ─
            BigDecimal grnValue       = grnAccQty.multiply(poRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal effectiveInvValue = effectiveInvoiceQty.multiply(invPrice).setScale(2, RoundingMode.HALF_UP);
            result.setGrnAcceptedValue(grnValue);
            result.setInvoiceLineValue(effectiveInvValue);
            result.setValueVariance(effectiveInvValue.subtract(grnValue).setScale(2, RoundingMode.HALF_UP));

            boolean qtyWithinTol   = qtyVariancePct.compareTo(tolerance) <= 0;
            boolean priceWithinTol = priceVariancePct.compareTo(tolerance) <= 0;

            // ── Excess check: GRN accepted must not exceed REMAINING PO qty ───
            // Uses remainingPoQty (not full poQty) for partial delivery awareness.
            // GRN-2 accepting 2 against remaining 2 is NOT excess.
            boolean excessQty = grnAccQty.compareTo(remainingPoQty) > 0;

            MatchLineResult.LineMatchStatus lineStatus;
            List<String> notes = new ArrayList<>();
            boolean lineTolerance = true;

            if (excessQty) {
                lineStatus = MatchLineResult.LineMatchStatus.QTY_EXCESS;
                notes.add(String.format(
                        "Excess delivery: GRN accepted %.3f > remaining PO qty %.3f " +
                        "(PO total: %.3f, previously accepted across all GRNs: %.3f)",
                        grnAccQty, remainingPoQty, poQty, previouslyAccepted));
                lineTolerance = false;
            } else if (!qtyWithinTol && !priceWithinTol) {
                lineStatus = MatchLineResult.LineMatchStatus.QTY_SHORT;
                notes.add(String.format(
                        "Quantity mismatch: GRN accepted %.3f vs effective invoice %.3f (%.2f%%)",
                        grnAccQty, effectiveInvoiceQty, qtyVariancePct));
                notes.add(String.format(
                        "Price variance: PO ₹%.2f vs Invoice ₹%.2f (%.2f%%)",
                        poRate, invPrice, priceVariancePct));
                lineTolerance = false;
            } else if (!qtyWithinTol) {
                lineStatus = MatchLineResult.LineMatchStatus.QTY_SHORT;
                notes.add(String.format(
                        "Quantity mismatch: GRN accepted %.3f vs effective invoice %.3f " +
                        "(%.2f%% — tolerance %.2f%%)",
                        grnAccQty, effectiveInvoiceQty, qtyVariancePct, tolerance));
                if (previouslyAccepted.compareTo(BigDecimal.ZERO) > 0) {
                    notes.add(String.format(
                            "(Full invoice qty: %.3f, previously accepted: %.3f, effective remaining: %.3f)",
                            fullInvQty, previouslyAccepted, effectiveInvoiceQty));
                }
                lineTolerance = false;
            } else if (!priceWithinTol) {
                lineStatus = MatchLineResult.LineMatchStatus.PRICE_VARIANCE;
                notes.add(String.format(
                        "Price variance: PO ₹%.2f vs Invoice ₹%.2f (%.2f%% — tolerance %.2f%%)",
                        poRate, invPrice, priceVariancePct, tolerance));
                lineTolerance = false;
            } else if (!qtyWithinTol || !priceWithinTol) {
                lineStatus = MatchLineResult.LineMatchStatus.PARTIALLY_MATCHED;
                notes.add("Within tolerance — treated as partial match.");
            } else {
                lineStatus = MatchLineResult.LineMatchStatus.MATCHED;
                notes.add(String.format(
                        "✓ Fully matched within tolerance. GRN accepted: %.3f, Effective invoice qty: %.3f",
                        grnAccQty, effectiveInvoiceQty));
                // Add context note for partial delivery scenario
                if (previouslyAccepted.compareTo(BigDecimal.ZERO) > 0) {
                    notes.add(String.format(
                            "(Partial delivery batch: %.3f previously received across other GRNs, " +
                            "%.3f in this GRN batch, %.3f total PO ordered)",
                            previouslyAccepted, grnAccQty, poQty));
                }
            }

            result.setLineMatchStatus(lineStatus);
            result.setWithinTolerance(lineTolerance);
            result.setMismatchNotes(String.join(" | ", notes));

            log.debug("  Line {}: {} | GRN accepted={} vs EffectiveInv={} (fullInv={}) | " +
                      "PO Rate ₹{} vs Inv Price ₹{} | remainingPO={} → {}",
                    poLine.getSlNo(), poLine.getItemDescription(),
                    grnAccQty, effectiveInvoiceQty, fullInvQty,
                    poRate, invPrice, remainingPoQty, lineStatus);

            results.add(result);
        }

        // ── Check for invoice lines not in PO (unauthorized items) ───────────
        for (InvoiceLineItem invLine : invoiceLines) {
            boolean foundInPO = poLines.stream().anyMatch(p ->
                    (invLine.getPoLineItemId() != null && invLine.getPoLineItemId().equals(p.getId()))
                    || (invLine.getItemOrder() != null && invLine.getItemOrder().equals(p.getSlNo())));
            if (!foundInPO) {
                MatchLineResult orphan = new MatchLineResult();
                orphan.setInvoiceLineItemId(invLine.getId());
                orphan.setItemOrder(invLine.getItemOrder());
                orphan.setItemCode(invLine.getItemCode());
                orphan.setItemDescription(invLine.getItemDescription());
                orphan.setUom(invLine.getUom());
                orphan.setInvoiceQuantity(safeQty(invLine.getQuantity()));
                orphan.setInvoiceUnitPrice(safeAmt(invLine.getUnitPrice()));
                orphan.setInvoiceLineValue(
                        safeQty(invLine.getQuantity()).multiply(safeAmt(invLine.getUnitPrice())));
                orphan.setLineMatchStatus(MatchLineResult.LineMatchStatus.NOT_IN_PO);
                orphan.setWithinTolerance(false);
                orphan.setMismatchNotes(
                        "Invoice line item has no corresponding PO line — unauthorized item.");
                results.add(orphan);
                log.warn("  ⚠️ Unauthorized invoice item: {} (not in PO)", invLine.getItemDescription());
            }
        }

        return results;
    }

    // =========================================================================
    // DTO CONVERSION
    // =========================================================================

    private GRNDto.MatchResponse toResponse(ThreeWayMatch m) {
        List<GRNDto.LineMatchResponse> lineResults = m.getLineResults() == null ? List.of()
                : m.getLineResults().stream().map(r -> GRNDto.LineMatchResponse.builder()
                        .id(r.getId())
                        .poLineItemId(r.getPoLineItemId())
                        .grnLineItemId(r.getGrnLineItemId())
                        .invoiceLineItemId(r.getInvoiceLineItemId())
                        .itemOrder(r.getItemOrder())
                        .itemCode(r.getItemCode())
                        .itemDescription(r.getItemDescription())
                        .uom(r.getUom())
                        .poOrderedQuantity(r.getPoOrderedQuantity())
                        .grnAcceptedQuantity(r.getGrnAcceptedQuantity())
                        .invoiceQuantity(r.getInvoiceQuantity())   // effective qty for this batch
                        .quantityVariance(r.getQuantityVariance())
                        .quantityVariancePct(r.getQuantityVariancePct())
                        .poUnitRate(r.getPoUnitRate())
                        .invoiceUnitPrice(r.getInvoiceUnitPrice())
                        .priceVariance(r.getPriceVariance())
                        .priceVariancePct(r.getPriceVariancePct())
                        .poLineValue(r.getPoLineValue())
                        .grnAcceptedValue(r.getGrnAcceptedValue())
                        .invoiceLineValue(r.getInvoiceLineValue())
                        .valueVariance(r.getValueVariance())
                        .lineMatchStatus(r.getLineMatchStatus() != null ? r.getLineMatchStatus().name() : null)
                        .withinTolerance(r.getWithinTolerance())
                        .mismatchNotes(r.getMismatchNotes())
                        .build())
                .collect(Collectors.toList());

        List<String> availableResolutions = buildAvailableResolutions(m.getMatchStatus());

        return GRNDto.MatchResponse.builder()
                .id(m.getId())
                .matchVersion(m.getMatchVersion())
                .purchaseOrderId(m.getPurchaseOrderId())
                .poNumber(m.getPoNumber())
                .grnId(m.getGrnId())
                .grnNumber(m.getGrnNumber())
                .invoiceId(m.getInvoiceId())
                .invoiceNumber(m.getInvoiceNumber())
                .tolerancePercentage(m.getTolerancePercentage())
                .matchStatus(m.getMatchStatus().name())
                .matchSummary(m.getMatchSummary())
                .poTotalValue(m.getPoTotalValue())
                .grnAcceptedValue(m.getGrnAcceptedValue())
                .invoiceTotalValue(m.getInvoiceTotalValue())   // effective total for this batch
                .varianceAmount(m.getVarianceAmount())
                .variancePercentage(m.getVariancePercentage())
                .hasQuantityMismatch(m.getHasQuantityMismatch())
                .hasPriceMismatch(m.getHasPriceMismatch())
                .hasItemMismatch(m.getHasItemMismatch())
                .hasExcessDelivery(m.getHasExcessDelivery())
                .totalMatchedLines(m.getTotalMatchedLines())
                .totalMismatchLines(m.getTotalMismatchLines())
                .resolution(m.getResolution() != null ? m.getResolution().name() : null)
                .resolutionRemarks(m.getResolutionRemarks())
                .resolvedByName(m.getResolvedByName())
                .resolvedAt(m.getResolvedAt())
                .approvedPaymentAmount(m.getApprovedPaymentAmount())
                .performedByName(m.getPerformedByName())
                .performedAt(m.getPerformedAt())
                .updatedAt(m.getUpdatedAt())
                .lineResults(lineResults)
                .availableResolutions(availableResolutions)
                .build();
    }

    private List<String> buildAvailableResolutions(ThreeWayMatchStatus status) {
        if (status == ThreeWayMatchStatus.MATCHED || status == ThreeWayMatchStatus.OVERRIDDEN_APPROVED)
            return List.of();
        return switch (status) {
            case QUANTITY_MISMATCH -> List.of(
                    "ACCEPTED_WITH_VARIANCE", "REVISED_INVOICE_REQUESTED",
                    "PARTIAL_PAYMENT_APPROVED", "CREDIT_NOTE_REQUESTED",
                    "DEBIT_NOTE_RAISED", "DISPUTED");
            case PRICE_MISMATCH -> List.of(
                    "ACCEPTED_WITH_VARIANCE", "REVISED_INVOICE_REQUESTED",
                    "CREDIT_NOTE_REQUESTED", "DEBIT_NOTE_RAISED", "DISPUTED");
            case ITEM_MISMATCH -> List.of(
                    "REVISED_INVOICE_REQUESTED", "CANCELLED", "DISPUTED");
            case EXCESS_DELIVERY -> List.of(
                    "ACCEPTED_WITH_VARIANCE", "PARTIAL_PAYMENT_APPROVED",
                    "REVISED_INVOICE_REQUESTED");
            default -> List.of(
                    "ACCEPTED_WITH_VARIANCE", "REVISED_INVOICE_REQUESTED",
                    "PARTIAL_PAYMENT_APPROVED", "CREDIT_NOTE_REQUESTED",
                    "DEBIT_NOTE_RAISED", "DISPUTED", "CANCELLED");
        };
    }

    private String buildSummary(ThreeWayMatchStatus status, int matched, int mismatched,
                                 BigDecimal variance, BigDecimal tolerance) {
        return switch (status) {
            case MATCHED -> String.format(
                    "✅ Full match: all %d lines matched within %.1f%% tolerance.", matched + mismatched, tolerance);
            case QUANTITY_MISMATCH -> String.format(
                    "⚠️ Quantity mismatch on %d line(s). %d line(s) matched. Effective batch variance: ₹%.2f.",
                    mismatched, matched, variance);
            case PRICE_MISMATCH -> String.format(
                    "⚠️ Price variance on %d line(s) exceeding %.1f%% tolerance. Effective batch variance: ₹%.2f.",
                    mismatched, tolerance, variance);
            case ITEM_MISMATCH -> String.format(
                    "🚫 Item mismatch: %d unauthorized or missing line(s) found.", mismatched);
            case EXCESS_DELIVERY -> String.format(
                    "📦 Excess delivery on %d line(s). Goods received exceed remaining PO quantity.", mismatched);
            case PARTIAL_MATCH -> String.format(
                    "⚠️ Partial match: %d line(s) matched, %d line(s) have variance. Effective batch variance: ₹%.2f.",
                    matched, mismatched, variance);
            default -> "Match performed. Review line-level results for details.";
        };
    }

    private BigDecimal safeQty(BigDecimal v) { return v != null ? v : BigDecimal.ZERO; }
    private BigDecimal safeAmt(BigDecimal v) { return v != null ? v : BigDecimal.ZERO; }
}