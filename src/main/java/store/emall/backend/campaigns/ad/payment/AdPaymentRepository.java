package store.emall.backend.campaigns.ad.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AdPaymentRepository extends JpaRepository<AdPayment, Long> {

    List<AdPayment> findByAdRequest_AdRequestIdOrderByPaymentDateDesc(Long adRequestId);

    Optional<AdPayment> findByStripePaymentIntentId(String stripePaymentIntentId);

    boolean existsByStripePaymentIntentId(String stripePaymentIntentId);

    List<AdPayment> findByAdRequest_ShopIdOrderByPaymentDateDesc(Long shopId);

    List<AdPayment> findAllByOrderByPaymentDateDesc();

}