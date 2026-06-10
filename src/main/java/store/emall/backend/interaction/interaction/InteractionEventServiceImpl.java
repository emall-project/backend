package store.emall.backend.interaction.interaction;

import store.emall.backend.interaction.interaction.ingestion.catalog.CatalogEvent;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class InteractionEventServiceImpl implements InteractionEventService {

    private final InteractionEventRepository interactionEventRepository;
    private final ObjectMapper objectMapper;


    @Override
    public InteractionEvent storeCatalogEvent(CatalogEvent event, String routingKey) {
        InteractionEvent interactionEvent = InteractionEvent.builder()
                .user(event.user())
                .entityId(event.entityId())
                .eventType(event.interactionType())
                .sourceService(defaultSource(event.source(), "catalog-service"))
                .routingKey(routingKey)
                .occurredAt(event.occurredAt())
                .correlationId(event.correlationId())
                .metadata(toJson(Map.of(
                        "source", event.source()
                )))
                .build();

        return interactionEventRepository.save(interactionEvent);
    }



    private String defaultSource(String source, String fallback) {
        return source == null || source.isBlank() ? fallback : source;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Failed to serialize interaction metadata", exception);
        }
    }
}