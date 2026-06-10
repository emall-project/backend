package store.emall.backend.accounts.dashboard.shopowner;

import lombok.*;
import store.emall.backend.accounts.dashboard.ChartEntry;
import store.emall.backend.accounts.dashboard.shopowner.dtos.MyMallSummaryDto;
import store.emall.backend.accounts.dashboard.shopowner.dtos.MyShopRequestDto;
import store.emall.backend.accounts.dashboard.shopowner.dtos.OwnedShopDto;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopOwnerDashboardDto {

    // Owner info
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String lastLoginAt;

    // Shop cards
    private long totalShops;
    private long activeShops;
    private long inactiveShops;
    private long maintenanceShops;

    // Request cards
    private long pendingShopRequests;
    private long approvedShopRequests;
    private long rejectedShopRequests;

    // Charts
    private List<ChartEntry> shopsByStatus;
    private List<ChartEntry> shopsByCategory;
    private List<ChartEntry> shopsByMall;
    private List<ChartEntry> requestsByStatus;

    // Tables
    private List<OwnedShopDto> myShops;
    private List<MyShopRequestDto> myShopRequests;
    private List<MyMallSummaryDto> myMalls;

}
