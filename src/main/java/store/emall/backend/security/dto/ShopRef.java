package store.emall.backend.security.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ShopRef {
    private Long shopId;
    private Long mallId;
}