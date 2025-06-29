package study.sunsuwedding.domain.payment.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import study.sunsuwedding.domain.user.entity.Couple;
import study.sunsuwedding.domain.user.entity.User;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Payment 엔티티 테스트")
class PaymentTest {

    private User testUser;
    private String testOrderId;
    private Long testAmount;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testOrderId = "ORDER_20240629_001";
        testAmount = 50000L;
    }

    @Nested
    @DisplayName("Payment 생성")
    class PaymentCreationTests {

        @Test
        @DisplayName("정상적인 파라미터로 Payment 생성 시 모든 필드가 올바르게 설정된다")
        void createPaymentWithValidParameters() {
            // when
            Payment payment = new Payment(testUser, testOrderId, testAmount);

            // then
            assertThat(payment.getUser()).isEqualTo(testUser);
            assertThat(payment.getOrderId()).isEqualTo(testOrderId);
            assertThat(payment.getPaidAmount()).isEqualTo(testAmount);
            assertThat(payment.getPaymentKey()).isNull();
            assertThat(payment.getPaidAt()).isNull();
            assertThat(payment.isApproved()).isFalse();
        }

        @Test
        @DisplayName("최소 금액(1원)으로 Payment 생성이 가능하다")
        void createPaymentWithMinimumAmount() {
            // given
            Long minimumAmount = 1L;

            // when
            Payment payment = new Payment(testUser, testOrderId, minimumAmount);

            // then
            assertThat(payment.getPaidAmount()).isEqualTo(minimumAmount);
        }

        @Test
        @DisplayName("높은 금액으로 Payment 생성이 가능하다")
        void createPaymentWithLargeAmount() {
            // given
            Long largeAmount = 1_000_000L;

            // when
            Payment payment = new Payment(testUser, testOrderId, largeAmount);

            // then
            assertThat(payment.getPaidAmount()).isEqualTo(largeAmount);
        }
    }

    @Nested
    @DisplayName("결제 승인 처리")
    class PaymentApprovalTests {

        @Test
        @DisplayName("결제 승인 시 paymentKey와 paidAt이 설정된다")
        void markAsApproved() {
            // given
            Payment payment = createPayment();
            String paymentKey = "toss_payment_20240629_001";

            // when
            payment.markAsApproved(paymentKey);

            // then
            assertThat(payment.getPaymentKey()).isEqualTo(paymentKey);
            assertThat(payment.getPaidAt()).isNotNull();
            assertThat(payment.isApproved()).isTrue();
        }

        @Test
        @DisplayName("이미 승인된 결제를 다시 승인하면 새로운 정보로 업데이트된다")
        void markAsApprovedTwice() {
            // given
            Payment payment = createPayment();
            String originalPaymentKey = "original_payment_key";
            payment.markAsApproved(originalPaymentKey);
            var originalPaidAt = payment.getPaidAt();

            String newPaymentKey = "new_payment_key";

            // when
            payment.markAsApproved(newPaymentKey);

            // then
            assertThat(payment.getPaymentKey()).isEqualTo(newPaymentKey);
            assertThat(payment.getPaidAt()).isNotEqualTo(originalPaidAt);
        }
    }

    @Nested
    @DisplayName("금액 일치 검증")
    class AmountMatchingTests {

        @Test
        @DisplayName("동일한 금액일 때 true를 반환한다")
        void matchesWithSameAmount() {
            // given
            Payment payment = createPayment();

            // when
            boolean matches = payment.matches(testAmount);

            // then
            assertThat(matches).isTrue();
        }

        @Test
        @DisplayName("다른 금액일 때 false를 반환한다")
        void matchesWithDifferentAmount() {
            // given
            Payment payment = createPayment();
            Long differentAmount = testAmount + 1000L;

            // when
            boolean matches = payment.matches(differentAmount);

            // then
            assertThat(matches).isFalse();
        }

        @Test
        @DisplayName("null 금액과 비교 시 false를 반환한다")
        void matchesWithNullAmount() {
            // given
            Payment payment = createPayment();

            // when
            boolean matches = payment.matches(null);

            // then
            assertThat(matches).isFalse();
        }

        @Test
        @DisplayName("0원과의 비교가 정확히 동작한다")
        void matchesWithZeroAmount() {
            // given
            Payment payment = new Payment(testUser, testOrderId, 0L);

            // when & then
            assertThat(payment.matches(0L)).isTrue();
            assertThat(payment.matches(1L)).isFalse();
        }
    }

    @Nested
    @DisplayName("승인 상태 확인")
    class ApprovalStatusTests {

        @Test
        @DisplayName("승인된 결제는 승인 상태를 반환한다")
        void isApprovedAfterApproval() {
            // given
            Payment payment = createPayment();
            payment.markAsApproved("payment_key");

            // when
            boolean approved = payment.isApproved();

            // then
            assertThat(approved).isTrue();
        }

        @Test
        @DisplayName("미승인 결제는 비승인 상태를 반환한다")
        void isApprovedBeforeApproval() {
            // given
            Payment payment = createPayment();

            // when
            boolean approved = payment.isApproved();

            // then
            assertThat(approved).isFalse();
        }
    }

    private User createTestUser() {
        return new Couple("testCouple", "test@example.com", "password123");
    }

    private Payment createPayment() {
        return new Payment(testUser, testOrderId, testAmount);
    }
}
