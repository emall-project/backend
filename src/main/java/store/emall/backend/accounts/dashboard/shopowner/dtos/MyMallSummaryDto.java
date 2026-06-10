package store.emall.backend.accounts.dashboard.shopowner.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyMallSummaryDto {
    private Long mallId;
    private String mallName;
    private String cityName;
    private String mallStatus;
    private long myShopsInThisMall;
    private long totalShopsInMall;
    private long restaurantsInMall;
    private long servicesInMall;
}
