package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.dto.RFQItemDTO;
import com.itti.leadcapturing.repo.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RFQItemService {

    @Autowired
    private RFQItemRepository rfqItemRepository;

    @Autowired
    private RFQRepository rfqRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== CREATE OPERATIONS ====================

    /**
     * Add item to RFQ with dynamic fields support
     * 
     * @param rfqId RFQ ID
     * @param itemDTO Item DTO with dynamic fields
     * @return Created RFQItem
     */
    @Transactional
    public RFQItem addItemToRFQ(Long rfqId, RFQItemDTO itemDTO) {
        System.out.println("[ITEM SERVICE] Adding item to RFQ ID: " + rfqId);
        System.out.println("  Item Description: " + itemDTO.getItemDescription());
        System.out.println("  Company Type: " + itemDTO.getCompanyType());
        System.out.println("  Has Dynamic Fields: " + itemDTO.hasDynamicFields());

        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found with ID: " + rfqId));

        if (!RFQStatus.DRAFT.equals(rfq.getStatus())) {
            throw new RuntimeException("Can only add items to DRAFT RFQ. Current status: " + rfq.getStatus());
        }

        RFQItem item = new RFQItem();
        item.setItemCode(itemDTO.getItemCode());
        item.setItemDescription(itemDTO.getItemDescription());
        item.setQuantity(itemDTO.getQuantity());
        item.setUom(itemDTO.getUom());
        item.setUnitPrice(itemDTO.getUnitPrice());
        item.setItemRequiredDate(itemDTO.getItemRequiredDate());
        item.setItemDescriptionDetailed(itemDTO.getItemDescriptionDetailed());
        item.setSpecifications(itemDTO.getSpecifications());
        item.setRfq(rfq);
        item.setItemOrder(rfq.getItems().size() + 1);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        // ==================== HANDLE DYNAMIC FIELDS ====================
        item.setCompanyType(itemDTO.getCompanyType());
        
        if (itemDTO.hasDynamicFields()) {
            try {
                String dynamicFieldsJson = objectMapper.writeValueAsString(itemDTO.getDynamicFields());
                item.setDynamicFields(dynamicFieldsJson);
                System.out.println("  Dynamic Fields Saved: " + dynamicFieldsJson);
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to serialize dynamic fields: " + e.getMessage());
                item.setDynamicFields(null);
            }
        }

        RFQItem savedItem = rfqItemRepository.save(item);
        System.out.println("[✓] Item added successfully with ID: " + savedItem.getId());
        return savedItem;
    }

    // ==================== UPDATE OPERATIONS ====================

    /**
     * Update RFQ item with dynamic fields support
     * 
     * @param itemId Item ID
     * @param itemDTO Item DTO with updated values
     * @return Updated RFQItem
     */
    @Transactional
    public RFQItem updateRFQItem(Long itemId, RFQItemDTO itemDTO) {
        System.out.println("[ITEM SERVICE] Updating item ID: " + itemId);

        RFQItem existing = rfqItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("RFQ Item not found with ID: " + itemId));

        // Update basic fields
        if (itemDTO.getItemCode() != null) {
            existing.setItemCode(itemDTO.getItemCode());
        }
        if (itemDTO.getItemDescription() != null) {
            existing.setItemDescription(itemDTO.getItemDescription());
        }
        if (itemDTO.getQuantity() != null) {
            existing.setQuantity(itemDTO.getQuantity());
        }
        if (itemDTO.getUom() != null) {
            existing.setUom(itemDTO.getUom());
        }
        if (itemDTO.getUnitPrice() != null) {
            existing.setUnitPrice(itemDTO.getUnitPrice());
        }
        if (itemDTO.getItemRequiredDate() != null) {
            existing.setItemRequiredDate(itemDTO.getItemRequiredDate());
        }
        if (itemDTO.getItemDescriptionDetailed() != null) {
            existing.setItemDescriptionDetailed(itemDTO.getItemDescriptionDetailed());
        }
        if (itemDTO.getSpecifications() != null) {
            existing.setSpecifications(itemDTO.getSpecifications());
        }

        // ==================== UPDATE DYNAMIC FIELDS ====================
        if (itemDTO.getCompanyType() != null) {
            existing.setCompanyType(itemDTO.getCompanyType());
            System.out.println("  Updated Company Type: " + itemDTO.getCompanyType());
        }

        if (itemDTO.hasDynamicFields()) {
            try {
                String dynamicFieldsJson = objectMapper.writeValueAsString(itemDTO.getDynamicFields());
                existing.setDynamicFields(dynamicFieldsJson);
                System.out.println("  Updated Dynamic Fields: " + dynamicFieldsJson);
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to serialize dynamic fields: " + e.getMessage());
            }
        }

        existing.setUpdatedAt(LocalDateTime.now());
        RFQItem updated = rfqItemRepository.save(existing);
        System.out.println("[✓] Item updated successfully");
        return updated;
    }

    // ==================== READ OPERATIONS ====================

    /**
     * Get all items for an RFQ
     * 
     * @param rfqId RFQ ID
     * @return List of RFQItemDTOs with dynamic fields
     */
    public List<RFQItemDTO> getItemsByRFQ(Long rfqId) {
        System.out.println("[ITEM SERVICE] Getting items for RFQ ID: " + rfqId);

        List<RFQItem> items = rfqItemRepository.findByRfqId(rfqId);
        System.out.println("  Found " + items.size() + " items");

        return items.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get item by ID
     * 
     * @param itemId Item ID
     * @return RFQItemDTO with dynamic fields
     */
    public RFQItemDTO getRFQItemById(Long itemId) {
        System.out.println("[ITEM SERVICE] Getting item ID: " + itemId);

        RFQItem item = rfqItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("RFQ Item not found with ID: " + itemId));

        return convertToDTO(item);
    }

    /**
     * Get items by company type
     * Useful for filtering items by company type
     * 
     * @param rfqId RFQ ID
     * @param companyType Company type
     * @return List of RFQItemDTOs matching company type
     */
    public List<RFQItemDTO> getItemsByCompanyType(Long rfqId, String companyType) {
        System.out.println("[ITEM SERVICE] Getting items for RFQ ID: " + rfqId + ", Company Type: " + companyType);

        List<RFQItem> items = rfqItemRepository.findByRfqId(rfqId);

        List<RFQItemDTO> filtered = items.stream()
                .filter(item -> companyType.equals(item.getCompanyType()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        System.out.println("  Found " + filtered.size() + " items with company type: " + companyType);
        return filtered;
    }

    // ==================== DELETE OPERATIONS ====================

    /**
     * Delete RFQ item
     * 
     * @param itemId Item ID
     */
    @Transactional
    public void deleteRFQItem(Long itemId) {
        System.out.println("[ITEM SERVICE] Deleting item ID: " + itemId);

        RFQItem item = rfqItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("RFQ Item not found with ID: " + itemId));

        rfqItemRepository.delete(item);
        System.out.println("[✓] Item deleted successfully");
    }

    // ==================== CONVERSION METHODS ====================

    /**
     * Convert RFQItem entity to DTO with dynamic fields
     * 
     * @param item RFQItem entity
     * @return RFQItemDTO
     */
    private RFQItemDTO convertToDTO(RFQItem item) {
        RFQItemDTO dto = new RFQItemDTO();
        dto.setId(item.getId());
        dto.setItemCode(item.getItemCode());
        dto.setItemDescription(item.getItemDescription());
        dto.setQuantity(item.getQuantity());
        dto.setUom(item.getUom());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setLineTotal(item.getLineTotal());
        dto.setItemRequiredDate(item.getItemRequiredDate());
        dto.setItemDescriptionDetailed(item.getItemDescriptionDetailed());
        dto.setSpecifications(item.getSpecifications());
        dto.setItemOrder(item.getItemOrder());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());

        // ==================== CONVERT DYNAMIC FIELDS ====================
        dto.setCompanyType(item.getCompanyType());

        if (item.getDynamicFields() != null && !item.getDynamicFields().isEmpty()) {
            try {
                Map<String, Object> dynamicFieldsMap = objectMapper.readValue(
                        item.getDynamicFields(),
                        new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}
                );
                dto.setDynamicFields(dynamicFieldsMap);
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to deserialize dynamic fields: " + e.getMessage());
                dto.setDynamicFields(null);
            }
        }

        return dto;
    }

    /**
     * Convert DTO to RFQItem entity
     * 
     * @param dto RFQItemDTO
     * @return RFQItem entity
     */
    public RFQItem convertToEntity(RFQItemDTO dto) {
        RFQItem item = new RFQItem();
        item.setId(dto.getId());
        item.setItemCode(dto.getItemCode());
        item.setItemDescription(dto.getItemDescription());
        item.setQuantity(dto.getQuantity());
        item.setUom(dto.getUom());
        item.setUnitPrice(dto.getUnitPrice());
        item.setLineTotal(dto.getLineTotal());
        item.setItemRequiredDate(dto.getItemRequiredDate());
        item.setItemDescriptionDetailed(dto.getItemDescriptionDetailed());
        item.setSpecifications(dto.getSpecifications());
        item.setItemOrder(dto.getItemOrder());
        item.setCreatedAt(dto.getCreatedAt());
        item.setUpdatedAt(dto.getUpdatedAt());

        // ==================== CONVERT DYNAMIC FIELDS ====================
        item.setCompanyType(dto.getCompanyType());

        if (dto.hasDynamicFields()) {
            try {
                String dynamicFieldsJson = objectMapper.writeValueAsString(dto.getDynamicFields());
                item.setDynamicFields(dynamicFieldsJson);
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to serialize dynamic fields: " + e.getMessage());
                item.setDynamicFields(null);
            }
        }

        return item;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Get dynamic field value from item
     * 
     * @param itemId Item ID
     * @param fieldName Field name
     * @return Field value
     */
    public Object getDynamicFieldValue(Long itemId, String fieldName) {
        RFQItem item = rfqItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("RFQ Item not found with ID: " + itemId));

        return item.getDynamicFieldValue(fieldName);
    }

    /**
     * Update a specific dynamic field value
     * 
     * @param itemId Item ID
     * @param fieldName Field name
     * @param value New value
     * @return Updated RFQItem
     */
    @Transactional
    public RFQItem updateDynamicFieldValue(Long itemId, String fieldName, Object value) {
        System.out.println("[ITEM SERVICE] Updating dynamic field - Item ID: " + itemId + ", Field: " + fieldName);

        RFQItem item = rfqItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("RFQ Item not found with ID: " + itemId));

        item.setDynamicFieldValue(fieldName, value);
        item.setUpdatedAt(LocalDateTime.now());

        RFQItem updated = rfqItemRepository.save(item);
        System.out.println("[✓] Dynamic field updated successfully");
        return updated;
    }

    /**
     * Check if item has dynamic fields
     * 
     * @param itemId Item ID
     * @return True if item has dynamic fields
     */
    public boolean hasDynamicFields(Long itemId) {
        RFQItem item = rfqItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("RFQ Item not found with ID: " + itemId));

        return item.getDynamicFields() != null && !item.getDynamicFields().isEmpty();
    }
}