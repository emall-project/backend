package store.emall.backend.campaigns.subscription.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPaymentRepository
        extends JpaRepository<SubscriptionPayment, Long> {

    List<SubscriptionPayment> findBySubscription_SubscriptionIdOrderByPaymentDateDesc(Long subscriptionId);

    Optional<SubscriptionPayment> findByTransactionId(String transactionId);

    boolean existsByTransactionId(String transactionId);

    List<SubscriptionPayment> findBySubscription_ShopIdOrderByPaymentDateDesc(Long shopId);

    List<SubscriptionPayment> findAllByOrderByPaymentDateDesc();

}