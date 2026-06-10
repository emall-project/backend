package store.emall.backend.accounts.request.admin;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDecisionDto {

    @NotNull(message = "adminDecision.requestId.notnull")
    @Positive(message = "adminDecision.requestId.positive")
    private Long shopOwnerRequestId;

    @NotNull(message = "adminDecision.approved.notnull")
    private Boolean approved;

    // Required only when approved = false
    private String rejectionReason;
}