package com.itti.leadcapturing.service;

import com.itti.leadcapturing.dto.GRNDto;
import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GRNService {

    private final GRNRepository           grnRepository;
    private final PurchaseOrderRepository poRepository;
    private final InvoiceRepository       invoiceRepository;
    private final GRNLineItemRepository   grnLineItemRepository;

    // =========================================================================
    // PHASE 1 — CREATE GRN (receiver fills orderedQty + receivedQty)
    //
    // PARTIAL DELIVERY NOTE:
    // Multiple GRNs can be created for the same PO and same Invoice.
    // Each GRN gets a unique GRN number and represents one delivery batch.
    //
    // Example:
    //   PO-001 (10 units), Invoice INV-100
    //   Batch 1: Create GRN-001 with receivedQty=8  (8 arrived today)
    //   Batch 2: Create GRN-002 with receivedQty=2  (remaining 2 arrived next month)
    //
    // Both GRNs link to the same PO and Invoice.
    // Each goes through its own QA → 3-Way Match flow independently.
    // The 3-Way Match engine automatically tracks previously accepted quantities
    // so the second batch is matched against the remaining 2, not the full 10.
    // =========================================================================

    @Transactional
    public GRNDto.Response createGRN(GRNDto.CreateRequest request) {
        log.info("=".repeat(60));
        log.info("📦 [CREATE GRN] PO: {}, by user: {}", request.getPurchaseOrderId(), request.getReceivedByUserId());

        PurchaseOrder po = poRepository.findByIdWithDetails(request.getPurchaseOrderId())
                .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + request.getPurchaseOrderId()));

        validatePOStatusForGRN(po);

        if (request.getLineItems() == null || request.getLineItems().isEmpty()) {
            throw new RuntimeException("GRN must have at least one line item.");
        }

        GRN grn = new GRN();
        grn.setGrnNumber(generateGRNNumber(po));
        grn.setReceivedDate(request.getReceivedDate() != null ? request.getReceivedDate() : LocalDateTime.now());
        grn.setPurchaseOrderId(po.getId());
        grn.setPoNumber(po.getPoNumber());
        grn.setSupplierId(po.getSupplier().getId());
        grn.setSupplierName(po.getSupplier().getCompanyName());
        grn.setReceivedByUserId(request.getReceivedByUserId());
        grn.setReceivedByName(request.getReceivedByName());
        grn.setDeliveryChallanNumber(request.getDeliveryChallanNumber());
        grn.setLrNumber(request.getLrNumber());
        grn.setTransporterName(request.getTransporterName());
        grn.setVehicleNumber(request.getVehicleNumber());
        grn.setDeliveryLocation(request.getDeliveryLocation());
        grn.setRemarks(request.getRemarks());
        grn.setInternalNotes(request.getInternalNotes());
        grn.setStatus(GRNStatus.DRAFT);
        grn.setTotalOrderedValue(po.getGrandTotal());

        // Link invoice if provided — same invoice can be linked to multiple GRNs
        // (one invoice, multiple delivery batches)
        if (request.getInvoiceId() != null) {
            invoiceRepository.findById(request.getInvoiceId()).ifPresent(inv -> {
                grn.setInvoiceId(inv.getId());
                grn.setInvoiceNumber(inv.getInvoiceNumber());
            });
        }

        // Build Phase-1 line items (only orderedQty + receivedQty)
        for (GRNDto.LineItemCreateRequest liReq : request.getLineItems()) {
            GRNLineItem li = buildPhase1LineItem(liReq);
            grn.addLineItem(li);
        }

        grn.recalculateTotals();

        GRN saved = grnRepository.save(grn);

        // Log partial delivery context if other GRNs exist for same PO+Invoice
        if (request.getInvoiceId() != null) {
            long batchCount = grnRepository.countByPurchaseOrderIdAndInvoiceId(
                    po.getId(), request.getInvoiceId());
            log.info("  ✅ GRN created: {} | Status: DRAFT | Lines: {} | Delivery batch #{} for PO+Invoice",
                    saved.getGrnNumber(), saved.getLineItems().size(), batchCount);
        } else {
            log.info("  ✅ GRN created: {} | Status: DRAFT | Lines: {}",
                    saved.getGrnNumber(), saved.getLineItems().size());
        }
        log.info("=".repeat(60));
        return toResponse(saved);
    }

    // =========================================================================
    // PHASE 1 — UPDATE GRN (DRAFT only — receiver corrects quantities)
    // =========================================================================

    @Transactional
    public GRNDto.Response updateGRN(Long grnId, GRNDto.CreateRequest request) {
        GRN grn = grnRepository.findByIdWithLineItems(grnId)
                .orElseThrow(() -> new RuntimeException("GRN not found: " + grnId));

        if (grn.getStatus() != GRNStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT GRNs can be updated. Current: " + grn.getStatus());
        }

        grn.setReceivedDate(request.getReceivedDate());
        grn.setDeliveryChallanNumber(request.getDeliveryChallanNumber());
        grn.setLrNumber(request.getLrNumber());
        grn.setTransporterName(request.getTransporterName());
        grn.setVehicleNumber(request.getVehicleNumber());
        grn.setDeliveryLocation(request.getDeliveryLocation());
        grn.setReceivedByName(request.getReceivedByName());
        grn.setRemarks(request.getRemarks());
        grn.setInternalNotes(request.getInternalNotes());

        if (request.getInvoiceId() != null) {
            invoiceRepository.findById(request.getInvoiceId()).ifPresent(inv -> {
                grn.setInvoiceId(inv.getId());
                grn.setInvoiceNumber(inv.getInvoiceNumber());
            });
        }

        if (request.getLineItems() != null && !request.getLineItems().isEmpty()) {
            grn.getLineItems().clear();
            for (GRNDto.LineItemCreateRequest liReq : request.getLineItems()) {
                grn.addLineItem(buildPhase1LineItem(liReq));
            }
        }

        grn.recalculateTotals();
        GRN saved = grnRepository.save(grn);
        log.info("✏️ GRN updated: {}", saved.getGrnNumber());
        return toResponse(saved);
    }

    // =========================================================================
    // PHASE 1 — SUBMIT GRN (DRAFT → SUBMITTED)
    // =========================================================================

    @Transactional
    public GRNDto.Response submitGRN(Long grnId) {
        GRN grn = grnRepository.findByIdWithLineItems(grnId)
                .orElseThrow(() -> new RuntimeException("GRN not found: " + grnId));

        if (grn.getStatus() != GRNStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT GRNs can be submitted. Current: " + grn.getStatus());
        }
        if (grn.getLineItems() == null || grn.getLineItems().isEmpty()) {
            throw new RuntimeException("GRN has no line items.");
        }
        for (GRNLineItem li : grn.getLineItems()) {
            if (li.getReceivedQuantity() == null) {
                throw new RuntimeException(
                        "Received quantity is missing for item: " + li.getItemDescription());
            }
        }

        grn.setStatus(GRNStatus.SUBMITTED);
        grnRepository.save(grn);
        log.info("📋 GRN {} submitted for QA review", grn.getGrnNumber());
        return toResponse(grn);
    }

    // =========================================================================
    // PHASE 2 — START QA REVIEW (SUBMITTED → QA_REVIEW)
    // =========================================================================

    @Transactional
    public GRNDto.Response startQAReview(Long grnId, String inspectedByName) {
        GRN grn = grnRepository.findByIdWithLineItems(grnId)
                .orElseThrow(() -> new RuntimeException("GRN not found: " + grnId));

        if (grn.getStatus() != GRNStatus.SUBMITTED) {
            throw new RuntimeException(
                    "QA Review can only start on SUBMITTED GRNs. Current: " + grn.getStatus());
        }

        grn.setStatus(GRNStatus.QA_REVIEW);
        if (inspectedByName != null) grn.setInspectedByName(inspectedByName);
        grnRepository.save(grn);
        log.info("🔬 GRN {} QA Review started by {}", grn.getGrnNumber(), inspectedByName);
        return toResponse(grn);
    }

    // =========================================================================
    // PHASE 2 — SUBMIT QA REVIEW
    // =========================================================================

    @Transactional
    public GRNDto.Response submitQAReview(GRNDto.QAReviewRequest request) {
        log.info("=".repeat(60));
        log.info("🔬 [QA REVIEW SUBMIT] GRN: {}", request.getGrnId());

        GRN grn = grnRepository.findByIdWithLineItems(request.getGrnId())
                .orElseThrow(() -> new RuntimeException("GRN not found: " + request.getGrnId()));

        if (grn.getStatus() != GRNStatus.QA_REVIEW && grn.getStatus() != GRNStatus.SUBMITTED) {
            throw new RuntimeException(
                    "QA Review can only be submitted for GRNs in SUBMITTED or QA_REVIEW status. Current: "
                            + grn.getStatus());
        }

        if (request.getInspectedByName() != null) {
            grn.setInspectedByName(request.getInspectedByName());
        }
        if (request.getQaOverallRemarks() != null) {
            grn.setInternalNotes(request.getQaOverallRemarks());
        }

        if (grn.getStatus() == GRNStatus.SUBMITTED) {
            grn.setStatus(GRNStatus.QA_REVIEW);
        }

        if (request.getLineItems() != null) {
            for (GRNDto.QALineItemRequest qaLine : request.getLineItems()) {
                GRNLineItem li = grn.getLineItems().stream()
                        .filter(l -> l.getId().equals(qaLine.getGrnLineItemId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException(
                                "GRN line item not found: " + qaLine.getGrnLineItemId()));

                BigDecimal rejected = qaLine.getRejectedQuantity() != null
                        ? qaLine.getRejectedQuantity()
                        : (qaLine.getDefectiveQuantity() != null ? qaLine.getDefectiveQuantity() : BigDecimal.ZERO);

                if (li.getReceivedQuantity() != null
                        && rejected.compareTo(li.getReceivedQuantity()) > 0) {
                    throw new RuntimeException(
                            "Rejected quantity (" + rejected + ") cannot exceed received quantity ("
                                    + li.getReceivedQuantity() + ") for item: " + li.getItemDescription());
                }

                if (rejected.compareTo(BigDecimal.ZERO) > 0
                        && (qaLine.getQaRemarks() == null || qaLine.getQaRemarks().isBlank())) {
                    throw new RuntimeException(
                            "QA remarks are mandatory when rejecting items. Item: " + li.getItemDescription());
                }

                li.setDefectiveQuantity(qaLine.getDefectiveQuantity() != null
                        ? qaLine.getDefectiveQuantity() : BigDecimal.ZERO);
                li.setRejectedQuantity(rejected);
                li.setQaRemarks(qaLine.getQaRemarks());

                if (qaLine.getItemCondition() != null && !qaLine.getItemCondition().isBlank()) {
                    try {
                        li.setItemCondition(GRNLineItem.ItemCondition.valueOf(qaLine.getItemCondition()));
                    } catch (IllegalArgumentException ignored) {}
                }

                li.recalculateAfterQA();

                log.info("  Line {}: received={} defective={} rejected={} accepted={} | {}",
                        li.getItemDescription(),
                        li.getReceivedQuantity(), li.getDefectiveQuantity(),
                        li.getRejectedQuantity(), li.getAcceptedQuantity(),
                        li.getItemCondition());
            }
        }

        grn.recalculateTotals();
        GRN saved = grnRepository.save(grn);
        log.info("  ✅ QA data saved for GRN: {} | Total accepted value: ₹{}",
                saved.getGrnNumber(), saved.getTotalReceivedValue());
        log.info("=".repeat(60));
        return toResponse(saved);
    }

    // =========================================================================
    // PHASE 2 — APPROVE GRN (QA_REVIEW → APPROVED)
    // =========================================================================

    @Transactional
    public GRNDto.Response approveGRN(Long grnId, String approvedByName) {
        GRN grn = grnRepository.findByIdWithLineItems(grnId)
                .orElseThrow(() -> new RuntimeException("GRN not found: " + grnId));

        if (grn.getStatus() != GRNStatus.QA_REVIEW) {
            throw new RuntimeException(
                    "Only GRNs in QA_REVIEW status can be approved. Current: " + grn.getStatus()
                    + ". Please complete QA review first.");
        }

        boolean qaIncomplete = grn.getLineItems().stream()
                .anyMatch(li -> li.getAcceptedQuantity() == null);
        if (qaIncomplete) {
            throw new RuntimeException(
                    "QA review is incomplete. Some line items are missing accepted quantity.");
        }

        grn.setStatus(GRNStatus.APPROVED);
        grn.setApprovedByName(approvedByName);
        grn.setApprovedAt(LocalDateTime.now());
        grnRepository.save(grn);
        log.info("✅ GRN {} approved by {}. Ready for 3-Way Match.", grn.getGrnNumber(), approvedByName);
        return toResponse(grn);
    }

    // =========================================================================
    // CLOSE GRN (APPROVED → CLOSED)
    // =========================================================================

    @Transactional
    public GRNDto.Response closeGRN(Long grnId) {
        GRN grn = grnRepository.findByIdWithLineItems(grnId)
                .orElseThrow(() -> new RuntimeException("GRN not found: " + grnId));

        if (grn.getStatus() != GRNStatus.APPROVED) {
            throw new RuntimeException("Only APPROVED GRNs can be closed. Current: " + grn.getStatus());
        }

        grn.setStatus(GRNStatus.CLOSED);
        grnRepository.save(grn);
        log.info("🔒 GRN {} closed after 3-Way Match.", grn.getGrnNumber());
        return toResponse(grn);
    }

    // =========================================================================
    // CANCEL GRN
    // =========================================================================

    @Transactional
    public GRNDto.Response cancelGRN(Long grnId, String reason) {
        GRN grn = grnRepository.findByIdWithLineItems(grnId)
                .orElseThrow(() -> new RuntimeException("GRN not found: " + grnId));

        if (grn.getStatus() == GRNStatus.CLOSED) {
            throw new RuntimeException("Cannot cancel a CLOSED GRN.");
        }
        if (grn.getStatus() == GRNStatus.APPROVED) {
            throw new RuntimeException(
                    "Cannot cancel an APPROVED GRN that may be linked to a 3-Way Match. Contact admin.");
        }

        grn.setStatus(GRNStatus.CANCELLED);
        grn.setInternalNotes((grn.getInternalNotes() != null ? grn.getInternalNotes() + " | " : "")
                + "Cancelled: " + reason);
        grnRepository.save(grn);
        log.info("❌ GRN {} cancelled: {}", grn.getGrnNumber(), reason);
        return toResponse(grn);
    }

    // =========================================================================
    // LINK INVOICE TO GRN
    // =========================================================================

    @Transactional
    public GRNDto.Response linkInvoice(Long grnId, Long invoiceId) {
        GRN grn = grnRepository.findByIdWithLineItems(grnId)
                .orElseThrow(() -> new RuntimeException("GRN not found: " + grnId));

        if (grn.getStatus() == GRNStatus.CANCELLED) {
            throw new RuntimeException("Cannot link invoice to a CANCELLED GRN.");
        }

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

        if (!invoice.getPoId().equals(grn.getPurchaseOrderId())) {
            throw new RuntimeException(
                    "Invoice PO (" + invoice.getPoNumber() + ") does not match GRN PO (" + grn.getPoNumber() + ").");
        }

        grn.setInvoiceId(invoice.getId());
        grn.setInvoiceNumber(invoice.getInvoiceNumber());
        grnRepository.save(grn);
        log.info("🔗 Invoice {} linked to GRN {}", invoice.getInvoiceNumber(), grn.getGrnNumber());
        return toResponse(grn);
    }

    // =========================================================================
    // REVERT TO DRAFT
    // =========================================================================

    @Transactional
    public GRNDto.Response revertToDraft(Long grnId) {
        GRN grn = grnRepository.findByIdWithLineItems(grnId)
                .orElseThrow(() -> new RuntimeException("GRN not found: " + grnId));

        Set<GRNStatus> revertableStatuses = Set.of(
                GRNStatus.SUBMITTED,
                GRNStatus.QA_REVIEW,
                GRNStatus.APPROVED
        );

        if (!revertableStatuses.contains(grn.getStatus())) {
            throw new RuntimeException(
                    "Cannot revert GRN to draft. Current status: " + grn.getStatus());
        }
        grn.setStatus(GRNStatus.DRAFT);
        grnRepository.save(grn);
        log.info("↩️ GRN {} reverted to DRAFT", grn.getGrnNumber());
        return toResponse(grn);
    }

    // =========================================================================
    // READ METHODS
    // =========================================================================

    @Transactional(readOnly = true)
    public GRNDto.Response getGRNById(Long grnId) {
        GRN grn = grnRepository.findByIdWithLineItems(grnId)
                .orElseThrow(() -> new RuntimeException("GRN not found: " + grnId));
        return toResponse(grn);
    }

    @Transactional(readOnly = true)
    public List<GRNDto.Response> getApprovedGRNsByPO(Long poId) {
        return grnRepository.findApprovedGrnsForPo(poId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GRNDto.Response> getAllGRNsByPO(Long poId) {
        return grnRepository.findByPurchaseOrderId(poId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GRNDto.Response> getGRNsByUser(Long userId) {
        return grnRepository.findByReceivedByUserId(userId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GRNDto.Response> getGRNsByInvoice(Long invoiceId) {
        return grnRepository.findByInvoiceId(invoiceId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * Get all GRN batches for a specific PO + Invoice combination.
     *
     * Returns batches in chronological order (oldest first).
     * Used by the frontend to show:
     *   - "Batch 1: GRN-001 — 8/10 units received (APPROVED)"
     *   - "Batch 2: GRN-002 — 2/10 units received (DRAFT)"
     *
     * This is the key endpoint for tracking partial deliveries.
     */
    @Transactional(readOnly = true)
    public List<GRNDto.Response> getGRNsByPoAndInvoice(Long poId, Long invoiceId) {
        return grnRepository.findByPurchaseOrderIdAndInvoiceId(poId, invoiceId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // =========================================================================
    // PRIVATE HELPERS
    // =========================================================================

    private GRNLineItem buildPhase1LineItem(GRNDto.LineItemCreateRequest req) {
        GRNLineItem li = new GRNLineItem();
        li.setPoLineItemId(req.getPoLineItemId());
        li.setItemOrder(req.getItemOrder());
        li.setItemCode(req.getItemCode());
        li.setItemDescription(req.getItemDescription());
        li.setSpecifications(req.getSpecifications());
        li.setBrandMakeModel(req.getBrandMakeModel());
        li.setUom(req.getUom());
        li.setOrderedQuantity(req.getOrderedQuantity());
        li.setPoUnitRate(req.getPoUnitRate());
        li.setRemarks(req.getRemarks());

        if (req.getOrderedQuantity() != null && req.getPoUnitRate() != null) {
            li.setPoLineTotal(req.getOrderedQuantity().multiply(req.getPoUnitRate()));
        }

        li.setReceivedQuantity(req.getReceivedQuantity());
        li.initialiseAfterCreation();

        return li;
    }

    private void validatePOStatusForGRN(PurchaseOrder po) {
        if (po.getStatus() != POStatus.APPROVED
                && po.getStatus() != POStatus.SENT_TO_SUPPLIER
                && po.getStatus() != POStatus.ACKNOWLEDGED
                && po.getStatus() != POStatus.IN_PROGRESS) {
            throw new RuntimeException(
                    "GRN can only be created for POs in APPROVED/SENT_TO_SUPPLIER/IN_PROGRESS status. Current: "
                            + po.getStatus());
        }
    }

    private String generateGRNNumber(PurchaseOrder po) {
        String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy"));
        String companyCode = po.getBuyer().getCompanyName()
                .substring(0, Math.min(4, po.getBuyer().getCompanyName().length()))
                .toUpperCase().replaceAll("\\s+", "");
        long count = grnRepository.count();
        return companyCode + "/GRN/" + year + "/" + String.format("%03d", count + 1);
    }

    private List<String> buildAvailableActions(GRNStatus status) {
        return switch (status) {
            case DRAFT      -> List.of("UPDATE", "SUBMIT", "CANCEL");
            case SUBMITTED  -> List.of("START_QA_REVIEW", "CANCEL");
            case QA_REVIEW  -> List.of("SUBMIT_QA_REVIEW", "APPROVE");
            case APPROVED   -> List.of("PERFORM_3WAY_MATCH");
            case CLOSED     -> List.of();
            case CANCELLED  -> List.of();
        };
    }

    // =========================================================================
    // DTO CONVERSION
    // =========================================================================

    GRNDto.Response toResponse(GRN grn) {
        List<GRNDto.LineItemResponse> lineItems = grn.getLineItems() == null ? List.of()
                : grn.getLineItems().stream().map(li -> {
                    BigDecimal shortQty = (li.getOrderedQuantity() != null && li.getReceivedQuantity() != null)
                            ? li.getOrderedQuantity().subtract(li.getReceivedQuantity()).max(BigDecimal.ZERO)
                            : null;
                    BigDecimal excessQty = (li.getOrderedQuantity() != null && li.getReceivedQuantity() != null)
                            ? li.getReceivedQuantity().subtract(li.getOrderedQuantity()).max(BigDecimal.ZERO)
                            : null;

                    return GRNDto.LineItemResponse.builder()
                            .id(li.getId())
                            .poLineItemId(li.getPoLineItemId())
                            .itemOrder(li.getItemOrder())
                            .itemCode(li.getItemCode())
                            .itemDescription(li.getItemDescription())
                            .specifications(li.getSpecifications())
                            .brandMakeModel(li.getBrandMakeModel())
                            .uom(li.getUom())
                            .orderedQuantity(li.getOrderedQuantity())
                            .poUnitRate(li.getPoUnitRate())
                            .poLineTotal(li.getPoLineTotal())
                            .receivedQuantity(li.getReceivedQuantity())
                            .defectiveQuantity(li.getDefectiveQuantity())
                            .rejectedQuantity(li.getRejectedQuantity())
                            .acceptedQuantity(li.getAcceptedQuantity())
                            .qaRemarks(li.getQaRemarks())
                            .acceptedValue(li.getAcceptedValue())
                            .rejectedValue(li.getRejectedValue())
                            .shortDeliveryQuantity(shortQty)
                            .excessQuantity(excessQty)
                            .itemCondition(li.getItemCondition() != null ? li.getItemCondition().name() : "GOOD")
                            .remarks(li.getRemarks())
                            .build();
                }).collect(Collectors.toList());

        boolean hasRejections = grn.getLineItems() != null && grn.getLineItems().stream()
                .anyMatch(li -> li.getRejectedQuantity() != null
                        && li.getRejectedQuantity().compareTo(BigDecimal.ZERO) > 0);

        boolean isFullyReceived = grn.getLineItems() != null && grn.getLineItems().stream()
                .allMatch(li -> li.getOrderedQuantity() != null && li.getReceivedQuantity() != null
                        && li.getReceivedQuantity().compareTo(li.getOrderedQuantity()) >= 0);

        boolean qaCompleted = grn.getStatus() == GRNStatus.QA_REVIEW
                || grn.getStatus() == GRNStatus.APPROVED
                || grn.getStatus() == GRNStatus.CLOSED;

        return GRNDto.Response.builder()
                .id(grn.getId())
                .grnNumber(grn.getGrnNumber())
                .receivedDate(grn.getReceivedDate())
                .purchaseOrderId(grn.getPurchaseOrderId())
                .poNumber(grn.getPoNumber())
                .invoiceId(grn.getInvoiceId())
                .invoiceNumber(grn.getInvoiceNumber())
                .supplierId(grn.getSupplierId())
                .supplierName(grn.getSupplierName())
                .deliveryChallanNumber(grn.getDeliveryChallanNumber())
                .lrNumber(grn.getLrNumber())
                .transporterName(grn.getTransporterName())
                .vehicleNumber(grn.getVehicleNumber())
                .deliveryLocation(grn.getDeliveryLocation())
                .receivedByUserId(grn.getReceivedByUserId())
                .receivedByName(grn.getReceivedByName())
                .inspectedByName(grn.getInspectedByName())
                .qaOverallRemarks(grn.getInternalNotes())
                .status(grn.getStatus().name())
                .approvedByName(grn.getApprovedByName())
                .approvedAt(grn.getApprovedAt())
                .remarks(grn.getRemarks())
                .internalNotes(grn.getInternalNotes())
                .totalOrderedValue(grn.getTotalOrderedValue())
                .totalReceivedValue(grn.getTotalReceivedValue())
                .totalRejectedValue(grn.getTotalRejectedValue())
                .hasRejections(hasRejections)
                .isFullyReceived(isFullyReceived)
                .qaCompleted(qaCompleted)
                .availableActions(buildAvailableActions(grn.getStatus()))
                .lineItems(lineItems)
                .createdAt(grn.getCreatedAt())
                .updatedAt(grn.getUpdatedAt())
                .build();
    }
}