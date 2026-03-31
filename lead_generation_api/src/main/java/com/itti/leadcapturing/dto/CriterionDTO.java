package com.itti.leadcapturing.dto;

import com.itti.leadcapturing.model.CriterionType;
import lombok.*;

/**
 * DTO for Evaluation Criterion
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriterionDTO {
    
    private Long id;
    private String criterionName;
    private String description;
    private CriterionType criterionType;
    private Integer maxScore;
    private Boolean isActive;
    private Integer displayOrder;
}