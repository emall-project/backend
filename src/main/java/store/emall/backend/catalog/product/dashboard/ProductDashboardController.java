package store.emall.backend.catalog.product.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.emall.backend.common.response.EMallsResponseEntity;



@RestController
@RequestMapping("stores/{shopId}/products/dashboard")
@PreAuthorize("@auth.isAdminOrShopOwnerOf(#shopId)")
@RequiredArgsConstructor
public class ProductDashboardController {
    private final ProductDashboardService productDashboardService;

    @GetMapping("/summary")
    public EMallsResponseEntity<ProductDashboardSummaryDto> getDashboardSummary(@PathVariable Long shopId) {
        ProductDashboardSummaryDto dashboardSummary = productDashboardService.getSummary(shopId);
        return EMallsResponseEntity.ok(dashboardSummary);
    }
}
