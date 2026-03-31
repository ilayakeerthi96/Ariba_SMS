

// package com.itti.leadcapturing.web;

// import com.itti.leadcapturing.dto.RFQCriterionDTO;
// import com.itti.leadcapturing.model.EvaluationCriterion;
// import com.itti.leadcapturing.service.SupplierEvaluationService;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.math.BigDecimal;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// /**
//  * ✅ UPDATED: Supplier Evaluation REST Controller
//  * New endpoint for automatic final ranking calculation
//  */
// @RestController
// @RequestMapping("/api/supplier-evaluation")
// @CrossOrigin(origins = "*")
// public class SupplierEvaluationController {

//     private static final Logger logger = LoggerFactory.getLogger(SupplierEvaluationController.class);

//     @Autowired
//     private SupplierEvaluationService evaluationService;

//     // ==================== CRITERION MANAGEMENT (ADMIN) ====================

//     @GetMapping("/criteria")
//     public ResponseEntity<?> getAllCriteria() {
//         logger.info("📋 [API] GET ALL CRITERIA (Active + Inactive)");

//         try {
//             List<EvaluationCriterion> criteria = evaluationService.getAllCriteria();

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("count", criteria.size());
//             response.put("data", criteria);

//             long activeCount = criteria.stream().filter(EvaluationCriterion::getIsActive).count();
//             response.put("activeCount", activeCount);
//             response.put("inactiveCount", criteria.size() - activeCount);

//             logger.info("  ✅ Returned {} criteria ({} active, {} inactive)", 
//                 criteria.size(), activeCount, criteria.size() - activeCount);
//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Error: {}", e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to fetch criteria");

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     @GetMapping("/criteria/active")
//     public ResponseEntity<?> getActiveCriteria() {
//         logger.info("📋 [API] GET ACTIVE CRITERIA");

//         try {
//             List<EvaluationCriterion> criteria = evaluationService.getAllActiveCriteria();

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("count", criteria.size());
//             response.put("data", criteria);

//             logger.info("  ✅ Returned {} active criteria", criteria.size());
//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Error: {}", e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to fetch active criteria");

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     @PostMapping("/criteria")
//     public ResponseEntity<?> createCriterion(@RequestBody EvaluationCriterion criterion) {
//         logger.info("➕ [API] CREATE CRITERION: {}", criterion.getCriterionName());

//         try {
//             EvaluationCriterion created = evaluationService.createCriterion(criterion);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Criterion created successfully (maxScore set to 5)");
//             response.put("data", created);

//             logger.info("  ✅ Criterion created with ID: {}", created.getId());
//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Error: {}", e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     @PutMapping("/criteria/{id}")
//     public ResponseEntity<?> updateCriterion(@PathVariable Long id, @RequestBody EvaluationCriterion criterion) {
//         logger.info("✏️ [API] UPDATE CRITERION: {}", id);

//         try {
//             EvaluationCriterion updated = evaluationService.updateCriterion(id, criterion);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Criterion updated successfully");
//             response.put("data", updated);

//             logger.info("  ✅ Criterion {} updated", id);
//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Error: {}", e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     @DeleteMapping("/criteria/{id}")
//     public ResponseEntity<?> deleteCriterion(@PathVariable Long id) {
//         logger.info("🗑️ [API] DELETE CRITERION: {}", id);

//         try {
//             evaluationService.deleteCriterion(id);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Criterion deleted successfully");

//             logger.info("  ✅ Criterion {} deleted", id);
//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Error: {}", e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     @PutMapping("/criteria/{id}/activate")
//     public ResponseEntity<?> activateCriterion(@PathVariable Long id) {
//         logger.info("✅ [API] ACTIVATE CRITERION: {}", id);

//         try {
//             EvaluationCriterion criterion = evaluationService.activateCriterion(id);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Criterion activated successfully");
//             response.put("data", criterion);

//             logger.info("  ✅ Criterion {} activated", id);
//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Error: {}", e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     @PutMapping("/criteria/{id}/deactivate")
//     public ResponseEntity<?> deactivateCriterion(@PathVariable Long id) {
//         logger.info("❌ [API] DEACTIVATE CRITERION: {}", id);

//         try {
//             EvaluationCriterion criterion = evaluationService.deactivateCriterion(id);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Criterion deactivated successfully");
//             response.put("data", criterion);

//             logger.info("  ✅ Criterion {} deactivated", id);
//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Error: {}", e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // ==================== RFQ CRITERION ASSIGNMENT ====================

//     @PostMapping("/assign-criteria")
//     public ResponseEntity<?> assignCriteriaToRFQ(@RequestBody Map<String, Object> payload) {
//         logger.info("🎯 [API] ASSIGN CRITERIA TO RFQ");

//         try {
//             Long rfqId = Long.valueOf(payload.get("rfqId").toString());
//             Long userId = Long.valueOf(payload.get("userId").toString());
            
//             @SuppressWarnings("unchecked")
//             Map<String, Object> criteriaWeightagesRaw = (Map<String, Object>) payload.get("criteriaWeightages");
            
//             Map<Long, Double> criteriaWeightages = new HashMap<>();
//             criteriaWeightagesRaw.forEach((key, value) -> {
//                 criteriaWeightages.put(Long.valueOf(key), Double.valueOf(value.toString()));
//             });

//             String result = evaluationService.assignCriteriaToRFQ(rfqId, criteriaWeightages, userId);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", result);

//             logger.info("  ✅ Criteria assigned successfully");
//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Error: {}", e.getMessage());
//             e.printStackTrace();

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());

//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     @GetMapping("/rfq/{rfqId}/criteria")
//     public ResponseEntity<?> getRFQCriteria(@PathVariable Long rfqId) {
//         logger.info("📋 [API] GET RFQ CRITERIA: {}", rfqId);

//         try {
//             List<RFQCriterionDTO> criteria = evaluationService.getRFQCriteria(rfqId);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("count", criteria.size());
//             response.put("data", criteria);

//             double totalWeightage = criteria.stream()
//                     .mapToDouble(RFQCriterionDTO::getWeightage)
//                     .sum();
            
//             response.put("totalWeightage", totalWeightage);
//             response.put("isComplete", Math.abs(totalWeightage - 100.0) < 0.01);
//             response.put("maxScore", 5);

//             logger.info("  ✅ Returned {} criteria, Total: {}%", criteria.size(), totalWeightage);
//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Error: {}", e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to fetch RFQ criteria");

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // ==================== SUPPLIER SCORING ====================

//     @PostMapping("/score")
//     public ResponseEntity<?> scoreSupplier(@RequestBody Map<String, Object> payload) {
//         logger.info("📊 [API] SCORE SUPPLIER");

//         try {
//             Long rfqId = Long.valueOf(payload.get("rfqId").toString());
//             Long supplierId = Long.valueOf(payload.get("supplierId").toString());
//             Long userId = Long.valueOf(payload.get("userId").toString());
            
//             @SuppressWarnings("unchecked")
//             Map<String, Object> scoresRaw = (Map<String, Object>) payload.get("scores");
            
//             Map<Long, BigDecimal> scores = new HashMap<>();
//             if (scoresRaw != null) {
//                 scoresRaw.forEach((key, value) -> {
//                     if (value != null && !value.toString().trim().isEmpty()) {
//                         scores.put(Long.valueOf(key), new BigDecimal(value.toString()));
//                     }
//                 });
//             }

//             String result = evaluationService.scoreSupplier(rfqId, supplierId, scores, userId);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", result);

//             logger.info("  ✅ Supplier scored successfully");
//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Error: {}", e.getMessage());
//             e.printStackTrace();

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());

//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     // ==================== ✅ NEW: AUTOMATIC FINAL RANKING ====================

//     /**
//      * ✅ NEW: Calculate final rankings AUTOMATICALLY
//      * POST /api/supplier-evaluation/calculate-rankings/{rfqId}
//      * 
//      * No weightage parameters needed - calculated automatically!
//      */
//     @PostMapping("/calculate-rankings/{rfqId}")
//     public ResponseEntity<?> calculateFinalRankings(@PathVariable Long rfqId) {
//         logger.info("🏆 [API] CALCULATE FINAL RANKINGS - AUTOMATIC");
//         logger.info("  RFQ ID: {}", rfqId);

//         try {
//             logger.info("  ℹ️ Using AUTOMATIC quote weightage calculation");
//             logger.info("  📊 Formula: Quote Weightage = (NumSuppliers × 10) - (PriceRank - 1) × 10");

//             String result = evaluationService.calculateFinalRankings(rfqId);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", result);
//             response.put("info", "Quote weightages calculated automatically based on price ranking");

//             logger.info("  ✅ Rankings calculated automatically");
//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Error: {}", e.getMessage());
//             e.printStackTrace();

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());

//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     @GetMapping("/rfq/{rfqId}/rankings")
//     public ResponseEntity<?> getSupplierRankings(@PathVariable Long rfqId) {
//         logger.info("🏆 [API] GET SUPPLIER RANKINGS: {}", rfqId);

//         try {
//             List<Map<String, Object>> rankings = evaluationService.getSupplierRankings(rfqId);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("count", rankings.size());
//             response.put("data", rankings);

//             long recommendedCount = rankings.stream()
//                     .filter(r -> Boolean.TRUE.equals(r.get("isRecommended")))
//                     .count();
//             response.put("recommendedCount", recommendedCount);

//             logger.info("  ✅ Returned {} rankings ({} recommended)", rankings.size(), recommendedCount);
//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Error: {}", e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to fetch rankings");

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     @GetMapping("/rfq/{rfqId}/supplier/{supplierId}/scorecard")
//     public ResponseEntity<?> getSupplierScorecard(@PathVariable Long rfqId, @PathVariable Long supplierId) {
//         logger.info("📊 [API] GET SUPPLIER SCORECARD: RFQ={}, Supplier={}", rfqId, supplierId);

//         try {
//             Map<String, Object> scorecard = evaluationService.getSupplierScorecard(rfqId, supplierId);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("data", scorecard);

//             logger.info("  ✅ Scorecard retrieved");
//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("  ❌ Error: {}", e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());

//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//         }
//     }
// }


package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.RFQCriterionDTO;
import com.itti.leadcapturing.model.EvaluationCriterion;
import com.itti.leadcapturing.service.SupplierEvaluationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ✅ UPDATED: Added DELETE and EDIT RFQ Criteria endpoints
 */
@RestController
@RequestMapping("/api/supplier-evaluation")
@CrossOrigin(origins = "*")
public class SupplierEvaluationController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierEvaluationController.class);

    @Autowired
    private SupplierEvaluationService evaluationService;

    // ==================== CRITERION MANAGEMENT (ADMIN) ====================

    @GetMapping("/criteria")
    public ResponseEntity<?> getAllCriteria() {
        logger.info("📋 [API] GET ALL CRITERIA (Active + Inactive)");

        try {
            List<EvaluationCriterion> criteria = evaluationService.getAllCriteria();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", criteria.size());
            response.put("data", criteria);

            long activeCount = criteria.stream().filter(EvaluationCriterion::getIsActive).count();
            response.put("activeCount", activeCount);
            response.put("inactiveCount", criteria.size() - activeCount);

            logger.info("  ✅ Returned {} criteria ({} active, {} inactive)", 
                criteria.size(), activeCount, criteria.size() - activeCount);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch criteria");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/criteria/active")
    public ResponseEntity<?> getActiveCriteria() {
        logger.info("📋 [API] GET ACTIVE CRITERIA");

        try {
            List<EvaluationCriterion> criteria = evaluationService.getAllActiveCriteria();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", criteria.size());
            response.put("data", criteria);

            logger.info("  ✅ Returned {} active criteria", criteria.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch active criteria");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/criteria")
    public ResponseEntity<?> createCriterion(@RequestBody EvaluationCriterion criterion) {
        logger.info("➕ [API] CREATE CRITERION: {}", criterion.getCriterionName());

        try {
            EvaluationCriterion created = evaluationService.createCriterion(criterion);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Criterion created successfully (maxScore set to 5)");
            response.put("data", created);

            logger.info("  ✅ Criterion created with ID: {}", created.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/criteria/{id}")
    public ResponseEntity<?> updateCriterion(@PathVariable Long id, @RequestBody EvaluationCriterion criterion) {
        logger.info("✏️ [API] UPDATE CRITERION: {}", id);

        try {
            EvaluationCriterion updated = evaluationService.updateCriterion(id, criterion);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Criterion updated successfully");
            response.put("data", updated);

            logger.info("  ✅ Criterion {} updated", id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/criteria/{id}")
    public ResponseEntity<?> deleteCriterion(@PathVariable Long id) {
        logger.info("🗑️ [API] DELETE CRITERION: {}", id);

        try {
            evaluationService.deleteCriterion(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Criterion deleted successfully");

            logger.info("  ✅ Criterion {} deleted", id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/criteria/{id}/activate")
    public ResponseEntity<?> activateCriterion(@PathVariable Long id) {
        logger.info("✅ [API] ACTIVATE CRITERION: {}", id);

        try {
            EvaluationCriterion criterion = evaluationService.activateCriterion(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Criterion activated successfully");
            response.put("data", criterion);

            logger.info("  ✅ Criterion {} activated", id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/criteria/{id}/deactivate")
    public ResponseEntity<?> deactivateCriterion(@PathVariable Long id) {
        logger.info("❌ [API] DEACTIVATE CRITERION: {}", id);

        try {
            EvaluationCriterion criterion = evaluationService.deactivateCriterion(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Criterion deactivated successfully");
            response.put("data", criterion);

            logger.info("  ✅ Criterion {} deactivated", id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== RFQ CRITERION ASSIGNMENT ====================

    @PostMapping("/assign-criteria")
    public ResponseEntity<?> assignCriteriaToRFQ(@RequestBody Map<String, Object> payload) {
        logger.info("🎯 [API] ASSIGN CRITERIA TO RFQ");

        try {
            Long rfqId = Long.valueOf(payload.get("rfqId").toString());
            Long userId = Long.valueOf(payload.get("userId").toString());
            
            @SuppressWarnings("unchecked")
            Map<String, Object> criteriaWeightagesRaw = (Map<String, Object>) payload.get("criteriaWeightages");
            
            Map<Long, Double> criteriaWeightages = new HashMap<>();
            criteriaWeightagesRaw.forEach((key, value) -> {
                criteriaWeightages.put(Long.valueOf(key), Double.valueOf(value.toString()));
            });

            String result = evaluationService.assignCriteriaToRFQ(rfqId, criteriaWeightages, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            logger.info("  ✅ Criteria assigned successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/rfq/{rfqId}/criteria")
    public ResponseEntity<?> getRFQCriteria(@PathVariable Long rfqId) {
        logger.info("📋 [API] GET RFQ CRITERIA: {}", rfqId);

        try {
            List<RFQCriterionDTO> criteria = evaluationService.getRFQCriteria(rfqId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", criteria.size());
            response.put("data", criteria);

            double totalWeightage = criteria.stream()
                    .mapToDouble(RFQCriterionDTO::getWeightage)
                    .sum();
            
            response.put("totalWeightage", totalWeightage);
            response.put("isComplete", Math.abs(totalWeightage - 100.0) < 0.01);
            response.put("maxScore", 5);

            logger.info("  ✅ Returned {} criteria, Total: {}%", criteria.size(), totalWeightage);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch RFQ criteria");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== ✅ NEW: DELETE RFQ CRITERION ====================

    /**
     * ✅ NEW: Delete RFQ Criterion
     * DELETE /api/supplier-evaluation/rfq-criteria/{rfqCriterionId}
     */
    @DeleteMapping("/rfq-criteria/{rfqCriterionId}")
    public ResponseEntity<?> deleteRFQCriterion(@PathVariable Long rfqCriterionId) {
        logger.info("🗑️ [API] DELETE RFQ CRITERION: {}", rfqCriterionId);

        try {
            String result = evaluationService.deleteRFQCriterion(rfqCriterionId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            logger.info("  ✅ RFQ Criterion {} deleted", rfqCriterionId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ==================== ✅ NEW: EDIT RFQ CRITERION WEIGHTAGE ====================

    /**
     * ✅ NEW: Update RFQ Criterion weightage
     * PUT /api/supplier-evaluation/rfq-criteria/{rfqCriterionId}/weightage
     */
    @PutMapping("/rfq-criteria/{rfqCriterionId}/weightage")
    public ResponseEntity<?> updateRFQCriterionWeightage(
            @PathVariable Long rfqCriterionId, 
            @RequestBody Map<String, Object> payload) {
        logger.info("✏️ [API] UPDATE RFQ CRITERION WEIGHTAGE: {}", rfqCriterionId);

        try {
            Integer newWeightage = Integer.valueOf(payload.get("weightage").toString());

            String result = evaluationService.updateRFQCriterionWeightage(rfqCriterionId, newWeightage);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            logger.info("  ✅ RFQ Criterion {} weightage updated", rfqCriterionId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ==================== SUPPLIER SCORING ====================

    @PostMapping("/score")
    public ResponseEntity<?> scoreSupplier(@RequestBody Map<String, Object> payload) {
        logger.info("📊 [API] SCORE SUPPLIER");

        try {
            Long rfqId = Long.valueOf(payload.get("rfqId").toString());
            Long supplierId = Long.valueOf(payload.get("supplierId").toString());
            Long userId = Long.valueOf(payload.get("userId").toString());
            
            @SuppressWarnings("unchecked")
            Map<String, Object> scoresRaw = (Map<String, Object>) payload.get("scores");
            
            Map<Long, BigDecimal> scores = new HashMap<>();
            if (scoresRaw != null) {
                scoresRaw.forEach((key, value) -> {
                    if (value != null && !value.toString().trim().isEmpty()) {
                        scores.put(Long.valueOf(key), new BigDecimal(value.toString()));
                    }
                });
            }

            String result = evaluationService.scoreSupplier(rfqId, supplierId, scores, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            logger.info("  ✅ Supplier scored successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ==================== ✅ NEW: AUTOMATIC FINAL RANKING ====================

    /**
     * ✅ NEW: Calculate final rankings AUTOMATICALLY
     * POST /api/supplier-evaluation/calculate-rankings/{rfqId}
     * 
     * No weightage parameters needed - calculated automatically!
     */
    @PostMapping("/calculate-rankings/{rfqId}")
    public ResponseEntity<?> calculateFinalRankings(@PathVariable Long rfqId) {
        logger.info("🏆 [API] CALCULATE FINAL RANKINGS - AUTOMATIC");
        logger.info("  RFQ ID: {}", rfqId);

        try {
            logger.info("  ℹ️ Using AUTOMATIC quote weightage calculation");
            logger.info("  📊 Formula: Quote Weightage = (NumSuppliers × 10) - (PriceRank - 1) × 10");

            String result = evaluationService.calculateFinalRankings(rfqId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            response.put("info", "Quote weightages calculated automatically based on price ranking");

            logger.info("  ✅ Rankings calculated automatically");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/rfq/{rfqId}/rankings")
    public ResponseEntity<?> getSupplierRankings(@PathVariable Long rfqId) {
        logger.info("🏆 [API] GET SUPPLIER RANKINGS: {}", rfqId);

        try {
            List<Map<String, Object>> rankings = evaluationService.getSupplierRankings(rfqId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", rankings.size());
            response.put("data", rankings);

            long recommendedCount = rankings.stream()
                    .filter(r -> Boolean.TRUE.equals(r.get("isRecommended")))
                    .count();
            response.put("recommendedCount", recommendedCount);

            logger.info("  ✅ Returned {} rankings ({} recommended)", rankings.size(), recommendedCount);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch rankings");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/rfq/{rfqId}/supplier/{supplierId}/scorecard")
    public ResponseEntity<?> getSupplierScorecard(@PathVariable Long rfqId, @PathVariable Long supplierId) {
        logger.info("📊 [API] GET SUPPLIER SCORECARD: RFQ={}, Supplier={}", rfqId, supplierId);

        try {
            Map<String, Object> scorecard = evaluationService.getSupplierScorecard(rfqId, supplierId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", scorecard);

            logger.info("  ✅ Scorecard retrieved");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("  ❌ Error: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}