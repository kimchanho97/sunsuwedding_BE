package study.sunsuwedding.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import study.sunsuwedding.domain.payment.entity.PaymentFailureLog;

import java.util.List;

public interface PaymentFailureLogRepository extends JpaRepository<PaymentFailureLog, Long> {

    @Query("SELECT l FROM PaymentFailureLog l WHERE l.recovered = false")
    List<PaymentFailureLog> findUnrecovered();

}
