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

    @Column(nullable = false, unique = true)
    private String orderId;
    private String paymentKey;
    private Long userId;
    private Long amount;

    @Enumerated(EnumType.STRING)
    private FailureType failureType;

    @Column(columnDefinition = "TEXT")
    private String errorDetail;
    private Boolean recovered;

    public PaymentFailureLog(String orderId, String paymentKey, Long userId, Long amount, FailureType failureType, String errorDetail) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.userId = userId;
        this.amount = amount;
        this.failureType = failureType;
        this.errorDetail = errorDetail;
        this.recovered = false;
    }

    public void markAsRecovered() {
        this.recovered = true;
    }

    public enum FailureType {
        NETWORK_UNCERTAIN,          // 네트워크 불확실
        DB_WRITE_FAILED,           // DB 쓰기 실패
    }
}
