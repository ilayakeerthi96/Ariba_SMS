package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.PONegotiationDTO;
import com.itti.leadcapturing.dto.POResponseDTO;
import com.itti.leadcapturing.service.PONegotiationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/po-negotiation")
@CrossOrigin(origins = "*")
public class PONegotiationController {

    private static final Logger logger = LoggerFactory.getLogger(PONegotiationController.class);

    @Autowired
    private PONegotiationService negotiationService;

    /**
     * POST /api/po-negotiation/init
     * ✅ BUG FIX: userId is now optional (frontend doesn't send it)
     * ROOT CAUSE WAS: request.get("userId").toString() → NPE when userId=null
     */
    @PostMapping("/init")
    @Transactional
    public ResponseEntity<?> initNegotiation(@RequestBody Map<String, Object> request) {
        try {
            Object rfqIdObj = request.get("rfqId");
            Object supplierIdObj = request.get("supplierId");
            Object userIdObj = request.get("userId"); // OPTIONAL

            if (rfqIdObj == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "rfqId is required"));
            }
            if (supplierIdObj == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "supplierId is required"));
            }

            Long rfqId = Long.valueOf(rfqIdObj.toString());
            Long supplierId = Long.valueOf(supplierIdObj.toString());
            Long userId = (userIdObj != null) ? Long.valueOf(userIdObj.toString()) : 0L;

            logger.info("📋 [API] INIT NEGOTIATION — RFQ: {}, Supplier: {}, User: {}", rfqId, supplierId, userId);

            PONegotiationDTO.Response result = negotiationService.initNegotiation(rfqId, supplierId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Price review initialized successfully.");
            response.put("data", result);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("❌ Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ Unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to initialize price review: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getNegotiation(@PathVariable Long id) {
        try {
            PONegotiationDTO.Response result = negotiationService.getNegotiation(id);
            return ResponseEntity.ok(Map.of("success", true, "data", result));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to get negotiation"));
        }
    }

    @GetMapping("/rfq/{rfqId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getNegotiationsByRfq(@PathVariable Long rfqId) {
        try {
            List<PONegotiationDTO.Response> results = negotiationService.getNegotiationsByRfq(rfqId);
            return ResponseEntity.ok(Map.of("success", true, "count", results.size(), "data", results));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to get negotiations"));
        }
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> saveNegotiation(
            @PathVariable Long id,
            @RequestBody PONegotiationDTO.SaveRequest request) {
        logger.info("💾 [API] SAVE NEGOTIATION — ID: {}", id);
        try {
            PONegotiationDTO.Response result = negotiationService.saveNegotiation(id, request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Price review saved successfully.");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("❌ Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ Unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to save price review"));
        }
    }

    @PostMapping("/{id}/create-po")
    @Transactional
    public ResponseEntity<?> createPOFromNegotiation(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> request) {
        logger.info("📄 [API] CREATE PO FROM NEGOTIATION — ID: {}", id);
        try {
            POResponseDTO poDto = negotiationService.createPOFromNegotiation(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Purchase Order created successfully from negotiated prices.");
            response.put("data", poDto);
            response.put("poId", poDto.getId());
            response.put("poNumber", poDto.getPoNumber());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            logger.error("❌ Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ Unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to create PO: " + e.getMessage()));
        }
    }
}