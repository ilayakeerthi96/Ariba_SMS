package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.Requisition;
import com.itti.leadcapturing.model.RequisitionItem;
import com.itti.leadcapturing.model.Department;
import com.itti.leadcapturing.model.User;
import com.itti.leadcapturing.repo.RequisitionRepository;
import com.itti.leadcapturing.repo.RequisitionItemRepository;
import com.itti.leadcapturing.repo.DepartmentRepository;
import com.itti.leadcapturing.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Service
public class RequisitionService {

    @Autowired
    private RequisitionRepository requisitionRepository;

    @Autowired
    private RequisitionItemRepository requisitionItemRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    // Create requisition
    public Requisition createRequisition(Requisition requisition) {
        // Validate department exists
        Department dept = departmentRepository.findById(requisition.getDepartment().getId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        // Validate user exists
        User user = userRepository.findById(requisition.getRequestedBy().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        requisition.setDepartment(dept);
        requisition.setRequestedBy(user);
        requisition.setIsDeleted(false);
        requisition.setStatus("DRAFT");

        return requisitionRepository.save(requisition);
    }

    // Get all requisitions
    public List<Requisition> getAllRequisitions() {
        return requisitionRepository.findAll();
    }

    // Get requisition by ID
    public Requisition getRequisitionById(Long id) {
        return requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requisition not found"));
    }

    // Get requisitions by department
    public List<Requisition> getRequisitionsByDepartment(Long departmentId) {
        return requisitionRepository.findByDepartmentId(departmentId);
    }

    // Get requisitions by user
    public List<Requisition> getRequisitionsByUser(Long userId) {
        return requisitionRepository.findByRequestedById(userId);
    }

    // Get requisitions by status
    public List<Requisition> getRequisitionsByStatus(String status) {
        return requisitionRepository.findByStatus(status);
    }

    // Update requisition
    public Requisition updateRequisition(Long id, Requisition requisitionReq) {
        Requisition existing = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requisition not found"));

        // Only allow updates if status is DRAFT
        if (!existing.getStatus().equals("DRAFT")) {
            throw new RuntimeException("Cannot update requisition that is not in DRAFT status");
        }

        if (requisitionReq.getTitle() != null && !requisitionReq.getTitle().isEmpty()) {
            existing.setTitle(requisitionReq.getTitle());
        }

        if (requisitionReq.getCategory() != null && !requisitionReq.getCategory().isEmpty()) {
            existing.setCategory(requisitionReq.getCategory());
        }

        if (requisitionReq.getDescription() != null) {
            existing.setDescription(requisitionReq.getDescription());
        }

        if (requisitionReq.getCurrency() != null && !requisitionReq.getCurrency().isEmpty()) {
            existing.setCurrency(requisitionReq.getCurrency());
        }

        if (requisitionReq.getEstimatedCost() != null) {
            existing.setEstimatedCost(requisitionReq.getEstimatedCost());
        }

        if (requisitionReq.getDeliveryDate() != null) {
            existing.setDeliveryDate(requisitionReq.getDeliveryDate());
        }

        if (requisitionReq.getDeliveryLocation() != null && !requisitionReq.getDeliveryLocation().isEmpty()) {
            existing.setDeliveryLocation(requisitionReq.getDeliveryLocation());
        }

        return requisitionRepository.save(existing);
    }

    // Submit requisition (change status from DRAFT to SUBMITTED)
    public Requisition submitRequisition(Long id) {
        Requisition requisition = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requisition not found"));

        if (!requisition.getStatus().equals("DRAFT")) {
            throw new RuntimeException("Only DRAFT requisitions can be submitted");
        }

        if (requisition.getItems() == null || requisition.getItems().isEmpty()) {
            throw new RuntimeException("Requisition must have at least one item");
        }

        requisition.setStatus("SUBMITTED");
        return requisitionRepository.save(requisition);
    }

    // Delete requisition (soft delete)
    public void deleteRequisition(Long id) {
        Requisition requisition = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requisition not found"));

        requisition.setIsDeleted(true);
        requisition.setDeletedAt(LocalDateTime.now());
        requisitionRepository.save(requisition);
    }

    // Add item to requisition
    public RequisitionItem addItemToRequisition(Long requisitionId, RequisitionItem item) {
        Requisition requisition = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> new RuntimeException("Requisition not found"));

        if (!requisition.getStatus().equals("DRAFT")) {
            throw new RuntimeException("Cannot add items to non-DRAFT requisition");
        }

        item.setRequisition(requisition);
        return requisitionItemRepository.save(item);
    }

    // Update item in requisition
    public RequisitionItem updateRequisitionItem(Long itemId, RequisitionItem itemReq) {
        RequisitionItem existing = requisitionItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        Requisition requisition = existing.getRequisition();
        if (!requisition.getStatus().equals("DRAFT")) {
            throw new RuntimeException("Cannot update items in non-DRAFT requisition");
        }

        if (itemReq.getItemName() != null && !itemReq.getItemName().isEmpty()) {
            existing.setItemName(itemReq.getItemName());
        }

        if (itemReq.getItemDescription() != null) {
            existing.setItemDescription(itemReq.getItemDescription());
        }

        if (itemReq.getQuantity() != null) {
            existing.setQuantity(itemReq.getQuantity());
        }

        if (itemReq.getPrice() != null) {
            existing.setPrice(itemReq.getPrice());
        }

        return requisitionItemRepository.save(existing);
    }

    // Delete item from requisition
    public void deleteRequisitionItem(Long itemId) {
        RequisitionItem item = requisitionItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        Requisition requisition = item.getRequisition();
        if (!requisition.getStatus().equals("DRAFT")) {
            throw new RuntimeException("Cannot delete items from non-DRAFT requisition");
        }

        requisitionItemRepository.deleteById(itemId);
    }

    // Get items by requisition
    public List<RequisitionItem> getItemsByRequisition(Long requisitionId) {
        return requisitionItemRepository.findByRequisitionId(requisitionId);
    }
}