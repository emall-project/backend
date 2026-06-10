package store.emall.backend.campaigns.subscription;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionStatusDto {
    private SubscriptionStatus status;
    private LocalDate trialEndDate;
    private LocalDate endDate;
    private LocalDateTime suspendedAt;
}