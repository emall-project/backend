package store.emall.backend.accounts.dashboard.admin;

import lombok.*;
import store.emall.backend.accounts.dashboard.ChartEntry;
import store.emall.backend.accounts.dashboard.TimeSeriesEntry;
import store.emall.backend.accounts.dashboard.admin.dtos.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDto {

    // User cards
    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;
    private long totalCustomers;
    private long totalShopOwners;
    private long usersNeverLoggedIn;
    private long usersInactiveLast30Days;

    // Mall cards
    private long totalMalls;
    private long activeMalls;
    private long inactiveMalls;
    private long maintenanceMalls;
    private long totalMallCapacity;
    private long totalRestaurants;
    private long activeRestaurants;
    private long totalMallServices;
    private long activeMallServices;

    // Shop cards
    private long totalShops;
    private long activeShops;
    private long inactiveShops;
    private long maintenanceShops;

    // City cards
    private long totalCities;
    private long activeCities;
    private long inactiveCities;

    // Request cards
    private long pendingShopOwnerRequests;
    private long approvedShopOwnerRequests;
    private long rejectedShopOwnerRequests;
    private long totalShopOwnerRequests;
    private double requestApprovalRate;

    private long pendingShopRequests;
    private long approvedShopRequests;
    private long rejectedShopRequests;
    private long totalShopRequests;
    private double shopRequestApprovalRate;
    private List<RecentShopRequestDto> recentPendingShopRequests;

    // Charts
    private List<ChartEntry> usersByRole;
    private List<ChartEntry> usersByGender;
    private List<ChartEntry> usersByAgeBucket;
    private List<ChartEntry> shopsByStatus;
    private List<ChartEntry> mallsByStatus;
    private List<ChartEntry> shopsByCategory;
    private List<ChartEntry> shopsPerMall;
    private List<ChartEntry> mallsPerCity;
    private List<ChartEntry> shopsPerCity;
    private List<ChartEntry> restaurantsPerMall;
    private List<ChartEntry> restaurantsByCuisine;
    private List<ChartEntry> cityBaseFees;
    private List<ChartEntry> mallCapacityUtilization;
    private List<ChartEntry> requestStatusDistribution;

    // Time series
    private List<TimeSeriesEntry> newUsersPerMonth;
    private List<TimeSeriesEntry> newShopsPerMonth;
    private List<TimeSeriesEntry> newMallsPerMonth;
    private List<TimeSeriesEntry> requestsPerMonth;

    // Tables
    private List<RecentRequestDto> recentPendingRequests;
    private List<RecentUserDto> recentUsers;
    private List<RecentShopDto> recentShops;
    private List<TopOwnerDto> topShopOwners;
    private List<CityStatsDto> cityStats;
    private List<MallStatsDto> mallStats;
}
