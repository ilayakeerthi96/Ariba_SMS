package com.itti.leadcapturing.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Supplier Ranking Result
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierRankingDTO {
    
    private Integer rank;
    private Long supplierId;
    private String supplierName;
    private BigDecimal totalScore;
    private BigDecimal quoteAmount;
    private Integer criteriaScored;
    private List<ScoreBreakdownDTO> scoreBreakdown;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScoreBreakdownDTO {
        private String criterion;
        private Double weightage;
        private Integer maxScore;
        private BigDecimal rawScore;
        private BigDecimal weightedScore;
        private Boolean isAutoCalculated;
    }
}