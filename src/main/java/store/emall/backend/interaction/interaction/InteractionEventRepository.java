package store.emall.backend.interaction.interaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface InteractionEventRepository extends JpaRepository<InteractionEvent, Long> {

    List<InteractionEvent> findByUserOrderByOccurredAtDesc(String user);

//    List<InteractionEvent> findByEventTypeOrderByOccurredAtDesc(InteractionType interactionType);

//    List<InteractionEvent> findByProductIdOrderByOccurredAtDesc(Long productId);

    List<InteractionEvent> findByUserAndEventTypeOrderByOccurredAtDesc(String user, InteractionType eventType);

//    List<InteractionEvent> findByOccurredAtBetweenOrderByOccurredAtDesc(Instant from, Instant to);

    List<InteractionEvent> findByUserAndOccurredAtBetweenOrderByOccurredAtDesc(String user, Instant from, Instant to);

//    List<InteractionEvent> findByProductIdAndOccurredAtBetweenOrderByOccurredAtDesc(Long productId, Instant from, Instant to);

//    long countByProductIdAndEventType(Long productId, InteractionType interactionType);
//
//    long countByEventTypeAndOccurredAtBetween(InteractionType interactionType, Instant from, Instant to);
//
//    long countByProductIdAndEventTypeAndOccurredAtBetween(Long productId, InteractionType interactionType, Instant from, Instant to);
}