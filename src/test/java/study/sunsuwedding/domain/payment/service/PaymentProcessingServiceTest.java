package study.sunsuwedding.domain.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.dto.TossPaymentResponse;
import study.sunsuwedding.domain.payment.entity.Payment;
import study.sunsuwedding.domain.payment.exception.PaymentException;
import study.sunsuwedding.domain.payment.repository.PaymentRepository;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentProcessingService 테스트")
class PaymentProcessingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentProcessingService paymentProcessingService;

    private Long testUserId;
    private String testOrderId;
    private Long testAmount;
    private PaymentApproveRequest testRequest;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        testOrderId = "ORDER_20240629_001";
        testAmount = 50000L;
        testRequest = new PaymentApproveRequest(testOrderId, "test_payment_key", testAmount);
    }

    @Nested
    @DisplayName("결제 승인 검증")
    class ValidateForApprovalTests {

        @Test
        @DisplayName("정상적인 결제 승인 검증이 성공한다")
        void validateForApprovalSuccessfully() {
            // given
            User user = createMockUser(false);
            Payment payment = createMockPayment();
            given(userRepository.findById(testUserId)).willReturn(Optional.of(user));
            given(paymentRepository.findByOrderId(testOrderId)).willReturn(Optional.of(payment));
            given(payment.matches(testAmount)).willReturn(true);

            // when & then
            assertDoesNotThrow(() -> paymentProcessingService.validateForApproval(testUserId, testRequest));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 검증 시 UserException이 발생한다")
        void throwUserExceptionWhenUserNotFound() {
            // given
            given(userRepository.findById(testUserId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentProcessingService.validateForApproval(testUserId, testRequest))
                    .isInstanceOf(UserException.class);
        }

        @Test
        @DisplayName("이미 프리미엄 사용자일 때 PaymentException이 발생한다")
        void throwPaymentExceptionWhenAlreadyPremium() {
            // given
            User premiumUser = createMockUser(true);
            given(userRepository.findById(testUserId)).willReturn(Optional.of(premiumUser));

            // when & then
            assertThatThrownBy(() -> paymentProcessingService.validateForApproval(testUserId, testRequest))
                    .isInstanceOf(PaymentException.class);
        }

        @Test
        @DisplayName("존재하지 않는 주문 ID로 검증 시 PaymentException이 발생한다")
        void throwPaymentExceptionWhenPaymentNotFound() {
            // given
            User user = createMockUser(false);
            given(userRepository.findById(testUserId)).willReturn(Optional.of(user));
            given(paymentRepository.findByOrderId(testOrderId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentProcessingService.validateForApproval(testUserId, testRequest))
                    .isInstanceOf(PaymentException.class);
        }

        @Test
        @DisplayName("결제 금액이 일치하지 않을 때 PaymentException이 발생한다")
        void throwPaymentExceptionWhenAmountMismatch() {
            // given
            User user = createMockUser(false);
            Payment payment = createMockPayment();
            given(userRepository.findById(testUserId)).willReturn(Optional.of(user));
            given(paymentRepository.findByOrderId(testOrderId)).willReturn(Optional.of(payment));
            given(payment.matches(testAmount)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> paymentProcessingService.validateForApproval(testUserId, testRequest))
                    .isInstanceOf(PaymentException.class);
        }

        @Test
        @DisplayName("이미 승인된 결제일 때 PaymentException이 발생한다")
        void throwPaymentExceptionWhenAlreadyApproved() {
            // given
            User user = createMockUser(false);
            Payment approvedPayment = createMockPayment();
            given(userRepository.findById(testUserId)).willReturn(Optional.of(user));
            given(paymentRepository.findByOrderId(testOrderId)).willReturn(Optional.of(approvedPayment));
            given(approvedPayment.getPaymentKey()).willReturn("existing_payment_key");
            given(approvedPayment.getPaidAt()).willReturn(LocalDateTime.now());

            // when & then
            assertThatThrownBy(() -> paymentProcessingService.validateForApproval(testUserId, testRequest))
                    .isInstanceOf(PaymentException.class);
        }
    }

    @Nested
    @DisplayName("결제 승인 적용")
    class ApplyApprovalTests {

        @Test
        @DisplayName("정상적인 결제 승인 적용이 성공한다")
        void applyApprovalSuccessfully() {
            // given
            String paymentKey = "toss_payment_20240629_001";
            TossPaymentResponse response = mock(TossPaymentResponse.class);
            given(response.getOrderId()).willReturn(testOrderId);
            given(response.getPaymentKey()).willReturn(paymentKey);

            User user = createMockUser(false);
            Payment payment = createMockPayment();

            given(userRepository.findById(testUserId)).willReturn(Optional.of(user));
            given(paymentRepository.findByOrderId(testOrderId)).willReturn(Optional.of(payment));

            // when
            paymentProcessingService.applyApproval(testUserId, response);

            // then
            verify(user).upgrade();
            verify(payment).markAsApproved(paymentKey);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 적용 시 UserException이 발생한다")
        void throwUserExceptionWhenUserNotFound() {
            // given
            TossPaymentResponse response = mock(TossPaymentResponse.class);
            given(userRepository.findById(testUserId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentProcessingService.applyApproval(testUserId, response))
                    .isInstanceOf(UserException.class);
        }

        @Test
        @DisplayName("이미 프리미엄 사용자일 때 PaymentException이 발생한다")
        void throwPaymentExceptionWhenAlreadyPremium() {
            // given
            TossPaymentResponse response = mock(TossPaymentResponse.class);
            User premiumUser = createMockUser(true);
            given(userRepository.findById(testUserId)).willReturn(Optional.of(premiumUser));

            // when & then
            assertThatThrownBy(() -> paymentProcessingService.applyApproval(testUserId, response))
                    .isInstanceOf(PaymentException.class);
        }

        @Test
        @DisplayName("존재하지 않는 주문 ID로 적용 시 PaymentException이 발생한다")
        void throwPaymentExceptionWhenPaymentNotFound() {
            // given
            TossPaymentResponse response = mock(TossPaymentResponse.class);
            given(response.getOrderId()).willReturn(testOrderId);

            User user = createMockUser(false);

            given(userRepository.findById(testUserId)).willReturn(Optional.of(user));
            given(paymentRepository.findByOrderId(testOrderId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentProcessingService.applyApproval(testUserId, response))
                    .isInstanceOf(PaymentException.class);
        }
    }

    // 테스트 픽스처 메서드들
    private User createMockUser(boolean isPremium) {
        User user = mock(User.class);
        given(user.isPremium()).willReturn(isPremium);
        return user;
    }

    private Payment createMockPayment() {
        return mock(Payment.class);
    }
}