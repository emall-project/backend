package store.emall.backend.campaigns.subscription;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.campaigns.ad.request.AdRequestService;

import java.math.BigDecimal;

import static com.stripe.net.ApiResource.GSON;

@RestController
@RequestMapping("/subscriptions/webhooks")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    private final ShopSubscriptionService subscriptionService;
    private final AdRequestService adRequestService;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe webhook signature");
            return ResponseEntity.badRequest().body("Invalid signature");
        } catch (Exception e) {
            log.error("Error parsing Stripe webhook: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Parse error");
        }

        log.info("Stripe webhook: type={}, id={}", event.getType(), event.getId());

        try {
            switch (event.getType()) {

                case "invoice.payment_succeeded" -> {
                    String rawJson = event.getDataObjectDeserializer().getRawJson();

                    // Parse the raw JSON to get the subscription ID from new API format
                    com.google.gson.JsonObject invoiceJson = com.google.gson.JsonParser
                            .parseString(rawJson).getAsJsonObject();

                    // Try direct subscription field first, then parent.subscription_details.subscription
                    String stripeSubscriptionId = null;
                    if (invoiceJson.has("subscription") && !invoiceJson.get("subscription").isJsonNull()) {
                        stripeSubscriptionId = invoiceJson.get("subscription").getAsString();
                    } else if (invoiceJson.has("parent") && !invoiceJson.get("parent").isJsonNull()) {
                        com.google.gson.JsonObject parent = invoiceJson.getAsJsonObject("parent");
                        if (parent.has("subscription_details") && !parent.get("subscription_details").isJsonNull()) {
                            com.google.gson.JsonObject subDetails = parent.getAsJsonObject("subscription_details");
                            if (subDetails.has("subscription") && !subDetails.get("subscription").isJsonNull()) {
                                stripeSubscriptionId = subDetails.get("subscription").getAsString();
                            }
                        }
                    }

                    long amountPaid = invoiceJson.has("amount_paid") ?
                            invoiceJson.get("amount_paid").getAsLong() : 0L;

                    if (amountPaid == 0) {
                        log.info("Skipping $0 invoice, stripeSubId={}", stripeSubscriptionId);
                        break;
                    }

                    String paymentIntent = invoiceJson.has("payment_intent") &&
                            !invoiceJson.get("payment_intent").isJsonNull() ?
                            invoiceJson.get("payment_intent").getAsString() : null;

                    String hostedInvoiceUrl = invoiceJson.has("hosted_invoice_url") &&
                            !invoiceJson.get("hosted_invoice_url").isJsonNull() ?
                            invoiceJson.get("hosted_invoice_url").getAsString() : null;

                    String currency = invoiceJson.has("currency") &&
                            !invoiceJson.get("currency").isJsonNull() ?
                            invoiceJson.get("currency").getAsString() : "usd";

                    BigDecimal amount = BigDecimal.valueOf(amountPaid).movePointLeft(2);

                    log.info("Processing payment_succeeded for stripeSubId={}, amount={}",
                            stripeSubscriptionId, amount);

                    subscriptionService.handlePaymentSuccess(
                            stripeSubscriptionId,
                            paymentIntent,
                            hostedInvoiceUrl,
                            amount,
                            currency
                    );
                }

                case "invoice.payment_failed" -> {
                    String rawJson = event.getDataObjectDeserializer().getRawJson();

                    com.google.gson.JsonObject invoiceJson = com.google.gson.JsonParser
                            .parseString(rawJson).getAsJsonObject();

                    // Get subscription ID
                    String stripeSubscriptionId = null;
                    if (invoiceJson.has("subscription") && !invoiceJson.get("subscription").isJsonNull()) {
                        stripeSubscriptionId = invoiceJson.get("subscription").getAsString();
                    } else if (invoiceJson.has("parent") && !invoiceJson.get("parent").isJsonNull()) {
                        com.google.gson.JsonObject parent = invoiceJson.getAsJsonObject("parent");
                        if (parent.has("subscription_details") && !parent.get("subscription_details").isJsonNull()) {
                            com.google.gson.JsonObject subDetails = parent.getAsJsonObject("subscription_details");
                            if (subDetails.has("subscription") && !subDetails.get("subscription").isJsonNull()) {
                                stripeSubscriptionId = subDetails.get("subscription").getAsString();
                            }
                        }
                    }

                    // Try to get the real decline reason from multiple possible fields
                    String reason = extractFailureReason(invoiceJson);

                    log.info("Processing payment_failed for stripeSubId={}, reason={}",
                            stripeSubscriptionId, reason);
                    subscriptionService.handlePaymentFailed(stripeSubscriptionId, reason);
                }

                case "customer.subscription.deleted" -> {
                    String rawJson = event.getDataObjectDeserializer().getRawJson();

                    com.google.gson.JsonObject subJson = com.google.gson.JsonParser
                            .parseString(rawJson).getAsJsonObject();

                    String stripeSubscriptionId = null;
                    if (subJson.has("id") && !subJson.get("id").isJsonNull()) {
                        stripeSubscriptionId = subJson.get("id").getAsString();
                    }

                    log.info("Processing subscription.deleted for stripeSubId={}", stripeSubscriptionId);
                    subscriptionService.handleSubscriptionCancelled(stripeSubscriptionId);
                }

                case "payment_intent.succeeded" -> {
                    String rawJson = event.getDataObjectDeserializer().getRawJson();

                    com.google.gson.JsonObject piJson = com.google.gson.JsonParser
                            .parseString(rawJson).getAsJsonObject();

                    // Only handle ad request payments — check metadata
                    String paymentIntentId = null;
                    if (piJson.has("id") && !piJson.get("id").isJsonNull()) {
                        paymentIntentId = piJson.get("id").getAsString();
                    }

                    // Check if this is an ad request payment via metadata
                    boolean isAdPayment = false;
                    if (piJson.has("metadata") && !piJson.get("metadata").isJsonNull()) {
                        com.google.gson.JsonObject metadata = piJson.getAsJsonObject("metadata");
                        isAdPayment = metadata.has("adRequestId");
                    }

                    if (isAdPayment && paymentIntentId != null) {
                        log.info("Processing ad payment success for paymentIntentId={}", paymentIntentId);
                        adRequestService.handleAdPaymentSuccess(paymentIntentId);
                    } else {
                        log.debug("payment_intent.succeeded is not an ad payment, skipping");
                    }
                }


                default -> log.debug("Unhandled Stripe event: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("Error handling Stripe event type={}: {}", event.getType(), e.getMessage(), e);
        }

        return ResponseEntity.ok("Received");
    }


    private String extractFailureReason(com.google.gson.JsonObject invoiceJson) {

        // 1. Check last_finalization_error
        if (invoiceJson.has("last_finalization_error")
                && !invoiceJson.get("last_finalization_error").isJsonNull()) {
            com.google.gson.JsonObject error =
                    invoiceJson.getAsJsonObject("last_finalization_error");
            if (error.has("message") && !error.get("message").isJsonNull()) {
                return error.get("message").getAsString();
            }
        }

        // 2. Check charge failure message via payment_intent
        if (invoiceJson.has("payment_intent")
                && !invoiceJson.get("payment_intent").isJsonNull()) {
            // payment_intent is just an ID string here, not the full object
            // so we can't get the decline reason from it in the webhook payload
            // but we can return a mapped message based on what Stripe sends
        }

        // 3. Check status and billing_reason to give a better message
        String billingReason = invoiceJson.has("billing_reason")
                && !invoiceJson.get("billing_reason").isJsonNull()
                ? invoiceJson.get("billing_reason").getAsString() : "";

        String status = invoiceJson.has("status")
                && !invoiceJson.get("status").isJsonNull()
                ? invoiceJson.get("status").getAsString() : "";

        // 4. Try to get from lines data — sometimes decline reason is there
        if (invoiceJson.has("charge") && !invoiceJson.get("charge").isJsonNull()) {
            // charge field present
            return "Your card was declined. Please check your card details and try again.";
        }

        // 5. Return meaningful message based on billing reason
        return switch (billingReason) {
            case "subscription_create"  -> "Payment failed during subscription setup. Please check your card details.";
            case "subscription_cycle"   -> "Auto-renewal payment failed. Please update your payment method.";
            case "subscription_update"  -> "Payment failed during subscription update. Please check your card details.";
            case "manual"               -> "Manual payment failed. Please check your card details and try again.";
            default                     -> "Payment failed. Please check your card details and try again.";
        };
    }
}