package store.emall.backend.accounts.dashboard;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartEntry {
    private String label;
    private long value;
}
