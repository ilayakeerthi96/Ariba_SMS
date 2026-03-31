
package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class RFQService {

    @Autowired
    private RFQRepository rfqRepository;

    @Autowired
    private RFQItemRepository rfqItemRepository;

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private DynamicRFQApprovalService approvalService;

    // ==================== HELPER METHODS ====================

    private String generateRFQNumber() {
        return "RFQ-" + System.currentTimeMillis();
    }

    // ==================== CREATE RFQ ====================

    /**
     * ✅ STEP 1: Create RFQ (Buyer creates basic RFQ)
     * Status: DRAFT
     */
    @Transactional
    public RFQ createRFQ(RFQ rfq, Long buyerId, Long locationId, Long userId) {
        System.out.println("=".repeat(80));
        System.out.println("[CREATE RFQ] Starting RFQ creation");
        System.out.println("  Buyer ID: " + buyerId);
        System.out.println("  User ID: " + userId);
        System.out.println("  Title: " + rfq.getRfqTitle());

        try {
            // Validate
            if (rfq.getRfqTitle() == null || rfq.getRfqTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("RFQ Title is required");
            }

            // Fetch entities
            Buyer buyer = buyerRepository.findById(buyerId)
                    .orElseThrow(() -> new RuntimeException("Buyer not found: " + buyerId));
            
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Location not found: " + locationId));
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            // Set properties
            rfq.setRfqNumber(generateRFQNumber());
            rfq.setBuyer(buyer);
            rfq.setLocation(location);
            rfq.setCreatedByUser(user);
            rfq.setStatus(RFQStatus.DRAFT);
            rfq.setApprovalStatus(ApprovalStatus.PENDING);
            rfq.setIssueDate(LocalDateTime.now());
            rfq.setIsDeleted(false);

            // Set defaults
            if (rfq.getPriority() == null) rfq.setPriority(Priority.MEDIUM);
            if (rfq.getTaxPercentage() == null) rfq.setTaxPercentage(0.0);
            if (rfq.getApprovalRequired() == null) rfq.setApprovalRequired(false);
            if (rfq.getAllowSplitPO() == null) rfq.setAllowSplitPO(false);
            if (rfq.getPreferredVendorsOnly() == null) rfq.setPreferredVendorsOnly(false);

            // ✅ Initialize collections to avoid null pointer
            if (rfq.getSelectedSuppliers() == null) {
                rfq.setSelectedSuppliers(new HashSet<>());
            }
            if (rfq.getApprovers() == null) {
                rfq.setApprovers(new HashSet<>());
            }
            if (rfq.getItems() == null) {
                rfq.setItems(new ArrayList<>());
            }
            if (rfq.getAttachments() == null) {
                rfq.setAttachments(new ArrayList<>());
            }

            RFQ saved = rfqRepository.save(rfq);

            System.out.println("[✓] RFQ CREATED - ID: " + saved.getId() + ", Number: " + saved.getRfqNumber());
            System.out.println("  Status: DRAFT (ready for items and suppliers)");
            System.out.println("=".repeat(80));

            return saved;

        } catch (Exception e) {
            System.out.println("[✗] ERROR: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create RFQ: " + e.getMessage());
        }
    }

    // ==================== UPDATE RFQ ====================

    /**
     * Update RFQ (only DRAFT status)
     */
    @Transactional
    public RFQ updateRFQ(Long rfqId, RFQ rfqReq) {
        System.out.println("[UPDATE RFQ] ID: " + rfqId);
        
        RFQ existing = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found"));

        if (!RFQStatus.DRAFT.equals(existing.getStatus())) {
            throw new RuntimeException("Can only edit DRAFT RFQs. Current: " + existing.getStatus());
        }

        // Update fields
        if (rfqReq.getRfqTitle() != null) existing.setRfqTitle(rfqReq.getRfqTitle());
        if (rfqReq.getRfqDescription() != null) existing.setRfqDescription(rfqReq.getRfqDescription());
        if (rfqReq.getDueDate() != null) existing.setDueDate(rfqReq.getDueDate());
        if (rfqReq.getItemRequiredDate() != null) existing.setItemRequiredDate(rfqReq.getItemRequiredDate());
        if (rfqReq.getCostCenterCode() != null) existing.setCostCenterCode(rfqReq.getCostCenterCode());
        if (rfqReq.getProjectCode() != null) existing.setProjectCode(rfqReq.getProjectCode());
        if (rfqReq.getPaymentTerms() != null) existing.setPaymentTerms(rfqReq.getPaymentTerms());
        if (rfqReq.getDeliveryTerms() != null) existing.setDeliveryTerms(rfqReq.getDeliveryTerms());
        if (rfqReq.getTaxPercentage() != null) existing.setTaxPercentage(rfqReq.getTaxPercentage());
        if (rfqReq.getJustification() != null) existing.setJustification(rfqReq.getJustification());
        if (rfqReq.getPriority() != null) existing.setPriority(rfqReq.getPriority());
        if (rfqReq.getAllowSplitPO() != null) existing.setAllowSplitPO(rfqReq.getAllowSplitPO());
        if (rfqReq.getPreferredVendorsOnly() != null) existing.setPreferredVendorsOnly(rfqReq.getPreferredVendorsOnly());

        existing.setUpdatedAt(LocalDateTime.now());
        
        System.out.println("[✓] RFQ updated");
        return rfqRepository.save(existing);
    }

    // ==================== ADD SUPPLIERS ====================

    /**
     * ✅ STEP 2: Add suppliers to RFQ (only DRAFT)
     * Buyer selects multiple suppliers
     * 
     * ✅ KEY FIX: Everything happens within ONE transaction
     * No lazy loading issues because we never leave the transaction
     */
    @Transactional
    public RFQ addSuppliersToRFQ(Long rfqId, List<Long> supplierIds) {
        System.out.println("=".repeat(80));
        System.out.println("[ADD SUPPLIERS] RFQ ID: " + rfqId);
        System.out.println("  Supplier IDs to add: " + supplierIds);
        
        try {
            // Validate input first
            if (supplierIds == null || supplierIds.isEmpty()) {
                throw new IllegalArgumentException("At least one supplier must be provided");
            }

            // ✅ STEP 1: Fetch RFQ within transaction
            RFQ rfq = rfqRepository.findById(rfqId)
                    .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));

            // ✅ STEP 2: Check status
            if (!RFQStatus.DRAFT.equals(rfq.getStatus())) {
                throw new RuntimeException("Can only add suppliers to DRAFT RFQ. Current: " + rfq.getStatus());
            }

            // ✅ STEP 3: Get the suppliers collection (initialize if needed)
            Set<Supplier> suppliers = rfq.getSelectedSuppliers();
            if (suppliers == null) {
                suppliers = new HashSet<>();
                rfq.setSelectedSuppliers(suppliers);
            }

            System.out.println("  Current supplier count: " + suppliers.size());

            // ✅ STEP 4: Fetch all suppliers and add them (all within transaction)
            int addedCount = 0;
            for (Long supplierId : supplierIds) {
                Supplier supplier = supplierRepository.findById(supplierId)
                        .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));
                
                boolean wasAdded = suppliers.add(supplier);
                if (wasAdded) {
                    addedCount++;
                    System.out.println("  [✓] Added: " + supplier.getCompanyName() + " (ID: " + supplierId + ")");
                } else {
                    System.out.println("  [SKIP] Already exists: " + supplier.getCompanyName());
                }
            }

            // ✅ STEP 5: Update timestamp
            rfq.setUpdatedAt(LocalDateTime.now());
            
            // ✅ STEP 6: Save (still within transaction)
            RFQ updated = rfqRepository.save(rfq);

            System.out.println("[✅ SUCCESS] Suppliers operation completed");
            System.out.println("  Total suppliers now: " + updated.getSelectedSuppliers().size());
            System.out.println("  New suppliers added: " + addedCount);
            System.out.println("=".repeat(80));
            
            // ✅ Return within transaction - no lazy loading issue
            return updated;

        } catch (Exception e) {
            System.out.println("[✗ ERROR] " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to add suppliers: " + e.getMessage());
        }
    }

    // ==================== PUBLISH RFQ ====================

    /**
     * ✅ STEP 3: Publish RFQ (sends to approval workflow)
     * Status: DRAFT → AWAITING_APPROVAL
     */
    @Transactional
    public RFQ publishRFQ(Long rfqId) {
        System.out.println("=".repeat(80));
        System.out.println("[PUBLISH RFQ] ID: " + rfqId);
        
        try {
            // Fetch RFQ
            RFQ rfq = rfqRepository.findById(rfqId)
                    .orElseThrow(() -> new RuntimeException("RFQ not found"));

            // Validate status
            if (!RFQStatus.DRAFT.equals(rfq.getStatus())) {
                throw new RuntimeException("Only DRAFT RFQs can be published. Current: " + rfq.getStatus());
            }

            // ✅ Access collections within transaction
            int supplierCount = rfq.getSelectedSuppliers() != null ? rfq.getSelectedSuppliers().size() : 0;
            if (supplierCount == 0) {
                throw new RuntimeException("At least one supplier must be selected before publishing");
            }
            System.out.println("  [✓] Suppliers validated: " + supplierCount);

            // Validate items
            List<RFQItem> items = rfqItemRepository.findByRfqId(rfqId);
            if (items.isEmpty()) {
                throw new RuntimeException("RFQ must have at least one item before publishing");
            }
            System.out.println("  [✓] Items validated: " + items.size());

            // Initiate approval workflow (this is also @Transactional)
            Long creatorUserId = rfq.getCreatedByUser().getId();
            System.out.println("  [→] Initiating approval workflow...");
            
            approvalService.initiateApprovalWorkflow(rfqId, creatorUserId);
            
            // Fetch updated RFQ (within same transaction context)
            RFQ published = rfqRepository.findById(rfqId)
                    .orElseThrow(() -> new RuntimeException("Failed to fetch updated RFQ"));
            
            System.out.println("[✅✅✅ SUCCESS] RFQ PUBLISHED");
            System.out.println("  Status: " + published.getStatus());
            System.out.println("  Approval Status: " + published.getApprovalStatus());
            System.out.println("=".repeat(80));
            
            return published;
            
        } catch (Exception e) {
            System.out.println("[✗ ERROR] " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to publish RFQ: " + e.getMessage());
        }
    }

    // ==================== READ OPERATIONS ====================

    /**
     * ✅ Get RFQ by ID
     * Returns basic RFQ without lazy collections loaded
     */
    @Transactional(readOnly = true)
    public RFQ getRFQById(Long rfqId) {
        return rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));
    }

    /**
     * ✅ Get all RFQs for a buyer
     */
    @Transactional(readOnly = true)
    public List<RFQ> getRFQsByBuyer(Long buyerId) {
        System.out.println("[GET RFQs] By Buyer: " + buyerId);
        List<RFQ> rfqs = rfqRepository.findByBuyerId(buyerId);
        System.out.println("  Found " + rfqs.size() + " RFQs");
        return rfqs;
    }

    /**
     * ✅ Get RFQs by status
     */
    @Transactional(readOnly = true)
    public List<RFQ> getRFQsByStatus(Long buyerId, RFQStatus status) {
        System.out.println("[GET RFQs] By Buyer: " + buyerId + ", Status: " + status);
        return rfqRepository.findByBuyerIdAndStatus(buyerId, status);
    }

    /**
     * ✅ Get all RFQs
     */
    @Transactional(readOnly = true)
    public List<RFQ> getAllRFQs() {
        System.out.println("[GET ALL RFQs]");
        return rfqRepository.findAll();
    }

    // ==================== DELETE RFQ ====================

    /**
     * Delete RFQ (soft delete)
     */
    @Transactional
    public void deleteRFQ(Long rfqId) {
        System.out.println("[DELETE RFQ] ID: " + rfqId);
        
        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found"));

        rfq.setIsDeleted(true);
        rfq.setDeletedAt(LocalDateTime.now());
        rfqRepository.save(rfq);

        System.out.println("[✓] RFQ deleted (soft)");
    }

    // ==================== CLOSE RFQ ====================

    /**
     * Close RFQ (manual close)
     */
    @Transactional
    public RFQ closeRFQ(Long rfqId) {
        System.out.println("[CLOSE RFQ] ID: " + rfqId);
        
        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found"));

        rfq.setStatus(RFQStatus.CLOSED);
        rfq.setUpdatedAt(LocalDateTime.now());

        System.out.println("[✓] RFQ closed");
        return rfqRepository.save(rfq);
    }

    // ==================== DEPRECATED - USE APPROVAL SERVICE ====================
    
    /**
     * @deprecated Use DynamicRFQApprovalService.approveRFQ() instead
     */
    @Deprecated
    public RFQ approveRFQ(Long rfqId, Long approverId, String comments) {
        throw new RuntimeException("Use DynamicRFQApprovalService.approveRFQ() instead");
    }

    /**
     * @deprecated Use DynamicRFQApprovalService.rejectRFQ() instead
     */
    @Deprecated
    public RFQ rejectRFQ(Long rfqId, Long rejectedBy, String comments) {
        throw new RuntimeException("Use DynamicRFQApprovalService.rejectRFQ() instead");
    }

    /**
     * @deprecated Not needed - use addSuppliersToRFQ()
     */
    @Deprecated
    public RFQ addApproversToRFQ(Long rfqId, List<Long> approverIds) {
        throw new RuntimeException("Not needed - approval is dynamic via hierarchy");
    }
}