package store.emall.backend.catalog.product.light;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductLightRow {
    Long getProductId();
    String getProductName();
    String getProductSlug();
    Long getDefaultVariantId();
    BigDecimal getBasePrice();
    UUID getMediumId();
    String getCategoryName();
    String getBrandName();
    Boolean getIsActive();
    Long getVariantsCount();
}