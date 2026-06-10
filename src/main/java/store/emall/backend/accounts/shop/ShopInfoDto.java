package store.emall.backend.accounts.shop;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopInfoDto {
    private Long shopId;
    private String name;
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;
    private Boolean isActive;         // true only if subscription ACTIVE + adminStatus NONE
    private Boolean hasWriteAccess;   // true if subscription ACTIVE + not BLOCKED
    private String adminStatus;
}
