package store.emall.backend.campaigns.ad.request;

/**
 * Lifecycle statuses for an AdRequest.
 *
 * Flow:
 *   PENDING → APPROVED → (payment confirmed) → displayed via isDisplayed flag
 *   PENDING → REJECTED  (by admin, or auto-rejected when another request is approved)
 *   PENDING → REJECTED  (cancelled by shop owner — rejectionReason = "Cancelled by shop owner")
 *
 * Payment state is tracked separately via AdPaymentStatus (UNPAID / PAID / OVERDUE).
 * Display state is tracked via the isDisplayed boolean on AdRequest.
 */

public enum AdRequestStatus {
    PENDING,
    APPROVED,
    REJECTED
}