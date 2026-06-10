package store.emall.backend.interaction.interaction;

public final class EventRoutingKeys {

    private EventRoutingKeys() {
    }


    public static final String CATALOG_PRODUCT_VIEWED = "catalog.product.viewed";
    public static final String CATALOG_CATEGORY_VIEWED = "catalog.category.viewed";

    public static final String ORDER_CREATED = "order-hub.order.created";
    public static final String CAMPAIGN_CLICKED = "campaigns.campaign.clicked";
}