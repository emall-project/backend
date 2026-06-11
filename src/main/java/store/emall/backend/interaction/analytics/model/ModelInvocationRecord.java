package store.emall.backend.interaction.analytics.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import store.emall.backend.common.base.EMallsBaseEntity;

import java.time.Instant;

@Entity
@Table(name = "model_invocation_records", schema = "interaction")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelInvocationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_execution_id")
    private Long jobExecutionId;

    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;

    @Column(name = "provider", nullable = false, length = 100)
    private String provider;

    @Column(name = "operation_name", nullable = false, length = 100)
    private String operationName;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ModelInvocationStatus status;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "metadata")
    private String metadata;
}
