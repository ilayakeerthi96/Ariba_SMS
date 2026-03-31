package com.itti.leadcapturing.model;

/**
 * ✅ Purchase Order Status Enum
 */
public enum POStatus {
    DRAFT,                    // PO created but not submitted for approval
    PENDING_APPROVAL,         // Submitted and waiting for approver action
    APPROVED,                 // Fully approved through all hierarchy levels
    SENT_TO_SUPPLIER,         // Sent to supplier after approval
    ACKNOWLEDGED,             // Supplier acknowledged receipt
    IN_PROGRESS,              // Order in progress / being fulfilled
    DELIVERED,                // Items delivered
    COMPLETED,                // PO lifecycle complete
    CANCELLED,                // Cancelled by buyer
    REJECTED,                 // Permanently rejected by an approver — no resubmission
    RETURNED_FOR_REVISION     // Returned by approver — creator can edit and resubmit
}