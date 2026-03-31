package com.itti.leadcapturing.model;

/**
 * Status of RFQ Creator's final supplier selection
 */
public enum SelectionStatus {
    SELECTED,           // RFQ creator has selected the supplier
    PENDING_REVIEW,     // Awaiting PO price review/negotiation
    IN_NEGOTIATION,     // Price negotiation in progress
    PO_CREATED,         // PO has been created after review
    CANCELLED           // Selection cancelled
}