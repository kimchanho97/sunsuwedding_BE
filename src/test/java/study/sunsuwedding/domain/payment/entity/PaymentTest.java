package study.sunsuwedding.domain.payment.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import study.sunsuwedding.domain.user.entity.Couple;
import study.sunsuwedding.domain.user.entity.User;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {

    @Test
    @DisplayName("결제 엔티티 생성 테스트")
    void createPayment() {
        // given
        User user = new Couple("test", "email", "password");
        String orderId = "order-123";
        Long amount = 50000L;

        // when
        Payment payment = new Payment(user, orderId, amount);

        // then
        assertThat(payment.getUser()).isEqualTo(user);
        assertThat(payment.getOrderId()).isEqualTo(orderId);
        assertThat(payment.getPaidAmount()).isEqualTo(amount);
    }

    @Test
    @DisplayName("결제 승인 테스트")
    void markAsApproved() {
        // given
        User user = new Couple("test", "email", "password");
        Payment payment = new Payment(user, "order-123", 50000L);
        String paymentKey = "pay-key-123";

        // when
        payment.markAsApproved(paymentKey);

        // then
        assertThat(payment.getPaymentKey()).isEqualTo(paymentKey);
        assertThat(payment.getPaidAt()).isNotNull();
    }
    
}
