package study.sunsuwedding.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.sunsuwedding.domain.payment.entity.Payment;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(String orderId);

}
