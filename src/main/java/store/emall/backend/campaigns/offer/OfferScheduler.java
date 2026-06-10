package store.emall.backend.campaigns.offer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OfferScheduler {

    private final OfferService offerService;

    /**
     * Runs every day at midnight.
     * Activates INACTIVE offers whose startDate has been reached.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void activateScheduledOffers() {
        log.info("Scheduler: activating scheduled offers");
        offerService.activateScheduledOffers();
    }

    /**
     * Runs every day at 00:05.
     * Marks ACTIVE offers whose endDate has passed as EXPIRED.
     */
    @Scheduled(cron = "0 5 0 * * *")
    public void expireEndedOffers() {
        log.info("Scheduler: expiring ended offers");
        offerService.expireEndedOffers();
    }
}