package store.emall.backend.campaigns.subscription.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import store.emall.backend.campaigns.subscription.ShopSubscriptionService;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SubscriptionScheduler {

    private final ShopSubscriptionService subscriptionService;

    /** Expire trials — daily at midnight */
    @Scheduled(cron = "0 0 0 * * *")
    public void expireTrials() {
        log.info("Scheduler: Expiring trials...");
        try { subscriptionService.expireTrials(); }
        catch (Exception e) { log.error("Scheduler error expiring trials", e); }
    }

    /** Expire active subscriptions — daily at 1 AM */
    @Scheduled(cron = "0 0 1 * * *")
    public void expireSubscriptions() {
        log.info("Scheduler: Expiring subscriptions...");
        try { subscriptionService.expireSubscriptions(); }
        catch (Exception e) { log.error("Scheduler error expiring subscriptions", e); }
    }

    /** Grace period ended check — daily at 2 AM */
    @Scheduled(cron = "0 0 2 * * *")
    public void checkGracePeriodExpiry() {
        log.info("Scheduler: Checking grace period expiry...");
        try { subscriptionService.suspendGracePeriodExpired(); }
        catch (Exception e) { log.error("Scheduler error checking grace periods", e); }
    }

    /** Trial ending in 7 days — daily at 9 AM */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendTrialEndingSoonNotifications() {
        log.info("Scheduler: Sending trial-ending-soon notifications...");
        try { subscriptionService.sendTrialEndingSoonNotifications(); }
        catch (Exception e) { log.error("Scheduler error sending trial notifications", e); }
    }

    /** Subscription expiring in 7 days — daily at 9:05 AM */
    @Scheduled(cron = "0 5 9 * * *")
    public void sendSubscriptionExpiringSoonNotifications() {
        log.info("Scheduler: Sending subscription-expiring-soon notifications...");
        try { subscriptionService.sendSubscriptionExpiringSoonNotifications(); }
        catch (Exception e) { log.error("Scheduler error sending expiry notifications", e); }
    }
}