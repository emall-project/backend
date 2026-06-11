package store.emall.backend.interaction.analytics.job;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import store.emall.backend.common.base.EMallsBaseEntity;

import java.time.Instant;

@Entity
@Table(name = "job_execution_records", schema = "interaction")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobExecutionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_type", nullable = false, length = 100)
    private String jobType;

    @Column(name = "entity_type", length = 100)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "source_service", length = 100)
    private String sourceService;

    @Column(name = "routing_key", length = 150)
    private String routingKey;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private JobExecutionStatus status;

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
