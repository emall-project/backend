package store.emall.backend.accounts.request.shopowner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.accounts.city.City;
import store.emall.backend.accounts.city.CityRepository;
import store.emall.backend.campaigns.subscription.ShopSubscriptionService;
import store.emall.backend.catalog.brand.BrandDto;
import store.emall.backend.catalog.brand.BrandService;
import store.emall.backend.common.audience.AgeGroup;
import store.emall.backend.common.audience.TargetedAudience;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.phone_number.PhoneNumberMapper;
import store.emall.backend.accounts.mall.Mall;
import store.emall.backend.accounts.mall.MallExceptions;
import store.emall.backend.accounts.mall.MallRepository;
import store.emall.backend.accounts.mall.MallStatus;
import store.emall.backend.accounts.notification.ShopRequestNotificationService;
import store.emall.backend.accounts.request.admin.AdminDecisionDto;
import store.emall.backend.accounts.request.shop.ExistingOwnerShopRequestDto;
import store.emall.backend.accounts.request.shop.ShopRequest;
import store.emall.backend.accounts.request.shop.ShopRequestDto;
import store.emall.backend.accounts.request.shop.ShopRequestRepository;
import store.emall.backend.accounts.request.shop.ShopRequestStatus;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.mediamanager.file.FileService;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.mediamanager.file.dto.FileTransferRequest;
import store.emall.backend.mediamanager.folder.FolderService;
import store.emall.backend.mediamanager.folder.dto.FolderDto;
import store.emall.backend.security.SecurityContextUtil;
import store.emall.backend.accounts.shop.Shop;
import store.emall.backend.accounts.shop.ShopExceptions;
import store.emall.backend.accounts.shop.ShopRepository;
import store.emall.backend.accounts.shop.ShopStatus;
import store.emall.backend.accounts.user.User;
import store.emall.backend.accounts.user.UserExceptions;
import store.emall.backend.accounts.user.UserRepository;
import store.emall.backend.accounts.user.role.Role;
import store.emall.backend.accounts.user.role.RoleExceptions;
import store.emall.backend.accounts.user.role.RoleRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopOwnerRequestServiceImpl implements ShopOwnerRequestService {

    private static final String SHOP_OWNER_ROLE_CODE = "ROLE_SHOP_OWNER";

    private final ShopOwnerRequestRepository shopOwnerRequestRepository;
    private final ShopRequestRepository shopRequestRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MallRepository mallRepository;
    private final ShopRepository shopRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final FolderService folderService;
    private final ShopRequestNotificationService notificationService;
    private final CityRepository cityRepository;
    private final ShopSubscriptionService shopSubscriptionService;

    private final BrandService brandService;

    // PATH A — New person: submit ShopOwnerRequest + ShopRequest together

    @Override
    @Transactional
    public ShopOwnerRequestDto submitRequest(ShopOwnerRequestDto dto) {

        // Manually validate shopRequest is present
        if (dto.getShopRequest() == null) {
            throw ShopOwnerRequestExceptions.shopRequestRequired();
        }

        String phoneString = PhoneNumberMapper.toPhoneString(dto.getPhone());

        // Check if there is an existing REJECTED request for this username — if so, resubmit it
        Optional<ShopOwnerRequest> existingRejected = shopOwnerRequestRepository
                .findByUsernameAndStatus(dto.getUsername(), ShopOwnerRequestStatus.REJECTED);

        if (existingRejected.isPresent()) {
            return resubmitRejectedRequest(existingRejected.get(), dto, phoneString);
        }

        // Block if PENDING or APPROVED already exists (ignore REJECTED —> handled above)
        if (shopOwnerRequestRepository.existsByUsernameAndStatusNot(
                dto.getUsername(), ShopOwnerRequestStatus.REJECTED)) {
            throw ShopOwnerRequestExceptions.usernameAlreadyPendingOrExists();
        }

        if (shopOwnerRequestRepository.existsByPhoneNumberAndStatusNot(
                phoneString, ShopOwnerRequestStatus.REJECTED)) {
            throw ShopOwnerRequestExceptions.phoneAlreadyPendingOrExists();
        }

        if (dto.getEmail() != null && shopOwnerRequestRepository.existsByEmailAndStatusNot(
                dto.getEmail(), ShopOwnerRequestStatus.REJECTED)) {
            throw ShopOwnerRequestExceptions.emailAlreadyPendingOrExists();
        }

        if (dto.getNationalIdNumber() != null && shopOwnerRequestRepository.existsByNationalIdNumberAndStatusNot(
                dto.getNationalIdNumber(), ShopOwnerRequestStatus.REJECTED)) {
            throw ShopOwnerRequestExceptions.nationalIdAlreadyPendingOrExists();
        }

        // Also validate against actual users table
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw UserExceptions.usernameExists();
        }
        if (userRepository.existsByPhoneNumber(phoneString)) {
            throw UserExceptions.phoneExists();
        }
        if (dto.getEmail() != null && userRepository.existsByEmail(dto.getEmail())) {
            throw UserExceptions.emailExists();
        }
        if (dto.getNationalIdNumber() != null && userRepository.existsByNationalIdNumber(dto.getNationalIdNumber())) {
            throw UserExceptions.nationalIdExists();
        }

        // Validate mall exists
        ShopRequestDto shopRequestDto = dto.getShopRequest();

        // Validate media files
        FileDto profilePictureImage = null;
        if (dto.getProfilePictureUuid() != null) {
            profilePictureImage = getAndValidateImage(dto.getProfilePictureUuid(), "ProfilePictureUuid");
        }

        FileDto licenseImage = getAndValidateImage(shopRequestDto.getLicenseImageUuid(), "LicenseImageUuid");
        List<FileDto> shopPhotos = getAndValidateImages(shopRequestDto.getShopPhotosUuids(), "ShopPhotosUuid");

        FileDto logoImage = null;
        if (shopRequestDto.getLogoUuid() != null) {
            logoImage = getAndValidateImage(shopRequestDto.getLogoUuid(), "LogoUuid");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        ShopOwnerRequest ownerRequest = ShopOwnerRequestMapper.toEntity(dto, encodedPassword, phoneString);

        ShopRequest shopRequest = ShopRequest.builder()
                .name(shopRequestDto.getName())
                .category(shopRequestDto.getCategory())
                .description(shopRequestDto.getDescription())
                .location(shopRequestDto.getLocation())
                .contactInfo(shopRequestDto.getContactInfo())
                .status(ShopRequestStatus.PENDING)
                .shopOwnerRequest(ownerRequest)
                .existingUser(null)
                .licenseImageUuid(shopRequestDto.getLicenseImageUuid())
                .shopPhotosUuids(shopRequestDto.getShopPhotosUuids())
                .logoUuid(shopRequestDto.getLogoUuid())
                .build();

        applyMallToShopRequest(shopRequest, shopRequestDto);

        ownerRequest.setShopRequest(shopRequest);

        ShopOwnerRequest saved = shopOwnerRequestRepository.save(ownerRequest);

        try {
            notificationService.notifyAdminNewRequest(saved);
        } catch (Exception e) {
            log.warn("Failed to send admin notification for shopOwnerRequestId={}: {}", saved.getId(), e.getMessage());
        }

        log.info("New shop owner request submitted: id={}, username={}", saved.getId(), saved.getUsername());
        return ShopOwnerRequestMapper.toFullDto(saved, licenseImage, shopPhotos, logoImage, profilePictureImage);
    }

    private ShopOwnerRequestDto resubmitRejectedRequest(
            ShopOwnerRequest existing, ShopOwnerRequestDto dto, String phoneString) {

        // Still validate against users table
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw UserExceptions.usernameExists();
        }
        if (userRepository.existsByPhoneNumber(phoneString)) {
            throw UserExceptions.phoneExists();
        }
        if (dto.getEmail() != null && userRepository.existsByEmail(dto.getEmail())) {
            throw UserExceptions.emailExists();
        }
        if (dto.getNationalIdNumber() != null && userRepository.existsByNationalIdNumber(dto.getNationalIdNumber())) {
            throw UserExceptions.nationalIdExists();
        }

        ShopRequestDto shopRequestDto = dto.getShopRequest();

        // Validate media files
        FileDto profilePictureImage = null;
        if (dto.getProfilePictureUuid() != null) {
            profilePictureImage = getAndValidateImage(dto.getProfilePictureUuid(), "ProfilePictureUuid");
        }

        FileDto licenseImage = getAndValidateImage(shopRequestDto.getLicenseImageUuid(), "LicenseImageUuid");
        List<FileDto> shopPhotos = getAndValidateImages(shopRequestDto.getShopPhotosUuids(), "ShopPhotosUuids");

        FileDto logoImage = null;
        if (shopRequestDto.getLogoUuid() != null) {
            logoImage = getAndValidateImage(shopRequestDto.getLogoUuid(), "LogoUuid");
        }

        // Reset owner request fields
        existing.setFullName(dto.getFullName());
        existing.setEmail(dto.getEmail());
        existing.setPhoneNumber(phoneString);
        existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        existing.setGender(dto.getGender());
        existing.setAge(dto.getAge());
        existing.setNationalIdNumber(dto.getNationalIdNumber());
        existing.setProfilePictureUuid(dto.getProfilePictureUuid());
        existing.setStatus(ShopOwnerRequestStatus.PENDING);
        existing.setRejectionReason(null);

        // Reset the linked shop request
        ShopRequest shopRequest = existing.getShopRequest();
        shopRequest.setName(shopRequestDto.getName());
        shopRequest.setCategory(shopRequestDto.getCategory());
        shopRequest.setDescription(shopRequestDto.getDescription());
        shopRequest.setLocation(shopRequestDto.getLocation());
        shopRequest.setContactInfo(shopRequestDto.getContactInfo());
        shopRequest.setLicenseImageUuid(shopRequestDto.getLicenseImageUuid());
        shopRequest.setShopPhotosUuids(shopRequestDto.getShopPhotosUuids());
        shopRequest.setLogoUuid(shopRequestDto.getLogoUuid());
        shopRequest.setStatus(ShopRequestStatus.PENDING);
        shopRequest.setRejectionReason(null);
        shopRequest.setCreatedShop(null);

        applyMallToShopRequest(shopRequest, shopRequestDto);

        ShopOwnerRequest saved = shopOwnerRequestRepository.save(existing);

        try {
            notificationService.notifyAdminNewRequest(saved);
        } catch (Exception e) {
            log.warn("Failed to send admin notification for resubmit shopOwnerRequestId={}: {}", saved.getId(), e.getMessage());
        }

        log.info("Rejected shop owner request resubmitted: id={}, username={}", saved.getId(), saved.getUsername());
        return ShopOwnerRequestMapper.toFullDto(saved, licenseImage, shopPhotos, logoImage, profilePictureImage);
    }


    // PATH B — Existing shop owner submits a new ShopRequest

    @Override
    @Transactional
    public ShopRequestDto submitShopRequestForExistingOwner(ExistingOwnerShopRequestDto dto) {

        Long currentUserId = SecurityContextUtil.getCurrentUserId();

        // Validate credentials — find user by username
        User existingUser = userRepository.findById(currentUserId)
                .orElseThrow(UserExceptions::userNotFound);

        // Validate the user has SHOP_OWNER role
        if (!existingUser.getRole().getCode().equals(SHOP_OWNER_ROLE_CODE)) {
            throw ShopOwnerRequestExceptions.notAShopOwner();
        }

        ShopRequestDto shopRequestDto = dto.getShopRequest();

        // Validate media files
        FileDto licenseImage = getAndValidateImage(shopRequestDto.getLicenseImageUuid(), "LicenseImageUuid");
        List<FileDto> shopPhotos = getAndValidateImages(shopRequestDto.getShopPhotosUuids(), "ShopPhotosUuids");

        FileDto logoImage = null;
        if (shopRequestDto.getLogoUuid() != null) {
            logoImage = getAndValidateImage(shopRequestDto.getLogoUuid(), "LogoUuid");
        }

        ShopRequest shopRequest = ShopRequest.builder()
                .name(shopRequestDto.getName())
                .category(shopRequestDto.getCategory())
                .description(shopRequestDto.getDescription())
                .location(shopRequestDto.getLocation())
                .contactInfo(shopRequestDto.getContactInfo())
                .status(ShopRequestStatus.PENDING)
                .shopOwnerRequest(null)
                .existingUser(existingUser)
                .licenseImageUuid(shopRequestDto.getLicenseImageUuid())
                .shopPhotosUuids(shopRequestDto.getShopPhotosUuids())
                .logoUuid(shopRequestDto.getLogoUuid())
                .build();

        applyMallToShopRequest(shopRequest, shopRequestDto);

        ShopRequest saved = shopRequestRepository.save(shopRequest);

        log.info("New shop request submitted by existing owner: userId={}, shopName={}, requestId={}",
                existingUser.getUserId(), saved.getName(), saved.getId());

        return ShopOwnerRequestMapper.toFullShopRequestDto(saved, licenseImage, shopPhotos, logoImage);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ShopOwnerRequestDto> getAll(Pageable pageable, Specification<ShopOwnerRequest> spec) {
        Page<ShopOwnerRequestDto> page = shopOwnerRequestRepository.findAll(spec, pageable)
                .map(this::toDtoWithMedia);
        return PaginatedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopOwnerRequestDto> getAllPending() {
        return shopOwnerRequestRepository.findByStatus(ShopOwnerRequestStatus.PENDING)
                .stream()
                .map(this::toDtoWithMedia)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ShopOwnerRequestDto getById(Long id) {
        ShopOwnerRequest entity = shopOwnerRequestRepository.findById(id)
                .orElseThrow(ShopOwnerRequestExceptions::requestNotFound);
        return toDtoWithMedia(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopOwnerRequestDto getByIdForOwner(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopOwnerRequestDto> getAllList(Specification<ShopOwnerRequest> spec) {

        List<ShopOwnerRequest> shopOwnerRequests = (spec == null)
                ? shopOwnerRequestRepository.findAll()
                : shopOwnerRequestRepository.findAll(spec);

        return shopOwnerRequests
                .stream()
                .map(this::toDtoWithMedia)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ShopRequestDto> getAllExistingOwnerRequests(
            Pageable pageable, Specification<ShopRequest> spec) {

        // Always restrict to existingUser IS NOT NULL — combine with incoming spec filters
        Specification<ShopRequest> existingUserOnly =
                (root, query, cb) -> cb.isNotNull(root.get("existingUser"));

        Specification<ShopRequest> combined = Specification.where(existingUserOnly).and(spec);

        Page<ShopRequestDto> page = shopRequestRepository.findAll(combined, pageable)
                .map(this::toShopRequestDtoWithMedia);

        return PaginatedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopRequestDto> getAllExistingOwnerRequestsList(Specification<ShopRequest> spec) {

        Specification<ShopRequest> existingUserOnly =
                (root, query, cb) -> cb.isNotNull(root.get("existingUser"));

        Specification<ShopRequest> combined = Specification.where(existingUserOnly).and(spec);

        return shopRequestRepository.findAll(combined)
                .stream()
                .map(this::toShopRequestDtoWithMedia)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ShopRequestDto getExistingOwnerRequestById(Long id) {
        ShopRequest entity = shopRequestRepository.findById(id)
                .orElseThrow(ShopOwnerRequestExceptions::requestNotFound);

        return toShopRequestDtoWithMedia(entity);
    }


    // Admin — PATH A decision (approve/reject ShopOwnerRequest + its ShopRequest)

    @Override
    @Transactional
    public void approve(AdminDecisionDto decision) {
        ShopOwnerRequest ownerRequest = shopOwnerRequestRepository.findById(decision.getShopOwnerRequestId())
                .orElseThrow(ShopOwnerRequestExceptions::requestNotFound);

        if (ownerRequest.getStatus() != ShopOwnerRequestStatus.PENDING) {
            throw ShopOwnerRequestExceptions.requestAlreadyProcessed();
        }

        ShopRequest shopRequest = ownerRequest.getShopRequest();
        if (shopRequest == null) {
            throw ShopOwnerRequestExceptions.missingShopRequest();
        }

        if (shopRequest.getExistingUser() != null) {
            throw ShopOwnerRequestExceptions.wrongRequestType();
        }

        // Resolve (or create) the mall first
        Mall mall = resolveOrCreateMall(shopRequest);

        // Check for name conflict using the resolved mall
        if (shopRepository.existsByNameAndMall_MallId(shopRequest.getName(), mall.getMallId())) {
            throw ShopExceptions.shopNameExistsInMall();
        }

        // Validate no conflicts in users table
        if (userRepository.existsByUsername(ownerRequest.getUsername())) {
            throw UserExceptions.usernameExists();
        }
        if (userRepository.existsByPhoneNumber(ownerRequest.getPhoneNumber())) {
            throw UserExceptions.phoneExists();
        }
        if (ownerRequest.getEmail() != null && userRepository.existsByEmail(ownerRequest.getEmail())) {
            throw UserExceptions.emailExists();
        }
        if (ownerRequest.getNationalIdNumber() != null
                && userRepository.existsByNationalIdNumber(ownerRequest.getNationalIdNumber())) {
            throw UserExceptions.nationalIdExists();
        }

        Role shopOwnerRole = roleRepository.findByCode(SHOP_OWNER_ROLE_CODE)
                .orElseThrow(RoleExceptions::roleNotFound);

        // Create User
        User user = User.builder()
                .username(ownerRequest.getUsername())
                .fullName(ownerRequest.getFullName())
                .email(ownerRequest.getEmail())
                .phoneNumber(ownerRequest.getPhoneNumber())
                .password(ownerRequest.getPassword())
                .role(shopOwnerRole)
                .isActive(true)
                .gender(ownerRequest.getGender())
                .age(ownerRequest.getAge())
                .nationalIdNumber(ownerRequest.getNationalIdNumber())
                .profilePictureUuid(ownerRequest.getProfilePictureUuid())
                .build();
        User savedUser = userRepository.save(user);

        // Create Shop
        Shop shop = Shop.builder()
                .mall(mall)
                .owner(savedUser)
                .name(shopRequest.getName())
                .category(shopRequest.getCategory())
                .description(shopRequest.getDescription())
                .location(shopRequest.getLocation())
                .contactInfo(shopRequest.getContactInfo())
                .logoUuid(shopRequest.getLogoUuid())
                .licenseImageUuid(shopRequest.getLicenseImageUuid())
                .shopPhotosUuids(shopRequest.getShopPhotosUuids() != null ? shopRequest.getShopPhotosUuids() : new ArrayList<>())
                .status(ShopStatus.ACTIVE)
                .build();
        Shop savedShop = shopRepository.save(shop);

        createShopFolder(savedShop);
        moveShopFilesToFolder(savedShop, shopRequest);
        createBrandForShopSafely(savedShop);
        triggerTrial(savedShop, savedUser);

        // Update statuses
        ownerRequest.setStatus(ShopOwnerRequestStatus.APPROVED);
        ownerRequest.setCreatedUser(savedUser);
        shopRequest.setStatus(ShopRequestStatus.APPROVED);
        shopRequest.setCreatedShop(savedShop);
        shopOwnerRequestRepository.save(ownerRequest);

        try {
            notificationService.notifyOwnerApproved(ownerRequest, savedUser, savedShop);
        } catch (Exception e) {
            log.warn("Failed to notify shop owner of approval: requestId={}", ownerRequest.getId());
        }

        log.info("Shop owner request approved: requestId={}, userId={}, shopId={}",
                ownerRequest.getId(), savedUser.getUserId(), savedShop.getShopId());
    }

    @Override
    @Transactional
    public void reject(AdminDecisionDto decision) {
        ShopOwnerRequest ownerRequest = shopOwnerRequestRepository.findById(decision.getShopOwnerRequestId())
                .orElseThrow(ShopOwnerRequestExceptions::requestNotFound);

        if (ownerRequest.getStatus() != ShopOwnerRequestStatus.PENDING) {
            throw ShopOwnerRequestExceptions.requestAlreadyProcessed();
        }

        if (decision.getRejectionReason() == null || decision.getRejectionReason().isBlank()) {
            throw ShopOwnerRequestExceptions.rejectionReasonRequired();
        }

        ownerRequest.setStatus(ShopOwnerRequestStatus.REJECTED);
        ownerRequest.setRejectionReason(decision.getRejectionReason());

        ShopRequest shopRequest = ownerRequest.getShopRequest();
        if (shopRequest != null && shopRequest.getExistingUser() != null) {
            throw ShopOwnerRequestExceptions.wrongRequestType();
        }

        if (shopRequest != null) {
            shopRequest.setStatus(ShopRequestStatus.REJECTED);
            shopRequest.setRejectionReason(decision.getRejectionReason());
        }

        shopOwnerRequestRepository.save(ownerRequest);

        try {
            notificationService.notifyOwnerRejected(ownerRequest, decision.getRejectionReason());
        } catch (Exception e) {
            log.warn("Failed to notify shop owner of rejection: requestId={}", ownerRequest.getId());
        }

        log.info("Shop owner request rejected: requestId={}, reason={}",
                ownerRequest.getId(), decision.getRejectionReason());
    }

    // Admin — PATH B decision (approve/reject standalone ShopRequest)

    @Override
    @Transactional
    public void approveShopRequest(Long shopRequestId) {
        ShopRequest shopRequest = shopRequestRepository.findById(shopRequestId)
                .orElseThrow(ShopOwnerRequestExceptions::missingShopRequest);

        if (shopRequest.getStatus() != ShopRequestStatus.PENDING) {
            throw ShopOwnerRequestExceptions.requestAlreadyProcessed();
        }

        if (shopRequest.getExistingUser() == null) {
            throw ShopOwnerRequestExceptions.wrongRequestType();
        }

        // existingUser must be present for Path B
        User owner = shopRequest.getExistingUser();
        if (owner == null) {
            throw ShopOwnerRequestExceptions.missingShopRequest();
        }

        Mall mall = resolveOrCreateMall(shopRequest);
        if (shopRepository.existsByNameAndMall_MallId(shopRequest.getName(), mall.getMallId())) {
            throw store.emall.backend.accounts.shop.ShopExceptions.shopNameExistsInMall();
        }

        // Create Shop
        Shop shop = Shop.builder()
                .mall(mall)
                .owner(owner)
                .name(shopRequest.getName())
                .category(shopRequest.getCategory())
                .description(shopRequest.getDescription())
                .location(shopRequest.getLocation())
                .contactInfo(shopRequest.getContactInfo())
                .logoUuid(shopRequest.getLogoUuid())
                .licenseImageUuid(shopRequest.getLicenseImageUuid())
                .shopPhotosUuids(shopRequest.getShopPhotosUuids() != null ? shopRequest.getShopPhotosUuids() : new ArrayList<>())
                .status(ShopStatus.ACTIVE)
                .build();
        Shop savedShop = shopRepository.save(shop);

        createShopFolder(savedShop);
        moveShopFilesToFolder(savedShop, shopRequest);
        createBrandForShopSafely(savedShop);
        triggerTrial(savedShop, owner);

        shopRequest.setStatus(ShopRequestStatus.APPROVED);
        shopRequest.setCreatedShop(savedShop);
        shopRequestRepository.save(shopRequest);

        try {
            notificationService.notifyExistingShopOwnerApproved(shopRequest.getExistingUser(), savedShop);
        } catch (Exception e) {
            log.warn("Failed to notify existing shop owner of rejection: requestId={}", shopRequest.getExistingUser().getUserId());
        }

        log.info("Standalone shop request approved: shopRequestId={}, userId={}, shopId={}",
                shopRequestId, owner.getUserId(), savedShop.getShopId());
    }

    @Override
    @Transactional
    public void rejectShopRequest(Long shopRequestId, String rejectionReason) {
        ShopRequest shopRequest = shopRequestRepository.findById(shopRequestId)
                .orElseThrow(ShopOwnerRequestExceptions::missingShopRequest);

        if (shopRequest.getStatus() != ShopRequestStatus.PENDING) {
            throw ShopOwnerRequestExceptions.requestAlreadyProcessed();
        }

        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw ShopOwnerRequestExceptions.rejectionReasonRequired();
        }

        if (shopRequest.getExistingUser() == null) {
            throw ShopOwnerRequestExceptions.wrongRequestType();
        }

        shopRequest.setStatus(ShopRequestStatus.REJECTED);
        shopRequest.setRejectionReason(rejectionReason);
        shopRequestRepository.save(shopRequest);

        try {
            notificationService.notifyExistingShopOwnerRejected(shopRequest.getExistingUser(), rejectionReason);
        } catch (Exception e) {
            log.warn("Failed to notify existing shop owner of rejection: requestId={}", shopRequest.getExistingUser().getUserId());
        }

        log.info("Standalone shop request rejected: shopRequestId={}, reason={}", shopRequestId, rejectionReason);
    }

    private ShopOwnerRequestDto toDtoWithMedia(ShopOwnerRequest entity) {
        ShopRequest shopRequest = entity.getShopRequest();
        if (shopRequest == null) {
            return ShopOwnerRequestMapper.toSimpleDto(entity);
        }

        FileDto profilePictureImage = fetchImageSafely(entity.getProfilePictureUuid());
        FileDto logoImage = fetchImageSafely(shopRequest.getLogoUuid());
        FileDto licenseImage = fetchImageSafely(shopRequest.getLicenseImageUuid());
        List<FileDto> shopPhotos = fetchImagesSafely(shopRequest.getShopPhotosUuids());

        return ShopOwnerRequestMapper.toFullDto(entity, licenseImage, shopPhotos, logoImage, profilePictureImage);
    }

    private ShopRequestDto toShopRequestDtoWithMedia(ShopRequest shopRequest) {
        FileDto logoImage = fetchImageSafely(shopRequest.getLogoUuid());
        FileDto licenseImage = fetchImageSafely(shopRequest.getLicenseImageUuid());
        List<FileDto> shopPhotos = fetchImagesSafely(shopRequest.getShopPhotosUuids());
        return ShopOwnerRequestMapper.toFullShopRequestDto(shopRequest, licenseImage, shopPhotos, logoImage);
    }

    private FileDto fetchImageSafely(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return fileService.getById(uuid);
    }

    private List<FileDto> fetchImagesSafely(List<UUID> uuids) {
        if (uuids == null || uuids.isEmpty()) {
            return Collections.emptyList();
        }
        return uuids.stream()
                .map(this::fetchImageSafely)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private FileDto getAndValidateImage(UUID uuid, String fieldName) {
        FileDto fileDto = fileService.getById(uuid);
        if (!isImage(fileDto.getMimeType())) {
            throw ShopOwnerRequestExceptions.invalidFileType(fieldName);
        }
        return fileDto;
    }

    private List<FileDto> getAndValidateImages(List<UUID> uuids, String fieldName) {
        if (uuids == null || uuids.isEmpty()) {
            return Collections.emptyList();
        }
        List<FileDto> result = new ArrayList<>();

        for (int i = 0; i < uuids.size(); i++) {
            result.add(getAndValidateImage(uuids.get(i), fieldName + "[" + i + "]"));
        }

        return result;
    }

    private boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    private void createShopFolder(Shop shop) {
        if (shop.getFolderId() != null) {
            return; // already has a folder
        }
        String folderName = "shop_" + shop.getShopId();
        FolderDto folderDto = FolderDto.builder()
                .name(folderName)
                .parentId(null)
                .storeId(shop.getShopId())
                .build();

        FolderDto saved = folderService.create(folderDto);
        Long newFolderId = saved.getId();
        shop.setFolderId(newFolderId);
        shopRepository.save(shop);
    }

    private Mall resolveOrCreateMall(ShopRequest shopRequest) {

        if (shopRequest.getMall() != null) {
            return shopRequest.getMall();
        }

        City city = shopRequest.getRequestedMallCity();
        String mallName = shopRequest.getRequestedMallName();

        Optional<Mall> existingMall = mallRepository.findByNameAndCity_CityId(mallName, city.getCityId());
        if (existingMall.isPresent()) {
            log.info("Mall '{}' already exists in city '{}', reusing mallId={}",
                    mallName, city.getName(), existingMall.get().getMallId());
            return existingMall.get();
        }

        Mall newMall = Mall.builder()
                .name(mallName)
                .city(city)
                .location(city.getName())
                .status(MallStatus.ACTIVE)
                .build();

        Mall savedMall = mallRepository.save(newMall);
        log.info("Auto-created new mall '{}' in city '{}', mallId={}",
                mallName, city.getName(), savedMall.getMallId());

        return savedMall;
    }


    private void applyMallToShopRequest(ShopRequest shopRequest, ShopRequestDto shopRequestDto) {

        if (shopRequestDto.getMallId() != null) {
            Mall mall = mallRepository.findById(shopRequestDto.getMallId())
                    .orElseThrow(MallExceptions::mallNotFound);

//            if (mall.getStatus() != MallStatus.ACTIVE) {
//                throw MallExceptions.mallNotActive();
//            }

            if (shopRequestRepository.existsByNameAndMall_MallIdAndStatusNot(
                    shopRequestDto.getName(), mall.getMallId(), ShopRequestStatus.REJECTED)) {
                throw ShopOwnerRequestExceptions.shopNameAlreadyRequestedInMall();
            }

            shopRequest.setMall(mall);
            shopRequest.setRequestedMallName(null);
            shopRequest.setRequestedMallCity(null);

        } else {
            City city = cityRepository.findById(shopRequestDto.getRequestedMallCityId())
                    .orElseThrow(ShopOwnerRequestExceptions::requestedMallCityNotFound);

            if (shopRequestRepository.existsByRequestedMallNameAndRequestedMallCity_CityIdAndStatusNot(
                    shopRequestDto.getRequestedMallName(),
                    city.getCityId(),
                    ShopRequestStatus.REJECTED)) {
                throw ShopOwnerRequestExceptions.requestedMallNameAlreadyExists();
            }

            shopRequest.setMall(null);
            shopRequest.setRequestedMallName(shopRequestDto.getRequestedMallName());
            shopRequest.setRequestedMallCity(city);
        }
    }

    private void moveShopFilesToFolder(Shop shop, ShopRequest shopRequest) {

        if (shop.getFolderId() == null) {
            log.warn("Skipping file move for shopId={}: folderId is null (folder creation may have failed)",
                    shop.getShopId());
            return;
        }

        Long folderId = shop.getFolderId();

        // Move license image
        moveFileSafely(shopRequest.getLicenseImageUuid(), folderId, "licenseImage", shop.getShopId());

        // Move every shop photo
        List<UUID> photoUuids = shopRequest.getShopPhotosUuids();
        if (photoUuids != null) {
            for (int i = 0; i < photoUuids.size(); i++) {
                moveFileSafely(photoUuids.get(i), folderId, "shopPhoto[" + i + "]", shop.getShopId());
            }
        }

        // Move logo
        if (shopRequest.getLogoUuid() != null) {
            moveFileSafely(shopRequest.getLogoUuid(), folderId, "logo", shop.getShopId());
        }
    }

    private void moveFileSafely(UUID fileUuid, Long targetFolderId, String fieldName, Long shopId) {
        if (fileUuid == null) {
            return;
        }
        FileTransferRequest fileTransferRequest = FileTransferRequest.builder()
                .id(fileUuid)
                .newFolderId(targetFolderId)
                .newStoreId(shopId)
                .newScope(ScopeType.STORE)
                .newManagedBy(ManagedByType.SYSTEM)
                .build();

        FileDto saved = fileService.transfer(fileTransferRequest);
    }

    private void triggerTrial(Shop savedShop, User owner) {
        shopSubscriptionService.createTrial(
                savedShop.getShopId(),
                owner.getEmail() != null ? owner.getEmail() : "",
                savedShop.getName()
        );
    }

    private void createBrandForShopSafely(Shop shop) {
        // todo you have the same function in ShopServiceImpl
        UUID imageId = shop.getLogoUuid() != null
                ? shop.getLogoUuid()
                : shop.getShopPhotosUuids().getFirst();

        String slug = shop.getName()
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        if (slug.length() < 5) {
            slug = slug + "-shop";
        }

        BrandDto brandDto = BrandDto.builder()
                .name(shop.getName())
                .slug(slug)
                .imageId(imageId)
                .targetedAudience(TargetedAudience.ALL)
                .ageGroup(AgeGroup.ALL)
                .build();

        brandService.create(brandDto);
    }//

}