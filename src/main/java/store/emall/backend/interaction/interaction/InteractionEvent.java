package store.emall.backend.interaction.interaction;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "interaction_events",
        indexes = {
                @Index(name = "idx_interaction_events_user_id", columnList = "user_id"),
                @Index(name = "idx_interaction_events_product_id", columnList = "product_id"),
                @Index(name = "idx_interaction_events_campaign_id", columnList = "campaign_id"),
                @Index(name = "idx_interaction_events_order_id", columnList = "order_id"),
                @Index(name = "idx_interaction_events_event_type", columnList = "event_type"),
                @Index(name = "idx_interaction_events_occurred_at", columnList = "occurred_at")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteractionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "occurred_by", nullable = false)
    private String user;

    @Column(name = "entity_id")
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 64)
    private InteractionType eventType;

    @Column(name = "source_service", nullable = false, length = 100)
    private String sourceService;

    @Column(name = "routing_key", nullable = false, length = 150)
    private String routingKey;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

//    @Lob
    @Column(name = "metadata")
    private String metadata;
}