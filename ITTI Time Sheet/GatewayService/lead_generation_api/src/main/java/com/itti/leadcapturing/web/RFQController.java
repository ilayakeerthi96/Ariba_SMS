
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

    /**
     * ✅ Safe DTO conversion that doesn't trigger lazy loading
     * Only accesses eagerly loaded fields
     */
    private RFQResponseDTO convertToDto(RFQ rfq) {
        if (rfq == null) return null;
        
        RFQResponseDTO dto = new RFQResponseDTO();
        
        // Basic fields (always loaded)
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

        // ✅ CRITICAL: Don't access lazy collections here!
        // Set counts to 0 by default - will be loaded separately if needed
        dto.setItemsCount(0);
        dto.setAttachmentsCount(0);
        dto.setSuppliersCount(0);
        dto.setApproversCount(0);

        return dto;
    }

    /**
     * ✅ DTO conversion with collection counts loaded within transaction
     */
    @Transactional(readOnly = true)
    private RFQResponseDTO convertToDtoWithCounts(RFQ rfq) {
        RFQResponseDTO dto = convertToDto(rfq);
        
        // Now safely access collections within transaction
        try {
            dto.setItemsCount(rfq.getItems() != null ? rfq.getItems().size() : 0);
            dto.setAttachmentsCount(rfq.getAttachments() != null ? rfq.getAttachments().size() : 0);
            dto.setSuppliersCount(rfq.getSelectedSuppliers() != null ? rfq.getSelectedSuppliers().size() : 0);
            dto.setApproversCount(rfq.getApprovers() != null ? rfq.getApprovers().size() : 0);
        } catch (Exception e) {
            System.out.println("[WARN] Could not load collection counts: " + e.getMessage());
            // Keep counts as 0
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

            // Convert DTO to Entity
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

    // ==================== GET RFQ BY ID ====================

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getRFQById(@PathVariable Long id) {
        try {
            RFQ rfq = rfqService.getRFQById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", convertToDtoWithCounts(rfq));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "RFQ not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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

    /**
     * ✅ Add suppliers to RFQ
     * All processing happens in service layer within transaction
     */
    @PostMapping("/{id}/suppliers")
    public ResponseEntity<?> addSuppliersToRFQ(
            @PathVariable Long id,
            @RequestBody List<Long> supplierIds) {
        try {
            System.out.println("=== Add Suppliers Request ===");
            System.out.println("RFQ ID: " + id);
            System.out.println("Supplier IDs: " + supplierIds);

            // ✅ Service method handles everything within transaction
            RFQ updated = rfqService.addSuppliersToRFQ(id, supplierIds);
            
            // ✅ Safe DTO conversion without lazy loading
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

    // ==================== ADD APPROVERS ====================

    @PostMapping("/{id}/approvers")
    public ResponseEntity<?> addApproversToRFQ(
            @PathVariable Long id,
            @RequestBody List<Long> approverIds) {
        try {
            RFQ updated = rfqService.addApproversToRFQ(id, approverIds);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Approvers added successfully");
            response.put("data", convertToDto(updated));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
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

    // ==================== APPROVE RFQ ====================

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveRFQ(
            @PathVariable Long id,
            @RequestParam Long approverId,
            @RequestParam(required = false) String comments) {
        try {
            RFQ approved = rfqService.approveRFQ(id, approverId, comments);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "RFQ approved successfully");
            response.put("data", convertToDto(approved));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ==================== REJECT RFQ ====================

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectRFQ(
            @PathVariable Long id,
            @RequestParam Long rejectedBy,
            @RequestParam String comments) {
        try {
            RFQ rejected = rfqService.rejectRFQ(id, rejectedBy, comments);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "RFQ rejected successfully");
            response.put("data", convertToDto(rejected));
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