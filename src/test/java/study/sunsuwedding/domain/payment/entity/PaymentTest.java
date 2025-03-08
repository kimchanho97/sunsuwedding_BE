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
    @DisplayName("결제 정보 업데이트 테스트")
    void updatePayment() {
        // given
        User user = new Couple("test", "email", "password");
        Payment payment = new Payment(user, "order-123", 50000L);
        String newOrderId = "order-456";
        Long newAmount = 70000L;

        // when
        payment.update(newOrderId, newAmount);

        // then
        assertThat(payment.getOrderId()).isEqualTo(newOrderId);
        assertThat(payment.getPaidAmount()).isEqualTo(newAmount);
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

    @Test
    @DisplayName("결제 정보 일치 여부 확인 테스트")
    void matches() {
        // given
        User user = new Couple("test", "email", "password");
        Payment payment = new Payment(user, "order-123", 50000L);

        // when & then
        assertThat(payment.matches("order-123", 50000L)).isTrue();
        assertThat(payment.matches("order-999", 50000L)).isFalse();
        assertThat(payment.matches("order-123", 10000L)).isFalse();
    }
}
