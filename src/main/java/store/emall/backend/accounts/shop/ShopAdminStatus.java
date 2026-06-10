package store.emall.backend.accounts.shop;

public enum ShopAdminStatus {
    NONE,         // No admin override — subscription controls visibility
    MAINTENANCE,  // Admin set shop under maintenance (owner keeps write access)
    BLOCKED       // Admin blocked the shop (owner loses write access too)
}