package study.sunsuwedding.domain.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.sunsuwedding.common.entity.BaseTimeEntity;

@Entity
@Table(name = "payment_failure_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentFailureLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_failure_log_id")
    private Long id;
    private String orderId;
    private String paymentKey;
    private Long userId;
    private String reason;
    private boolean recovered;

    public PaymentFailureLog(String orderId, String paymentKey, Long userId, String reason) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.userId = userId;
        this.reason = reason;
    }

    public void markAsRecovered() {
        this.recovered = true;
    }
}
