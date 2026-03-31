package com.itti.leadcapturing.model;

/**
 * Result of a 3-Way Match (PO ↔ GRN ↔ Invoice).
 *
 * PENDING                → Match not yet performed.
 * MATCHED                → All line items fully match within tolerance.
 * PARTIAL_MATCH          → Some items match; others have variance (within tolerance).
 * QUANTITY_MISMATCH      → Received/invoiced quantity differs from PO quantity beyond tolerance.
 * PRICE_MISMATCH         → Invoice unit price differs from PO unit rate beyond tolerance.
 * ITEM_MISMATCH          → Invoice contains items not present in PO/GRN.
 * EXCESS_DELIVERY        → GRN quantity exceeds PO ordered quantity.
 * OVERRIDDEN_APPROVED    → Authorized user manually approved despite mismatch (with justification).
 * DISPUTED               → Formal dispute raised with supplier.
 * FAILED                 → Match definitively failed; invoice rejected.
 */
public enum ThreeWayMatchStatus {
    PENDING,
    MATCHED,
    PARTIAL_MATCH,
    QUANTITY_MISMATCH,
    PRICE_MISMATCH,
    ITEM_MISMATCH,
    EXCESS_DELIVERY,
    OVERRIDDEN_APPROVED,
    DISPUTED,
    FAILED
}