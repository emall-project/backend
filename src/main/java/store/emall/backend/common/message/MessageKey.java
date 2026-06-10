package store.emall.backend.common.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageKey {


    // =====================================================
    // ==================== Accounts =======================
    // =====================================================

    // ==================== User Errors ====================
    USER_NOT_FOUND("user.not.found"),
    PHONE_EXISTS("phone.exists"),
    EMAIL_EXISTS("email.exists"),
    USERNAME_EXISTS("username.exists"),
    USER_ID_NULL("user.userId.null"),
    USER_ID_NOT_NULL("user.userId.notnull"),
    USER_ID_POSITIVE("user.userId.positive"),
    USER_FULL_NAME_NOT_BLANK("user.fullName.notblank"),
    USER_EMAIL_INVALID("user.email.invalid"),
    USER_PHONE_NOT_NULL("user.phone.notnull"),
    USER_ROLE_NOT_NULL("user.role.notnull"),
    USER_AGE_MIN("user.age.min"),
    USER_AGE_MAX("user.age.max"),
    USER_NATIONAL_ID_SIZE("user.nationalIdNumber.size"),
    NATIONAL_ID_EXISTS("nationalId.exists"),
    USER_IMAGE_NOT_FOUND("user.image.notfound"),
    USER_FILE_INVALID_TYPE("user.file.invalid.type"),
    USER_MEDIA_SERVICE_UNAVAILABLE("user.media.service.unavailable"),
    USER_IS_PROTECTED("user.is.protected"),

    // ==================== Role Errors ====================
    ROLE_NOT_FOUND("role.not.found"),
    ROLE_EXISTS("role.exists"),
    ROLE_ID_NULL("role.roleId.null"),
    ROLE_ID_NOT_NULL("role.roleId.notnull"),
    ROLE_ID_POSITIVE("role.roleId.positive"),
    ROLE_HAS_USERS("role.has.users"),

    // ==================== Phone Errors ====================
    PHONE_NUMBER_INVALID("phone.number.invalid"),
    PHONE_PREFIX_INVALID("phone.prefix.invalid"),
    PHONE_NUMBER_REQUIRED("phone.number.required"),

    // ==================== HTTP Status Messages ====================
    HTTP_OK("http.ok"),
    HTTP_CREATED("http.created"),
    HTTP_NO_CONTENT("http.no.content"),

    HTTP_BAD_REQUEST("http.bad.request"),
    HTTP_UNAUTHORIZED("http.unauthorized"),
    HTTP_FORBIDDEN("http.forbidden"),
    HTTP_NOT_FOUND("http.not.found"),
    HTTP_CONFLICT("http.conflict"),
    HTTP_MESSAGE_NOT_READABLE("http.message.not.readable"),


    HTTP_INTERNAL_SERVER_ERROR("http.internal.server.error"),
    HTTP_SERVICE_UNAVAILABLE("http.service.unavailable"),

    // ==================== Password Errors ====================
    USER_PASSWORD_NOT_BLANK("user.password.notblank"),
    USER_PASSWORD_MUST_BE_NULL("user.password.null"),
    USER_PASSWORD_MIN_SIZE("user.password.size"),
    USER_PASSWORD_PATTERN("user.password.pattern"),

    // ==================== City Errors ====================
    CITY_NOT_FOUND("city.not.found"),
    CITY_NAME_EXISTS("city.name.exists"),
    CITY_ID_NULL("city.cityId.null"),
    CITY_ID_NOT_NULL("city.cityId.notnull"),
    CITY_ID_POSITIVE("city.cityId.positive"),
    CITY_NAME_NOT_BLANK("city.name.notblank"),
    CITY_NAME_SIZE("city.name.size"),
    CITY_BASE_FEE_NOT_NULL("city.baseFee.notnull"),
    CITY_BASE_FEE_MIN("city.baseFee.min"),
    CITY_BASE_FEE_DIGITS("city.baseFee.digits"),
    CITY_ID_REQUIRED("city.id.required"),

    // ==================== Mall Errors ====================
    MALL_MALL_ID_NULL("mall.mallId.null"),
    MALL_MALL_ID_NOTNULL("mall.mallId.notnull"),
    MALL_MALL_ID_POSITIVE("mall.mallId.positive"),
    MALL_CITY_ID_NOTNULL("mall.cityId.notnull"),
    MALL_NAME_NOTBLANK("mall.name.notblank"),
    MALL_NAME_SIZE("mall.name.size"),
    MALL_DESCRIPTION_NOTBLANK("mall.description.notblank"),
    MALL_CAPACITY_POSITIVE("mall.capacity.positive"),
    MALL_LOCATION_NOTBLANK("mall.location.notblank"),
    MALL_LOCATION_SIZE("mall.location.size"),
    MALL_STATUS_NOTNULL("mall.status.notnull"),
    MALL_NOT_FOUND("mall.not.found"),
    MALL_NAME_EXISTS_IN_CITY("mall.name.exists.in.city"),
    MALL_INVALID_STATUS("mall.invalid.status"),
    MALL_IMAGE_NOT_FOUND("mall.image.notfound"),
    MALL_FILE_INVALID_TYPE("mall.file.invalid.type"),
    MALL_MEDIA_SERVICE_UNAVAILABLE("mall.media.service.unavailable"),
    MALL_HAS_ACTIVE_SHOPS("mall.has.active.shops"),

    // ==================== MallService Errors ====================
    MALL_SERVICE_SERVICE_ID_NULL("mallService.serviceId.null"),
    MALL_SERVICE_SERVICE_ID_NOTNULL("mallService.serviceId.notnull"),
    MALL_SERVICE_SERVICE_ID_POSITIVE("mallService.serviceId.positive"),
    MALL_SERVICE_NAME_NOTBLANK("mallService.name.notblank"),
    MALL_SERVICE_NAME_SIZE("mallService.name.size"),
    MALL_SERVICE_MALL_NOTNULL("mallService.mall.notnull"),
    MALL_SERVICE_BATCH_DIFFERENT_MALLS("mall.service.batch.different.malls"),
    MALL_SERVICE_NOT_FOUND("mall.service.not.found"),
    MALL_SERVICE_NAME_EXISTS("mall.service.name.exists"),

    // ==================== MallRestaurant Errors ====================
    RESTAURANT_RESTAURANT_ID_NULL("restaurant.restaurantId.null"),
    RESTAURANT_RESTAURANT_ID_NOTNULL("restaurant.restaurantId.notnull"),
    RESTAURANT_RESTAURANT_ID_POSITIVE("restaurant.restaurantId.positive"),
    RESTAURANT_NAME_NOTBLANK("restaurant.name.notblank"),
    RESTAURANT_NAME_SIZE("restaurant.name.size"),
    RESTAURANT_CUISINE_TYPE_SIZE("restaurant.cuisineType.size"),
    RESTAURANT_LOCATION_IN_MALL_SIZE("restaurant.locationInMall.size"),
    RESTAURANT_LOGO_URL_SIZE("restaurant.logoUrl.size"),
    RESTAURANT_MALL_NOTNULL("restaurant.mall.notnull"),
    MALL_RESTAURANT_NOT_FOUND("mall.restaurant.not.found"),
    MALL_RESTAURANT_NAME_EXISTS("mall.restaurant.name.exists"),
    MALL_RESTAURANT_BATCH_DIFFERENT_MALLS("mall.restaurant.batch.different.malls"),
    RESTAURANT_IMAGE_NOT_FOUND("restaurant.image.notfound"),
    RESTAURANT_FILE_INVALID_TYPE("restaurant.file.invalid.type"),
    RESTAURANT_MEDIA_SERVICE_UNAVAILABLE("restaurant.media.service.unavailable"),

    // ==================== Shop Errors ====================
    SHOP_SHOP_ID_NULL("shop.shopId.null"),
    SHOP_SHOP_ID_NOTNULL("shop.shopId.notnull"),
    SHOP_SHOP_ID_POSITIVE("shop.shopId.positive"),
    SHOP_MALL_NOTNULL("shop.mall.notnull"),
    SHOP_OWNER_NOTNULL("shop.owner.notnull"),
    SHOP_NAME_NOTBLANK("shop.name.notblank"),
    SHOP_NAME_SIZE("shop.name.size"),
    SHOP_CATEGORY_NOTNULL("shop.category.notnull"),
    SHOP_LOCATION_NOTBLANK("shop.location.notblank"),
    SHOP_LOCATION_SIZE("shop.location.size"),
    SHOP_LOGO_URL_SIZE("shop.logoUrl.size"),
    SHOP_NOT_FOUND("shop.not.found"),
    SHOP_NAME_EXISTS_IN_MALL("shop.name.exists.in.mall"),
    SHOP_INVALID_STATUS("shop.invalid.status"),
    SHOP_IMAGE_NOT_FOUND("shop.image.notfound"),
    SHOP_FILE_INVALID_TYPE("shop.file.invalid.type"),
    SHOP_MEDIA_SERVICE_UNAVAILABLE("shop.media.service.unavailable"),
    SHOP_CANNOT_CHANGE_MALL("shop.cannot.change.mall"),
    SHOP_CANNOT_CHANGE_OWNER("shop.cannot.change.owner"),

    // ==================== ShopOwnerRequest Errors ====================
    SHOP_OWNER_REQUEST_NOT_FOUND("shop.owner.request.not.found"),
    SHOP_OWNER_REQUEST_ALREADY_PROCESSED("shop.owner.request.already.processed"),
    SHOP_OWNER_REQUEST_REJECTION_REASON_REQUIRED("shop.owner.request.rejection.reason.required"),
    SHOP_OWNER_REQUEST_MISSING_SHOP_REQUEST("shop.owner.request.missing.shop.request"),
    SHOP_OWNER_REQUEST_USERNAME_PENDING("shop.owner.request.username.pending"),
    SHOP_OWNER_REQUEST_PHONE_PENDING("shop.owner.request.phone.pending"),
    SHOP_OWNER_REQUEST_EMAIL_PENDING("shop.owner.request.email.pending"),
    SHOP_OWNER_REQUEST_MEDIA_NOT_FOUND("shop.owner.request.media.not.found"),
    MEDIA_SERVICE_UNAVAILABLE("media.service.unavailable"),
    SHOP_OWNER_REQUEST_NATIONAL_ID_EXISTS_OR_PENDING("shop.owner.request.national.id.pending"),
    MEDIA_NOT_FOUND("media.not.found"),
    INVALID_FILE_TYPE("media.invalid.file.type"),
    SHOP_REQUEST_NAME_EXISTS_IN_MALL("shop.request.name.exists.in.mall"),
    INVALID_CREDENTIALS("invalid.credentials"),
    NOT_A_SHOP_OWNER("not.a.shop.owner"),
    WRONG_REQUEST_TYPE("wrong.request.type"),
    SHOP_REQUEST_REQUIRED("shop.owner.request.missing.shop.request"),
    SHOP_REQUEST_MALL_INVALID("shopRequest.mall.invalid"),
    SHOP_REQUEST_MALL_CITY_NOT_FOUND("shopRequest.requestedMallCityId.notfound"),
    SHOP_REQUEST_REQUESTED_MALL_NAME_EXISTS("shopRequest.requestedMallName.exists"),

    // Profile
    USER_INVALID_CURRENT_PASSWORD("user.invalid.current.password"),
    USER_NEW_PASSWORD_SAME_AS_CURRENT("user.new.password.same.as.current"),
    USER_UNAUTHORIZED_PROFILE_UPDATE("user.unauthorized.profile.update"),
    USER_FULL_NAME_SIZE("user.fullName.size"),
    USER_PASSWORD_CONFIRM_MISMATCH("user.password.confirm.mismatch"),

    // ==================== Auth / Security ====================
    AUTH_INVALID_CREDENTIALS("auth.invalid.credentials"),
    AUTH_ACCOUNT_DISABLED("auth.account.disabled"),
    AUTH_INVALID_TOKEN("auth.invalid.token"),
    AUTH_TOKEN_EXPIRED("auth.token.expired"),
    AUTH_INVALID_OTP("auth.invalid.otp"),
    AUTH_OTP_EXPIRED("auth.otp.expired"),
    AUTH_PHONE_NOT_REGISTERED("auth.phone.not.registered"),
    AUTH_ACCESS_DENIED("auth.access.denied"),

    SHOP_BLOCKED("shop.blocked"),

    // ==================== Forgot Password ====================
    AUTH_FORGOT_USER_NOT_FOUND("auth.forgot.user.not.found"),
    AUTH_RESET_PASSWORD_MISMATCH("auth.reset.password.mismatch"),
    AUTH_RESET_PASSWORD_SAME_AS_CURRENT("auth.reset.password.same.as.current"),
    AUTH_RESET_TOKEN_INVALID("auth.reset.token.invalid"),
    AUTH_FORGOT_EMAIL_NOT_FOUND("auth.forgot.email.not.found"),



    // =========================================================
    // ===================== Catalog ===========================
    // =========================================================



    // =========================================================
    // CATEGORY - ERRORS
    // =========================================================

    CATEGORY_NOT_FOUND("category.not.found"),
    CATEGORY_IMAGE_NOT_FOUND("category.image.not.found"),
    CATEGORY_PARENT_NOT_FOUND("category.parent.not.found"),
    CATEGORY_AUDIENCE_CONFIG_NOT_FOUND("category.audience.config.not.found"),

    CATEGORY_SLUG_EXISTS("category.slug.exists"),

    CATEGORY_SELF_PARENT("category.self.parent"),
    CATEGORY_CIRCULAR_HIERARCHY("category.circular.hierarchy"),
    CATEGORY_AUDIENCE_CONFIG_DUPLICATE("category.audience.config.duplicate"),
    AUDIENCE_CONFIG_NOT_ALLOWED_HERE("audience.config.not.allowed.here"),
    AUDIENCE_CONFIG_OUTSIDE_CATEGORY_SCOPE("audience.config.outside.category.scope"),
    CATEGORY_HAS_CHILDREN("category.has.children"),
    CATEGORY_HAS_PRODUCTS("category.has.product"),

    CATEGORY_IMAGE_COULD_NOT_BE_VALIDATED("category.image.could.not.be.validated"),
    CATEGORY_IMAGE_INVALID("category.image.invalid"),

    // =========================================================
    // CATEGORY - DTO VALIDATION
    // =========================================================
    CATEGORY_ID_NULL("category.id.null"),
    CATEGORY_ID_NOT_NULL("category.id.notnull"),

    CATEGORY_NAME_NOT_BLANK("category.name.notblank"),
    CATEGORY_NAME_SIZE("category.name.size"), // min=5, max=50

    CATEGORY_SLUG_NOT_BLANK("category.slug.notblank"),
    CATEGORY_SLUG_SHOULD_NOT_HAVE_WHITE_SPACES("category.slug.white.spaces"),
    CATEGORY_SLUG_LOWERCASE("category.slug.lowercase"),
    CATEGORY_SLUG_START_END_LETTER("category.slug.start.end.letter"),
    CATEGORY_SLUG_SIZE("category.slug.size"), // min=5, max=50

    CATEGORY_IMAGE_ID_BLANK("category.imageId.notnull"),

    CATEGORY_PARENT_ID_NOT_NULL("category.parentId.notnull"),

    CATEGORY_IS_ACTIVE_NOT_BLANK("category.isActive.notnull"),

    CATEGORY_TARGETED_AUDIENCE_NOT_NULL("category.targetedAudience.notnull"),

    CATEGORY_AGE_GROUP_NOT_NULL("category.ageGroup.notnull"),

    // =========================================================
    // BRAND - ERRORS
    // =========================================================
    BRAND_NOT_FOUND("brand.not.found"),
    BRAND_SLUG_EXISTS("brand.slug.exists"),
    BRAND_IMAGE_NOT_FOUND("brand.image.notfound"),
    BRAND_IMAGE_COULD_NOT_BE_VALIDATED("brand.image.could.not.be.validated"),
    BRAND_IMAGE_INVALID("brand.image.invalid"),
    BRAND_INACTIVE("brand.inactive"),
    BRAND_HAS_PRODUCTS("brand.has.products"),

    // =========================================================
    // BRAND - DTO VALIDATION
    // =========================================================
    BRAND_ID_NULL("brand.id.null"),
    BRAND_ID_NOT_NULL("brand.id.notnull"),

    BRAND_NAME_NOT_BLANK("brand.name.notblank"),
    BRAND_NAME_SIZE("brand.name.size"), // min=5, max=50

    BRAND_SLUG_NOT_BLANK("brand.slug.notblank"),
    BRAND_SLUG_WHITE_SPACES("brand.slug.white.spaces"),
    BRAND_SLUG_LOWERCASE("brand.slug.lowercase"),
    BRAND_SLUG_START_END_LETTER("brand.slug.start.end.letter"),
    BRAND_SLUG_SIZE("brand.slug.size"), // min=5, max=50

    BRAND_TARGETED_AUDIENCE_NOT_NULL("brand.targetedAudience.notnull"),

    BRAND_AGE_GROUP_NOT_NULL("brand.ageGroup.notnull"),

    BRAND_IS_ACTIVE_NOT_BLANK("brand.isActive.notnull"),

    BRAND_IMAGE_ID_BLANK("brand.imageId.notnull"),

    // =========================================================
    // TAG - ERRORS
    // =========================================================
    TAG_NOT_FOUND("tag.not.found"),
    TAG_NAME_EXIST("tag.name.exist"),
    TAG_HAS_PRODUCTS("tag.has.products"),

    // =========================================================
    // TAG - DTO VALIDATION
    // =========================================================
    TAG_ID_NULL("tag.id.null"),
    TAG_ID_NOT_NULL("tag.id.notnull"),

    TAG_NAME_NOT_BLANK("tag.name.notblank"),
    TAG_NAME_SIZE("tag.name.size"), // min=5, max=50

    // =========================================================
    // ATTRIBUTE OPTION - ERRORS
    // =========================================================
    DUPLICATION_IN_ORDER_SORT("attribute.options.orderSort.duplication"),
    DUPLICATION_IN_VALUE("attribute.options.value.duplication"),
    ATTRIBUTE_OPTION_NO_FOUND("attribute.options.no.found"),

    // =========================================================
    // ATTRIBUTE OPTION - DTO VALIDATION
    // =========================================================
    ATTRIBUTE_OPTION_ID_NULL("attribute.option.id.null"),
    ATTRIBUTE_OPTION_ID_NOT_NULL("attribute.option.id.notnull"),

    ATTRIBUTE_OPTION_VALUE_NOT_BLANK("attribute.option.value.notblank"),
    ATTRIBUTE_OPTION_VALUE_SIZE("attribute.option.value.size"),

    ATTRIBUTE_OPTION_SORT_ORDER_NOT_NULL("attribute.option.sortOrder.notnull"),

    // =========================================================
    // ATTRIBUTE - ERRORS
    // =========================================================
    ATTRIBUTE_NOT_FOUND("attribute.not.found"),
    ATTRIBUTE_SLUG_EXISTS("attribute.slug.exists"),
    ATTRIBUTE_INACTIVE("attribute.inactive"),
    ATTRIBUTE_HAS_PRODUCTS("attribute.has.products"),

    // =========================================================
    // ATTRIBUTE - DTO VALIDATION
    // =========================================================
    ATTRIBUTE_ID_NULL("attribute.id.null"),
    ATTRIBUTE_ID_NOT_NULL("attribute.id.notnull"),

    ATTRIBUTE_NAME_NOT_BLANK("attribute.name.notblank"),
    ATTRIBUTE_NAME_SIZE("attribute.name.size"),

    ATTRIBUTE_SLUG_NOT_BLANK("attribute.slug.notblank"),
    ATTRIBUTE_SLUG_WHITE_SPACES("attribute.slug.white.spaces"),
    ATTRIBUTE_SLUG_LOWERCASE("attribute.slug.lowercase"),
    ATTRIBUTE_SLUG_START_END_LETTER("attribute.slug.start.end.letter"),
    ATTRIBUTE_SLUG_SIZE("attribute.slug.size"),

    ATTRIBUTE_IS_ACTIVE_NOT_NULL("attribute.isActive.notnull"),

    ATTRIBUTE_OPTIONS_NOT_NULL("attribute.options.notnull"),

    // =========================================================
    // PRODUCT - ERRORS
    // =========================================================
    PRODUCT_NOT_FOUND("product.not.found"),
    PRODUCT_NOT_ACTIVE("product.not.active"),
    PRODUCT_SLUG_EXISTS("product.slug.exists"),
    PRODUCT_SLUG_EXISTS_IN_THE_SAME_STORE("product.slug.exists.in.the.same.store"),
    PRODUCT_INACTIVE("product.inactive"),
    PRODUCT_HAS_MULTIPLE_DEFAULT_VARIANTS("product.has.multiple.default.variants"),
    DEFAULT_VARIANTS_REQUIRED("default.variants.required"),
    PRODUCT_VARIANT_SHOULD_HAS_ATTRIBUTE("product.variant.should.have.attribute"),
    INVALID_PRODUCT_AUDIENCE_FOR_CATEGORY("invalid.product.audience.for.category"),
    INVALID_PRODUCT_AGE_GROUP_FOR_CATEGORY("invalid.product.ageGroup.for.category"),
    INTERACTION_SERVICE_NOT_AVAILABLE("interaction.service.not.available"),
    PRODUCT_DOSE_NOT_BELONG_TO_MALL("product.dose.not.belong.to.mall"),
    PRODUCT_DOSE_NOT_BELONG_TO_STORE("product.dose.not.belong.to.store"),
    // =========================================================
    // PRODUCT - DTO VALIDATION
    // =========================================================
    PRODUCT_ID_NULL("product.id.null"),
    PRODUCT_ID_NOT_NULL("product.id.notnull"),

    PRODUCT_NAME_NOT_BLANK("product.name.notblank"),
    PRODUCT_NAME_SIZE("product.name.size"),

    PRODUCT_SLUG_NOT_BLANK("product.slug.notblank"),
    PRODUCT_SLUG_WHITE_SPACES("product.slug.white.spaces"),
    PRODUCT_SLUG_LOWERCASE("product.slug.lowercase"),
    PRODUCT_SLUG_START_END_LETTER("product.slug.start.end.letter"),
    PRODUCT_SLUG_SIZE("product.slug.size"),

    PRODUCT_TARGETED_AUDIENCE_NOT_NULL("product.targetedAudience.notnull"),
    PRODUCT_AGE_GROUP_NOT_NULL("product.ageGroup.notnull"),
    PRODUCT_IS_ACTIVE_NOT_NULL("product.isActive.notnull"),

    PRODUCT_SHORT_DESCRIPTION_NOT_BLANK("product.shortDescription.notblank"),
    PRODUCT_SHORT_DESCRIPTION_SIZE("product.shortDescription.size"),

    PRODUCT_DESCRIPTION_NOT_BLANK("product.description.notblank"),
    PRODUCT_DESCRIPTION_SIZE("product.description.size"),

    PRODUCT_MALL_ID_NULL("product.mallId.null"),
    PRODUCT_STORE_ID_NULL("product.storeId.null"),

    PRODUCT_VARIANTS_NOT_NULL("product.variants.not.null"),

    // =========================================================
    // PRODUCT VARIANT - ERRORS
    // =========================================================
    PRODUCT_VARIANT_NOT_FOUND("product.variant.not.found"),
    PRODUCT_VARIANT_DUPLICATE_ATTRIBUTE("product.variant.duplicate.attribute"),
    PRODUCT_VARIANT_DUPLICATE_MEDIUM_SORT("product.variant.duplicate.image.sort"),
    PRODUCT_VARIANT_MEDIA_LIMIT_EXCEEDED("product.variant.image.limitExceeded"),
    PRODUCT_VARIANT_MUST_HAVE_AT_LEAST_ONE_MEDIUM("product.variant.must.haveAtLeastOne"),
    PRODUCT_DEFAULT_VARIANT_DELETION_NOT_ALLOWED("product.default.variant.deletion.notallowed"),
    PRODUCT_VARIANT_MEDIUM_NOT_FOUND("product.variant.image.not.found"),
    PRODUCT_VARIANT_MEDIUM_COULD_NOT_BE_VALIDATED("product.variant.image.couldNotBeValidated"),
    PRODUCT_VARIANT_MEDIUM_TYPE_INVALID("product.variant.type.invalid"),
    PRODUCT_VARIANT_SLUG_EXISTS("product.variant.slug.exists"),
    PRODUCT_VARIANT_INACTIVE("product.variant.inactive"),

    // =========================================================
    // PRODUCT VARIANT - DTO VALIDATION
    // =========================================================
    PRODUCT_VARIANT_ID_NULL("product.variant.id.null"),
    PRODUCT_VARIANT_ID_NOT_NULL("product.variant.id.notnull"),
    PRODUCT_VARIANT_NAME_NOT_BLANK("product.variant.name.notblank"),

    PRODUCT_VARIANT_BASE_PRICE_NOT_BLANK("product.variant.basePrice.notblank"),
    PRODUCT_VARIANT_BASE_PRICE_POSITIVE("product.variant.basePrice.positive"),

    PRODUCT_VARIANT_IS_DEFAULT_NOT_NULL("product.variant.isDefault.notnull"),

    PRODUCT_VARIANT_MEDIA_NOT_NULL("product.variant.media.not.null"),

    // =========================================================
    // FAVORITE ERROR
    // =========================================================
    FAVORITE_NOT_FOUND("favorite.not.found"),
    FAVORITE_PRODUCT_NOT_FOUND("favorite.product.not.found"),
    FAVORITE_ALREADY_EXISTS("favorite.already.exists"),
    FAVORITE_ACCESS_DENIED("favorite.access.denied"),

    // Reviews
    REVIEW_NOT_FOUND("review.not.found"),
    REVIEW_ALREADY_EXISTS("review.already.exists"),
    USER_NOT_FOUND_FOR_REVIEW("user.not.found.for.review"),


    // Comments
    COMMENT_NOT_FOUND("comment.not.found"),
    COMMENT_ALREADY_EXISTS("comment.already.exists"),
    COMMENT_REJECTED_BY_MODERATION("comment.rejected.by.moderation"),
    COMMENT_CANNOT_EDIT_REJECTED("comment.cannot.edit.rejected"),
    COMMENT_ALREADY_REPORTED("comment.already.reported"),
    COMMENT_CANNOT_REPORT_OWN("comment.cannot.report.own"),
    COMMENT_NOT_APPROVED_FOR_REPORT("comment.not.approved.for.report"),
    COMMENT_EDIT_NOT_ALLOWED_UNDER_INVESTIGATION("comment.edit.not.allowed.under.investigation"),


    // ====================================================
    // ==================== Campaigns  ====================
    // ====================================================

    // ==================== Ad Template Messages  ====================
    AD_TEMPLATE_NOT_FOUND("ad.template.not.found"),
    AD_TEMPLATE_NAME_EXISTS("ad.template.name.exists"),
    AD_TEMPLATE_INVALID_STATUS("ad.template.invalid.status"),
    AD_TEMPLATE_INVALID_DATES("ad.template.invalid.dates"),
    AD_TEMPLATE_ALREADY_RESERVED("ad.template.already.reserved"),
    AD_TEMPLATE_NOT_ACTIVE("ad.template.not.active"),
    AD_TEMPLATE_HAS_ACTIVE_REQUESTS("ad.template.has.active.requests"),
    AD_TEMPLATE_DATE_EXPIRED("ad.template.date.expired"),
    AD_TEMPLATE_POSITION_DATE_CONFLICT("ad.template.position.date.conflict"),
    AD_TEMPLATE_ARCHIVED("ad.template.archived"),
    AD_TEMPLATE_RESERVED_IMMUTABLE("ad.template.reserved.immutable"),
    AD_TEMPLATE_IMAGE_RATIO_INVALID("adTemplate.imageRatio.invalid"),
    AD_TEMPLATE_START_DATE_IN_PAST("ad.template.start.date.in.past"),
    AD_TEMPLATE_END_DATE_NOT_AFTER_START("ad.template.end.date.not.after.start"),

    // ==================== Ad Request Messages ====================
    AD_REQUEST_NOT_FOUND("ad.request.not.found"),
    AD_REQUEST_ALREADY_EXISTS("ad.request.already.exists"),
    AD_REQUEST_TEMPLATE_NOT_ACTIVE("ad.request.template.not.active"),
    AD_REQUEST_INVALID_STATUS("ad.request.invalid.status"),
    AD_REQUEST_ALREADY_PROCESSED("ad.request.already.processed"),
    AD_REQUEST_NOT_APPROVED("ad.request.not.approved"),
    AD_REQUEST_PAYMENT_ALREADY_MADE("ad.request.payment.already.made"),
    AD_REQUEST_CANNOT_MODIFY_PROCESSED("ad.request.cannot.modify.processed"),
    AD_REQUEST_TEMPLATE_ALREADY_RESERVED("ad.request.template.already.reserved"),
    AD_REQUEST_PAYMENT_NOT_MADE("ad.request.payment.not.made"),
    AD_REQUEST_SHOP_NOT_FOUND("ad.request.shop.not.found"),
    AD_REQUEST_TEMPLATE_DATE_EXPIRED("ad.request.template.date.expired"),
    AD_REQUEST_DUPLICATE_FOR_SHOP("ad.request.duplicate.for.shop"),
    AD_REQUEST_TEMPLATE_START_DATE_PASSED("ad.request.template.start.date.passed"),
    AD_REQUEST_NOT_FOUND_FOR_SHOP("ad.request.not.found.for.shop"),
    AD_REQUEST_PAYMENT_OVERDUE("ad.request.payment.overdue"),
    /** Thrown when a shop owner submits a request for dates already covered by an approved request. */
    AD_REQUEST_TIME_SLOT_TAKEN("ad.request.time.slot.taken"),
    IMAGE_NOT_FOUND("request.image.notfound"),
    FILE_INVALID_TYPE("request.file.invalid.type"),

    // ==================== Offer Messages ====================
    OFFER_NOT_FOUND("offer.not.found"),
    OFFER_ITEM_NOT_FOUND("offer.item.not.found"),
    OFFER_TITLE_EXISTS("offer.title.exists"),
    OFFER_START_DATE_IN_PAST("offer.start.date.in.past"),
    OFFER_END_DATE_NOT_AFTER_START("offer.end.date.not.after.start"),
    OFFER_EXPIRED("offer.expired"),
    OFFER_ALREADY_EXPIRED("offer.already.expired"),
    OFFER_PRODUCT_ALREADY_IN_OFFER("offer.product.already.in.offer"),
    OFFER_PRODUCT_NOT_FOUND("offer.product.not.found"),
    OFFER_DISCOUNT_EXCEEDS_PRICE("offer.discount.exceeds.price"),
    OFFER_INVALID_PERCENT_VALUE("offer.invalid.percent.value"),
    OFFER_MAX_USES_REACHED("offer.max.uses.reached"),
    OFFER_CANNOT_MODIFY_ACTIVE("offer.cannot.modify.active"),
    OFFER_NO_ACTIVE_ITEMS("offer.no.active.items"),
    OFFER_PRODUCT_ALREADY_IN_OVERLAPPING_OFFER("offer.product.already.in.overlapping.offer"),


    // ==================== Subscription ====================
    SUBSCRIPTION_NOT_FOUND("subscription.not.found"),
    SUBSCRIPTION_SHOP_ALREADY_HAS_ONE("subscription.shop.already.has.one"),
    SUBSCRIPTION_PLAN_NOT_FOUND("subscription.plan.not.found"),
    SUBSCRIPTION_ALREADY_ACTIVE("subscription.already.active"),
    SUBSCRIPTION_CANCELLED("subscription.cancelled"),
    SUBSCRIPTION_SHOP_NOT_FOUND("subscription.shop.not.found"),
    SUBSCRIPTION_WRITE_ACCESS_DENIED("subscription.write.access.denied"),
    SUBSCRIPTION_STRIPE_ERROR("subscription.stripe.error"),


    // =======================================================
    // ==================== Media Manager ====================
    // =======================================================

    // ==================== File VALIDATION ====================
    FILE_FILE_NOT_NULL("file.file.notNull"),
    FILE_ID_NOT_NULL("file.id.notNull"),
    FILE_NAME_NOT_NULL("file.name.notNull"),
    FILE_EXTENSION_NOT_NULL("file.extension.notNull"),
    FILE_FOLDERID_NOT_NULL("file.folderId.notNull"),
    FILE_FOLDERID_POSITIVE("file.folderId.positive"),

    // ==================== File Error ====================
    FILE_NOT_FOUND("file.not.found"),
    FILE_NAME_EXISTS("file.name.exist"),
    FILE_UPLOAD_FAILED("file.upload.failed"),
    FILE_DELETE_FAILED("file.delete.failed"),
    FILE_STORE_ID_MISMATCH("file.store.id.mismatch"),
    FILE_NOT_APPROVED_YET("file.not.approved"),
    FILE_REJECTED("file.rejected"),
    FILE_TOO_LARGE("file.too.large"),
    CHANGING_FILE_SCOPE_NOT_ALLOWED("file.changing.scope.notAllowed"),
    FILE_SCOPE_MISMATCH("file.scope.mismatch"),
    CHANGING_FILE_MANAGER_NOT_ALLOWED("file.changing.manager.notAllowed"),
    FILE_SCOPE_NOT_FOUND("file.scope.notFound"),
    FILE_MANAGED_BY_NOT_FOUND("file.managedBy.notFound"),
    FILE_IN_USE("file.in.use"),
    FILE_IN_USE_VALIDATION_FAILED("file.inUse.validationFailed"),


    // ==================== Folder VALIDATION ====================
    FOLDER_ID_NOT_NULL("folder.id.notnull"),
    FOLDER_ID_NULL("folder.id.null"),
    FOLDER_ID_POSITIVE("folder.id.positive"),
    FOLDER_NAME_NOT_BLANK("folder.name.notblank"),
    FOLDER_STORE_ID_NOT_BLANK("folder.storeId.notblank"),
    FOLDER_NAME_SIZE("folder.name.size"),
    FOLDER_PARENTID_POSITIVE("folder.parent.id.positive"),

    // ==================== Folder Error ====================
    FOLDER_NOT_FOUND("folder.not.found"),
    FOLDER_NAME_EXISTS("folder.name.exist"),
    FOLDER_HIERARCHY_CYCLIC("folder.hierarchy.cyclic"),
    FOLDER_STORE_ID_MISMATCH("folder.store.id.mismatch"),
    CHANGING_FOLDER_SCOPE_NOT_ALLOWED("changing.folder.scope.not.allowed"),
    ROOT_FOLDER_CREATION_NOT_ALLOWED("root.folder.creation.not.allowed"),
    FOLDER_SCOPE_MISMATCH("folder.scope.mismatch"),
    CHANGING_FOLDER_MANAGER_NOT_ALLOWED("changing.folder.manager.not.allowed"),
    FOLDER_SCOPE_NOT_FOUND("folder.scope.not.found"),
    FOLDER_MANAGED_BY_NOT_FOUND("folder.managed.by.not.found");

    private final String key;
}
