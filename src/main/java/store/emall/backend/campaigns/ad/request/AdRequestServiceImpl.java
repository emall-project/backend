package store.emall.backend.campaigns.ad.request;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.accounts.shop.ShopInfoDto;
import store.emall.backend.accounts.shop.ShopService;
import store.emall.backend.campaigns.ad.payment.*;
import store.emall.backend.campaigns.ad.template.AdTemplate;
import store.emall.backend.campaigns.ad.template.AdTemplateExceptions;
import store.emall.backend.campaigns.ad.template.AdTemplateRepository;
import store.emall.backend.campaigns.ad.template.AdTemplateStatus;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.security.SecurityContextUtil;
import store.emall.backend.campaigns.subscription.*;
import store.emall.backend.mediamanager.file.service.FileService;
import store.emall.backend.mediamanager.file.dto.FileDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdRequestServiceImpl implements AdRequestService {

    private final AdRequestRepository adRequestRepository;
    private final AdTemplateRepository adTemplateRepository;
    private final ShopService shopService;
    private final FileService fileService;
    private final ShopSubscriptionService subscriptionService;
    private final ShopSubscriptionRepository subscriptionRepository;
    private final AdPaymentRepository adPaymentRepository;


    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<AdRequestDto> getAll(Pageable pageable, Specification<AdRequest> spec) {
        Page<AdRequestDto> page = adRequestRepository.findAll(spec, pageable)
                .map(this::toDtoWithMediaAndShopInfo);
        return PaginatedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdRequestDto> getAllRequests(Specification<AdRequest> spec) {
        List<AdRequest> requests = (spec == null)
                ? adRequestRepository.findAll()
                : adRequestRepository.findAll(spec);

        return requests.stream()
                .map(this::toDtoWithMediaAndShopInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdRequestDto getById(Long id) {
        AdRequest request = adRequestRepository.findById(id)
                .orElseThrow(AdRequestExceptions::requestNotFound);

        if (!SecurityContextUtil.isAdmin() && !SecurityContextUtil.isShopOwnerOf(request.getShopId())) {
            throw AdRequestExceptions.requestNotFoundForShop();
        }

        return toDtoWithMediaAndShopInfo(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdRequestDto> getByShopId(Long shopId) {
        return adRequestRepository.findByShopId(shopId)
                .stream()
                .map(this::toDtoWithMediaAndShopInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdRequestDto> getByStatus(AdRequestStatus status) {
        return adRequestRepository.findByStatus(status)
                .stream()
                .map(this::toDtoWithMediaAndShopInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdRequestDto> getByShopIdAndStatus(Long shopId, AdRequestStatus status) {
        return adRequestRepository.findByShopIdAndStatus(shopId, status).stream()
                .map(this::toDtoWithMediaAndShopInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdRequestDto> getByTemplateId(Long templateId) {
        return adRequestRepository.findByTemplate_AdTemplateId(templateId)
                .stream()
                .map(this::toDtoWithMediaAndShopInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdRequestDto> getCurrentlyDisplayedAds() {
        return adRequestRepository.findCurrentlyDisplayedAds()
                .stream()
                .map(this::toDtoWithMediaAndShopInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByStatus(AdRequestStatus status) {
        return adRequestRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByShopId(Long shopId) {
        return adRequestRepository.countByShopId(shopId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByTemplateId(Long templateId) {
        return adRequestRepository.countByTemplate_AdTemplateId(templateId);
    }

    @Override
    @Transactional
    public AdRequestDto createRequest(AdRequestDto dto) {

        // Validate template exists
        AdTemplate template = adTemplateRepository.findById(dto.getTemplateId())
                .orElseThrow(AdTemplateExceptions::templateNotFound);

        // Auth & subscription check
        if (!SecurityContextUtil.isAdmin()) {
            if (!SecurityContextUtil.isShopOwnerOf(dto.getShopId())) {
                throw AdRequestExceptions.shopNotFound();
            }
            validateSubscriptionWriteAccess(dto.getShopId());
        }

        // Template must not be ARCHIVED.
        // RESERVED is no longer used as a global block — availability is time-based.
        if (template.getStatus() == AdTemplateStatus.ARCHIVED) {
            throw AdRequestExceptions.templateNotActive();
        }

        // startDate must not be in the past
        if (dto.getStartDate().isBefore(LocalDateTime.now())) {
            throw AdRequestExceptions.templateStartDatePassed();
        }

        // endDate must be after startDate
        if (!dto.getEndDate().isAfter(dto.getStartDate())) {
            throw AdRequestExceptions.templateDateExpired();
        }

        // Validate shop exists
        validateShopExists(dto.getShopId());

        // Validate ad image
        getAndValidateImage(dto.getAdRequestImageUuid());

        // Prevent the same shop from having a duplicate PENDING or APPROVED request
        // for the same template (regardless of dates — one active attempt per shop per template)
        boolean hasDuplicate = adRequestRepository.existsByTemplate_AdTemplateIdAndShopIdAndStatusIn(
                dto.getTemplateId(), dto.getShopId(),
                List.of(AdRequestStatus.PENDING, AdRequestStatus.APPROVED));
        if (hasDuplicate) {
            throw AdRequestExceptions.duplicateRequestForShop();
        }

        // Block submission if an APPROVED request already occupies this time window.
        // This gives the shop owner an immediate, clear error instead of waiting for admin review.
        boolean timeConflict = adRequestRepository.existsApprovedOverlappingRequest(
                dto.getTemplateId(), dto.getStartDate(), dto.getEndDate());
        if (timeConflict) {
            throw AdRequestExceptions.templateTimeSlotTaken();
        }

        // Calculate total price: pricePerHour × number of hours in the requested period
        BigDecimal totalPrice = calculateTotalPrice(
                template.getPricePerHour(), dto.getStartDate(), dto.getEndDate());

        AdRequest request = AdRequestMapper.toEntity(dto, template, totalPrice);
        AdRequest saved = adRequestRepository.save(request);

        log.info("Ad request created: id={}, shopId={}, templateId={}, startDate={}, endDate={}, totalPrice={}",
                saved.getAdRequestId(), saved.getShopId(), template.getAdTemplateId(),
                saved.getStartDate(), saved.getEndDate(), saved.getTotalPrice());

        return toDtoWithMediaAndShopInfo(saved);
    }

    @Override
    @Transactional
    public AdRequestDto updateRequest(AdRequestDto dto) {
        AdRequest existing = adRequestRepository.findById(dto.getAdRequestId())
                .orElseThrow(AdRequestExceptions::requestNotFound);

        if (!SecurityContextUtil.isAdmin()) {
            if (!SecurityContextUtil.isShopOwnerOf(existing.getShopId())) {
                throw AdRequestExceptions.requestNotFoundForShop();
            }
            validateSubscriptionWriteAccess(existing.getShopId());
        }

        // Only PENDING requests can be modified
        if (existing.getStatus() != AdRequestStatus.PENDING) {
            throw AdRequestExceptions.cannotModifyProcessedRequest();
        }

        // Validate image if it changed
        if (dto.getAdRequestImageUuid() != null
                && !dto.getAdRequestImageUuid().equals(existing.getAdRequestImageUuid())) {
            getAndValidateImage(dto.getAdRequestImageUuid());
        }

        // Only title and imageUuid can be updated (dates are immutable after creation)
        AdRequestMapper.merge(existing, dto);
        AdRequest savedAdRequest = adRequestRepository.save(existing);
        return toDtoWithMediaAndShopInfo(savedAdRequest);
    }

    @Override
    @Transactional
    public void cancelRequest(Long requestId, Long shopId) {
        AdRequest request = adRequestRepository.findById(requestId)
                .orElseThrow(AdRequestExceptions::requestNotFound);

        if (!SecurityContextUtil.isAdmin()) {
            if (!SecurityContextUtil.isShopOwnerOf(request.getShopId())) {
                throw AdRequestExceptions.requestNotFoundForShop();
            }
            validateSubscriptionWriteAccess(request.getShopId());
        }

        if (request.getStatus() != AdRequestStatus.PENDING) {
            throw AdRequestExceptions.cannotModifyProcessedRequest();
        }

        request.setStatus(AdRequestStatus.REJECTED);
        request.setRejectionReason("Cancelled by shop owner");
        adRequestRepository.save(request);

        log.info("Ad request cancelled by shop owner: id={}, shopId={}", requestId, shopId);
    }

    /**
     * Approves a PENDING ad request.
     * <p>
     * Key logic change vs. original:
     * - The template is NO LONGER set to RESERVED globally. Availability is
     * determined by time-overlap checks on the requests table, so multiple
     * non-overlapping requests can be approved on the same template.
     * - Only other PENDING requests whose time range OVERLAPS with the approved
     * request are auto-rejected. Non-overlapping pending requests remain PENDING
     * and the admin can approve them independently.
     */
    @Override
    @Transactional
    public void approveRequest(Long requestId) {
        AdRequest request = adRequestRepository.findById(requestId)
                .orElseThrow(AdRequestExceptions::requestNotFound);

        // Only PENDING requests can be approved
        if (request.getStatus() != AdRequestStatus.PENDING) {
            throw AdRequestExceptions.requestAlreadyProcessed();
        }

        AdTemplate template = request.getTemplate();

        // Template must not be ARCHIVED
        if (template.getStatus() == AdTemplateStatus.ARCHIVED) {
            throw AdRequestExceptions.templateNotActive();
        }

        // Dates must still be valid at approval time
        if (request.getEndDate().isBefore(LocalDateTime.now())) {
            throw AdRequestExceptions.templateDateExpired();
        }
        if (request.getStartDate().isBefore(LocalDateTime.now())) {
            throw AdRequestExceptions.templateStartDatePassed();
        }

        // Guard: make sure no other APPROVED request already occupies this time window.
        // This can happen if two admins work simultaneously or if the data is in an
        // unexpected state — fail fast with a clear error rather than double-booking.
        boolean conflict = adRequestRepository.existsApprovedOverlappingRequest(
                template.getAdTemplateId(), request.getStartDate(), request.getEndDate());
        if (conflict) {
            throw AdRequestExceptions.templateAlreadyReserved();
        }

        // Approve this request
        request.setStatus(AdRequestStatus.APPROVED);
        adRequestRepository.save(request);

        // Auto-reject ONLY the pending requests that overlap with this time window.
        // Pending requests for non-overlapping dates are left alone — the admin can
        // approve them when their time comes.
        List<AdRequest> overlappingPending = adRequestRepository.findPendingOverlappingRequests(
                template.getAdTemplateId(), request.getStartDate(), request.getEndDate());

        List<AdRequest> toReject = overlappingPending.stream()
                .filter(other -> !other.getAdRequestId().equals(requestId))
                .collect(Collectors.toList());

        toReject.forEach(other -> {
            other.setStatus(AdRequestStatus.REJECTED);
            other.setRejectionReason("Another request was approved for this time slot on this template");
            log.info("Auto-rejected overlapping ad request {} (template={}, {} → {})",
                    other.getAdRequestId(), template.getAdTemplateId(),
                    other.getStartDate(), other.getEndDate());
        });

        if (!toReject.isEmpty()) {
            adRequestRepository.saveAll(toReject);
        }

        // NOTE: template status is intentionally NOT set to RESERVED.
        // The template stays ACTIVE so other shops can still submit requests
        // for non-overlapping time slots.

        // Notify the approved shop owner
        notifyShopOwner(request.getShopId(),
                "Your ad request '" + request.getTitle() + "' has been APPROVED. " +
                        "Total price: " + request.getTotalPrice() +
                        " (from " + request.getStartDate() + " to " + request.getEndDate() + "). " +
                        "Please complete payment before " + request.getStartDate() +
                        " for the ad to go live.");

        // Notify the auto-rejected shop owners
        toReject.forEach(other ->
                notifyShopOwner(other.getShopId(),
                        "Your ad request '" + other.getTitle() + "' has been REJECTED. " +
                                "Reason: Another request was approved for the same time slot on this ad template.")
        );

        log.info("Ad request {} approved for template {}. {} overlapping requests auto-rejected.",
                requestId, template.getAdTemplateId(), toReject.size());
    }

    // -------------------------------------------------------------------------
    // REJECT
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void rejectRequest(Long requestId, String reason) {
        AdRequest request = adRequestRepository.findById(requestId)
                .orElseThrow(AdRequestExceptions::requestNotFound);

        if (request.getStatus() != AdRequestStatus.PENDING) {
            throw AdRequestExceptions.requestAlreadyProcessed();
        }

        request.setStatus(AdRequestStatus.REJECTED);
        request.setRejectionReason(reason);
        adRequestRepository.save(request);

        notifyShopOwner(request.getShopId(),
                "Your ad request '" + request.getTitle() + "' has been REJECTED. " +
                        (reason != null ? "Reason: " + reason : ""));

        log.info("Ad request {} rejected. Reason: {}", requestId, reason);
    }

    @Override
    @Transactional
    public void confirmPayment(Long requestId) {
        AdRequest request = adRequestRepository.findById(requestId)
                .orElseThrow(AdRequestExceptions::requestNotFound);

        if (request.getStatus() != AdRequestStatus.APPROVED) {
            throw AdRequestExceptions.requestNotApproved();
        }

        if (request.getPaymentStatus() == AdPaymentStatus.PAID) {
            throw AdRequestExceptions.paymentAlreadyMade();
        }

        if (request.getPaymentStatus() == AdPaymentStatus.OVERDUE) {
            throw AdRequestExceptions.paymentOverdue();
        }

        request.setPaymentStatus(AdPaymentStatus.PAID);
        request.setPaidAt(LocalDateTime.now());

        // If the ad's start date has already arrived, activate immediately
        if (!request.getStartDate().isAfter(LocalDateTime.now())
                && !request.getEndDate().isBefore(LocalDateTime.now())) {
            request.setIsDisplayed(true);
            log.info("Ad request {} paid and immediately activated (start date already reached)", requestId);
        } else {
            log.info("Ad request {} paid. Will be activated on {}", requestId, request.getStartDate());
        }

        adRequestRepository.save(request);

        notifyShopOwner(request.getShopId(),
                "Payment confirmed for ad '" + request.getTitle() + "'. " +
                        "Your ad will go live on " + request.getStartDate() + ".");
    }

    @Override
    @Transactional
    public AdPaymentResponseDto initiatePayment(Long requestId) {
        AdRequest request = adRequestRepository.findById(requestId)
                .orElseThrow(AdRequestExceptions::requestNotFound);

        if (!SecurityContextUtil.isAdmin()) {
            if (!SecurityContextUtil.isShopOwnerOf(request.getShopId())) {
                throw AdRequestExceptions.requestNotFoundForShop();
            }
        }

        if (request.getStatus() != AdRequestStatus.APPROVED) {
            throw AdRequestExceptions.requestNotApproved();
        }

        if (request.getPaymentStatus() == AdPaymentStatus.PAID) {
            throw AdRequestExceptions.paymentAlreadyMade();
        }

        if (request.getPaymentStatus() == AdPaymentStatus.OVERDUE) {
            throw AdRequestExceptions.paymentOverdue();
        }

        // Get shop's Stripe customer ID from subscription
        String stripeCustomerId = subscriptionRepository
                .findByShopId(request.getShopId())
                .map(ShopSubscription::getStripeCustomerId)
                .orElse(null);

        try {
            long amountInCents = request.getTotalPrice()
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency("usd")
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .putMetadata("adRequestId", String.valueOf(request.getAdRequestId()))
                    .putMetadata("shopId", String.valueOf(request.getShopId()));

            if (stripeCustomerId != null) {
                paramsBuilder.setCustomer(stripeCustomerId);
            }

            PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());

            request.setStripePaymentIntentId(paymentIntent.getId());
            adRequestRepository.save(request);

            log.info("Ad payment initiated: requestId={}, shopId={}, amount={}, paymentIntentId={}",
                    requestId, request.getShopId(), request.getTotalPrice(), paymentIntent.getId());

            return AdPaymentResponseDto.builder()
                    .adRequestId(request.getAdRequestId())
                    .clientSecret(paymentIntent.getClientSecret())
                    .amount(request.getTotalPrice())
                    .currency("USD")
                    .adTitle(request.getTitle())
                    .build();

        } catch (StripeException e) {
            log.error("Stripe error initiating ad payment for requestId={}: {}", requestId, e.getMessage());
            throw SubscriptionExceptions.stripeError();
        }
    }

    @Override
    @Transactional
    public void handleAdPaymentSuccess(String paymentIntentId) {
        AdRequest request = adRequestRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElse(null);

        if (request == null) {
            log.warn("Ad payment webhook: no ad request found for paymentIntentId={}", paymentIntentId);
            return;
        }

        // Idempotency guards
        if (request.getPaymentStatus() == AdPaymentStatus.PAID) {
            log.info("Ad payment already processed for requestId={}, skipping", request.getAdRequestId());
            return;
        }
        if (adPaymentRepository.existsByStripePaymentIntentId(paymentIntentId)) {
            log.info("Ad payment record already exists for paymentIntentId={}, skipping", paymentIntentId);
            return;
        }

        request.setPaymentStatus(AdPaymentStatus.PAID);
        request.setPaidAt(LocalDateTime.now());

        if (!request.getStartDate().isAfter(LocalDateTime.now())
                && !request.getEndDate().isBefore(LocalDateTime.now())) {
            request.setIsDisplayed(true);
            log.info("Ad request {} paid via Stripe and immediately activated", request.getAdRequestId());
        }

        adRequestRepository.save(request);

        AdPayment payment = AdPayment.builder()
                .adRequest(request)
                .amount(request.getTotalPrice())
                .currency("USD")
                .paymentDate(LocalDateTime.now())
                .paymentMethod(AdPaymentMethod.STRIPE)
                .paymentStatus(AdPaymentRecordStatus.SUCCESS)
                .stripePaymentIntentId(paymentIntentId)
                .build();
        adPaymentRepository.save(payment);

        notifyShopOwner(request.getShopId(),
                "Payment confirmed for ad '" + request.getTitle() + "'. " +
                        "Your ad will go live on " + request.getStartDate() + ".");

        log.info("Ad payment SUCCESS for requestId={}, shopId={}, amount={}",
                request.getAdRequestId(), request.getShopId(), request.getTotalPrice());
    }

    // -------------------------------------------------------------------------
    // SCHEDULERS
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void activatePaidAds() {
        List<AdRequest> readyAds = adRequestRepository.findPaidReadyToDisplay();

        readyAds.forEach(ad -> {
            ad.setIsDisplayed(true);
            log.info("Ad activated: requestId={}, shopId={}, templateId={}",
                    ad.getAdRequestId(), ad.getShopId(), ad.getTemplate().getAdTemplateId());
        });

        if (!readyAds.isEmpty()) {
            adRequestRepository.saveAll(readyAds);
            log.info("Activated {} paid ads", readyAds.size());
        }
    }

    @Override
    @Transactional
    public void deactivateExpiredAds() {
        List<AdRequest> expiredAds = adRequestRepository.findDisplayedButExpired();

        expiredAds.forEach(ad -> {
            ad.setIsDisplayed(false);
            log.info("Ad deactivated (expired): requestId={}, templateId={}",
                    ad.getAdRequestId(), ad.getTemplate().getAdTemplateId());

            // NOTE: we no longer touch template.status here.
            // The template was never set to RESERVED, so there is nothing to release.
            // Availability for new requests is always determined by time-overlap checks.
        });

        if (!expiredAds.isEmpty()) {
            adRequestRepository.saveAll(expiredAds);
            log.info("Deactivated {} expired ads", expiredAds.size());
        }
    }

    @Override
    @Transactional
    public void sendPaymentReminders() {
        List<AdRequest> unpaidRequests = adRequestRepository.findApprovedUnpaidWithoutReminder();

        unpaidRequests.forEach(request -> {
            notifyShopOwner(request.getShopId(),
                    "REMINDER: Your ad '" + request.getTitle() + "' is approved but unpaid. " +
                            "Total due: " + request.getTotalPrice() +
                            ". Please pay before " + request.getStartDate() +
                            " to ensure your ad goes live on time.");

            request.setPaymentReminderSent(true);
            log.info("Payment reminder sent for requestId={}, shopId={}",
                    request.getAdRequestId(), request.getShopId());
        });

        if (!unpaidRequests.isEmpty()) {
            adRequestRepository.saveAll(unpaidRequests);
            log.info("Sent {} payment reminders", unpaidRequests.size());
        }
    }

    @Override
    @Transactional
    public void handleOverduePayments() {
        List<AdRequest> overdueRequests = adRequestRepository.findApprovedUnpaidOverdue();

        overdueRequests.forEach(request -> {
            request.setPaymentStatus(AdPaymentStatus.OVERDUE);

            notifyShopOwner(request.getShopId(),
                    "OVERDUE: Your ad '" + request.getTitle() + "' was approved but payment was not received. " +
                            "The ad start date has passed and the ad will NOT be displayed. " +
                            "Please contact support if you wish to resolve this.");

            log.info("Payment overdue for requestId={}, shopId={}. Ad will not be displayed.",
                    request.getAdRequestId(), request.getShopId());

            // NOTE: we no longer touch template.status here.
            // Because the template was never globally set to RESERVED, there is nothing
            // to release. The time slot covered by this overdue request naturally becomes
            // available again because it has no APPROVED request occupying it anymore
            // (the request is now OVERDUE, not APPROVED — the overlap query only checks APPROVED).
        });

        if (!overdueRequests.isEmpty()) {
            adRequestRepository.saveAll(overdueRequests);
            log.info("Handled {} overdue payments", overdueRequests.size());
        }
    }

    // -------------------------------------------------------------------------
    // PAYMENT HISTORY
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<AdPaymentDto> getPaymentHistoryByShop(Long shopId) {
        if (!SecurityContextUtil.isAdmin()) {
            if (!SecurityContextUtil.isShopOwnerOf(shopId)) {
                throw AdRequestExceptions.requestNotFoundForShop();
            }
        }
        return adPaymentRepository
                .findByAdRequest_ShopIdOrderByPaymentDateDesc(shopId)
                .stream()
                .map(AdPaymentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdPaymentDto> getAllPaymentHistory() {
        return adPaymentRepository
                .findAllByOrderByPaymentDateDesc()
                .stream()
                .map(AdPaymentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdPaymentDto> getPaymentHistory(Long adRequestId) {
        if (!adRequestRepository.existsById(adRequestId)) {
            throw AdRequestExceptions.requestNotFound();
        }
        return adPaymentRepository
                .findByAdRequest_AdRequestIdOrderByPaymentDateDesc(adRequestId)
                .stream()
                .map(AdPaymentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdPaymentDto> getPaymentHistoryForShop(Long adRequestId) {
        AdRequest request = adRequestRepository.findById(adRequestId)
                .orElseThrow(AdRequestExceptions::requestNotFound);

        if (!SecurityContextUtil.isShopOwnerOf(request.getShopId())) {
            throw AdRequestExceptions.requestNotFoundForShop();
        }

        return adPaymentRepository
                .findByAdRequest_AdRequestIdOrderByPaymentDateDesc(adRequestId)
                .stream()
                .map(AdPaymentMapper::toDto)
                .toList();
    }

    /**
     * Calculates the total price for an ad request.
     * totalPrice = pricePerHour × number of hours between startDate and endDate.
     * Pricing rule: any started hour is billed as a full hour (inclusive end)
     */
    private BigDecimal calculateTotalPrice(BigDecimal pricePerHour, LocalDateTime startDate, LocalDateTime endDate) {
        long seconds = ChronoUnit.SECONDS.between(startDate, endDate) + 1;
        long hours = (long) Math.ceil(seconds / 3600.0);

        return pricePerHour.multiply(BigDecimal.valueOf(hours));
    }

    private void validateShopExists(Long shopId) {
        ShopInfoDto shop = shopService.getShopById(shopId);// TODO: could be replace with existsByIdAndIsActiveTrue
        if (shop == null || Boolean.FALSE.equals(shop.getIsActive())) {
            throw AdRequestExceptions.shopNotFound();
        }

    }

    private void notifyShopOwner(Long shopId, String message) {
        // TODO: Implement WhatsApp notification integration
        ShopInfoDto shop = shopService.getShopById(shopId);

        if (shop != null && shop.getOwnerPhone() != null) {
            log.info("NOTIFICATION [shopId={}, phone={}]: {}",
                    shopId, shop.getOwnerPhone(), message);
            // Future: whatsAppService.sendMessage(shop.getOwnerPhone(), message);
        } else {
            log.info("NOTIFICATION [shopId={}]: {}", shopId, message);
        }
    }

    private AdRequestDto toDtoWithMediaAndShopInfo(AdRequest adRequest) {
        FileDto adRequestImage = fetchImageSafely(adRequest.getAdRequestImageUuid());
        ShopInfoDto shop = fetchShopSafely(adRequest.getShopId());
        return AdRequestMapper.toFullDto(adRequest, adRequestImage, shop);
    }

    private FileDto fetchImageSafely(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return fileService.getById(uuid);
    }

    private ShopInfoDto fetchShopSafely(Long shopId) {
        if (shopId == null) {
            return null;
        }
        return shopService.getShopById(shopId);
    }

    private FileDto getAndValidateImage(UUID uuid) {
        FileDto fileDto = fileService.getById(uuid);
        if (!isImage(fileDto.getMimeType())) {
            throw AdRequestExceptions.invalidFileType();
        }
        return fileDto;
    }

    private boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    private void validateSubscriptionWriteAccess(Long shopId) {
        // 1. Check subscription status
        ShopSubscription sub = subscriptionRepository.findByShopId(shopId).orElse(null);
        if (sub == null || sub.getStatus() == SubscriptionStatus.EXPIRED
                || sub.getStatus() == SubscriptionStatus.CANCELLED) {
            throw SubscriptionExceptions.subscriptionWriteAccessDenied();
        }

        // 2. Check admin block — delegated to accounts via isEffectivelyActive()
        // The accounts service already embeds adminStatus logic in hasWriteAccess
        try {
//            AccountsResponse<Boolean> writeAccessResponse = accountsClient.hasWriteAccess(shopId);
//            Boolean canWrite = writeAccessResponse != null ? writeAccessResponse.getData() : null;
//
//            if (Boolean.FALSE.equals(canWrite)) {
//                throw SubscriptionExceptions.shopBlocked();
//            }
        } catch (FeignException e) {
            // Graceful degradation — if accounts is down, fall back to subscription check only
            log.warn("Could not verify admin write-access for shopId={}, proceeding with subscription check only", shopId);
        }
    }
}