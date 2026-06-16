package store.emall.backend.common.scope;

public enum ManagedByType {
    SYSTEM,
    ADMIN,
    SHOP;

    public boolean isInternalService() {
        return this != ADMIN && this != SHOP;
    }
}
