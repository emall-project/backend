package store.emall.backend.accounts.mall;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.accounts.mall.dtos.MallDto;

import java.util.List;

public interface MallManagementService {

    PaginatedResponse<MallDto> getAll(Pageable pageable, Specification<Mall> spec);
    List<MallDto> getAllMalls(Specification<Mall> spec);
    MallDto getById(Long id);
    MallDto create(MallDto mallDto);
    MallDto update(MallDto mallDto);
    void delete(Long id);

    void changeStatus(Long id, MallStatus status);
    void activate(Long id);
    void deactivate(Long id);
    void setMaintenance(Long id);

    List<MallDto> getMallsByCity(Long cityId);
    List<MallDto> getActiveMallsByCity(Long cityId);
    List<MallDto> getMallsByStatus(MallStatus status);

}
