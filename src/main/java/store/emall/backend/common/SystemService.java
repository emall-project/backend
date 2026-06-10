package store.emall.backend.common;

public enum SystemService {
    CATALOG("catalog service"),
    ACCOUNTS("accounts service"),
    CAMPAIGNS("campaigns service");

    private final String serviceName;
    SystemService(String serviceName) {
        this.serviceName = serviceName;
    }
    public String getServiceName() {
        return serviceName;
    }
}
