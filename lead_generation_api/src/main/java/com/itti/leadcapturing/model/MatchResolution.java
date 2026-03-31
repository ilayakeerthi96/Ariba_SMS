package com.itti.leadcapturing.model;

/**
 * Resolution action taken after a 3-Way Match mismatch is identified.
 *
 * PENDING_RESOLUTION        → Mismatch found; awaiting buyer decision.
 * ACCEPTED_WITH_VARIANCE    → Authorized buyer approved despite variance (justification recorded).
 * REVISED_INVOICE_REQUESTED → Supplier asked to submit a corrected invoice.
 * PARTIAL_PAYMENT_APPROVED  → Pay only for the matched/accepted quantity; rest held.
 * CREDIT_NOTE_REQUESTED     → Supplier to issue a credit note for overcharged amount.
 * DEBIT_NOTE_RAISED         → Buyer raised a debit note for short-delivery/overcharge.
 * DISPUTED                  → Formal dispute raised; under review.
 * CANCELLED                 → Invoice or GRN cancelled; no payment.
 */
public enum MatchResolution {
    PENDING_RESOLUTION,
    ACCEPTED_WITH_VARIANCE,
    REVISED_INVOICE_REQUESTED,
    PARTIAL_PAYMENT_APPROVED,
    CREDIT_NOTE_REQUESTED,
    DEBIT_NOTE_RAISED,
    DISPUTED,
    CANCELLED
}