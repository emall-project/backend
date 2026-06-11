package store.emall.backend.campaigns.ad.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import store.emall.backend.campaigns.ad.request.AdRequestService;

@Component
@Profile("!test")
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class AdScheduler {

    private final AdRequestService adRequestManagementService;

    /**
     * Runs every hour — Activates paid ads whose startDate has arrived.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void activatePaidAds() {
        log.info("Scheduler: Activating paid ads...");
        try {
            adRequestManagementService.activatePaidAds();
        } catch (Exception e) {
            log.error("Scheduler: Error activating paid ads", e);
        }
    }

    /**
     * Runs every hour at :15 — Deactivates ads whose endDate has passed.
     * Also releases the template back to ACTIVE.
     */
    @Scheduled(cron = "0 15 * * * *")
    public void deactivateExpiredAds() {
        log.info("Scheduler: Deactivating expired ads...");
        try {
            adRequestManagementService.deactivateExpiredAds();
        } catch (Exception e) {
            log.error("Scheduler: Error deactivating expired ads", e);
        }
    }

    /**
     * Runs daily at 9 AM — Sends payment reminders to shop owners
     * who have approved ads but haven't paid yet.
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendPaymentReminders() {
        log.info("Scheduler: Sending payment reminders...");
        try {
            adRequestManagementService.sendPaymentReminders();
        } catch (Exception e) {
            log.error("Scheduler: Error sending payment reminders", e);
        }
    }

    /**
     * Runs every 2 hours at :30 — Handles overdue payments.
     * If an approved ad's startDate has passed and payment was not made:
     * - Marks payment as OVERDUE
     * - Ad is NOT displayed
     * - Template is released back to ACTIVE so others can request it
     * - Notifies the shop owner
     */
    @Scheduled(cron = "0 30 */2 * * *")
    public void handleOverduePayments() {
        log.info("Scheduler: Handling overdue payments...");
        try {
            adRequestManagementService.handleOverduePayments();
        } catch (Exception e) {
            log.error("Scheduler: Error handling overdue payments", e);
        }
    }
}
