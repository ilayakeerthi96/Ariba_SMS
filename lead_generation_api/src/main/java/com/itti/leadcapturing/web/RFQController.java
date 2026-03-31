package com.itti.leadcapturing.web;

import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.dto.*;
import com.itti.leadcapturing.service.RFQService;
import com.itti.leadcapturing.repo.RFQRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rfq")
@CrossOrigin(origins = "*")
public class RFQController {

    @Autowired
    private RFQService rfqService;

    @Autowired
    private RFQRepository rfqRepository;

    // ==================== DTO CONVERSION HELPERS ====================

    private RFQResponseDTO convertToDto(RFQ rfq) {
        if (rfq == null) return null;
        
        RFQResponseDTO dto = new RFQResponseDTO();
        
        // Basic fields
        dto.setId(rfq.getId());
        dto.setRfqNumber(rfq.getRfqNumber());
        dto.setRfqTitle(rfq.getRfqTitle());
        dto.setRfqDescription(rfq.getRfqDescription());
        dto.setIssueDate(rfq.getIssueDate());
        dto.setDueDate(rfq.getDueDate());
        dto.setItemRequiredDate(rfq.getItemRequiredDate());
        dto.setStatus(rfq.getStatus());
        dto.setApprovalRequired(rfq.getApprovalRequired());
        dto.setApprovalStatus(rfq.getApprovalStatus());
        dto.setApprovedBy(rfq.getApprovedBy());
        dto.setApprovalDate(rfq.getApprovalDate());
        dto.setApprovalComments(rfq.getApprovalComments());
        dto.setCostCenterCode(rfq.getCostCenterCode());
        dto.setProjectCode(rfq.getProjectCode());
        dto.setPaymentTerms(rfq.getPaymentTerms());
        dto.setDeliveryTerms(rfq.getDeliveryTerms());
        dto.setTaxPercentage(rfq.getTaxPercentage());
        dto.setJustification(rfq.getJustification());
        dto.setPriority(rfq.getPriority());
        dto.setAllowSplitPO(rfq.getAllowSplitPO());
        dto.setPreferredVendorsOnly(rfq.getPreferredVendorsOnly());
        dto.setCreatedAt(rfq.getCreatedAt());
        dto.setUpdatedAt(rfq.getUpdatedAt());

        // Eagerly loaded associations
        if (rfq.getBuyer() != null) {
            BuyerDto buyerDto = new BuyerDto(
                rfq.getBuyer().getId(), 
                rfq.getBuyer().getCompanyName(), 
                rfq.getBuyer().getContactPersonEmail()
            );
            dto.setBuyer(buyerDto);
        }

        if (rfq.getLocation() != null) {
            LocationDto locationDto = new LocationDto(
                rfq.getLocation().getId(), 
                rfq.getLocation().getLocationName()
            );
            dto.setLocation(locationDto);
        }

        if (rfq.getCreatedByUser() != null) {
            UserDto userDto = new UserDto(
                rfq.getCreatedByUser().getId(), 
                rfq.getCreatedByUser().getFirstName(), 
                rfq.getCreatedByUser().getLastName(),
                rfq.getCreatedByUser().getEmail()
            );
            dto.setCreatedByUser(userDto);
        }

        // Set counts to 0 by default
        dto.setItemsCount(0);
        dto.setAttachmentsCount(0);
        dto.setSuppliersCount(0);
        dto.setApproversCount(0);

        return dto;
    }

// ==================== ✅ UPDATED: convertToDtoWithCounts METHOD ====================
// Replace this ENTIRE method in your RFQController.java

@Transactional(readOnly = true)
private RFQResponseDTO convertToDtoWithCounts(RFQ rfq) {
    RFQResponseDTO dto = convertToDto(rfq);
    
    try {
        // ✅ CRITICAL FIX: Convert items to DTOs
        if (rfq.getItems() != null && !rfq.getItems().isEmpty()) {
            System.out.println("[CONVERTING ITEMS] Count: " + rfq.getItems().size());
            
            List<RFQItemDTO> itemDtos = new ArrayList<>();
            for (RFQItem item : rfq.getItems()) {
                RFQItemDTO itemDto = new RFQItemDTO();
                itemDto.setId(item.getId());
                itemDto.setItemCode(item.getItemCode());
                itemDto.setItemDescription(item.getItemDescription());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setUom(item.getUom());
                itemDto.setUnitPrice(item.getUnitPrice());
                itemDto.setLineTotal(item.getLineTotal());
                itemDto.setItemRequiredDate(item.getItemRequiredDate());
                itemDto.setSpecifications(item.getSpecifications());
                itemDto.setItemOrder(item.getItemOrder());
                itemDto.setCompanyType(item.getCompanyType());
                itemDto.setCreatedAt(item.getCreatedAt());
                itemDto.setUpdatedAt(item.getUpdatedAt());
                
                // Convert dynamic fields
                if (item.getDynamicFields() != null && !item.getDynamicFields().isEmpty()) {
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        Map<String, Object> dynamicFieldsMap = mapper.readValue(
                            item.getDynamicFields(),
                            new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}
                        );
                        itemDto.setDynamicFields(dynamicFieldsMap);
                    } catch (Exception e) {
                        System.err.println("[WARN] Failed to parse dynamic fields for item " + item.getId());
                    }
                }
                
                itemDtos.add(itemDto);
            }
            
            dto.setItems(itemDtos);
            System.out.println("[✓] Converted " + itemDtos.size() + " items");
        } else {
            dto.setItems(new ArrayList<>());
            dto.setItemsCount(0);
        }
        
        // ✅ CRITICAL FIX: Convert suppliers to DTOs
        if (rfq.getSelectedSuppliers() != null && !rfq.getSelectedSuppliers().isEmpty()) {
            System.out.println("[CONVERTING SUPPLIERS] Count: " + rfq.getSelectedSuppliers().size());
            
            List<SupplierDto> supplierDtos = new ArrayList<>();
            for (Supplier supplier : rfq.getSelectedSuppliers()) {
                SupplierDto supplierDto = new SupplierDto();
                supplierDto.setId(supplier.getId());
                supplierDto.setCompanyName(supplier.getCompanyName());
                supplierDto.setContactPersonEmail(supplier.getContactPersonEmail());
                supplierDto.setContactPersonPhone(supplier.getContactPersonPhone());
                supplierDto.setContactPersonName(supplier.getContactPersonName());
                supplierDto.setCity(supplier.getCity());
                supplierDto.setState(supplier.getState());
                supplierDtos.add(supplierDto);
            }
            
            dto.setSelectedSuppliers(supplierDtos);
            System.out.println("[✓] Converted " + supplierDtos.size() + " suppliers");
        } else {
            dto.setSelectedSuppliers(new ArrayList<>());
            dto.setSuppliersCount(0);
        }
        
        // Attachments count
        dto.setAttachmentsCount(rfq.getAttachments() != null ? rfq.getAttachments().size() : 0);
        
        // Approvers count
        dto.setApproversCount(rfq.getApprovers() != null ? rfq.getApprovers().size() : 0);
        
    } catch (Exception e) {
        System.err.println("[ERROR] Could not load collections: " + e.getMessage());
        e.printStackTrace();
    }
    
    return dto;
}
    // ==================== CREATE RFQ ====================

    @PostMapping("/{buyerId}/{locationId}/{userId}")
    public ResponseEntity<?> createRFQ(
            @PathVariable Long buyerId,
            @PathVariable Long locationId,
            @PathVariable Long userId,
            @Valid @RequestBody RFQCreateDTO createDTO) {
        try {
            System.out.println("=== RFQ Creation Request ===");
            System.out.println("buyerId: " + buyerId);
            System.out.println("locationId: " + locationId);
            System.out.println("userId: " + userId);
            System.out.println("RFQ Title: " + createDTO.getRfqTitle());

            RFQ rfq = new RFQ();
            rfq.setRfqTitle(createDTO.getRfqTitle());
            rfq.setRfqDescription(createDTO.getRfqDescription());
            rfq.setDueDate(createDTO.getDueDate());
            rfq.setItemRequiredDate(createDTO.getItemRequiredDate());
            rfq.setCostCenterCode(createDTO.getCostCenterCode());
            rfq.setProjectCode(createDTO.getProjectCode());
            rfq.setPaymentTerms(createDTO.getPaymentTerms());
            rfq.setDeliveryTerms(createDTO.getDeliveryTerms());
            rfq.setTaxPercentage(createDTO.getTaxPercentage() != null ? createDTO.getTaxPercentage() : 0.0);
            rfq.setJustification(createDTO.getJustification());
            rfq.setPriority(createDTO.getPriority() != null ? createDTO.getPriority() : Priority.MEDIUM);
            rfq.setApprovalRequired(createDTO.getApprovalRequired() != null ? createDTO.getApprovalRequired() : false);
            rfq.setAllowSplitPO(createDTO.getAllowSplitPO() != null ? createDTO.getAllowSplitPO() : false);
            rfq.setPreferredVendorsOnly(createDTO.getPreferredVendorsOnly() != null ? createDTO.getPreferredVendorsOnly() : false);

            RFQ createdRFQ = rfqService.createRFQ(rfq, buyerId, locationId, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "RFQ created successfully");
            response.put("data", convertToDto(createdRFQ));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            System.out.println("RuntimeException: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("error", "RuntimeException");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== UPDATE RFQ ====================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRFQ(
            @PathVariable Long id,
            @Valid @RequestBody RFQUpdateDTO updateDTO) {
        try {
            RFQ rfqReq = new RFQ();
            rfqReq.setRfqTitle(updateDTO.getRfqTitle());
            rfqReq.setRfqDescription(updateDTO.getRfqDescription());
            rfqReq.setDueDate(updateDTO.getDueDate());
            rfqReq.setItemRequiredDate(updateDTO.getItemRequiredDate());
            rfqReq.setCostCenterCode(updateDTO.getCostCenterCode());
            rfqReq.setProjectCode(updateDTO.getProjectCode());
            rfqReq.setPaymentTerms(updateDTO.getPaymentTerms());
            rfqReq.setDeliveryTerms(updateDTO.getDeliveryTerms());
            rfqReq.setTaxPercentage(updateDTO.getTaxPercentage());
            rfqReq.setJustification(updateDTO.getJustification());
            rfqReq.setPriority(updateDTO.getPriority());
            rfqReq.setApprovalRequired(updateDTO.getApprovalRequired());
            rfqReq.setAllowSplitPO(updateDTO.getAllowSplitPO());
            rfqReq.setPreferredVendorsOnly(updateDTO.getPreferredVendorsOnly());

            RFQ updated = rfqService.updateRFQ(id, rfqReq);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "RFQ updated successfully");
            response.put("data", convertToDto(updated));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ==================== ✅ FIXED: GET RFQ BY ID WITH EAGER LOADING ====================

    /**
     * ✅ CRITICAL FIX: Use eager loading query to fetch items and suppliers
     */
// ==================== ✅ REPLACE getRFQById() METHOD IN RFQController.java ====================

@GetMapping("/{id}")
@Transactional(readOnly = true)
public ResponseEntity<?> getRFQById(@PathVariable Long id) {
    System.out.println("=== GET RFQ BY ID ===");
    System.out.println("RFQ ID: " + id);
    
    try {
        // ✅ STEP 1: Load RFQ with basic details (buyer, location, user)
        RFQ rfq = rfqRepository.findByIdWithAllDetails(id)
                .orElseThrow(() -> new RuntimeException("RFQ not found with id: " + id));
        
        // ✅ STEP 2: Force load items separately (prevents duplicates)
        rfqRepository.findByIdWithItems(id).ifPresent(r -> {
            rfq.getItems().size(); // Force initialization
        });
        
        // ✅ STEP 3: Force load suppliers separately (prevents duplicates)
        rfqRepository.findByIdWithSuppliers(id).ifPresent(r -> {
            rfq.getSelectedSuppliers().size(); // Force initialization
        });
        
        System.out.println("✅ RFQ Found: " + rfq.getRfqNumber());
        System.out.println("  Items Count: " + rfq.getItems().size());
        System.out.println("  Suppliers Count: " + rfq.getSelectedSuppliers().size());
        
        RFQResponseDTO response = convertToDtoWithCounts(rfq);
        
        System.out.println("=== SUCCESS ===");
        return ResponseEntity.ok(Map.of(
            "data", response,
            "success", true
        ));
        
    } catch (Exception e) {
        System.err.println("❌ Error: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Error fetching RFQ: " + e.getMessage()
                ));
    }
}
    // ==================== GET ALL RFQs ====================

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllRFQs() {
        try {
            List<RFQ> rfqs = rfqService.getAllRFQs();
            List<RFQResponseDTO> dtos = rfqs.stream()
                    .map(this::convertToDtoWithCounts)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", dtos.size());
            response.put("data", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Exception in getAllRFQs: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch RFQs");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== GET RFQs BY BUYER ====================

    @GetMapping("/buyer/{buyerId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getRFQsByBuyer(@PathVariable Long buyerId) {
        try {
            List<RFQ> rfqs = rfqService.getRFQsByBuyer(buyerId);
            List<RFQResponseDTO> dtos = rfqs.stream()
                    .map(this::convertToDtoWithCounts)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", dtos.size());
            response.put("data", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch RFQs for buyer");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== ✅ NEW: GET RFQs NEEDING BUYER ACTION ====================

    /**
     * ✅ NEW: GET /api/rfq/buyer/{buyerId}/returned
     * Get all RFQs that were returned for revision (need buyer action)
     */
    @GetMapping("/buyer/{buyerId}/returned")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getReturnedRFQs(@PathVariable Long buyerId) {
        try {
            System.out.println("=== GET RETURNED RFQs ===");
            System.out.println("Buyer ID: " + buyerId);
            
            List<RFQ> returnedRFQs = rfqService.getRFQsNeedingBuyerAction(buyerId);
            List<RFQResponseDTO> dtos = returnedRFQs.stream()
                    .map(this::convertToDtoWithCounts)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", dtos.size());
            response.put("data", dtos);
            response.put("message", dtos.size() + " RFQ(s) returned for revision");
            
            System.out.println("Found " + dtos.size() + " returned RFQs");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error fetching returned RFQs: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch returned RFQs");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== GET RFQs BY STATUS ====================

    @GetMapping("/buyer/{buyerId}/status/{status}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getRFQsByStatus(
            @PathVariable Long buyerId,
            @PathVariable RFQStatus status) {
        try {
            List<RFQ> rfqs = rfqService.getRFQsByStatus(buyerId, status);
            List<RFQResponseDTO> dtos = rfqs.stream()
                    .map(this::convertToDtoWithCounts)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", dtos.size());
            response.put("data", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch RFQs by status");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== ADD SUPPLIERS ====================

    @PostMapping("/{id}/suppliers")
    public ResponseEntity<?> addSuppliersToRFQ(
            @PathVariable Long id,
            @RequestBody List<Long> supplierIds) {
        try {
            System.out.println("=== Add Suppliers Request ===");
            System.out.println("RFQ ID: " + id);
            System.out.println("Supplier IDs: " + supplierIds);

            RFQ updated = rfqService.addSuppliersToRFQ(id, supplierIds);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Suppliers added successfully");
            response.put("data", convertToDto(updated));
            
            System.out.println("=== Add Suppliers Success ===");
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            System.out.println("=== Add Suppliers Error ===");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ==================== PUBLISH RFQ ====================

    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publishRFQ(@PathVariable Long id) {
        try {
            RFQ published = rfqService.publishRFQ(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "RFQ published successfully");
            response.put("data", convertToDto(published));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ==================== CLOSE RFQ ====================

    @PostMapping("/{id}/close")
    public ResponseEntity<?> closeRFQ(@PathVariable Long id) {
        try {
            RFQ closed = rfqService.closeRFQ(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "RFQ closed successfully");
            response.put("data", convertToDto(closed));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ==================== DELETE RFQ ====================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRFQ(@PathVariable Long id) {
        try {
            rfqService.deleteRFQ(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "RFQ deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}