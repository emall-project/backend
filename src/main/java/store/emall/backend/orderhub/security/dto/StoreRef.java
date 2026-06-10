package store.emall.backend.orderhub.security.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class StoreRef {
    private Long storeId;
    private Long mallId;
}