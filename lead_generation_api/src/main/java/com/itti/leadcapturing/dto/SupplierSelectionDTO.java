package com.itti.leadcapturing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for supplier selection/rejection
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierSelectionDTO {
    
    private Long rfqId;
    private Long buyerUserId;
    private List<ItemSelection> selections;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemSelection {
        private Long itemId;
        private Long selectedQuoteId;
        private Long selectedSupplierId;
        private String buyerRemarks;
    }
}