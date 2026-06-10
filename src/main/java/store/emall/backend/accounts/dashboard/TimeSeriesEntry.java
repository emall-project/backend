package store.emall.backend.accounts.dashboard;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesEntry {
    private String period;
    private long value;
}