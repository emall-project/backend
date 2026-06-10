package store.emall.backend.interaction.interaction.ingestion.catalog;

import jakarta.validation.constraints.NotNull;
import store.emall.backend.interaction.interaction.InteractionType;

import java.time.Instant;

public record CatalogEvent(
        @NotNull String user,
        @NotNull Long entityId,
        @NotNull  InteractionType interactionType,
        @NotNull Instant occurredAt,
        String source,
        String correlationId,
        String metadata

) {
}
