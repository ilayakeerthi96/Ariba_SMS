package com.itti.leadcapturing.web;

import com.itti.leadcapturing.model.Requisition;
import com.itti.leadcapturing.model.RequisitionItem;
import com.itti.leadcapturing.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requisition")
@CrossOrigin(origins = "*")
public class RequisitionController {

    @Autowired
    private RequisitionService requisitionService;

    // ============================================
    // REQUISITION ENDPOINTS
    // ============================================

    /**
     * POST /api/requisition
     * Create a new requisition (STEP 1)
     */
    @PostMapping
    public ResponseEntity<?> createRequisition(@Valid @RequestBody Requisition requisition) {
        try {
            System.out.println("📝 Creating requisition: " + requisition.getTitle());
            
            Requisition created = requisitionService.createRequisition(requisition);
            
            System.out.println("✅ Requisition created with ID: " + created.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Requisition created successfully");
            response.put("data", created);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e)
         {
            System.out.println("❌ Error creating requisition: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * GET /api/requisition
     * Get all requisitions
     */
    @GetMapping
    public ResponseEntity<?> getAllRequisitions() {
        try {
            System.out.println("📋 Fetching all requisitions");
            
            List<Requisition> requisitions = requisitionService.getAllRequisitions();
            
            System.out.println("✅ Found " + requisitions.size() + " requisitions");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Requisitions fetched successfully");
            response.put("data", requisitions);
            response.put("total", requisitions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ Error fetching requisitions: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch requisitions: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * GET /api/requisition/{id}
     * Get requisition by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRequisitionById(@PathVariable Long id) {
        try {
            System.out.println("🔍 Fetching requisition ID: " + id);
            
            Requisition requisition = requisitionService.getRequisitionById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Requisition fetched successfully");
            response.put("data", requisition);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.out.println("❌ Requisition not found: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Requisition not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * GET /api/requisition/department/{departmentId}
     * Get all requisitions for a department
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<?> getRequisitionsByDepartment(@PathVariable Long departmentId) {
        try {
            System.out.println("🏢 Fetching requisitions for department ID: " + departmentId);
            
            List<Requisition> requisitions = requisitionService.getRequisitionsByDepartment(departmentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Requisitions fetched successfully");
            response.put("data", requisitions);
            response.put("total", requisitions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ Error fetching requisitions: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch requisitions");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * GET /api/requisition/user/{userId}
     * Get all requisitions requested by a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRequisitionsByUser(@PathVariable Long userId) {
        try {
            System.out.println("👤 Fetching requisitions for user ID: " + userId);
            
            List<Requisition> requisitions = requisitionService.getRequisitionsByUser(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Requisitions fetched successfully");
            response.put("data", requisitions);
            response.put("total", requisitions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ Error fetching requisitions: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch requisitions");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * GET /api/requisition/status/{status}
     * Get all requisitions by status (DRAFT, SUBMITTED, APPROVED, REJECTED)
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getRequisitionsByStatus(@PathVariable String status) {
        try {
            System.out.println("📊 Fetching requisitions with status: " + status);
            
            List<Requisition> requisitions = requisitionService.getRequisitionsByStatus(status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Requisitions fetched successfully");
            response.put("data", requisitions);
            response.put("total", requisitions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ Error fetching requisitions: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch requisitions");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * PUT /api/requisition/{id}
     * Update requisition (only DRAFT status can be updated)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRequisition(@PathVariable Long id, @Valid @RequestBody Requisition requisitionReq) {
        try {
            System.out.println("✏️ Updating requisition ID: " + id);
            
            Requisition updated = requisitionService.updateRequisition(id, requisitionReq);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Requisition updated successfully");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.out.println("❌ Error updating requisition: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * POST /api/requisition/{id}/submit
     * Submit requisition (change status from DRAFT to SUBMITTED)
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitRequisition(@PathVariable Long id) {
        try {
            System.out.println("📤 Submitting requisition ID: " + id);
            
            Requisition requisition = requisitionService.submitRequisition(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Requisition submitted successfully");
            response.put("data", requisition);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.out.println("❌ Error submitting requisition: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * DELETE /api/requisition/{id}
     * Delete requisition (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequisition(@PathVariable Long id) {
        try {
            System.out.println("🗑️ Deleting requisition ID: " + id);
            
            requisitionService.deleteRequisition(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Requisition deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.out.println("❌ Error deleting requisition: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ============================================
    // REQUISITION ITEM ENDPOINTS
    // ============================================

    /**
     * POST /api/requisition/{requisitionId}/item
     * Add item to requisition (STEP 2 - Called multiple times)
     */
    @PostMapping("/{requisitionId}/item")
    public ResponseEntity<?> addItemToRequisition(@PathVariable Long requisitionId, 
                                                   @Valid @RequestBody RequisitionItem item) {
        try {
            System.out.println("➕ Adding item to requisition ID: " + requisitionId);
            System.out.println("   Item name: " + item.getItemName());
            
            RequisitionItem created = requisitionService.addItemToRequisition(requisitionId, item);
            
            System.out.println("✅ Item added with ID: " + created.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Item added successfully");
            response.put("data", created);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.out.println("❌ Error adding item: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * PUT /api/requisition/item/{itemId}
     * Update requisition item
     */
    @PutMapping("/item/{itemId}")
    public ResponseEntity<?> updateRequisitionItem(@PathVariable Long itemId, 
                                                    @Valid @RequestBody RequisitionItem itemReq) {
        try {
            System.out.println("✏️ Updating item ID: " + itemId);
            
            RequisitionItem updated = requisitionService.updateRequisitionItem(itemId, itemReq);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Item updated successfully");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.out.println("❌ Error updating item: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * GET /api/requisition/{requisitionId}/items
     * Get all items for a requisition
     */
    @GetMapping("/{requisitionId}/items")
    public ResponseEntity<?> getItemsByRequisition(@PathVariable Long requisitionId) {
        try {
            System.out.println("📦 Fetching items for requisition ID: " + requisitionId);
            
            List<RequisitionItem> items = requisitionService.getItemsByRequisition(requisitionId);
            
            System.out.println("✅ Found " + items.size() + " items");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Items fetched successfully");
            response.put("data", items);
            response.put("total", items.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ Error fetching items: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch items");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * DELETE /api/requisition/item/{itemId}
     * Delete item from requisition
     */
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<?> deleteRequisitionItem(@PathVariable Long itemId) {
        try {
            System.out.println("🗑️ Deleting item ID: " + itemId);
            
            requisitionService.deleteRequisitionItem(itemId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Item deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.out.println("❌ Error deleting item: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}