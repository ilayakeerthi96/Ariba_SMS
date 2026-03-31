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
     * ✅ FIXED: Update RFQ - Now clears items and suppliers before updating
     * Can update DRAFT or RETURNED_FOR_REVISION status
     */
    @Transactional
    public RFQ updateRFQ(Long rfqId, RFQ rfqReq) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║        UPDATE RFQ SERVICE              ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("📋 RFQ ID: " + rfqId);
        
        try {
            RFQ existing = rfqRepository.findById(rfqId)
                    .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));

            System.out.println("\n📦 BEFORE UPDATE:");
            System.out.println("   ├─ Current Items: " + existing.getItems().size());
            System.out.println("   ├─ Current Suppliers: " + existing.getSelectedSuppliers().size());
            System.out.println("   ├─ Current Status: " + existing.getStatus());
            System.out.println("   └─ Current Title: " + existing.getRfqTitle());

            // ✅ UPDATED: Allow editing DRAFT or RETURNED_FOR_REVISION
            if (!RFQStatus.DRAFT.equals(existing.getStatus()) && 
                !RFQStatus.RETURNED_FOR_REVISION.equals(existing.getStatus())) {
                throw new RuntimeException("Can only edit DRAFT or RETURNED_FOR_REVISION RFQs. Current: " + existing.getStatus());
            }

            // Update basic fields
            System.out.println("\n🔄 Updating basic fields...");
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

            // ==================== ✅ CRITICAL FIX: UPDATE ITEMS ====================
            if (rfqReq.getItems() != null && !rfqReq.getItems().isEmpty()) {
                System.out.println("\n📦 UPDATING ITEMS:");
                System.out.println("   ├─ Old Items Count: " + existing.getItems().size());
                System.out.println("   ├─ New Items Count: " + rfqReq.getItems().size());
                System.out.println("   ├─ 🗑️  CLEARING old items...");
                
                // ✅ CRITICAL: Clear existing items to prevent duplication
                existing.getItems().clear();
                
                System.out.println("   └─ ➕ ADDING new items...");
                
                // Add new items
                int itemIndex = 0;
                for (RFQItem newItem : rfqReq.getItems()) {
                    newItem.setRfq(existing);
                    existing.getItems().add(newItem);
                    System.out.println("      ├─ Item " + (itemIndex + 1) + ": " + 
                                     (newItem.getItemDescription() != null ? newItem.getItemDescription() : "N/A"));
                    itemIndex++;
                }
                
                System.out.println("   ✅ Items updated: " + existing.getItems().size());
            }

            // ==================== ✅ CRITICAL FIX: UPDATE SUPPLIERS ====================
            if (rfqReq.getSelectedSuppliers() != null && !rfqReq.getSelectedSuppliers().isEmpty()) {
                System.out.println("\n🏢 UPDATING SUPPLIERS:");
                System.out.println("   ├─ Old Suppliers Count: " + existing.getSelectedSuppliers().size());
                System.out.println("   ├─ New Suppliers Count: " + rfqReq.getSelectedSuppliers().size());
                System.out.println("   ├─ 🗑️  CLEARING old suppliers...");
                
                // ✅ CRITICAL: Clear existing suppliers to prevent duplication
                existing.getSelectedSuppliers().clear();
                
                System.out.println("   └─ ➕ ADDING new suppliers...");
                
                // Add new suppliers
                existing.getSelectedSuppliers().addAll(rfqReq.getSelectedSuppliers());
                
                System.out.println("   ✅ Suppliers updated: " + existing.getSelectedSuppliers().size());
            }

            // Update buyer if provided
            if (rfqReq.getBuyer() != null) {
                existing.setBuyer(rfqReq.getBuyer());
                System.out.println("   ✅ Buyer updated: " + rfqReq.getBuyer().getCompanyName());
            }

            // Update location if provided
            if (rfqReq.getLocation() != null) {
                existing.setLocation(rfqReq.getLocation());
                System.out.println("   ✅ Location updated: " + rfqReq.getLocation().getLocationName());
            }

            // Update approvers if provided
            if (rfqReq.getApprovers() != null && !rfqReq.getApprovers().isEmpty()) {
                existing.getApprovers().clear();
                existing.getApprovers().addAll(rfqReq.getApprovers());
                System.out.println("   ✅ Approvers updated: " + existing.getApprovers().size());
            }

            existing.setUpdatedAt(LocalDateTime.now());
            
            // Save
            RFQ savedRFQ = rfqRepository.save(existing);
            
            System.out.println("\n✅ UPDATE SUCCESSFUL!");
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║          FINAL STATE                   ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ RFQ ID: " + savedRFQ.getId());
            System.out.println("║ RFQ Number: " + savedRFQ.getRfqNumber());
            System.out.println("║ Title: " + savedRFQ.getRfqTitle());
            System.out.println("║ Items: " + savedRFQ.getItems().size());
            System.out.println("║ Suppliers: " + savedRFQ.getSelectedSuppliers().size());
            System.out.println("║ Status: " + savedRFQ.getStatus());
            System.out.println("╚════════════════════════════════════════╝\n");
            
            return savedRFQ;
            
        } catch (Exception e) {
            System.err.println("\n❌ UPDATE FAILED!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update RFQ: " + e.getMessage());
        }
    }

    // ==================== ADD SUPPLIERS ====================

    /**
     * ✅ UPDATED: Add suppliers to RFQ (DRAFT or RETURNED_FOR_REVISION)
     */
    @Transactional
    public RFQ addSuppliersToRFQ(Long rfqId, List<Long> supplierIds) {
        System.out.println("=".repeat(80));
        System.out.println("[ADD SUPPLIERS] RFQ ID: " + rfqId);
        System.out.println("  Supplier IDs to add: " + supplierIds);
        
        try {
            if (supplierIds == null || supplierIds.isEmpty()) {
                throw new IllegalArgumentException("At least one supplier must be provided");
            }

            RFQ rfq = rfqRepository.findById(rfqId)
                    .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));

            // ✅ UPDATED: Allow adding suppliers to DRAFT or RETURNED_FOR_REVISION
            if (!RFQStatus.DRAFT.equals(rfq.getStatus()) && 
                !RFQStatus.RETURNED_FOR_REVISION.equals(rfq.getStatus())) {
                throw new RuntimeException("Can only add suppliers to DRAFT or RETURNED_FOR_REVISION RFQ. Current: " + rfq.getStatus());
            }

            Set<Supplier> suppliers = rfq.getSelectedSuppliers();
            if (suppliers == null) {
                suppliers = new HashSet<>();
                rfq.setSelectedSuppliers(suppliers);
            }

            System.out.println("  Current supplier count: " + suppliers.size());

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

            rfq.setUpdatedAt(LocalDateTime.now());
            RFQ updated = rfqRepository.save(rfq);

            System.out.println("[✅ SUCCESS] Suppliers operation completed");
            System.out.println("  Total suppliers now: " + updated.getSelectedSuppliers().size());
            System.out.println("  New suppliers added: " + addedCount);
            System.out.println("=".repeat(80));
            
            return updated;

        } catch (Exception e) {
            System.out.println("[✗ ERROR] " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to add suppliers: " + e.getMessage());
        }
    }

    // ==================== PUBLISH RFQ ====================

    /**
     * ✅ UPDATED: Publish RFQ (sends to approval workflow)
     * Can publish DRAFT or RETURNED_FOR_REVISION
     * Status: DRAFT/RETURNED_FOR_REVISION → AWAITING_APPROVAL
     */
    @Transactional
    public RFQ publishRFQ(Long rfqId) {
        System.out.println("=".repeat(80));
        System.out.println("[PUBLISH RFQ] ID: " + rfqId);
        
        try {
            RFQ rfq = rfqRepository.findById(rfqId)
                    .orElseThrow(() -> new RuntimeException("RFQ not found"));

            // ✅ UPDATED: Allow publishing DRAFT or RETURNED_FOR_REVISION
            if (!RFQStatus.DRAFT.equals(rfq.getStatus()) && 
                !RFQStatus.RETURNED_FOR_REVISION.equals(rfq.getStatus())) {
                throw new RuntimeException("Only DRAFT or RETURNED_FOR_REVISION RFQs can be published. Current: " + rfq.getStatus());
            }

            // Validate
            int supplierCount = rfq.getSelectedSuppliers() != null ? rfq.getSelectedSuppliers().size() : 0;
            if (supplierCount == 0) {
                throw new RuntimeException("At least one supplier must be selected before publishing");
            }
            System.out.println("  [✓] Suppliers validated: " + supplierCount);

            List<RFQItem> items = rfqItemRepository.findByRfqId(rfqId);
            if (items.isEmpty()) {
                throw new RuntimeException("RFQ must have at least one item before publishing");
            }
            System.out.println("  [✓] Items validated: " + items.size());

            // Initiate approval workflow
            Long creatorUserId = rfq.getCreatedByUser().getId();
            System.out.println("  [→] Initiating approval workflow...");
            
            approvalService.initiateApprovalWorkflow(rfqId, creatorUserId);
            
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

    @Transactional(readOnly = true)
    public RFQ getRFQById(Long rfqId) {
        return rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));
    }

    @Transactional(readOnly = true)
    public List<RFQ> getRFQsByBuyer(Long buyerId) {
        System.out.println("[GET RFQs] By Buyer: " + buyerId);
        List<RFQ> rfqs = rfqRepository.findByBuyerId(buyerId);
        System.out.println("  Found " + rfqs.size() + " RFQs");
        return rfqs;
    }

    @Transactional(readOnly = true)
    public List<RFQ> getRFQsByStatus(Long buyerId, RFQStatus status) {
        System.out.println("[GET RFQs] By Buyer: " + buyerId + ", Status: " + status);
        return rfqRepository.findByBuyerIdAndStatus(buyerId, status);
    }

    @Transactional(readOnly = true)
    public List<RFQ> getAllRFQs() {
        System.out.println("[GET ALL RFQs]");
        return rfqRepository.findAll();
    }

    /**
     * ✅ NEW: Get RFQs that need buyer action (RETURNED_FOR_REVISION)
     */
    @Transactional(readOnly = true)
    public List<RFQ> getRFQsNeedingBuyerAction(Long buyerId) {
        System.out.println("[GET RFQs NEEDING ACTION] Buyer: " + buyerId);
        
        List<RFQ> allRFQs = rfqRepository.findByBuyerId(buyerId);
        List<RFQ> needingAction = allRFQs.stream()
                .filter(rfq -> RFQStatus.RETURNED_FOR_REVISION.equals(rfq.getStatus()))
                .toList();
        
        System.out.println("  Found " + needingAction.size() + " RFQs needing action");
        return needingAction;
    }

    // ==================== DELETE RFQ ====================

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