package ps.emall.mediamanager.common.scope;

public enum ManagedByType {
    SYSTEM,
    ADMIN,
    STORE;

    public boolean isInternalService() {
        return this != ADMIN && this != STORE;
    }
}
