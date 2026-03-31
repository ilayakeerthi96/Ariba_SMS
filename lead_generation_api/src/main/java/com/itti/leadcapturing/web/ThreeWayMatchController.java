package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.GRNDto;
import com.itti.leadcapturing.service.ThreeWayMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/three-way-match")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ThreeWayMatchController {

    private final ThreeWayMatchService matchService;

    // ── Perform Match ─────────────────────────────────────────────────────────

    /**
     * POST /api/three-way-match/perform
     *
     * Body:
     * {
     *   "purchaseOrderId": 1,
     *   "grnId": 1,
     *   "invoiceId": 1,
     *   "tolerancePercentage": 2.00,     // optional, defaults to 2%
     *   "performedByUserId": 10,
     *   "performedByName": "Priya Buyer"
     * }
     *
     * Response:
     *   { success, data: { matchStatus, matchSummary, lineResults, availableResolutions, ... } }
     *
     * If MATCHED → invoice status auto-changes to APPROVED.
     * If mismatch → client uses availableResolutions to show action buttons.
     */
    @PostMapping("/perform")
    public ResponseEntity<?> performMatch(@RequestBody GRNDto.PerformMatchRequest request) {
        log.info("🔍 [API] PERFORM 3-WAY MATCH — PO: {}, GRN: {}, Invoice: {}",
                request.getPurchaseOrderId(), request.getGrnId(), request.getInvoiceId());
        try {
            GRNDto.MatchResponse result = matchService.performMatch(request);
            String msg = "MATCHED".equals(result.getMatchStatus())
                    ? "✅ 3-Way Match successful. Invoice auto-approved."
                    : "⚠️ Mismatch detected: " + result.getMatchStatus() + ". Choose a resolution.";
            return ResponseEntity.ok(Map.of("success", true, "message", msg, "data", result));
        } catch (RuntimeException e) {
            log.error("❌ {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("❌ Unexpected error", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to perform match: " + e.getMessage()));
        }
    }

    // ── Resolve Mismatch ─────────────────────────────────────────────────────

    /**
     * PUT /api/three-way-match/{id}/resolve
     *
     * Body:
     * {
     *   "resolution": "REVISED_INVOICE_REQUESTED",  // or any MatchResolution value
     *   "resolutionRemarks": "Invoice qty > GRN qty for item 2. Please send revised invoice.",
     *   "resolvedByUserId": 10,
     *   "resolvedByName": "Priya Buyer",
     *   "approvedPaymentAmount": 45000.00  // required only for PARTIAL_PAYMENT_APPROVED
     * }
     *
     * Available resolution values:
     *   ACCEPTED_WITH_VARIANCE    → Approve as-is (justification required)
     *   REVISED_INVOICE_REQUESTED → Invoice → REJECTED (supplier resubmits)
     *   PARTIAL_PAYMENT_APPROVED  → Pay only accepted-goods value
     *   CREDIT_NOTE_REQUESTED     → Supplier sends credit note; invoice approved for accepted value
     *   DEBIT_NOTE_RAISED         → Buyer issues debit note; invoice approved for PO value
     *   DISPUTED                  → Escalate dispute (invoice stays SUBMITTED)
     *   CANCELLED                 → Cancel invoice permanently
     */
    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> resolveMatch(@PathVariable Long id,
                                          @RequestBody GRNDto.ResolveMatchRequest request) {
        request.setMatchId(id);
        log.info("⚖️ [API] RESOLVE MATCH — ID: {}, Resolution: {}", id, request.getResolution());
        try {
            GRNDto.MatchResponse result = matchService.resolveMatch(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Mismatch resolved: " + result.getResolution(),
                    "data", result));
        } catch (RuntimeException e) {
            log.error("❌ {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("❌ Unexpected error", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to resolve match."));
        }
    }

    // ── Get by ID ────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<?> getMatch(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", matchService.getMatchById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to fetch match."));
        }
    }

    // ── By Invoice (history) ─────────────────────────────────────────────────

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<?> getMatchesByInvoice(@PathVariable Long invoiceId) {
        try {
            List<GRNDto.MatchResponse> list = matchService.getMatchesByInvoice(invoiceId);
            return ResponseEntity.ok(Map.of("success", true, "count", list.size(), "data", list));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to fetch matches."));
        }
    }

    // ── Latest match for invoice ─────────────────────────────────────────────

    @GetMapping("/invoice/{invoiceId}/latest")
    public ResponseEntity<?> getLatestMatch(@PathVariable Long invoiceId) {
        try {
            Optional<GRNDto.MatchResponse> match = matchService.getLatestMatch(invoiceId);
            if (match.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "No match found for invoice: " + invoiceId));
            return ResponseEntity.ok(Map.of("success", true, "data", match.get()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to fetch latest match."));
        }
    }

    // ── By PO ────────────────────────────────────────────────────────────────

    @GetMapping("/po/{poId}")
    public ResponseEntity<?> getMatchesByPO(@PathVariable Long poId) {
        try {
            List<GRNDto.MatchResponse> list = matchService.getMatchesByPO(poId);
            return ResponseEntity.ok(Map.of("success", true, "count", list.size(), "data", list));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to fetch matches for PO."));
        }
    }

    // ── Pending Resolution (buyer dashboard) ─────────────────────────────────

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingResolutions() {
        try {
            List<GRNDto.MatchResponse> list = matchService.getPendingResolutions();
            return ResponseEntity.ok(Map.of("success", true, "count", list.size(), "data", list));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to fetch pending resolutions."));
        }
    }
}