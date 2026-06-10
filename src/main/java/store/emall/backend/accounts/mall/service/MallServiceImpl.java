package store.emall.backend.accounts.mall.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.accounts.mall.Mall;
import store.emall.backend.accounts.mall.MallExceptions;
import store.emall.backend.accounts.mall.MallRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MallServiceImpl implements MallService {

    private final MallServiceRepository mallServiceRepository;
    private final MallRepository mallRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MallServiceDto> getServicesByMall(Long mallId) {
        return mallServiceRepository.findByMall_MallId(mallId).stream()
                .map(MallServiceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MallServiceDto> getActiveServicesByMall(Long mallId) {
        return mallServiceRepository.findByMall_MallIdAndIsActiveTrue(mallId).stream()
                .map(MallServiceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MallServiceDto getById(Long serviceId) {
        MallServiceEntity service = mallServiceRepository.findById(serviceId)
                .orElseThrow(MallServiceExceptions::serviceNotFound);
        return MallServiceMapper.toDto(service);
    }

    @Override
    @Transactional
    public MallServiceDto addService(MallServiceDto serviceDto) {
        Long mallId = serviceDto.getMall().getMallId();
        Mall mall = mallRepository.findById(mallId)
                .orElseThrow(MallExceptions::mallNotFound);

        if (mallServiceRepository.existsByNameAndMall_MallId(serviceDto.getName(), mallId)) {
            throw MallServiceExceptions.serviceNameExists();
        }

        MallServiceEntity service = MallServiceMapper.toEntity(serviceDto);
        service.setMall(mall);

        return MallServiceMapper.toDto(mallServiceRepository.save(service));
    }

    @Override
    @Transactional
    public List<MallServiceDto> addServices(List<MallServiceDto> serviceDtos) {

        if(serviceDtos == null || serviceDtos.isEmpty()) {
            return Collections.emptyList();
        }

        // All services in the batch must belong to the same mall (from first DTO)
        Long commonMallId = serviceDtos.getFirst().getMall().getMallId();
        boolean allSameMall = serviceDtos.stream().allMatch(dto ->
                dto.getMall() != null && commonMallId.equals(dto.getMall().getMallId()));

        if(!allSameMall) {
            throw MallServiceExceptions.servicesBelongToDifferentMalls();
        }

        Mall mall = mallRepository.findById(commonMallId)
                .orElseThrow(MallExceptions::mallNotFound);

        List<MallServiceEntity> services = serviceDtos.stream()
                .peek(dto -> {
                    if (mallServiceRepository.existsByNameAndMall_MallId(dto.getName(), commonMallId)) {
                        throw MallServiceExceptions.serviceNameExists();
                    }
                })
                .map(dto -> {
                    MallServiceEntity service = MallServiceMapper.toEntity(dto);
                    service.setMall(mall);
                    return service;
                })
                .collect(Collectors.toList());

        return mallServiceRepository.saveAll(services).stream()
                .map(MallServiceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MallServiceDto updateService(MallServiceDto serviceDto) {
        MallServiceEntity existing = mallServiceRepository.findById(serviceDto.getServiceId())
                .orElseThrow(MallServiceExceptions::serviceNotFound);

        if(serviceDto.getMall() != null) {
            Long newMallId = serviceDto.getMall().getMallId();
            Long currentMallId = existing.getMall().getMallId();
            if(!newMallId.equals(currentMallId)) {

                // validate name uniqueness in the new mall (this service name must not be in the new mall)
                if(mallServiceRepository.existsByNameAndMall_MallIdAndServiceIdNot(
                        serviceDto.getName(), newMallId, serviceDto.getServiceId())) {
                    throw MallServiceExceptions.serviceNameExists();
                }

                Mall newMall = mallRepository.findById(newMallId)
                        .orElseThrow(MallExceptions::mallNotFound);

                existing.setMall(newMall);
            }
        }

        if (serviceDto.getName() != null
                && !serviceDto.getName().equals(existing.getName())
                && mallServiceRepository.existsByNameAndMall_MallIdAndServiceIdNot(
                serviceDto.getName(), existing.getMall().getMallId(), serviceDto.getServiceId())) {
            throw MallServiceExceptions.serviceNameExists();
        }

        MallServiceMapper.merge(existing, serviceDto);
        return MallServiceMapper.toDto(mallServiceRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteService(Long serviceId) {
        MallServiceEntity service = mallServiceRepository.findById(serviceId)
                .orElseThrow(MallServiceExceptions::serviceNotFound);
        mallServiceRepository.delete(service);
    }

    @Override
    @Transactional
    public void deleteAllServicesByMall(Long mallId) {
        mallServiceRepository.deleteByMall_MallId(mallId);
    }

    @Override
    @Transactional
    public void activateService(Long serviceId) {
        MallServiceEntity service = mallServiceRepository.findById(serviceId)
                .orElseThrow(MallServiceExceptions::serviceNotFound);

        if (Boolean.TRUE.equals(service.getIsActive())) {
            return;
        }
        service.setIsActive(Boolean.TRUE);
        mallServiceRepository.save(service);
    }

    @Override
    @Transactional
    public void deactivateService(Long serviceId) {
        MallServiceEntity service = mallServiceRepository.findById(serviceId)
                .orElseThrow(MallServiceExceptions::serviceNotFound);

        if (Boolean.FALSE.equals(service.getIsActive())) {
            return;
        }
        service.setIsActive(Boolean.FALSE);
        mallServiceRepository.save(service);
    }

}