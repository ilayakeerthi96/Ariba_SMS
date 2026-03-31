

// package com.itti.leadcapturing.service;

// import com.itti.leadcapturing.dto.RFQCriterionDTO;
// import com.itti.leadcapturing.model.*;
// import com.itti.leadcapturing.repo.*;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.math.BigDecimal;
// import java.math.RoundingMode;
// import java.util.*;
// import java.util.stream.Collectors;

// /**
//  * ✅ FIXED: Automatic Quote Weightage Calculation
//  * Formula: Lower Price = Higher Weightage
//  * 
//  * Quote Weightage Logic:
//  * - 3 suppliers: 30, 20, 10 (lowest to highest price)
//  * - 6 suppliers: 60, 50, 40, 30, 20, 10
//  * - Formula: (numSuppliers × 10) - (priceRank - 1) × 10
//  * 
//  * Final Score = Total Criteria Score + Quote Weightage
//  */
// @Service
// public class SupplierEvaluationService {

//     private static final Logger logger = LoggerFactory.getLogger(SupplierEvaluationService.class);
//     private static final int MAX_SCORE = 5; // ✅ FIXED

//     @Autowired
//     private EvaluationCriterionRepository criterionRepository;

//     @Autowired
//     private RFQCriterionRepository rfqCriterionRepository;

//     @Autowired
//     private SupplierScoreRepository supplierScoreRepository;

//     @Autowired
//     private RFQSupplierRepository rfqSupplierRepository;

//     @Autowired
//     private RFQRepository rfqRepository;

//     @Autowired
//     private SupplierRepository supplierRepository;

//     @Autowired
//     private SupplierRecommendationRepository recommendationRepository;

//     // ==================== CRITERION MANAGEMENT (ADMIN) ====================

//     /**
//      * Get all active criteria (for RFQ Creator to select from)
//      */
//     @Transactional(readOnly = true)
//     public List<EvaluationCriterion> getAllActiveCriteria() {
//         logger.info("📋 [GET ACTIVE CRITERIA]");
//         List<EvaluationCriterion> criteria = criterionRepository.findAllActive();
//         logger.info("  ✅ Found {} active criteria", criteria.size());
//         return criteria;
//     }

//     /**
//      * Create new evaluation criterion (Admin only)
//      */
//     @Transactional
//     public EvaluationCriterion createCriterion(EvaluationCriterion criterion) {
//         logger.info("➕ [CREATE CRITERION] Name: {}", criterion.getCriterionName());
        
//         // ✅ FORCE maxScore = 5
//         criterion.setMaxScore(MAX_SCORE);
        
//         EvaluationCriterion saved = criterionRepository.save(criterion);
//         logger.info("  ✅ Criterion created with ID: {} (maxScore forced to {})", saved.getId(), MAX_SCORE);
//         return saved;
//     }

//     // ==================== RFQ CRITERION ASSIGNMENT ====================

//     /**
//      * Assign criteria to RFQ with weightages
//      * Weightages must sum to 100%
//      */
//     @Transactional
//     public String assignCriteriaToRFQ(Long rfqId, Map<Long, Double> criteriaWeightages, Long userId) {
//         logger.info("=" .repeat(80));
//         logger.info("🎯 [ASSIGN CRITERIA TO RFQ] RFQ ID: {}, User: {}", rfqId, userId);
//         logger.info("  Criteria to assign: {}", criteriaWeightages.size());

//         RFQ rfq = rfqRepository.findById(rfqId)
//                 .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));

//         // Validate total = 100%
//         double totalWeightage = criteriaWeightages.values().stream()
//                 .mapToDouble(Double::doubleValue)
//                 .sum();

//         if (Math.abs(totalWeightage - 100.0) > 0.01) {
//             throw new RuntimeException(
//                 String.format("Total weightage must be 100%%. Current: %.2f%%", totalWeightage)
//             );
//         }

//         logger.info("  ✅ Weightage validation passed: 100.00%");

//         // Clear existing
//         rfqCriterionRepository.deleteByRfqId(rfqId);
//         logger.info("  🗑️  Cleared existing criteria");

//         // Add new
//         int assignedCount = 0;
//         for (Map.Entry<Long, Double> entry : criteriaWeightages.entrySet()) {
//             Long criterionId = entry.getKey();
//             Double weightage = entry.getValue();

//             EvaluationCriterion criterion = criterionRepository.findById(criterionId)
//                     .orElseThrow(() -> new RuntimeException("Criterion not found: " + criterionId));

//             RFQCriterion rfqCriterion = new RFQCriterion(rfq, criterion, weightage, userId);
//             rfqCriterion.setIsRfqSpecific(false);
//             rfqCriterionRepository.save(rfqCriterion);

//             logger.info("    ✓ {} - {}%", criterion.getCriterionName(), weightage);
//             assignedCount++;
//         }

//         logger.info("  ✅ Assigned {} criteria to RFQ", assignedCount);
//         logger.info("=" .repeat(80));

//         return String.format("Successfully assigned %d criteria to RFQ", assignedCount);
//     }

//     /**
//      * Get criteria assigned to RFQ (returns DTOs)
//      */
//     @Transactional(readOnly = true)
//     public List<RFQCriterionDTO> getRFQCriteria(Long rfqId) {
//         logger.info("📋 [GET RFQ CRITERIA] RFQ ID: {}", rfqId);
        
//         List<RFQCriterion> criteria = rfqCriterionRepository.findByRfqIdWithCriterion(rfqId);
        
//         List<RFQCriterionDTO> dtos = criteria.stream()
//                 .map(this::convertToRFQCriterionDTO)
//                 .collect(Collectors.toList());
        
//         logger.info("  ✅ Found {} criteria", dtos.size());
//         return dtos;
//     }

//     private RFQCriterionDTO convertToRFQCriterionDTO(RFQCriterion rfqCriterion) {
//         RFQCriterionDTO dto = new RFQCriterionDTO();
//         dto.setId(rfqCriterion.getId());
//         dto.setRfqId(rfqCriterion.getRfq().getId());
//         dto.setWeightage(rfqCriterion.getWeightage());
//         dto.setCreatedBy(rfqCriterion.getCreatedBy());
//         dto.setIsRfqSpecific(rfqCriterion.getIsRfqSpecific() != null 
//             ? rfqCriterion.getIsRfqSpecific() 
//             : false);
        
//         EvaluationCriterion criterion = rfqCriterion.getCriterion();
//         RFQCriterionDTO.CriterionDTO criterionDTO = new RFQCriterionDTO.CriterionDTO(
//             criterion.getId(),
//             criterion.getCriterionName(),
//             criterion.getDescription(),
//             criterion.getCriterionType().name(),
//             MAX_SCORE, // ✅ ALWAYS 5
//             criterion.getIsActive()
//         );
//         dto.setCriterion(criterionDTO);
        
//         return dto;
//     }

//     // ==================== SUPPLIER SCORING ====================

//     /**
//      * ✅ Submit scores for supplier (RFQ Creator rates suppliers)
//      * scores: Map<rfqCriterionId, rawScore (0-5)>
//      */
//     @Transactional
//     public String scoreSupplier(Long rfqId, Long supplierId, Map<Long, BigDecimal> scores, Long userId) {
//         logger.info("=" .repeat(80));
//         logger.info("📊 [SCORE SUPPLIER] RFQ: {}, Supplier: {}, Scorer: {}", rfqId, supplierId, userId);

//         RFQ rfq = rfqRepository.findById(rfqId)
//                 .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));

//         Supplier supplier = supplierRepository.findById(supplierId)
//                 .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));

//         List<RFQCriterion> rfqCriteria = rfqCriterionRepository.findByRfqIdWithCriterion(rfqId);
        
//         if (rfqCriteria.isEmpty()) {
//             throw new RuntimeException("No criteria assigned. Please assign criteria first.");
//         }

//         int scoredCount = 0;

//         for (RFQCriterion rfqCriterion : rfqCriteria) {
//             BigDecimal rawScore = scores.get(rfqCriterion.getId());
            
//             if (rawScore == null) {
//                 logger.warn("  ⚠️  No score for: {}", rfqCriterion.getCriterion().getCriterionName());
//                 continue;
//             }

//             // ✅ Validate: must be 0-5
//             if (rawScore.compareTo(BigDecimal.ZERO) < 0 || 
//                 rawScore.compareTo(new BigDecimal(MAX_SCORE)) > 0) {
//                 throw new RuntimeException(
//                     String.format("Score for %s must be 0-%d (got %s)", 
//                                  rfqCriterion.getCriterion().getCriterionName(), 
//                                  MAX_SCORE,
//                                  rawScore)
//                 );
//             }

//             // Save score
//             SupplierScore supplierScore = supplierScoreRepository
//                     .findByRfqIdAndSupplierIdAndCriterionId(rfqId, supplierId, rfqCriterion.getId())
//                     .orElse(new SupplierScore());

//             supplierScore.setRfq(rfq);
//             supplierScore.setSupplier(supplier);
//             supplierScore.setRfqCriterion(rfqCriterion);
//             supplierScore.setRawScore(rawScore);
//             supplierScore.setScoredBy(userId);
//             supplierScore.setIsAutoCalculated(false);
//             supplierScore.calculateWeightedScore(); // Auto-calculates: (rawScore/5) × weightage

//             supplierScoreRepository.save(supplierScore);

//             logger.info("    ✓ {} - Raw: {}/{}, Weighted: {}", 
//                        rfqCriterion.getCriterion().getCriterionName(),
//                        rawScore, MAX_SCORE,
//                        supplierScore.getWeightedScore());
//             scoredCount++;
//         }

//         logger.info("  ✅ Scored {} criteria", scoredCount);
//         logger.info("=" .repeat(80));

//         return String.format("Scored %d criteria successfully", scoredCount);
//     }

//     // ==================== ✅ FIXED: AUTOMATIC FINAL RANKING ====================

//     /**
//      * ✅ FIXED: Calculate final rankings with AUTOMATIC quote weightage
//      * 
//      * Logic:
//      * 1. Get total criteria scores for all suppliers
//      * 2. Sort suppliers by quote amount (LOW to HIGH)
//      * 3. Calculate quote weightage automatically:
//      *    - Formula: (numSuppliers × 10) - (priceRank - 1) × 10
//      *    - Lowest price gets highest weightage (numSuppliers × 10)
//      *    - Each rank down reduces by 10
//      *    - Highest price gets 10
//      * 4. Final Score = Total Criteria Score + Quote Weightage
//      * 5. Rank by Final Score (HIGH to LOW)
//      * 6. Top 5 are recommended
//      */
//     @Transactional
//     public String calculateFinalRankings(Long rfqId) {
//         logger.info("=" .repeat(80));
//         logger.info("🏆 [CALCULATE FINAL RANKINGS - AUTOMATIC] RFQ ID: {}", rfqId);

//         try {
//             // Clear existing recommendations
//             recommendationRepository.deleteByRfqId(rfqId);
//             logger.info("  🗑️  Cleared existing recommendations");

//             // Get all suppliers who responded
//             List<RFQSupplier> respondedSuppliers = rfqSupplierRepository.findByRFQAndStatus(rfqId, "RESPONDED");
            
//             if (respondedSuppliers.isEmpty()) {
//                 throw new RuntimeException("No suppliers have submitted quotes");
//             }

//             int numSuppliers = respondedSuppliers.size();
//             logger.info("  👥 Total suppliers with quotes: {}", numSuppliers);

//             // Step 1: Get total criteria scores and quote amounts
//             Map<Long, BigDecimal> supplierTotalScores = new HashMap<>();
//             Map<Long, BigDecimal> supplierQuoteAmounts = new HashMap<>();

//             logger.info("\n  📊 STEP 1: Calculate Criteria Scores");
//             for (RFQSupplier rfqSupplier : respondedSuppliers) {
//                 Long supplierId = rfqSupplier.getSupplier().getId();
                
//                 BigDecimal totalScore = supplierScoreRepository
//                         .calculateTotalScoreForSupplier(rfqId, supplierId);
                
//                 if (totalScore == null) {
//                     logger.warn("    ⚠️  No scores found for {}, using 0", 
//                                rfqSupplier.getSupplier().getCompanyName());
//                     totalScore = BigDecimal.ZERO;
//                 }

//                 BigDecimal quoteAmount = rfqSupplier.getQuoteAmount();
//                 if (quoteAmount == null) {
//                     throw new RuntimeException(
//                         String.format("Supplier %s has no quote amount", 
//                                     rfqSupplier.getSupplier().getCompanyName())
//                     );
//                 }

//                 supplierTotalScores.put(supplierId, totalScore);
//                 supplierQuoteAmounts.put(supplierId, quoteAmount);

//                 logger.info("    {} - Score: {}/100, Quote: ₹{}", 
//                            rfqSupplier.getSupplier().getCompanyName(),
//                            totalScore,
//                            quoteAmount);
//             }

//             // Step 2: Sort by price (LOW to HIGH for ranking)
//             logger.info("\n  💰 STEP 2: Price Ranking (Low to High)");
//             List<RFQSupplier> sortedByPrice = new ArrayList<>(respondedSuppliers);
//             sortedByPrice.sort(Comparator.comparing(s -> s.getQuoteAmount()));

//             for (int i = 0; i < sortedByPrice.size(); i++) {
//                 RFQSupplier s = sortedByPrice.get(i);
//                 logger.info("    Price Rank {}: {} - ₹{}", 
//                            i + 1,
//                            s.getSupplier().getCompanyName(),
//                            s.getQuoteAmount());
//             }

//             // Step 3: Calculate AUTOMATIC quote weightages
//             logger.info("\n  ⚖️  STEP 3: Automatic Quote Weightage Calculation");
//             Map<Long, Integer> quoteWeightages = new HashMap<>();
//             int maxWeightage = numSuppliers * 10;
            
//             logger.info("    Formula: (numSuppliers × 10) - (rank - 1) × 10");
//             logger.info("    Max Weightage: {} (for {} suppliers)", maxWeightage, numSuppliers);
            
//             for (int i = 0; i < sortedByPrice.size(); i++) {
//                 Long supplierId = sortedByPrice.get(i).getSupplier().getId();
//                 String supplierName = sortedByPrice.get(i).getSupplier().getCompanyName();
//                 BigDecimal price = sortedByPrice.get(i).getQuoteAmount();
                
//                 // ✅ Formula: Lower price = Higher weightage
//                 // Rank 1 (lowest price) gets maxWeightage
//                 // Rank 2 gets maxWeightage - 10
//                 // Rank 3 gets maxWeightage - 20, etc.
//                 int quoteWeightage = maxWeightage - (i * 10);
                
//                 quoteWeightages.put(supplierId, quoteWeightage);
                
//                 logger.info("    Price Rank {} (₹{}): {} → Weightage = {}", 
//                            i + 1,
//                            price,
//                            supplierName,
//                            quoteWeightage);
//             }

//             // Step 4: Calculate Final Scores
//             logger.info("\n  🎯 STEP 4: Calculate Final Scores");
//             logger.info("    Formula: Total Score + Quote Weightage");
            
//             List<SupplierRecommendation> recommendations = new ArrayList<>();

//             for (RFQSupplier rfqSupplier : respondedSuppliers) {
//                 Long supplierId = rfqSupplier.getSupplier().getId();
//                 String supplierName = rfqSupplier.getSupplier().getCompanyName();
                
//                 BigDecimal totalScore = supplierTotalScores.get(supplierId);
//                 Integer quoteWeightage = quoteWeightages.get(supplierId);
//                 BigDecimal quoteAmount = supplierQuoteAmounts.get(supplierId);

//                 if (totalScore == null || quoteWeightage == null || quoteAmount == null) {
//                     logger.error("    ❌ Missing data for {}", supplierName);
//                     continue;
//                 }

//                 // ✅ FINAL FORMULA: Total Score + Quote Weightage
//                 BigDecimal finalScore = totalScore.add(new BigDecimal(quoteWeightage))
//                         .setScale(2, RoundingMode.HALF_UP);

//                 SupplierRecommendation recommendation = new SupplierRecommendation();
//                 recommendation.setRfq(rfqSupplier.getRfq());
//                 recommendation.setSupplier(rfqSupplier.getSupplier());
//                 recommendation.setTotalCriteriaScore(totalScore);
//                 recommendation.setQualityWeightage(new BigDecimal("100")); // Not used
//                 recommendation.setQuoteAmount(quoteAmount);
//                 recommendation.setPriceScore(new BigDecimal(quoteWeightage)); // Store quote weightage
//                 recommendation.setQuoteWeightage(new BigDecimal("0")); // Not used
//                 recommendation.setFinalScore(finalScore);

//                 recommendations.add(recommendation);

//                 logger.info("    {} = {} + {} = {}", 
//                            supplierName,
//                            totalScore,
//                            quoteWeightage,
//                            finalScore);
//             }

//             // Step 5: Sort by final score (HIGH to LOW) and assign ranks
//             logger.info("\n  🏆 STEP 5: Final Rankings");
//             recommendations.sort((a, b) -> b.getFinalScore().compareTo(a.getFinalScore()));

//             for (int i = 0; i < recommendations.size(); i++) {
//                 recommendations.get(i).setRank(i + 1);
//                 recommendations.get(i).setIsRecommended(i < 5); // Top 5
                
//                 SupplierRecommendation rec = recommendations.get(i);
//                 logger.info("    Rank {}: {} - Final: {} (Criteria: {}, Quote Weight: +{}, Price: ₹{}){}", 
//                            rec.getRank(),
//                            rec.getSupplier().getCompanyName(),
//                            rec.getFinalScore(),
//                            rec.getTotalCriteriaScore(),
//                            rec.getPriceScore().intValue(),
//                            rec.getQuoteAmount(),
//                            rec.getIsRecommended() ? " ⭐ RECOMMENDED" : "");
//             }

//             // Save all
//             recommendationRepository.saveAll(recommendations);

//             logger.info("\n  ✅ SUCCESS: Rankings calculated and saved");
//             logger.info("  🌟 Top 5 suppliers marked as recommended");
//             logger.info("=" .repeat(80));

//             return String.format("Successfully calculated rankings for %d suppliers", recommendations.size());

//         } catch (Exception e) {
//             logger.error("❌ ERROR in calculateFinalRankings: {}", e.getMessage());
//             logger.error("Stack trace:", e);
//             throw new RuntimeException("Failed to calculate rankings: " + e.getMessage(), e);
//         }
//     }

//     /**
//      * Get supplier rankings
//      */
//     @Transactional(readOnly = true)
//     public List<Map<String, Object>> getSupplierRankings(Long rfqId) {
//         logger.info("🏆 [GET SUPPLIER RANKINGS] RFQ ID: {}", rfqId);

//         List<SupplierRecommendation> recommendations = recommendationRepository
//                 .findByRfqIdOrderByRank(rfqId);

//         if (recommendations.isEmpty()) {
//             logger.warn("  ⚠️  No rankings found - may need to calculate first");
//             return Collections.emptyList();
//         }

//         List<Map<String, Object>> rankings = recommendations.stream()
//                 .map(rec -> {
//                     Map<String, Object> ranking = new HashMap<>();
//                     ranking.put("rank", rec.getRank());
//                     ranking.put("supplierId", rec.getSupplier().getId());
//                     ranking.put("supplierName", rec.getSupplier().getCompanyName());
//                     ranking.put("totalScore", rec.getTotalCriteriaScore());
//                     ranking.put("quoteAmount", rec.getQuoteAmount());
//                     ranking.put("quoteWeightage", rec.getPriceScore()); // This is the quote weightage
//                     ranking.put("finalScore", rec.getFinalScore());
//                     ranking.put("isRecommended", rec.getIsRecommended());
                    
//                     // Get score breakdown
//                     List<SupplierScore> scores = supplierScoreRepository
//                             .findByRfqIdAndSupplierId(rfqId, rec.getSupplier().getId());
                    
//                     List<Map<String, Object>> scoreBreakdown = scores.stream()
//                             .map(score -> {
//                                 Map<String, Object> detail = new HashMap<>();
//                                 detail.put("criterion", score.getRfqCriterion().getCriterion().getCriterionName());
//                                 detail.put("weightage", score.getRfqCriterion().getWeightage());
//                                 detail.put("maxScore", MAX_SCORE);
//                                 detail.put("rawScore", score.getRawScore());
//                                 detail.put("weightedScore", score.getWeightedScore());
//                                 return detail;
//                             })
//                             .collect(Collectors.toList());
                    
//                     ranking.put("scoreBreakdown", scoreBreakdown);
//                     ranking.put("criteriaScored", scores.size());
                    
//                     return ranking;
//                 })
//                 .collect(Collectors.toList());

//         logger.info("  ✅ Returned {} rankings", rankings.size());
//         return rankings;
//     }

//     /**
//      * Get detailed scorecard for supplier
//      */
//     @Transactional(readOnly = true)
//     public Map<String, Object> getSupplierScorecard(Long rfqId, Long supplierId) {
//         logger.info("📊 [GET SUPPLIER SCORECARD] RFQ: {}, Supplier: {}", rfqId, supplierId);

//         List<SupplierScore> scores = supplierScoreRepository.findByRfqIdAndSupplierId(rfqId, supplierId);

//         if (scores.isEmpty()) {
//             throw new RuntimeException("No scores found for this supplier");
//         }

//         Supplier supplier = scores.get(0).getSupplier();
//         BigDecimal totalScore = scores.stream()
//                 .map(SupplierScore::getWeightedScore)
//                 .reduce(BigDecimal.ZERO, BigDecimal::add);

//         // Get recommendation if exists
//         Optional<SupplierRecommendation> recommendationOpt = recommendationRepository
//                 .findByRfqIdAndSupplierId(rfqId, supplierId);

//         Map<String, Object> scorecard = new HashMap<>();
//         scorecard.put("supplierId", supplierId);
//         scorecard.put("supplierName", supplier.getCompanyName());
//         scorecard.put("totalScore", totalScore);
//         scorecard.put("maxPossibleScore", 100.0);
//         scorecard.put("scorePercentage", totalScore.doubleValue());
        
//         if (recommendationOpt.isPresent()) {
//             SupplierRecommendation rec = recommendationOpt.get();
//             scorecard.put("rank", rec.getRank());
//             scorecard.put("finalScore", rec.getFinalScore());
//             scorecard.put("isRecommended", rec.getIsRecommended());
//             scorecard.put("quoteAmount", rec.getQuoteAmount());
//             scorecard.put("quoteWeightage", rec.getPriceScore());
//         }
        
//         List<Map<String, Object>> criteriaScores = scores.stream()
//                 .map(score -> {
//                     Map<String, Object> detail = new HashMap<>();
//                     detail.put("criterionId", score.getRfqCriterion().getCriterion().getId());
//                     detail.put("criterionName", score.getRfqCriterion().getCriterion().getCriterionName());
//                     detail.put("weightage", score.getRfqCriterion().getWeightage());
//                     detail.put("maxScore", MAX_SCORE);
//                     detail.put("rawScore", score.getRawScore());
//                     detail.put("weightedScore", score.getWeightedScore());
//                     detail.put("comments", score.getComments());
//                     detail.put("scoredBy", score.getScoredBy());
//                     detail.put("scoredAt", score.getScoredAt());
//                     return detail;
//                 })
//                 .collect(Collectors.toList());

//         scorecard.put("criteriaScores", criteriaScores);

//         logger.info("  ✅ Scorecard generated - Total: {}/100", totalScore);

//         return scorecard;
//     }

//     // ==================== ADMIN CRUD ====================

//     @Transactional(readOnly = true)
//     public List<EvaluationCriterion> getAllCriteria() {
//         logger.info("📋 [GET ALL CRITERIA]");
//         List<EvaluationCriterion> criteria = criterionRepository.findAll();
//         logger.info("  ✅ Found {} criteria", criteria.size());
//         return criteria;
//     }

//     @Transactional
//     public EvaluationCriterion updateCriterion(Long id, EvaluationCriterion criterionData) {
//         logger.info("✏️ [UPDATE CRITERION] ID: {}", id);
        
//         EvaluationCriterion existing = criterionRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Criterion not found: " + id));
        
//         if (criterionData.getCriterionName() != null) {
//             existing.setCriterionName(criterionData.getCriterionName());
//         }
//         if (criterionData.getDescription() != null) {
//             existing.setDescription(criterionData.getDescription());
//         }
        
//         existing.setMaxScore(MAX_SCORE);
        
//         EvaluationCriterion updated = criterionRepository.save(existing);
//         logger.info("  ✅ Criterion {} updated", id);
//         return updated;
//     }

//     @Transactional
//     public void deleteCriterion(Long id) {
//         logger.info("🗑️ [DELETE CRITERION] ID: {}", id);
        
//         EvaluationCriterion criterion = criterionRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Criterion not found: " + id));
        
//         long usageCount = rfqCriterionRepository.countByCriterionId(id);
//         if (usageCount > 0) {
//             throw new RuntimeException(
//                 String.format("Cannot delete '%s'. Used in %d RFQ(s)", 
//                              criterion.getCriterionName(), usageCount)
//             );
//         }
        
//         criterionRepository.deleteById(id);
//         logger.info("  ✅ Criterion {} deleted", id);
//     }

//     @Transactional
//     public EvaluationCriterion activateCriterion(Long id) {
//         logger.info("✅ [ACTIVATE CRITERION] ID: {}", id);
        
//         EvaluationCriterion criterion = criterionRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Criterion not found: " + id));
        
//         criterion.setIsActive(true);
//         EvaluationCriterion saved = criterionRepository.save(criterion);
        
//         logger.info("  ✅ Activated", id);
//         return saved;
//     }

//     @Transactional
//     public EvaluationCriterion deactivateCriterion(Long id) {
//         logger.info("❌ [DEACTIVATE CRITERION] ID: {}", id);
        
//         EvaluationCriterion criterion = criterionRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Criterion not found: " + id));
        
//         criterion.setIsActive(false);
//         EvaluationCriterion saved = criterionRepository.save(criterion);
        
//         logger.info("  ✅ Deactivated", id);
//         return saved;
//     }
// }


package com.itti.leadcapturing.service;

import com.itti.leadcapturing.dto.RFQCriterionDTO;
import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ✅ UPDATED: Added DELETE and EDIT RFQ Criteria functionality
 * - Delete RFQ Criterion (with confirmation)
 * - Edit RFQ Criterion weightage (with 100% validation)
 * - Delete associated supplier scores when criterion is deleted
 */
@Service
public class SupplierEvaluationService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierEvaluationService.class);
    private static final int MAX_SCORE = 5; // ✅ FIXED

    @Autowired
    private EvaluationCriterionRepository criterionRepository;

    @Autowired
    private RFQCriterionRepository rfqCriterionRepository;

    @Autowired
    private SupplierScoreRepository supplierScoreRepository;

    @Autowired
    private RFQSupplierRepository rfqSupplierRepository;

    @Autowired
    private RFQRepository rfqRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierRecommendationRepository recommendationRepository;

    // ==================== CRITERION MANAGEMENT (ADMIN) ====================

    /**
     * Get all active criteria (for RFQ Creator to select from)
     */
    @Transactional(readOnly = true)
    public List<EvaluationCriterion> getAllActiveCriteria() {
        logger.info("📋 [GET ACTIVE CRITERIA]");
        List<EvaluationCriterion> criteria = criterionRepository.findAllActive();
        logger.info("  ✅ Found {} active criteria", criteria.size());
        return criteria;
    }

    /**
     * Create new evaluation criterion (Admin only)
     */
    @Transactional
    public EvaluationCriterion createCriterion(EvaluationCriterion criterion) {
        logger.info("➕ [CREATE CRITERION] Name: {}", criterion.getCriterionName());
        
        // ✅ FORCE maxScore = 5
        criterion.setMaxScore(MAX_SCORE);
        
        EvaluationCriterion saved = criterionRepository.save(criterion);
        logger.info("  ✅ Criterion created with ID: {} (maxScore forced to {})", saved.getId(), MAX_SCORE);
        return saved;
    }

    // ==================== RFQ CRITERION ASSIGNMENT ====================

    /**
     * Assign criteria to RFQ with weightages
     * Weightages must sum to 100%
     */
    @Transactional
    public String assignCriteriaToRFQ(Long rfqId, Map<Long, Double> criteriaWeightages, Long userId) {
        logger.info("=" .repeat(80));
        logger.info("🎯 [ASSIGN CRITERIA TO RFQ] RFQ ID: {}, User: {}", rfqId, userId);
        logger.info("  Criteria to assign: {}", criteriaWeightages.size());

        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));

        // Validate total = 100%
        double totalWeightage = criteriaWeightages.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        if (Math.abs(totalWeightage - 100.0) > 0.01) {
            throw new RuntimeException(
                String.format("Total weightage must be 100%%. Current: %.2f%%", totalWeightage)
            );
        }

        logger.info("  ✅ Weightage validation passed: 100.00%");

        // Clear existing
        rfqCriterionRepository.deleteByRfqId(rfqId);
        logger.info("  🗑️  Cleared existing criteria");

        // Add new
        int assignedCount = 0;
        for (Map.Entry<Long, Double> entry : criteriaWeightages.entrySet()) {
            Long criterionId = entry.getKey();
            Double weightage = entry.getValue();

            EvaluationCriterion criterion = criterionRepository.findById(criterionId)
                    .orElseThrow(() -> new RuntimeException("Criterion not found: " + criterionId));

            RFQCriterion rfqCriterion = new RFQCriterion(rfq, criterion, weightage, userId);
            rfqCriterion.setIsRfqSpecific(false);
            rfqCriterionRepository.save(rfqCriterion);

            logger.info("    ✓ {} - {}%", criterion.getCriterionName(), weightage);
            assignedCount++;
        }

        logger.info("  ✅ Assigned {} criteria to RFQ", assignedCount);
        logger.info("=" .repeat(80));

        return String.format("Successfully assigned %d criteria to RFQ", assignedCount);
    }

    /**
     * Get criteria assigned to RFQ (returns DTOs)
     */
    @Transactional(readOnly = true)
    public List<RFQCriterionDTO> getRFQCriteria(Long rfqId) {
        logger.info("📋 [GET RFQ CRITERIA] RFQ ID: {}", rfqId);
        
        List<RFQCriterion> criteria = rfqCriterionRepository.findByRfqIdWithCriterion(rfqId);
        
        List<RFQCriterionDTO> dtos = criteria.stream()
                .map(this::convertToRFQCriterionDTO)
                .collect(Collectors.toList());
        
        logger.info("  ✅ Found {} criteria", dtos.size());
        return dtos;
    }

    private RFQCriterionDTO convertToRFQCriterionDTO(RFQCriterion rfqCriterion) {
        RFQCriterionDTO dto = new RFQCriterionDTO();
        dto.setId(rfqCriterion.getId());
        dto.setRfqId(rfqCriterion.getRfq().getId());
        dto.setWeightage(rfqCriterion.getWeightage());
        dto.setCreatedBy(rfqCriterion.getCreatedBy());
        dto.setIsRfqSpecific(rfqCriterion.getIsRfqSpecific() != null 
            ? rfqCriterion.getIsRfqSpecific() 
            : false);
        
        EvaluationCriterion criterion = rfqCriterion.getCriterion();
        RFQCriterionDTO.CriterionDTO criterionDTO = new RFQCriterionDTO.CriterionDTO(
            criterion.getId(),
            criterion.getCriterionName(),
            criterion.getDescription(),
            criterion.getCriterionType().name(),
            MAX_SCORE, // ✅ ALWAYS 5
            criterion.getIsActive()
        );
        dto.setCriterion(criterionDTO);
        
        return dto;
    }

    // ==================== ✅ NEW: DELETE RFQ CRITERION ====================

    /**
     * ✅ NEW: Delete RFQ Criterion and all associated scores
     * Used when RFQ creator wants to remove a criterion from evaluation
     */
    @Transactional
    public String deleteRFQCriterion(Long rfqCriterionId) {
        logger.info("=" .repeat(80));
        logger.info("🗑️  [DELETE RFQ CRITERION] ID: {}", rfqCriterionId);

        RFQCriterion rfqCriterion = rfqCriterionRepository.findById(rfqCriterionId)
                .orElseThrow(() -> new RuntimeException("RFQ Criterion not found: " + rfqCriterionId));

        String criterionName = rfqCriterion.getCriterion().getCriterionName();
        Long rfqId = rfqCriterion.getRfq().getId();

        logger.info("  Criterion: {}", criterionName);
        logger.info("  RFQ ID: {}", rfqId);

        // ✅ Delete all supplier scores for this criterion
        List<SupplierScore> scores = supplierScoreRepository.findByRfqId(rfqId).stream()
                .filter(s -> s.getRfqCriterion().getId().equals(rfqCriterionId))
                .collect(Collectors.toList());

        if (!scores.isEmpty()) {
            supplierScoreRepository.deleteAll(scores);
            logger.info("  🗑️  Deleted {} associated supplier scores", scores.size());
        }

        // ✅ Delete the RFQ criterion
        rfqCriterionRepository.delete(rfqCriterion);
        logger.info("  ✅ RFQ Criterion deleted: {}", criterionName);
        logger.info("=" .repeat(80));

        return String.format("Successfully deleted criterion '%s' and %d associated scores", 
                           criterionName, scores.size());
    }

    // ==================== ✅ NEW: EDIT RFQ CRITERION WEIGHTAGE ====================

    /**
     * ✅ NEW: Edit RFQ Criterion weightage
     * Validates that total weightage = 100% after edit
     */
    @Transactional
    public String updateRFQCriterionWeightage(Long rfqCriterionId, Integer newWeightage) {
        logger.info("=" .repeat(80));
        logger.info("✏️ [UPDATE RFQ CRITERION WEIGHTAGE] ID: {}, New: {}%", rfqCriterionId, newWeightage);

        // ✅ Validate weightage is integer (1-99)
        if (newWeightage == null || newWeightage < 1 || newWeightage > 99) {
            throw new RuntimeException("Weightage must be between 1 and 99");
        }

        RFQCriterion rfqCriterion = rfqCriterionRepository.findById(rfqCriterionId)
                .orElseThrow(() -> new RuntimeException("RFQ Criterion not found: " + rfqCriterionId));

        Long rfqId = rfqCriterion.getRfq().getId();
        String criterionName = rfqCriterion.getCriterion().getCriterionName();

        logger.info("  Criterion: {}", criterionName);
        logger.info("  Old weightage: {}%", rfqCriterion.getWeightage());

        // Update weightage
        rfqCriterion.setWeightage(newWeightage.doubleValue());
        rfqCriterionRepository.save(rfqCriterion);

        // ✅ Validate total = 100%
        Double totalWeightage = rfqCriterionRepository.sumWeightageByRfqId(rfqId);
        
        logger.info("  New weightage: {}%", newWeightage);
        logger.info("  Total weightage: {}%", totalWeightage);

        if (totalWeightage == null || Math.abs(totalWeightage - 100.0) > 0.01) {
            logger.error("  ❌ Total weightage is not 100%: {}", totalWeightage);
            throw new RuntimeException(
                String.format("Total weightage must be 100%%. Current: %.2f%%", totalWeightage)
            );
        }

        logger.info("  ✅ Weightage updated successfully");
        logger.info("=" .repeat(80));

        return String.format("Successfully updated '%s' weightage to %d%%", criterionName, newWeightage);
    }

    // ==================== SUPPLIER SCORING ====================

    /**
     * ✅ Submit scores for supplier (RFQ Creator rates suppliers)
     * scores: Map<rfqCriterionId, rawScore (1-99)>
     */
    @Transactional
    public String scoreSupplier(Long rfqId, Long supplierId, Map<Long, BigDecimal> scores, Long userId) {
        logger.info("=" .repeat(80));
        logger.info("📊 [SCORE SUPPLIER] RFQ: {}, Supplier: {}, Scorer: {}", rfqId, supplierId, userId);

        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));

        List<RFQCriterion> rfqCriteria = rfqCriterionRepository.findByRfqIdWithCriterion(rfqId);
        
        if (rfqCriteria.isEmpty()) {
            throw new RuntimeException("No criteria assigned. Please assign criteria first.");
        }

        int scoredCount = 0;

        for (RFQCriterion rfqCriterion : rfqCriteria) {
            BigDecimal rawScore = scores.get(rfqCriterion.getId());
            
            if (rawScore == null) {
                logger.warn("  ⚠️  No score for: {}", rfqCriterion.getCriterion().getCriterionName());
                continue;
            }

            // ✅ Validate: must be 1-5 (integers only)
            if (rawScore.compareTo(BigDecimal.ONE) < 0 || 
                rawScore.compareTo(new BigDecimal(MAX_SCORE)) > 0) {
                throw new RuntimeException(
                    String.format("Score for %s must be 1-%d (got %s)", 
                                 rfqCriterion.getCriterion().getCriterionName(), 
                                 MAX_SCORE,
                                 rawScore)
                );
            }

            // ✅ Check if it's an integer (no decimals)
            if (rawScore.stripTrailingZeros().scale() > 0) {
                throw new RuntimeException(
                    String.format("Score for %s must be an integer (no decimals)", 
                                 rfqCriterion.getCriterion().getCriterionName())
                );
            }

            // Save score
            SupplierScore supplierScore = supplierScoreRepository
                    .findByRfqIdAndSupplierIdAndCriterionId(rfqId, supplierId, rfqCriterion.getId())
                    .orElse(new SupplierScore());

            supplierScore.setRfq(rfq);
            supplierScore.setSupplier(supplier);
            supplierScore.setRfqCriterion(rfqCriterion);
            supplierScore.setRawScore(rawScore);
            supplierScore.setScoredBy(userId);
            supplierScore.setIsAutoCalculated(false);
            supplierScore.calculateWeightedScore(); // Auto-calculates: (rawScore/5) × weightage

            supplierScoreRepository.save(supplierScore);

            logger.info("    ✓ {} - Raw: {}/{}, Weighted: {}", 
                       rfqCriterion.getCriterion().getCriterionName(),
                       rawScore, MAX_SCORE,
                       supplierScore.getWeightedScore());
            scoredCount++;
        }

        logger.info("  ✅ Scored {} criteria", scoredCount);
        logger.info("=" .repeat(80));

        return String.format("Scored %d criteria successfully", scoredCount);
    }

    // ==================== ✅ FIXED: AUTOMATIC FINAL RANKING ====================

    /**
     * ✅ FIXED: Calculate final rankings with AUTOMATIC quote weightage
     * 
     * Logic:
     * 1. Get total criteria scores for all suppliers
     * 2. Sort suppliers by quote amount (LOW to HIGH)
     * 3. Calculate quote weightage automatically:
     *    - Formula: (numSuppliers × 10) - (priceRank - 1) × 10
     *    - Lowest price gets highest weightage (numSuppliers × 10)
     *    - Each rank down reduces by 10
     *    - Highest price gets 10
     * 4. Final Score = Total Criteria Score + Quote Weightage
     * 5. Rank by Final Score (HIGH to LOW)
     * 6. Top 5 are recommended
     */
    @Transactional
    public String calculateFinalRankings(Long rfqId) {
        logger.info("=" .repeat(80));
        logger.info("🏆 [CALCULATE FINAL RANKINGS - AUTOMATIC] RFQ ID: {}", rfqId);

        try {
            // Clear existing recommendations
            recommendationRepository.deleteByRfqId(rfqId);
            logger.info("  🗑️  Cleared existing recommendations");

            // Get all suppliers who responded
            List<RFQSupplier> respondedSuppliers = rfqSupplierRepository.findByRFQAndStatus(rfqId, "RESPONDED");
            
            if (respondedSuppliers.isEmpty()) {
                throw new RuntimeException("No suppliers have submitted quotes");
            }

            int numSuppliers = respondedSuppliers.size();
            logger.info("  👥 Total suppliers with quotes: {}", numSuppliers);

            // Step 1: Get total criteria scores and quote amounts
            Map<Long, BigDecimal> supplierTotalScores = new HashMap<>();
            Map<Long, BigDecimal> supplierQuoteAmounts = new HashMap<>();

            logger.info("\n  📊 STEP 1: Calculate Criteria Scores");
            for (RFQSupplier rfqSupplier : respondedSuppliers) {
                Long supplierId = rfqSupplier.getSupplier().getId();
                
                BigDecimal totalScore = supplierScoreRepository
                        .calculateTotalScoreForSupplier(rfqId, supplierId);
                
                if (totalScore == null) {
                    logger.warn("    ⚠️  No scores found for {}, using 0", 
                               rfqSupplier.getSupplier().getCompanyName());
                    totalScore = BigDecimal.ZERO;
                }

                BigDecimal quoteAmount = rfqSupplier.getQuoteAmount();
                if (quoteAmount == null) {
                    throw new RuntimeException(
                        String.format("Supplier %s has no quote amount", 
                                    rfqSupplier.getSupplier().getCompanyName())
                    );
                }

                supplierTotalScores.put(supplierId, totalScore);
                supplierQuoteAmounts.put(supplierId, quoteAmount);

                logger.info("    {} - Score: {}/100, Quote: ₹{}", 
                           rfqSupplier.getSupplier().getCompanyName(),
                           totalScore,
                           quoteAmount);
            }

            // Step 2: Sort by price (LOW to HIGH for ranking)
            logger.info("\n  💰 STEP 2: Price Ranking (Low to High)");
            List<RFQSupplier> sortedByPrice = new ArrayList<>(respondedSuppliers);
            sortedByPrice.sort(Comparator.comparing(s -> s.getQuoteAmount()));

            for (int i = 0; i < sortedByPrice.size(); i++) {
                RFQSupplier s = sortedByPrice.get(i);
                logger.info("    Price Rank {}: {} - ₹{}", 
                           i + 1,
                           s.getSupplier().getCompanyName(),
                           s.getQuoteAmount());
            }

            // Step 3: Calculate AUTOMATIC quote weightages
            logger.info("\n  ⚖️  STEP 3: Automatic Quote Weightage Calculation");
            Map<Long, Integer> quoteWeightages = new HashMap<>();
            int maxWeightage = numSuppliers * 10;
            
            logger.info("    Formula: (numSuppliers × 10) - (rank - 1) × 10");
            logger.info("    Max Weightage: {} (for {} suppliers)", maxWeightage, numSuppliers);
            
            for (int i = 0; i < sortedByPrice.size(); i++) {
                Long supplierId = sortedByPrice.get(i).getSupplier().getId();
                String supplierName = sortedByPrice.get(i).getSupplier().getCompanyName();
                BigDecimal price = sortedByPrice.get(i).getQuoteAmount();
                
                // ✅ Formula: Lower price = Higher weightage
                int quoteWeightage = maxWeightage - (i * 10);
                
                quoteWeightages.put(supplierId, quoteWeightage);
                
                logger.info("    Price Rank {} (₹{}): {} → Weightage = {}", 
                           i + 1,
                           price,
                           supplierName,
                           quoteWeightage);
            }

            // Step 4: Calculate Final Scores
            logger.info("\n  🎯 STEP 4: Calculate Final Scores");
            logger.info("    Formula: Total Score + Quote Weightage");
            
            List<SupplierRecommendation> recommendations = new ArrayList<>();

            for (RFQSupplier rfqSupplier : respondedSuppliers) {
                Long supplierId = rfqSupplier.getSupplier().getId();
                String supplierName = rfqSupplier.getSupplier().getCompanyName();
                
                BigDecimal totalScore = supplierTotalScores.get(supplierId);
                Integer quoteWeightage = quoteWeightages.get(supplierId);
                BigDecimal quoteAmount = supplierQuoteAmounts.get(supplierId);

                if (totalScore == null || quoteWeightage == null || quoteAmount == null) {
                    logger.error("    ❌ Missing data for {}", supplierName);
                    continue;
                }

                // ✅ FINAL FORMULA: Total Score + Quote Weightage
                BigDecimal finalScore = totalScore.add(new BigDecimal(quoteWeightage))
                        .setScale(2, RoundingMode.HALF_UP);

                SupplierRecommendation recommendation = new SupplierRecommendation();
                recommendation.setRfq(rfqSupplier.getRfq());
                recommendation.setSupplier(rfqSupplier.getSupplier());
                recommendation.setTotalCriteriaScore(totalScore);
                recommendation.setQualityWeightage(new BigDecimal("100")); // Not used
                recommendation.setQuoteAmount(quoteAmount);
                recommendation.setPriceScore(new BigDecimal(quoteWeightage)); // Store quote weightage
                recommendation.setQuoteWeightage(new BigDecimal("0")); // Not used
                recommendation.setFinalScore(finalScore);

                recommendations.add(recommendation);

                logger.info("    {} = {} + {} = {}", 
                           supplierName,
                           totalScore,
                           quoteWeightage,
                           finalScore);
            }

            // Step 5: Sort by final score (HIGH to LOW) and assign ranks
            logger.info("\n  🏆 STEP 5: Final Rankings");
            recommendations.sort((a, b) -> b.getFinalScore().compareTo(a.getFinalScore()));

            for (int i = 0; i < recommendations.size(); i++) {
                recommendations.get(i).setRank(i + 1);
                recommendations.get(i).setIsRecommended(i < 5); // Top 5
                
                SupplierRecommendation rec = recommendations.get(i);
                logger.info("    Rank {}: {} - Final: {} (Criteria: {}, Quote Weight: +{}, Price: ₹{}){}", 
                           rec.getRank(),
                           rec.getSupplier().getCompanyName(),
                           rec.getFinalScore(),
                           rec.getTotalCriteriaScore(),
                           rec.getPriceScore().intValue(),
                           rec.getQuoteAmount(),
                           rec.getIsRecommended() ? " ⭐ RECOMMENDED" : "");
            }

            // Save all
            recommendationRepository.saveAll(recommendations);

            logger.info("\n  ✅ SUCCESS: Rankings calculated and saved");
            logger.info("  🌟 Top 5 suppliers marked as recommended");
            logger.info("=" .repeat(80));

            return String.format("Successfully calculated rankings for %d suppliers", recommendations.size());

        } catch (Exception e) {
            logger.error("❌ ERROR in calculateFinalRankings: {}", e.getMessage());
            logger.error("Stack trace:", e);
            throw new RuntimeException("Failed to calculate rankings: " + e.getMessage(), e);
        }
    }

    /**
     * Get supplier rankings
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSupplierRankings(Long rfqId) {
        logger.info("🏆 [GET SUPPLIER RANKINGS] RFQ ID: {}", rfqId);

        List<SupplierRecommendation> recommendations = recommendationRepository
                .findByRfqIdOrderByRank(rfqId);

        if (recommendations.isEmpty()) {
            logger.warn("  ⚠️  No rankings found - may need to calculate first");
            return Collections.emptyList();
        }

        List<Map<String, Object>> rankings = recommendations.stream()
                .map(rec -> {
                    Map<String, Object> ranking = new HashMap<>();
                    ranking.put("rank", rec.getRank());
                    ranking.put("supplierId", rec.getSupplier().getId());
                    ranking.put("supplierName", rec.getSupplier().getCompanyName());
                    ranking.put("totalScore", rec.getTotalCriteriaScore());
                    ranking.put("quoteAmount", rec.getQuoteAmount());
                    ranking.put("quoteWeightage", rec.getPriceScore()); // This is the quote weightage
                    ranking.put("finalScore", rec.getFinalScore());
                    ranking.put("isRecommended", rec.getIsRecommended());
                    
                    // Get score breakdown
                    List<SupplierScore> scores = supplierScoreRepository
                            .findByRfqIdAndSupplierId(rfqId, rec.getSupplier().getId());
                    
                    List<Map<String, Object>> scoreBreakdown = scores.stream()
                            .map(score -> {
                                Map<String, Object> detail = new HashMap<>();
                                detail.put("criterion", score.getRfqCriterion().getCriterion().getCriterionName());
                                detail.put("weightage", score.getRfqCriterion().getWeightage());
                                detail.put("maxScore", MAX_SCORE);
                                detail.put("rawScore", score.getRawScore());
                                detail.put("weightedScore", score.getWeightedScore());
                                return detail;
                            })
                            .collect(Collectors.toList());
                    
                    ranking.put("scoreBreakdown", scoreBreakdown);
                    ranking.put("criteriaScored", scores.size());
                    
                    return ranking;
                })
                .collect(Collectors.toList());

        logger.info("  ✅ Returned {} rankings", rankings.size());
        return rankings;
    }

    /**
     * Get detailed scorecard for supplier
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSupplierScorecard(Long rfqId, Long supplierId) {
        logger.info("📊 [GET SUPPLIER SCORECARD] RFQ: {}, Supplier: {}", rfqId, supplierId);

        List<SupplierScore> scores = supplierScoreRepository.findByRfqIdAndSupplierId(rfqId, supplierId);

        if (scores.isEmpty()) {
            throw new RuntimeException("No scores found for this supplier");
        }

        Supplier supplier = scores.get(0).getSupplier();
        BigDecimal totalScore = scores.stream()
                .map(SupplierScore::getWeightedScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Get recommendation if exists
        Optional<SupplierRecommendation> recommendationOpt = recommendationRepository
                .findByRfqIdAndSupplierId(rfqId, supplierId);

        Map<String, Object> scorecard = new HashMap<>();
        scorecard.put("supplierId", supplierId);
        scorecard.put("supplierName", supplier.getCompanyName());
        scorecard.put("totalScore", totalScore);
        scorecard.put("maxPossibleScore", 100.0);
        scorecard.put("scorePercentage", totalScore.doubleValue());
        
        if (recommendationOpt.isPresent()) {
            SupplierRecommendation rec = recommendationOpt.get();
            scorecard.put("rank", rec.getRank());
            scorecard.put("finalScore", rec.getFinalScore());
            scorecard.put("isRecommended", rec.getIsRecommended());
            scorecard.put("quoteAmount", rec.getQuoteAmount());
            scorecard.put("quoteWeightage", rec.getPriceScore());
        }
        
        List<Map<String, Object>> criteriaScores = scores.stream()
                .map(score -> {
                    Map<String, Object> detail = new HashMap<>();
                    detail.put("criterionId", score.getRfqCriterion().getCriterion().getId());
                    detail.put("criterionName", score.getRfqCriterion().getCriterion().getCriterionName());
                    detail.put("weightage", score.getRfqCriterion().getWeightage());
                    detail.put("maxScore", MAX_SCORE);
                    detail.put("rawScore", score.getRawScore());
                    detail.put("weightedScore", score.getWeightedScore());
                    detail.put("comments", score.getComments());
                    detail.put("scoredBy", score.getScoredBy());
                    detail.put("scoredAt", score.getScoredAt());
                    return detail;
                })
                .collect(Collectors.toList());

        scorecard.put("criteriaScores", criteriaScores);

        logger.info("  ✅ Scorecard generated - Total: {}/100", totalScore);

        return scorecard;
    }

    // ==================== ADMIN CRUD ====================

    @Transactional(readOnly = true)
    public List<EvaluationCriterion> getAllCriteria() {
        logger.info("📋 [GET ALL CRITERIA]");
        List<EvaluationCriterion> criteria = criterionRepository.findAll();
        logger.info("  ✅ Found {} criteria", criteria.size());
        return criteria;
    }

    @Transactional
    public EvaluationCriterion updateCriterion(Long id, EvaluationCriterion criterionData) {
        logger.info("✏️ [UPDATE CRITERION] ID: {}", id);
        
        EvaluationCriterion existing = criterionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Criterion not found: " + id));
        
        if (criterionData.getCriterionName() != null) {
            existing.setCriterionName(criterionData.getCriterionName());
        }
        if (criterionData.getDescription() != null) {
            existing.setDescription(criterionData.getDescription());
        }
        
        existing.setMaxScore(MAX_SCORE);
        
        EvaluationCriterion updated = criterionRepository.save(existing);
        logger.info("  ✅ Criterion {} updated", id);
        return updated;
    }

    @Transactional
    public void deleteCriterion(Long id) {
        logger.info("🗑️ [DELETE CRITERION] ID: {}", id);
        
        EvaluationCriterion criterion = criterionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Criterion not found: " + id));
        
        long usageCount = rfqCriterionRepository.countByCriterionId(id);
        if (usageCount > 0) {
            throw new RuntimeException(
                String.format("Cannot delete '%s'. Used in %d RFQ(s)", 
                             criterion.getCriterionName(), usageCount)
            );
        }
        
        criterionRepository.deleteById(id);
        logger.info("  ✅ Criterion {} deleted", id);
    }

    @Transactional
    public EvaluationCriterion activateCriterion(Long id) {
        logger.info("✅ [ACTIVATE CRITERION] ID: {}", id);
        
        EvaluationCriterion criterion = criterionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Criterion not found: " + id));
        
        criterion.setIsActive(true);
        EvaluationCriterion saved = criterionRepository.save(criterion);
        
        logger.info("  ✅ Activated", id);
        return saved;
    }

    @Transactional
    public EvaluationCriterion deactivateCriterion(Long id) {
        logger.info("❌ [DEACTIVATE CRITERION] ID: {}", id);
        
        EvaluationCriterion criterion = criterionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Criterion not found: " + id));
        
        criterion.setIsActive(false);
        EvaluationCriterion saved = criterionRepository.save(criterion);
        
        logger.info("  ✅ Deactivated", id);
        return saved;
    }
}