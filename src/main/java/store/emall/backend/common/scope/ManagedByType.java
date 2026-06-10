package store.emall.backend.common.scope;

public enum ManagedByType {
    SYSTEM,
    ADMIN,
    STORE;

    public boolean isInternalService() {
        return this != ADMIN && this != STORE;
    }
}
