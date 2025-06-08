package study.sunsuwedding.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.sunsuwedding.domain.payment.entity.PaymentFailureLog;

import java.util.List;
import java.util.Optional;

public interface PaymentFailureLogRepository extends JpaRepository<PaymentFailureLog, Long> {

    @Query("select l from PaymentFailureLog l where l.failureType = :failureType and l.recovered = false")
    List<PaymentFailureLog> findByFailureTypeAndNotRecovered(@Param("failureType") PaymentFailureLog.FailureType failureType);

    Optional<PaymentFailureLog> findPaymentFailureLogByOrderId(String orderId);
}
