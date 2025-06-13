package study.sunsuwedding.domain.payment.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import study.sunsuwedding.domain.user.entity.Couple;
import study.sunsuwedding.domain.user.entity.User;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {

    @Test
    @DisplayName("결제 엔티티 생성 테스트")
    void createPayment_validUserAndAmount_createsPaymentEntity() {
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
    void markAsApproved_validPaymentKey_setsPaymentKeyAndTimestamp() {
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
    @DisplayName("결제 금액 일치 여부 확인 테스트 - 일치")
    void matches_sameAmount_returnsTrue() {
        // given
        User user = new Couple("test", "email", "password");
        Payment payment = new Payment(user, "order-123", 50000L);
        Long amountToCompare = 50000L;

        // when
        boolean result = payment.matches(amountToCompare);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("결제 금액 일치 여부 확인 테스트 - 불일치")
    void matches_differentAmount_returnsFalse() {
        // given
        User user = new Couple("test", "email", "password");
        Payment payment = new Payment(user, "order-123", 50000L);
        Long amountToCompare = 60000L;

        // when
        boolean result = payment.matches(amountToCompare);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("결제 승인 상태 확인 테스트 - 승인됨")
    void isApproved_approvedPayment_returnsTrue() {
        // given
        User user = new Couple("test", "email", "password");
        Payment payment = new Payment(user, "order-123", 50000L);
        payment.markAsApproved("pay-key-123");

        // when
        boolean result = payment.isApproved();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("결제 승인 상태 확인 테스트 - 미승인 (초기 상태)")
    void isApproved_initialState_returnsFalse() {
        // given
        User user = new Couple("test", "email", "password");
        Payment payment = new Payment(user, "order-123", 50000L);

        // when
        boolean result = payment.isApproved();

        // then
        assertThat(result).isFalse();
    }
}
