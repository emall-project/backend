package store.emall.backend.accounts.dashboard.customer;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDashboardDto {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String gender;
    private Integer age;
    private String lastLoginAt;
    private boolean profileComplete;
    private int profileCompletionPercent;

    // Platform overview
    private long totalActiveMalls;
    private long totalActiveShops;
    private long totalActiveCities;
}
