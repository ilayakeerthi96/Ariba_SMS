// ==================== FILE: src/main/java/com/itti/leadcapturing/web/RFQItemController.java ====================

package com.itti.leadcapturing.web;

import com.itti.leadcapturing.model.RFQItem;
import com.itti.leadcapturing.dto.RFQItemDTO;
import com.itti.leadcapturing.service.RFQItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for RFQ Item operations
 * Handles CRUD operations for RFQ items with dynamic fields support
 */
@RestController
@RequestMapping("/api/rfq-item")
@CrossOrigin(origins = "*")
public class RFQItemController {

    @Autowired
    private RFQItemService rfqItemService;

    // ==================== CREATE OPERATIONS ====================

    /**
     * POST /api/rfq-item/{rfqId}
     * Add item to RFQ with dynamic fields
     * 
     * @param rfqId RFQ ID
     * @param itemDTO Item DTO with dynamic fields
     * @return Created item response
     */
    @PostMapping("/{rfqId}")
    public ResponseEntity<?> addItemToRFQ(
            @PathVariable Long rfqId,
            @RequestBody RFQItemDTO itemDTO) {
        try {
            System.out.println("[CONTROLLER] Adding item to RFQ: " + rfqId);
            System.out.println("  Item: " + itemDTO.getItemDescription());
            System.out.println("  Company Type: " + itemDTO.getCompanyType());
            System.out.println("  Has Dynamic Fields: " + itemDTO.hasDynamicFields());

            RFQItem created = rfqItemService.addItemToRFQ(rfqId, itemDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Item added successfully");
            response.put("data", convertToDTO(created));

            System.out.println("[✓] Item created with ID: " + created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            System.err.println("[✗] Runtime Error: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("error", "RuntimeException");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            System.err.println("[✗] Unexpected Error: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("error", e.getClass().getSimpleName());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== UPDATE OPERATIONS ====================

    /**
     * PUT /api/rfq-item/{id}
     * Update RFQ item with dynamic fields
     * 
     * @param id Item ID
     * @param itemDTO Item DTO with updated values
     * @return Updated item response
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRFQItem(
            @PathVariable Long id,
            @RequestBody RFQItemDTO itemDTO) {
        try {
            System.out.println("[CONTROLLER] Updating item: " + id);

            RFQItem updated = rfqItemService.updateRFQItem(id, itemDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Item updated successfully");
            response.put("data", convertToDTO(updated));

            System.out.println("[✓] Item updated successfully");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.err.println("[✗] Runtime Error: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            System.err.println("[✗] Unexpected Error: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update item");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== READ OPERATIONS ====================

    /**
     * GET /api/rfq-item/rfq/{rfqId}
     * Get all items for an RFQ with dynamic fields
     * 
     * @param rfqId RFQ ID
     * @return List of items
     */
    @GetMapping("/rfq/{rfqId}")
    public ResponseEntity<?> getItemsByRFQ(@PathVariable Long rfqId) {
        try {
            System.out.println("[CONTROLLER] Getting items for RFQ: " + rfqId);

            List<RFQItemDTO> items = rfqItemService.getItemsByRFQ(rfqId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", items.size());
            response.put("data", items);

            System.out.println("[✓] Found " + items.size() + " items");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("[✗] Error: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch items");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/rfq-item/{id}
     * Get item by ID with dynamic fields
     * 
     * @param id Item ID
     * @return Item details
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRFQItemById(@PathVariable Long id) {
        try {
            System.out.println("[CONTROLLER] Getting item: " + id);

            RFQItemDTO item = rfqItemService.getRFQItemById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", item);

            System.out.println("[✓] Item retrieved successfully");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.err.println("[✗] Runtime Error: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Item not found");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            System.err.println("[✗] Unexpected Error: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch item");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/rfq-item/rfq/{rfqId}/company-type/{companyType}
     * Get items filtered by company type
     * 
     * @param rfqId RFQ ID
     * @param companyType Company type
     * @return Filtered items
     */
    @GetMapping("/rfq/{rfqId}/company-type/{companyType}")
    public ResponseEntity<?> getItemsByCompanyType(
            @PathVariable Long rfqId,
            @PathVariable String companyType) {
        try {
            System.out.println("[CONTROLLER] Getting items by company type - RFQ: " + rfqId + ", Type: " + companyType);

            List<RFQItemDTO> items = rfqItemService.getItemsByCompanyType(rfqId, companyType);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", items.size());
            response.put("companyType", companyType);
            response.put("data", items);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("[✗] Error: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch items");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== DELETE OPERATIONS ====================

    /**
     * DELETE /api/rfq-item/{id}
     * Delete RFQ item
     * 
     * @param id Item ID
     * @return Delete response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRFQItem(@PathVariable Long id) {
        try {
            System.out.println("[CONTROLLER] Deleting item: " + id);

            rfqItemService.deleteRFQItem(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Item deleted successfully");

            System.out.println("[✓] Item deleted successfully");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.err.println("[✗] Runtime Error: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            System.err.println("[✗] Unexpected Error: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to delete item");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== UTILITY OPERATIONS ====================

    /**
     * GET /api/rfq-item/{id}/dynamic-field/{fieldName}
     * Get a specific dynamic field value
     * 
     * @param id Item ID
     * @param fieldName Field name
     * @return Field value
     */
    @GetMapping("/{id}/dynamic-field/{fieldName}")
    public ResponseEntity<?> getDynamicFieldValue(
            @PathVariable Long id,
            @PathVariable String fieldName) {
        try {
            System.out.println("[CONTROLLER] Getting dynamic field - Item: " + id + ", Field: " + fieldName);

            Object value = rfqItemService.getDynamicFieldValue(id, fieldName);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("itemId", id);
            response.put("fieldName", fieldName);
            response.put("value", value);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.err.println("[✗] Runtime Error: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * PUT /api/rfq-item/{id}/dynamic-field/{fieldName}
     * Update a specific dynamic field value
     * 
     * @param id Item ID
     * @param fieldName Field name
     * @param valueMap Map with "value" key
     * @return Updated item
     */
    @PutMapping("/{id}/dynamic-field/{fieldName}")
    public ResponseEntity<?> updateDynamicFieldValue(
            @PathVariable Long id,
            @PathVariable String fieldName,
            @RequestBody Map<String, Object> valueMap) {
        try {
            System.out.println("[CONTROLLER] Updating dynamic field - Item: " + id + ", Field: " + fieldName);

            Object newValue = valueMap.get("value");
            RFQItem updated = rfqItemService.updateDynamicFieldValue(id, fieldName, newValue);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Dynamic field updated successfully");
            response.put("data", convertToDTO(updated));

            System.out.println("[✓] Dynamic field updated");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.err.println("[✗] Runtime Error: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            System.err.println("[✗] Unexpected Error: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update dynamic field");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== CONVERSION HELPER ====================

    /**
     * Convert RFQItem entity to DTO
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
        dto.setCompanyType(item.getCompanyType());
        dto.setDynamicFields(item.getDynamicFieldsAsMap());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());
        return dto;
    }
}