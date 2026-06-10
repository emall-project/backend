package store.emall.backend.accounts.dashboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.accounts.city.CityRepository;
import store.emall.backend.accounts.dashboard.admin.AdminDashboardDto;
import store.emall.backend.accounts.dashboard.admin.dtos.*;
import store.emall.backend.accounts.dashboard.customer.CustomerDashboardDto;
import store.emall.backend.accounts.dashboard.shopowner.ShopOwnerDashboardDto;
import store.emall.backend.accounts.dashboard.shopowner.dtos.MyMallSummaryDto;
import store.emall.backend.accounts.dashboard.shopowner.dtos.MyShopRequestDto;
import store.emall.backend.accounts.dashboard.shopowner.dtos.OwnedShopDto;
import store.emall.backend.accounts.mall.Mall;
import store.emall.backend.accounts.mall.MallRepository;
import store.emall.backend.accounts.mall.MallStatus;
import store.emall.backend.accounts.mall.restaurant.MallRestaurantRepository;
import store.emall.backend.accounts.mall.service.MallServiceRepository;
import store.emall.backend.accounts.request.shop.ShopRequestRepository;
import store.emall.backend.accounts.request.shop.ShopRequestStatus;
import store.emall.backend.accounts.request.shopowner.ShopOwnerRequestRepository;
import store.emall.backend.accounts.request.shopowner.ShopOwnerRequestStatus;
import store.emall.backend.accounts.shop.Shop;
import store.emall.backend.accounts.shop.ShopRepository;
import store.emall.backend.accounts.shop.ShopStatus;
import store.emall.backend.accounts.user.Gender;
import store.emall.backend.accounts.user.User;
import store.emall.backend.accounts.user.UserExceptions;
import store.emall.backend.accounts.user.UserRepository;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final MallRepository mallRepository;
    private final CityRepository cityRepository;
    private final MallRestaurantRepository restaurantRepository;
    private final MallServiceRepository mallServiceRepository;
    private final ShopOwnerRequestRepository shopOwnerRequestRepository;
    private final ShopRequestRepository shopRequestRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MMM yyyy");
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
    private static final String ROLE_SHOP_OWNER = "ROLE_SHOP_OWNER";

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardDto getAdminDashboard() {
        log.debug("Building admin dashboard");

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActive(true);
        long inactiveUsers = totalUsers - activeUsers;
        long totalCustomers = userRepository.countByRole_Code(ROLE_CUSTOMER);
        long totalShopOwners = userRepository.countByRole_Code(ROLE_SHOP_OWNER);
        long usersNeverLoggedIn = userRepository.countByLastLoginAtIsNull();
        long usersInactiveLast30Days = userRepository.countByLastLoginAtBefore(
                LocalDateTime.now().minusDays(30));


        long totalMalls = mallRepository.count();
        long activeMalls = mallRepository.countByStatus(MallStatus.ACTIVE);
        long inactiveMalls = mallRepository.countByStatus(MallStatus.INACTIVE);
        long maintenanceMalls = mallRepository.countByStatus(MallStatus.MAINTENANCE);
        long totalMallCapacity = mallRepository.sumCapacity();

        long totalRestaurants = restaurantRepository.count();
        long activeRestaurants = restaurantRepository.countByIsActive(true);
        long totalMallServices = mallServiceRepository.count();
        long activeMallServices = mallServiceRepository.countByIsActive(true);

        long totalShops = shopRepository.count();
        long activeShops = shopRepository.countByStatus(ShopStatus.ACTIVE);
        long inactiveShops = shopRepository.countByStatus(ShopStatus.INACTIVE);

        long totalCities = cityRepository.count();
        long activeCities = cityRepository.countByIsActive(true);
        long inactiveCities = totalCities - activeCities;

        long pendingOwnerReq = shopOwnerRequestRepository.countByStatus(ShopOwnerRequestStatus.PENDING);
        long approvedOwnerReq = shopOwnerRequestRepository.countByStatus(ShopOwnerRequestStatus.APPROVED);
        long rejectedOwnerReq = shopOwnerRequestRepository.countByStatus(ShopOwnerRequestStatus.REJECTED);
        long totalOwnerReqs = shopOwnerRequestRepository.count();
        double approvalRate = totalOwnerReqs > 0
                ? Math.round((approvedOwnerReq * 100.0 / totalOwnerReqs) * 10) / 10.0 : 0.0;

        long totalShopReqs = shopRequestRepository.countByExistingUserIsNotNull();
        long pendingShopReq = shopRequestRepository
                .countByExistingUserIsNotNullAndStatus(ShopRequestStatus.PENDING);
        long approvedShopReq = shopRequestRepository
                .countByExistingUserIsNotNullAndStatus(ShopRequestStatus.APPROVED);
        long rejectedShopReq = shopRequestRepository
                .countByExistingUserIsNotNullAndStatus(ShopRequestStatus.REJECTED);
        double shopReqApprovalRate = totalShopReqs > 0
                ? Math.round((approvedShopReq * 100.0 / totalShopReqs) * 10) / 10.0 : 0.0;

        List<RecentShopRequestDto> recentPendingShopRequests =
                shopRequestRepository
                        .findRecentPendingByExistingOwner(PageRequest.of(0, 5))
                        .stream()
                        .map(r -> RecentShopRequestDto.builder()
                                .id(r.getId())
                                .ownerUsername(r.getExistingUser().getUsername())
                                .shopName(r.getName())
                                .mallName(r.getMall().getName())
                                .status(r.getStatus().name())
                                .submittedAt(r.getCreatedAt() != null
                                        ? r.getCreatedAt().format(FORMATTER) : null)
                                .build())
                        .collect(Collectors.toList());

        // Users by role
        List<ChartEntry> usersByRole = List.of(
                new ChartEntry("Admin", userRepository.countByRole_Code(ROLE_ADMIN)),
                new ChartEntry("Customer", totalCustomers),
                new ChartEntry("Shop owner", totalShopOwners)
        );

        // Users by gender
        Map<Gender, Long> genderMap = userRepository.countByGenderGrouped().stream()
                .collect(Collectors.toMap(
                        g -> g.getGender() != null ? g.getGender() : null,
                        UserRepository.GenderCount::getValue,
                        Long::sum
                ));

        long maleCount = genderMap.getOrDefault(Gender.MALE, 0L);
        long femaleCount = genderMap.getOrDefault(Gender.FEMALE, 0L);
        long noGender = totalUsers - maleCount - femaleCount;
        List<ChartEntry> usersByGender = List.of(
                new ChartEntry("Male", maleCount),
                new ChartEntry("Female", femaleCount),
                new ChartEntry("Not set", noGender)
        );

        List<ChartEntry> usersByAgeBucket = buildAgeBuckets();

        // Shops by status
        List<ChartEntry> shopsByStatus = List.of(
                new ChartEntry("Active", activeShops),
                new ChartEntry("Inactive", inactiveShops)
        );

        // Malls by status
        List<ChartEntry> mallsByStatus = List.of(
                new ChartEntry("Active", activeMalls),
                new ChartEntry("Inactive", inactiveMalls),
                new ChartEntry("Maintenance", maintenanceMalls)
        );

        // Shops by category
        List<ChartEntry> shopsByCategory = shopRepository.countByCategory().stream()
                .map(r -> new ChartEntry(r.getLabel(), r.getValue()))
                .collect(Collectors.toList());

        // Shops per mall
        List<ChartEntry> shopsPerMall = shopRepository.countGroupedByMall().stream()
                .limit(10)
                .map(r -> new ChartEntry(r.getLabel(), r.getValue()))
                .collect(Collectors.toList());

        // Shops per city
        List<ChartEntry> shopsPerCity = shopRepository.countGroupedByCity().stream()
                .map(r -> new ChartEntry(r.getLabel(), r.getValue()))
                .collect(Collectors.toList());

        // Malls per city
        List<ChartEntry> mallsPerCity = mallRepository.countGroupedByCity().stream()
                .map(r -> new ChartEntry(r.getLabel(), r.getValue()))
                .collect(Collectors.toList());

        // Restaurants per mall
        List<ChartEntry> restaurantsPerMall = restaurantRepository.countGroupedByMall().stream()
                .limit(10)
                .map(r -> new ChartEntry(r.getLabel(), r.getValue()))
                .collect(Collectors.toList());

        // Restaurants by cuisine
        List<ChartEntry> restaurantsByCuisine = restaurantRepository.countByCuisineGrouped().stream()
                .map(r -> new ChartEntry(r.getLabel(), r.getValue()))
                .collect(Collectors.toList());

        // City base fees
        List<ChartEntry> cityBaseFees = cityRepository.findAll().stream()
                .map(c -> new ChartEntry(c.getName(),
                        c.getBaseFee() != null ? c.getBaseFee().longValue() : 0L))
                .sorted(Comparator.comparingLong(ChartEntry::getValue).reversed())
                .collect(Collectors.toList());


        List<ChartEntry> mallCapacityUtilization = buildMallCapacityChart();

        // Request status distribution
        List<ChartEntry> requestStatusDistribution = List.of(
                new ChartEntry("Pending", pendingOwnerReq),
                new ChartEntry("Approved", approvedOwnerReq),
                new ChartEntry("Rejected", rejectedOwnerReq)
        );

        List<TimeSeriesEntry> newUsersPerMonth = buildMonthlyTimeSeries(userRepository.findAllCreatedAt());
        List<TimeSeriesEntry> newShopsPerMonth = buildMonthlyTimeSeries(shopRepository.findAllCreatedAt());
        List<TimeSeriesEntry> newMallsPerMonth = buildMonthlyTimeSeries(mallRepository.findAllCreatedAt());
        List<TimeSeriesEntry> requestsPerMonth = buildMonthlyTimeSeries(shopOwnerRequestRepository.findAllCreatedAt());

        // Recent pending requests
        List<RecentRequestDto> recentPendingRequests =
                shopOwnerRequestRepository.findByStatus(ShopOwnerRequestStatus.PENDING).stream()
                        .sorted(Comparator.comparing(
                                r -> r.getCreatedAt() != null ? r.getCreatedAt() : LocalDateTime.MIN,
                                Comparator.reverseOrder()))
                        .limit(5)
                        .map(r -> RecentRequestDto.builder()
                                .id(r.getId())
                                .username(r.getUsername())
                                .shopName(r.getShopRequest() != null ? r.getShopRequest().getName() : "N/A")
                                .status(r.getStatus().name())
                                .submittedAt(r.getCreatedAt() != null ? r.getCreatedAt().format(FORMATTER) : null)
                                .build())
                        .collect(Collectors.toList());

        // Recent users
        List<RecentUserDto> recentUsers =
                userRepository.findAll().stream()
                        .filter(u -> u.getCreatedAt() != null)
                        .sorted(Comparator.comparing(User::getCreatedAt, Comparator.reverseOrder()))
                        .limit(5)
                        .map(u -> RecentUserDto.builder()
                                .userId(u.getUserId())
                                .username(u.getUsername())
                                .fullName(u.getFullName())
                                .role(u.getRole().getCode())
                                .createdAt(u.getCreatedAt().format(FORMATTER))
                                .build())
                        .collect(Collectors.toList());

        // Recent shops
        List<RecentShopDto> recentShops =
                shopRepository.findAll().stream()
                        .filter(s -> s.getCreatedAt() != null)
                        .sorted(Comparator.comparing(Shop::getCreatedAt, Comparator.reverseOrder()))
                        .limit(5)
                        .map(s -> RecentShopDto.builder()
                                .shopId(s.getShopId())
                                .name(s.getName())
                                .mallName(s.getMall().getName())
                                .ownerUsername(s.getOwner().getUsername())
                                .status(s.getStatus().name())
                                .createdAt(s.getCreatedAt().format(FORMATTER))
                                .build())
                        .collect(Collectors.toList());

        // Top shop owners
        List<TopOwnerDto> topShopOwners = buildTopOwners();

        // City stats
        List<CityStatsDto> cityStats = buildCityStats();

        // Mall stats
        List<MallStatsDto> mallStats = buildMallStats();

        return AdminDashboardDto.builder()
                // user cards
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .inactiveUsers(inactiveUsers)
                .totalCustomers(totalCustomers)
                .totalShopOwners(totalShopOwners)
                .usersNeverLoggedIn(usersNeverLoggedIn)
                .usersInactiveLast30Days(usersInactiveLast30Days)
                // mall cards
                .totalMalls(totalMalls)
                .activeMalls(activeMalls)
                .inactiveMalls(inactiveMalls)
                .maintenanceMalls(maintenanceMalls)
                .totalMallCapacity(totalMallCapacity)
                .totalRestaurants(totalRestaurants)
                .activeRestaurants(activeRestaurants)
                .totalMallServices(totalMallServices)
                .activeMallServices(activeMallServices)
                // shop cards
                .totalShops(totalShops)
                .activeShops(activeShops)
                .inactiveShops(inactiveShops)
                // city cards
                .totalCities(totalCities)
                .activeCities(activeCities)
                .inactiveCities(inactiveCities)
                // request cards
                .pendingShopOwnerRequests(pendingOwnerReq)
                .approvedShopOwnerRequests(approvedOwnerReq)
                .rejectedShopOwnerRequests(rejectedOwnerReq)
                .totalShopOwnerRequests(totalOwnerReqs)
                .requestApprovalRate(approvalRate)
                .totalShopRequests(totalShopReqs)
                .shopRequestApprovalRate(shopReqApprovalRate)
                .pendingShopRequests(pendingShopReq)
                .approvedShopRequests(approvedShopReq)
                .rejectedShopRequests(rejectedShopReq)
                .recentPendingShopRequests(recentPendingShopRequests)
                // charts
                .usersByRole(usersByRole)
                .usersByGender(usersByGender)
                .usersByAgeBucket(usersByAgeBucket)
                .shopsByStatus(shopsByStatus)
                .mallsByStatus(mallsByStatus)
                .shopsByCategory(shopsByCategory)
                .shopsPerMall(shopsPerMall)
                .mallsPerCity(mallsPerCity)
                .shopsPerCity(shopsPerCity)
                .restaurantsPerMall(restaurantsPerMall)
                .restaurantsByCuisine(restaurantsByCuisine)
                .cityBaseFees(cityBaseFees)
                .mallCapacityUtilization(mallCapacityUtilization)
                .requestStatusDistribution(requestStatusDistribution)
                // time series
                .newUsersPerMonth(newUsersPerMonth)
                .newShopsPerMonth(newShopsPerMonth)
                .newMallsPerMonth(newMallsPerMonth)
                .requestsPerMonth(requestsPerMonth)
                // tables
                .recentPendingRequests(recentPendingRequests)
                .recentUsers(recentUsers)
                .recentShops(recentShops)
                .topShopOwners(topShopOwners)
                .cityStats(cityStats)
                .mallStats(mallStats)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ShopOwnerDashboardDto getShopOwnerDashboard(Long userId) {
        log.debug("Building shop owner dashboard for userId={}", userId);

        User owner = userRepository.findById(userId)
                .orElseThrow(UserExceptions::userNotFound);

        // Shop counts
        long totalShops = shopRepository.countByOwner_UserId(userId);
        long activeShops = shopRepository.countByOwner_UserIdAndStatus(userId, ShopStatus.ACTIVE);
        long inactiveShops = shopRepository.countByOwner_UserIdAndStatus(userId, ShopStatus.INACTIVE);

        // Request counts
        long pendingReqs = shopRequestRepository
                .countByExistingUserIsNotNullAndStatus(ShopRequestStatus.PENDING);
        long approvedReqs = shopRequestRepository
                .countByExistingUserIsNotNullAndStatus(ShopRequestStatus.APPROVED);
        long rejectedReqs = shopRequestRepository
                .countByExistingUserIsNotNullAndStatus(ShopRequestStatus.REJECTED);

        // Charts
        Map<ShopStatus, Long> statusCounts = shopRepository
                .countByOwnerIdGroupByStatus(userId).stream()
                .collect(Collectors.toMap(
                        ShopRepository.StatusCount::getStatus,
                        ShopRepository.StatusCount::getValue
                ));

        List<ChartEntry> shopsByStatus = List.of(
                new ChartEntry("Active", statusCounts.getOrDefault(ShopStatus.ACTIVE, 0L)),
                new ChartEntry("Inactive", statusCounts.getOrDefault(ShopStatus.INACTIVE, 0L))
        );

        List<ChartEntry> requestsByStatus = List.of(
                new ChartEntry("Pending",  pendingReqs),
                new ChartEntry("Approved", approvedReqs),
                new ChartEntry("Rejected", rejectedReqs)
        );

        List<Shop> myShops = shopRepository.findByOwner_UserId(userId);

        List<ChartEntry> shopsByCategory = myShops.stream()
                .collect(Collectors.groupingBy(s -> s.getCategory().name(), Collectors.counting()))
                .entrySet().stream()
                .map(e -> new ChartEntry(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(ChartEntry::getValue).reversed())
                .collect(Collectors.toList());

        List<ChartEntry> shopsByMall = myShops.stream()
                .collect(Collectors.groupingBy(s -> s.getMall().getName(), Collectors.counting()))
                .entrySet().stream()
                .map(e -> new ChartEntry(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(ChartEntry::getValue).reversed())
                .collect(Collectors.toList());

        List<OwnedShopDto> shopTable = myShops.stream()
                .map(s -> OwnedShopDto.builder()
                        .shopId(s.getShopId())
                        .name(s.getName())
                        .mallName(s.getMall().getName())
                        .cityName(s.getMall().getCity().getName())
                        .category(s.getCategory().name())
                        .location(s.getLocation())
                        .status(s.getStatus().name())
                        .createdAt(s.getCreatedAt() != null ? s.getCreatedAt().format(FORMATTER) : null)
                        .build())
                .collect(Collectors.toList());

        List<MyShopRequestDto> requestTable =
                shopRequestRepository.findByExistingUser_UserId(userId).stream()
                        .sorted(Comparator.comparing(
                                r -> r.getCreatedAt() != null ? r.getCreatedAt() : LocalDateTime.MIN,
                                Comparator.reverseOrder()))
                        .map(r -> MyShopRequestDto.builder()
                                .requestId(r.getId())
                                .shopName(r.getName())
                                .mallName(r.getMall().getName())
                                .status(r.getStatus().name())
                                .rejectionReason(r.getRejectionReason())
                                .submittedAt(r.getCreatedAt() != null ? r.getCreatedAt().format(FORMATTER) : null)
                                .build())
                        .collect(Collectors.toList());

        Map<Long, Long> restaurantsByMallId = restaurantRepository.countPerMall().stream()
                .collect(Collectors.toMap(
                        MallRestaurantRepository.MallCount::getMallId,
                        MallRestaurantRepository.MallCount::getCount
                ));

        Map<Long, Long> servicesByMallId = mallServiceRepository.countPerMall().stream()
                .collect(Collectors.toMap(
                        MallServiceRepository.MallServiceCount::getMallId,
                        MallServiceRepository.MallServiceCount::getCount
                ));

        Map<Long, List<Shop>> myShopsByMallId = myShops.stream()
                .collect(Collectors.groupingBy(s -> s.getMall().getMallId()));

        List<Long> myMallIds = new ArrayList<>(myShopsByMallId.keySet());
        Map<Long, Mall> mallById = mallRepository.findAllWithCity().stream()
                .filter(m -> myMallIds.contains(m.getMallId()))
                .collect(Collectors.toMap(Mall::getMallId, m -> m));

        List<MyMallSummaryDto> myMalls = myShopsByMallId.entrySet().stream()
                .map(e -> {
                    Long mallId = e.getKey();
                    Mall mall = mallById.get(mallId);
                    if (mall == null) return null;
                    long totalInMall = shopRepository.countByMall_MallId(mallId);
                    return MyMallSummaryDto.builder()
                            .mallId(mallId)
                            .mallName(mall.getName())
                            .cityName(mall.getCity().getName())
                            .mallStatus(mall.getStatus().name())
                            .myShopsInThisMall(e.getValue().size())
                            .totalShopsInMall(totalInMall)
                            .restaurantsInMall(restaurantsByMallId.getOrDefault(mallId, 0L))
                            .servicesInMall(servicesByMallId.getOrDefault(mallId, 0L))
                            .build();
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(
                        MyMallSummaryDto::getMyShopsInThisMall).reversed())
                .collect(Collectors.toList());

        return ShopOwnerDashboardDto.builder()
                .userId(owner.getUserId())
                .username(owner.getUsername())
                .fullName(owner.getFullName())
                .email(owner.getEmail())
                .lastLoginAt(owner.getLastLoginAt() != null
                        ? owner.getLastLoginAt().format(FORMATTER) : null)
                .totalShops(totalShops)
                .activeShops(activeShops)
                .inactiveShops(inactiveShops)
                .pendingShopRequests(pendingReqs)
                .approvedShopRequests(approvedReqs)
                .rejectedShopRequests(rejectedReqs)
                .shopsByStatus(shopsByStatus)
                .shopsByCategory(shopsByCategory)
                .shopsByMall(shopsByMall)
                .requestsByStatus(requestsByStatus)
                .myShops(shopTable)
                .myShopRequests(requestTable)
                .myMalls(myMalls)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDashboardDto getCustomerDashboard(Long userId) {
        log.debug("Building customer dashboard for userId={}", userId);

        User customer = userRepository.findById(userId)
                .orElseThrow(UserExceptions::userNotFound);

        long totalActiveMalls = mallRepository.countByStatus(MallStatus.ACTIVE);
        long totalActiveShops = shopRepository.countByStatus(ShopStatus.ACTIVE);
        long totalActiveCities = cityRepository.countByIsActive(true);

        // Profile completion
        int filled = 0;
        if (customer.getFullName() != null) filled++;
        if (customer.getEmail() != null) filled++;
        if (customer.getGender() != null) filled++;
        if (customer.getAge() != null) filled++;
        if (customer.getNationalIdNumber() != null) filled++;
        if (customer.getProfilePictureUuid() != null) filled++;
        int completionPct = Math.round((filled * 100.0f) / 6);

        return CustomerDashboardDto.builder()
                .userId(customer.getUserId())
                .username(customer.getUsername())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .gender(customer.getGender() != null ? customer.getGender().name() : null)
                .age(customer.getAge())
                .lastLoginAt(customer.getLastLoginAt() != null
                        ? customer.getLastLoginAt().format(FORMATTER) : null)
                .profileComplete(completionPct == 100)
                .profileCompletionPercent(completionPct)
                .totalActiveMalls(totalActiveMalls)
                .totalActiveShops(totalActiveShops)
                .totalActiveCities(totalActiveCities)
                .build();
    }

    private List<ChartEntry> buildAgeBuckets() {
        List<Integer> ages = userRepository.findAllAges();
        long noAge = userRepository.countWithNoAge();
        long age0to18 = ages.stream().filter(a -> a <= 18).count();
        long age19to30 = ages.stream().filter(a -> a >= 19 && a <= 30).count();
        long age31to45 = ages.stream().filter(a -> a >= 31 && a <= 45).count();
        long age46to60 = ages.stream().filter(a -> a >= 46 && a <= 60).count();
        long age60plus = ages.stream().filter(a -> a > 60).count();
        return List.of(
                new ChartEntry("0 – 18",  age0to18),
                new ChartEntry("19 – 30", age19to30),
                new ChartEntry("31 – 45", age31to45),
                new ChartEntry("46 – 60", age46to60),
                new ChartEntry("60+",     age60plus),
                new ChartEntry("Not set", noAge)
        );
    }

    private List<ChartEntry> buildMallCapacityChart() {
        Map<Long, Long> shopCountByMallId = shopRepository.countPerMall().stream()
                .collect(Collectors.toMap(
                        ShopRepository.MallShopCount::getMallId,
                        ShopRepository.MallShopCount::getCount
                ));

        return mallRepository.findAllWithCity().stream()
                .filter(m -> m.getCapacity() != null && m.getCapacity() > 0)
                .map(m -> {
                    long shopCount = shopCountByMallId.getOrDefault(m.getMallId(), 0L);
                    long pct = Math.min(Math.round((shopCount * 100.0) / m.getCapacity()), 100L);
                    return new ChartEntry(m.getName(), pct);
                })
                .sorted(Comparator.comparingLong(ChartEntry::getValue).reversed())
                .collect(Collectors.toList());
    }

    private List<TopOwnerDto> buildTopOwners() {
        List<ShopRepository.OwnerShopCount> topCounts =
                shopRepository.countGroupedByOwner(PageRequest.of(0, 5));

        if (topCounts.isEmpty()) return Collections.emptyList();

        // Fetch only those 5 users
        Set<Long> topOwnerIds = topCounts.stream()
                .map(ShopRepository.OwnerShopCount::getUserId)
                .collect(Collectors.toSet());

        Map<Long, User> userById = userRepository.findAllById(topOwnerIds).stream()
                .collect(Collectors.toMap(User::getUserId, u -> u));

        return topCounts.stream()
                .map(oc -> {
                    User user = userById.get(oc.getUserId());
                    if (user == null) return null;
                    long activeCount = shopRepository.countByOwner_UserIdAndStatus(
                            oc.getUserId(), ShopStatus.ACTIVE);
                    return TopOwnerDto.builder()
                            .userId(user.getUserId())
                            .username(user.getUsername())
                            .fullName(user.getFullName())
                            .shopCount(oc.getShopCount())
                            .activeShops(activeCount)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    private List<CityStatsDto> buildCityStats() {
        return cityRepository.findAll().stream()
                .map(c -> {
                    long mallCount = mallRepository.countByCity_CityId(c.getCityId());
                    long shopCount = shopRepository.countByMall_City_CityId(c.getCityId());
                    return CityStatsDto.builder()
                            .cityId(c.getCityId())
                            .name(c.getName())
                            .baseFee(c.getBaseFee() != null ? c.getBaseFee().toPlainString() : "0")
                            .isActive(Boolean.TRUE.equals(c.getIsActive()))
                            .mallCount(mallCount)
                            .shopCount(shopCount)
                            .build();
                })
                .sorted(Comparator.comparingLong(CityStatsDto::getShopCount).reversed())
                .collect(Collectors.toList());
    }

    private List<MallStatsDto> buildMallStats() {

        Map<Long, Long> shopCountPerMall = shopRepository.countPerMall().stream()
                .collect(Collectors.toMap(
                        ShopRepository.MallShopCount::getMallId,
                        ShopRepository.MallShopCount::getCount
                ));

        Map<Long, Long> activeShopCountPerMall = shopRepository.countActivePerMall().stream()
                .collect(Collectors.toMap(
                        ShopRepository.MallShopCount::getMallId,
                        ShopRepository.MallShopCount::getCount
                ));

        Map<Long, Long> restaurantCountPerMall = restaurantRepository.countPerMall().stream()
                .collect(Collectors.toMap(
                        MallRestaurantRepository.MallCount::getMallId,
                        MallRestaurantRepository.MallCount::getCount
                ));

        Map<Long, Long> serviceCountPerMall = mallServiceRepository.countPerMall().stream()
                .collect(Collectors.toMap(
                        MallServiceRepository.MallServiceCount::getMallId,
                        MallServiceRepository.MallServiceCount::getCount
                ));

        return mallRepository.findAllWithCity().stream()
                .map(m -> {
                    long shopCount = shopCountPerMall.getOrDefault(m.getMallId(), 0L);
                    long activeShops = activeShopCountPerMall.getOrDefault(m.getMallId(), 0L);
                    long restaurants = restaurantCountPerMall.getOrDefault(m.getMallId(), 0L);
                    long services = serviceCountPerMall.getOrDefault(m.getMallId(), 0L);
                    String capUsage = m.getCapacity() != null && m.getCapacity() > 0
                            ? shopCount + " / " + m.getCapacity() + " ("
                            + Math.round(shopCount * 100.0 / m.getCapacity()) + "%)"
                            : shopCount + " shops";
                    return MallStatsDto.builder()
                            .mallId(m.getMallId())
                            .name(m.getName())
                            .cityName(m.getCity().getName())
                            .status(m.getStatus().name())
                            .capacity(m.getCapacity())
                            .shopCount(shopCount)
                            .activeShopCount(activeShops)
                            .restaurantCount(restaurants)
                            .serviceCount(services)
                            .capacityUsage(capUsage)
                            .build();
                })
                .sorted(Comparator.comparingLong(MallStatsDto::getShopCount).reversed())
                .collect(Collectors.toList());
    }

     // Monthly time series — last 12 months.
    private List<TimeSeriesEntry> buildMonthlyTimeSeries(List<LocalDateTime> timestamps) {
        YearMonth now = YearMonth.now();
        LinkedHashMap<String, Long> map = new LinkedHashMap<>();
        for (int i = 11; i >= 0; i--) {
            map.put(now.minusMonths(i).format(MONTH_FMT), 0L);
        }
        timestamps.forEach(ts -> {
            String key = YearMonth.from(ts).format(MONTH_FMT);
            if (map.containsKey(key)) map.merge(key, 1L, Long::sum);
        });
        return map.entrySet().stream()
                .map(e -> TimeSeriesEntry.builder().period(e.getKey()).value(e.getValue()).build())
                .collect(Collectors.toList());
    }

    private Long extractMallIdFromLabel(List<ChartEntry> ignored, String label) { return null; }
    private Long extractCityIdFromName(String name) { return null; }
}
