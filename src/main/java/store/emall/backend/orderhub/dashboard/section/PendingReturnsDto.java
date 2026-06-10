package store.emall.backend.orderhub.dashboard.section;

import store.emall.backend.orderhub.returnrequest.ReturnRequestDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PendingReturnsDto {
    private List<ReturnRequestDto> returns; // all PENDING for this shop
}
