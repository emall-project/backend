package store.emall.backend.common.base;

import jakarta.persistence.MappedSuperclass;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@MappedSuperclass
@SuperBuilder(toBuilder = true)
@Setter
@Getter
@NoArgsConstructor
public class EMallsBaseDto {
    @Null
    private LocalDateTime createdAt;
    @Null
    private String createdBy;
    @Null
    private LocalDateTime updatedAt;
    @Null
    private String updatedBy;
}
