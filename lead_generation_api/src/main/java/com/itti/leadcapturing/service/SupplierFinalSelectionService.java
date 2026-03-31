// package com.itti.leadcapturing.service;

// import com.itti.leadcapturing.dto.SupplierFinalSelectionDTO;
// import com.itti.leadcapturing.model.*;
// import com.itti.leadcapturing.repo.*;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.Optional;

// /**
//  * ✅ Service: Supplier Final Selection
//  *
//  * Handles the step where the RFQ creator picks the supplier to proceed with.
//  * Two cases:
//  *  1. System-recommended supplier → remarks optional
//  *  2. Non-recommended supplier    → justification MANDATORY
//  */
// @Service
// public class SupplierFinalSelectionService {

//     private static final Logger logger = LoggerFactory.getLogger(SupplierFinalSelectionService.class);

//     @Autowired
//     private SupplierFinalSelectionRepository selectionRepository;

//     @Autowired
//     private RFQRepository rfqRepository;

//     @Autowired
//     private SupplierRepository supplierRepository;

//     @Autowired
//     private SupplierRecommendationRepository recommendationRepository;

//     // ===================== SELECT SUPPLIER =====================

//     @Transactional
//     public SupplierFinalSelectionDTO.Response selectSupplier(SupplierFinalSelectionDTO.Request request) {
//         logger.info("=".repeat(70));
//         logger.info("🎯 [SELECT SUPPLIER] RFQ: {}, Supplier: {}", request.getRfqId(), request.getSupplierId());

//         // Validate RFQ
//         RFQ rfq = rfqRepository.findById(request.getRfqId())
//                 .orElseThrow(() -> new RuntimeException("RFQ not found: " + request.getRfqId()));

//         // Validate Supplier
//         Supplier supplier = supplierRepository.findById(request.getSupplierId())
//                 .orElseThrow(() -> new RuntimeException("Supplier not found: " + request.getSupplierId()));

//         // ✅ RULE: If NOT system-recommended, justification is MANDATORY
//         boolean isSystemRec = Boolean.TRUE.equals(request.getIsSystemRecommended());
//         if (!isSystemRec) {
//             if (request.getJustification() == null || request.getJustification().trim().isEmpty()) {
//                 throw new RuntimeException(
//                     "Justification is MANDATORY when selecting a supplier that is not system-recommended. " +
//                     "Please explain why you are choosing this supplier over the system recommendation."
//                 );
//             }
//             logger.info("  📝 Non-recommended supplier selected. Justification provided.");
//         } else {
//             logger.info("  ✅ System-recommended supplier selected.");
//         }

//         // Look up system recommendation data for this supplier
//         Optional<SupplierRecommendation> recOpt = recommendationRepository
//                 .findByRfqIdAndSupplierId(request.getRfqId(), request.getSupplierId());

//         // If an existing selection exists for this RFQ, update it (only one supplier selected per RFQ)
//         SupplierFinalSelection selection = selectionRepository.findByRfqId(request.getRfqId())
//                 .orElse(new SupplierFinalSelection());

//         selection.setRfq(rfq);
//         selection.setSupplier(supplier);
//         selection.setIsSystemRecommended(isSystemRec);
//         selection.setJustification(isSystemRec ? null : request.getJustification().trim());
//         selection.setRemarks(request.getRemarks());
//         selection.setSelectedByUserId(request.getSelectedByUserId());
//         selection.setSelectedByUserName(request.getSelectedByUserName());
//         selection.setSelectionStatus(SelectionStatus.SELECTED);

//         recOpt.ifPresent(rec -> {
//             selection.setSystemRecommendedRank(rec.getRank());
//             selection.setSystemFinalScore(rec.getFinalScore());
//         });

//         SupplierFinalSelection saved = selectionRepository.save(selection);

//         logger.info("  ✅ Supplier selected: {} ({})",
//                 supplier.getCompanyName(),
//                 isSystemRec ? "System Recommended" : "Manual Override");
//         logger.info("=".repeat(70));

//         return toResponse(saved);
//     }

//     // ===================== GET SELECTION =====================

//     @Transactional(readOnly = true)
//     public SupplierFinalSelectionDTO.Response getSelectionByRfqId(Long rfqId) {
//         SupplierFinalSelection selection = selectionRepository.findByRfqId(rfqId)
//                 .orElseThrow(() -> new RuntimeException("No supplier selection found for RFQ: " + rfqId));
//         return toResponse(selection);
//     }

//     @Transactional(readOnly = true)
//     public boolean hasSelectionForRfq(Long rfqId) {
//         return Boolean.TRUE.equals(selectionRepository.existsByRfqId(rfqId));
//     }

//     // ===================== GET SYSTEM RECOMMENDATION STATUS =====================

//     /**
//      * Returns the system recommended supplier(s) for highlighting in the UI.
//      * The #1 ranked supplier is highlighted as system recommended.
//      */
//     @Transactional(readOnly = true)
//     public java.util.Map<String, Object> getRecommendationStatus(Long rfqId) {
//         java.util.List<SupplierRecommendation> recommendations = recommendationRepository
//                 .findByRfqIdOrderByRank(rfqId);

//         java.util.Map<String, Object> status = new java.util.HashMap<>();
//         status.put("hasRecommendations", !recommendations.isEmpty());

//         if (!recommendations.isEmpty()) {
//             SupplierRecommendation topRec = recommendations.get(0);
//             status.put("systemRecommendedSupplierId", topRec.getSupplier().getId());
//             status.put("systemRecommendedSupplierName", topRec.getSupplier().getCompanyName());
//             status.put("systemRecommendedRank", 1);
//             status.put("systemRecommendedScore", topRec.getFinalScore());
//         }

//         // Include all rankings for highlighting
//         java.util.List<java.util.Map<String, Object>> rankingList = recommendations.stream()
//                 .map(rec -> {
//                     java.util.Map<String, Object> r = new java.util.HashMap<>();
//                     r.put("supplierId", rec.getSupplier().getId());
//                     r.put("supplierName", rec.getSupplier().getCompanyName());
//                     r.put("rank", rec.getRank());
//                     r.put("finalScore", rec.getFinalScore());
//                     r.put("isRecommended", rec.getIsRecommended());
//                     r.put("isTopRecommendation", rec.getRank() == 1);
//                     return r;
//                 })
//                 .collect(java.util.stream.Collectors.toList());

//         status.put("rankings", rankingList);

//         // Check if creator has already made a selection
//         selectionRepository.findByRfqId(rfqId).ifPresent(sel -> {
//             java.util.Map<String, Object> selectionInfo = new java.util.HashMap<>();
//             selectionInfo.put("supplierId", sel.getSupplier().getId());
//             selectionInfo.put("supplierName", sel.getSupplier().getCompanyName());
//             selectionInfo.put("isSystemRecommended", sel.getIsSystemRecommended());
//             selectionInfo.put("status", sel.getSelectionStatus().name());
//             selectionInfo.put("selectionId", sel.getId());
//             status.put("existingSelection", selectionInfo);
//         });

//         return status;
//     }

//     // ===================== MAPPER =====================

//     private SupplierFinalSelectionDTO.Response toResponse(SupplierFinalSelection s) {
//         return SupplierFinalSelectionDTO.Response.builder()
//                 .id(s.getId())
//                 .rfqId(s.getRfq().getId())
//                 .rfqNumber(s.getRfq().getRfqNumber())
//                 .rfqTitle(s.getRfq().getRfqTitle())
//                 .supplierId(s.getSupplier().getId())
//                 .supplierName(s.getSupplier().getCompanyName())
//                 .supplierEmail(s.getSupplier().getContactPersonEmail())
//                 .supplierPhone(s.getSupplier().getContactPersonPhone())
//                 .isSystemRecommended(s.getIsSystemRecommended())
//                 .systemRecommendedRank(s.getSystemRecommendedRank())
//                 .systemFinalScore(s.getSystemFinalScore())
//                 .justification(s.getJustification())
//                 .remarks(s.getRemarks())
//                 .selectedByUserId(s.getSelectedByUserId())
//                 .selectedByUserName(s.getSelectedByUserName())
//                 .selectionStatus(s.getSelectionStatus().name())
//                 .selectedAt(s.getSelectedAt())
//                 .poNegotiationId(s.getPoNegotiationId())
//                 .purchaseOrderId(s.getPurchaseOrderId())
//                 .createdAt(s.getCreatedAt())
//                 .updatedAt(s.getUpdatedAt())
//                 .build();
//     }
// }

package com.itti.leadcapturing.service;

import com.itti.leadcapturing.dto.SupplierFinalSelectionDTO;
import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * ✅ FIXED: getRecommendationStatus() now includes `totalQuotedAmount` in each ranking entry.
 *
 * ROOT CAUSE of ₹0.00 bug:
 * The supplier-selection component uses `supplier.totalQuotedAmount` in its template.
 * The old code only returned `supplierId`, `supplierName`, `rank`, `finalScore`,
 * `isRecommended`, `isTopRecommendation` — but NOT `totalQuotedAmount`.
 * SupplierRecommendation has `quoteAmount` which is the total quote — that is now
 * included as `totalQuotedAmount` in the response.
 *
 * Also added `contactEmail`, `manualScore`, `quoteWeightage` to match the
 * SupplierRanking interface in the Angular component.
 */
@Service
public class SupplierFinalSelectionService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierFinalSelectionService.class);

    @Autowired
    private SupplierFinalSelectionRepository selectionRepository;

    @Autowired
    private RFQRepository rfqRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierRecommendationRepository recommendationRepository;

    // ===================== SELECT SUPPLIER =====================

    @Transactional
    public SupplierFinalSelectionDTO.Response selectSupplier(SupplierFinalSelectionDTO.Request request) {
        logger.info("=".repeat(70));
        logger.info("🎯 [SELECT SUPPLIER] RFQ: {}, Supplier: {}", request.getRfqId(), request.getSupplierId());

        RFQ rfq = rfqRepository.findById(request.getRfqId())
                .orElseThrow(() -> new RuntimeException("RFQ not found: " + request.getRfqId()));

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found: " + request.getSupplierId()));

        boolean isSystemRec = Boolean.TRUE.equals(request.getIsSystemRecommended());
        if (!isSystemRec) {
            if (request.getJustification() == null || request.getJustification().trim().isEmpty()) {
                throw new RuntimeException(
                    "Justification is MANDATORY when selecting a supplier that is not system-recommended. " +
                    "Please explain why you are choosing this supplier over the system recommendation."
                );
            }
            logger.info("  📝 Non-recommended supplier selected. Justification provided.");
        } else {
            logger.info("  ✅ System-recommended supplier selected.");
        }

        Optional<SupplierRecommendation> recOpt = recommendationRepository
                .findByRfqIdAndSupplierId(request.getRfqId(), request.getSupplierId());

        SupplierFinalSelection selection = selectionRepository.findByRfqId(request.getRfqId())
                .orElse(new SupplierFinalSelection());

        selection.setRfq(rfq);
        selection.setSupplier(supplier);
        selection.setIsSystemRecommended(isSystemRec);
        selection.setJustification(isSystemRec ? null : request.getJustification().trim());
        selection.setRemarks(request.getRemarks());
        selection.setSelectedByUserId(request.getSelectedByUserId());
        selection.setSelectedByUserName(request.getSelectedByUserName());
        selection.setSelectionStatus(SelectionStatus.SELECTED);

        recOpt.ifPresent(rec -> {
            selection.setSystemRecommendedRank(rec.getRank());
            selection.setSystemFinalScore(rec.getFinalScore());
        });

        SupplierFinalSelection saved = selectionRepository.save(selection);

        logger.info("  ✅ Supplier selected: {} ({})",
                supplier.getCompanyName(),
                isSystemRec ? "System Recommended" : "Manual Override");
        logger.info("=".repeat(70));

        return toResponse(saved);
    }

    // ===================== GET SELECTION =====================

    @Transactional(readOnly = true)
    public SupplierFinalSelectionDTO.Response getSelectionByRfqId(Long rfqId) {
        SupplierFinalSelection selection = selectionRepository.findByRfqId(rfqId)
                .orElseThrow(() -> new RuntimeException("No supplier selection found for RFQ: " + rfqId));
        return toResponse(selection);
    }

    @Transactional(readOnly = true)
    public boolean hasSelectionForRfq(Long rfqId) {
        return Boolean.TRUE.equals(selectionRepository.existsByRfqId(rfqId));
    }

    // ===================== ✅ FIXED: GET SYSTEM RECOMMENDATION STATUS =====================

    /**
     * ✅ FIXED: Now includes `totalQuotedAmount`, `contactEmail`, `manualScore`,
     * and `quoteWeightage` in each ranking entry so the supplier-selection
     * component can display the Total Quote column correctly.
     *
     * Angular SupplierRanking interface requires:
     *   supplierId, supplierName, contactEmail, rank, finalScore,
     *   totalQuotedAmount, isTopRecommendation, quoteWeightage, manualScore
     */
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getRecommendationStatus(Long rfqId) {
        java.util.List<SupplierRecommendation> recommendations = recommendationRepository
                .findByRfqIdOrderByRank(rfqId);

        java.util.Map<String, Object> status = new java.util.HashMap<>();
        status.put("hasRecommendations", !recommendations.isEmpty());

        if (!recommendations.isEmpty()) {
            SupplierRecommendation topRec = recommendations.get(0);
            status.put("systemRecommendedSupplierId", topRec.getSupplier().getId());
            status.put("systemRecommendedSupplierName", topRec.getSupplier().getCompanyName());
            status.put("systemRecommendedRank", 1);
            status.put("systemRecommendedScore", topRec.getFinalScore());
        }

        // ✅ FIXED: Include all fields the Angular SupplierRanking interface needs
        java.util.List<java.util.Map<String, Object>> rankingList = recommendations.stream()
                .map(rec -> {
                    java.util.Map<String, Object> r = new java.util.HashMap<>();
                    r.put("supplierId", rec.getSupplier().getId());
                    r.put("supplierName", rec.getSupplier().getCompanyName());

                    // ✅ FIX: contactEmail was missing
                    r.put("contactEmail", rec.getSupplier().getContactPersonEmail() != null
                            ? rec.getSupplier().getContactPersonEmail() : "");

                    r.put("rank", rec.getRank());
                    r.put("finalScore", rec.getFinalScore());

                    // ✅ FIX: totalQuotedAmount was missing — this is what caused ₹0.00
                    r.put("totalQuotedAmount", rec.getQuoteAmount() != null
                            ? rec.getQuoteAmount() : BigDecimal.ZERO);

                    r.put("isRecommended", rec.getIsRecommended());
                    r.put("isTopRecommendation", rec.getRank() == 1);

                    // ✅ FIX: quoteWeightage and manualScore were missing
                    // quoteWeightage = priceScore (the auto-calculated price rank bonus)
                    r.put("quoteWeightage", rec.getPriceScore() != null
                            ? rec.getPriceScore() : BigDecimal.ZERO);

                    // manualScore = totalCriteriaScore (the evaluation score)
                    r.put("manualScore", rec.getTotalCriteriaScore() != null
                            ? rec.getTotalCriteriaScore() : BigDecimal.ZERO);

                    return r;
                })
                .collect(java.util.stream.Collectors.toList());

        status.put("rankings", rankingList);

        // Check if creator has already made a selection
        selectionRepository.findByRfqId(rfqId).ifPresent(sel -> {
            java.util.Map<String, Object> selectionInfo = new java.util.HashMap<>();
            selectionInfo.put("supplierId", sel.getSupplier().getId());
            selectionInfo.put("supplierName", sel.getSupplier().getCompanyName());
            selectionInfo.put("isSystemRecommended", sel.getIsSystemRecommended());
            selectionInfo.put("status", sel.getSelectionStatus().name());
            selectionInfo.put("selectionId", sel.getId());
            selectionInfo.put("justification", sel.getJustification());
            selectionInfo.put("remarks", sel.getRemarks());
            status.put("existingSelection", selectionInfo);
        });

        return status;
    }

    // ===================== MAPPER =====================

    private SupplierFinalSelectionDTO.Response toResponse(SupplierFinalSelection s) {
        return SupplierFinalSelectionDTO.Response.builder()
                .id(s.getId())
                .rfqId(s.getRfq().getId())
                .rfqNumber(s.getRfq().getRfqNumber())
                .rfqTitle(s.getRfq().getRfqTitle())
                .supplierId(s.getSupplier().getId())
                .supplierName(s.getSupplier().getCompanyName())
                .supplierEmail(s.getSupplier().getContactPersonEmail())
                .supplierPhone(s.getSupplier().getContactPersonPhone())
                .isSystemRecommended(s.getIsSystemRecommended())
                .systemRecommendedRank(s.getSystemRecommendedRank())
                .systemFinalScore(s.getSystemFinalScore())
                .justification(s.getJustification())
                .remarks(s.getRemarks())
                .selectedByUserId(s.getSelectedByUserId())
                .selectedByUserName(s.getSelectedByUserName())
                .selectionStatus(s.getSelectionStatus().name())
                .selectedAt(s.getSelectedAt())
                .poNegotiationId(s.getPoNegotiationId())
                .purchaseOrderId(s.getPurchaseOrderId())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}