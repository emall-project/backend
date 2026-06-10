package store.emall.backend.accounts.mall.service;

import java.util.List;

public interface MallService {

    List<MallServiceDto> getServicesByMall(Long mallId);
    List<MallServiceDto> getActiveServicesByMall(Long mallId);
    MallServiceDto getById(Long serviceId);

    MallServiceDto addService(MallServiceDto serviceDto);
    List<MallServiceDto> addServices(List<MallServiceDto> serviceDto);
    MallServiceDto updateService(MallServiceDto serviceDto);

    void deleteService(Long serviceId);
    void deleteAllServicesByMall(Long mallId);
    void activateService(Long serviceId);
    void deactivateService(Long serviceId);
}
