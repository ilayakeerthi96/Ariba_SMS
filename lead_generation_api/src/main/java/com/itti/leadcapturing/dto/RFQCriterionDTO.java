
package com.itti.leadcapturing.dto;

public class RFQCriterionDTO {
    private Long id;
    private Long rfqId;
    private CriterionDTO criterion;
    private Double weightage;
    private Long createdBy;
    private Boolean isRfqSpecific;
    
    public static class CriterionDTO {
        private Long id;
        private String criterionName;
        private String description;
        private String criterionType;
        private Integer maxScore;
        private Boolean isActive;
        
        public CriterionDTO() {}
        
        public CriterionDTO(Long id, String criterionName, String description, 
                           String criterionType, Integer maxScore, Boolean isActive) {
            this.id = id;
            this.criterionName = criterionName;
            this.description = description;
            this.criterionType = criterionType;
            this.maxScore = maxScore;
            this.isActive = isActive;
        }
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCriterionName() { return criterionName; }
        public void setCriterionName(String criterionName) { this.criterionName = criterionName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCriterionType() { return criterionType; }
        public void setCriterionType(String criterionType) { this.criterionType = criterionType; }
        public Integer getMaxScore() { return maxScore; }
        public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRfqId() { return rfqId; }
    public void setRfqId(Long rfqId) { this.rfqId = rfqId; }
    public CriterionDTO getCriterion() { return criterion; }
    public void setCriterion(CriterionDTO criterion) { this.criterion = criterion; }
    public Double getWeightage() { return weightage; }
    public void setWeightage(Double weightage) { this.weightage = weightage; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public Boolean getIsRfqSpecific() { return isRfqSpecific; }
    public void setIsRfqSpecific(Boolean isRfqSpecific) { this.isRfqSpecific = isRfqSpecific; }
}