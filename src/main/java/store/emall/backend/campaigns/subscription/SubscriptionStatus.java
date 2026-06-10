package store.emall.backend.campaigns.subscription;

public enum SubscriptionStatus {
    TRIAL,      // 3-month free period — full access
    ACTIVE,     // paid and active — full access
    SUSPENDED,  // autoRenew failed — read-only during grace period (3 days)
    EXPIRED,    // trial or sub ended — read-only
    CANCELLED   // manually cancelled — read-only
}