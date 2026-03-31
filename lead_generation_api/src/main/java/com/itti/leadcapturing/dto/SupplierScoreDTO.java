package com.itti.leadcapturing.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Supplier Score
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierScoreDTO {
    
    private Long id;
    private Long supplierId;
    private String supplierName;
    private Long criterionId;
    private String criterionName;
    private BigDecimal rawScore;
    private BigDecimal weightedScore;
    private Double weightage;
    private Integer maxScore;
    private String comments;
    private Boolean isAutoCalculated;
    private LocalDateTime scoredAt;
}