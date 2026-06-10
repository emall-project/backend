//package store.emall.backend.interaction.interaction;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.web.bind.annotation.*;
//import store.emall.backend.interaction.analytics.ProductInteractionStatsService;
//import store.emall.backend.interaction.analytics.UserInteractionQueryService;
//
//import java.time.Instant;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/interactions")
//@RequiredArgsConstructor
//public class InteractionEventController {
//
//    private final InteractionEventRepository interactionEventRepository;
//    private final UserInteractionQueryService userInteractionQueryService;
//    private final ProductInteractionStatsService productInteractionStatsService;
//
//    @GetMapping
//    public List<InteractionEvent> getInteractions(
//            @RequestParam(required = false) String user,
//            @RequestParam(required = false) Long productId,
//            @RequestParam(required = false) InteractionType interactionType,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
//    ) {
//        if (user != null && interactionType != null) {
//            return userInteractionQueryService.getUserInteractionsByType(user, interactionType);
//        }
//
//        if (user != null && from != null && to != null) {
//            return userInteractionQueryService.getUserInteractionsByDateRange(user, from, to);
//        }
//
//        if (user != null) {
//            return userInteractionQueryService.getUserInteractions(user);
//        }
//
//        if (productId != null && from != null && to != null) {
//            return interactionEventRepository.findByProductIdAndOccurredAtBetweenOrderByOccurredAtDesc(productId, from, to);
//        }
//
//        if (productId != null) {
//            return interactionEventRepository.findByProductIdOrderByOccurredAtDesc(productId);
//        }
//
//        if (interactionType != null) {
//            return interactionEventRepository.findByEventTypeOrderByOccurredAtDesc(interactionType);
//        }
//
//        if (from != null && to != null) {
//            return interactionEventRepository.findByOccurredAtBetweenOrderByOccurredAtDesc(from, to);
//        }
//
//        return interactionEventRepository.findAll();
//    }
//
//    @GetMapping("/users/{user}")
//    public List<InteractionEvent> getUserInteractions(@PathVariable String user) {
//        return userInteractionQueryService.getUserInteractions(user);
//    }
//
//    @GetMapping("/stats/products/{productId}")
//    public Map<String, Object> getProductStats(
//            @PathVariable Long productId,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
//    ) {
//        long totalViews = (from != null && to != null)
//                ? productInteractionStatsService.getProductViewsByDateRange(productId, from, to)
//                : productInteractionStatsService.getTotalProductViews(productId);
//
//        return Map.of(
//                "productId", productId,
//                "totalViews", totalViews,
//                "from", from,
//                "to", to
//        );
//    }
//}