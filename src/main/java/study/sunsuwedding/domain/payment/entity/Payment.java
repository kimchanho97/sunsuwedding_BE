package study.sunsuwedding.domain.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.sunsuwedding.common.entity.BaseTimeEntity;
import study.sunsuwedding.domain.user.entity.User;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    private User user;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Long payedAmount;

    private String paymentKey;
    private LocalDateTime payedAt;

    public Payment(User user, String orderId, Long payedAmount) {
        this.user = user;
        this.orderId = orderId;
        this.payedAmount = payedAmount;
    }

    public void update(String orderId, Long amount) {
        this.orderId = orderId;
        this.payedAmount = amount;
    }

    public void markAsApproved(String paymentKey) {
        this.paymentKey = paymentKey;
        this.payedAt = LocalDateTime.now();
    }

    public boolean matches(String orderId, Long amount) {
        return this.orderId.equals(orderId) && this.payedAmount.equals(amount);
    }
}
