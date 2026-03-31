
package com.itti.leadcapturing.model;

/**
 * Status of each approval action
 */
public enum ApprovalActionStatus {
    PENDING,      // Waiting for approval
    APPROVED,     // Approved by current level
    REJECTED,     // Rejected by current level
    RESUBMITTED,  // Resubmitted after rejection
    SKIPPED       // Skipped (in case of workflow changes)
}