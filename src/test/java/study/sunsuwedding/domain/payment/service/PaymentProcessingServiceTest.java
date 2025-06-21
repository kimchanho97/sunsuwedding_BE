package study.sunsuwedding.domain.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class PaymentProcessingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentProcessingService paymentProcessingService;

    private Long userId;
    private String orderId;
    private Long amount;
    private PaymentApproveRequest request;

    @BeforeEach
    void setUp() {
        userId = 1L;
        orderId = "test-order-id";
        amount = 50000L;
        request = new PaymentApproveRequest(orderId, "test-payment-key", amount);
    }

    @Test
    @DisplayName("결제 승인 검증 성공")
    void validateForApproval_success() {
        // given
        User user = mock(User.class);
        Payment payment = mock(Payment.class);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(user.isPremium()).willReturn(false);

        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.of(payment));
        given(payment.matches(amount)).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> paymentProcessingService.validateForApproval(userId, request));
    }

    @Test
    @DisplayName("결제 승인 검증 실패: 사용자를 찾을 수 없음")
    void validateForApproval_fail_user_not_found() {
        // given
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentProcessingService.validateForApproval(userId, request))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("결제 승인 검증 실패: 이미 프리미엄 사용자")
    void validateForApproval_fail_already_premium() {
        // given
        User user = mock(User.class);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(user.isPremium()).willReturn(true);

        // when & then
        assertThatThrownBy(() -> paymentProcessingService.validateForApproval(userId, request))
                .isInstanceOf(PaymentException.class);
    }

    @Test
    @DisplayName("결제 승인 검증 실패: 결제 정보를 찾을 수 없음")
    void validateForApproval_fail_payment_not_found() {
        // given
        User user = mock(User.class);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentProcessingService.validateForApproval(userId, request))
                .isInstanceOf(PaymentException.class);
    }

    @Test
    @DisplayName("결제 승인 검증 실패: 금액 불일치")
    void validateForApproval_fail_payment_mismatch() {
        // given
        User user = mock(User.class);
        Payment payment = mock(Payment.class);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.of(payment));
        given(payment.matches(amount)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> paymentProcessingService.validateForApproval(userId, request))
                .isInstanceOf(PaymentException.class);
    }

    @Test
    @DisplayName("결제 승인 검증 실패: 이미 처리된 결제")
    void validateForApproval_fail_already_approved() {
        // given
        User user = mock(User.class);
        Payment payment = mock(Payment.class);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.of(payment));
        given(payment.getPaymentKey()).willReturn("existing-payment-key");
        given(payment.getPaidAt()).willReturn(LocalDateTime.now());

        // when & then
        assertThatThrownBy(() -> paymentProcessingService.validateForApproval(userId, request))
                .isInstanceOf(PaymentException.class);
    }

    @Test
    @DisplayName("결제 승인 적용 성공")
    void applyApproval_success() {
        // given
        String paymentKey = "test-payment-key";
        TossPaymentResponse response = mock(TossPaymentResponse.class);
        User user = mock(User.class);
        Payment payment = mock(Payment.class);

        given(response.getOrderId()).willReturn(orderId);
        given(response.getPaymentKey()).willReturn(paymentKey);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(user.isPremium()).willReturn(false);
        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.of(payment));

        // when
        paymentProcessingService.applyApproval(userId, response);

        // then
        verify(user).upgrade();
        verify(payment).markAsApproved(paymentKey);
    }

    @Test
    @DisplayName("결제 승인 적용 실패: 사용자를 찾을 수 없음")
    void applyApproval_fail_user_not_found() {
        // given
        TossPaymentResponse response = mock(TossPaymentResponse.class);
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentProcessingService.applyApproval(userId, response))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("결제 승인 적용 실패: 이미 프리미엄 사용자")
    void applyApproval_fail_already_premium() {
        // given
        TossPaymentResponse response = mock(TossPaymentResponse.class);
        User user = mock(User.class);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(user.isPremium()).willReturn(true);

        // when & then
        assertThatThrownBy(() -> paymentProcessingService.applyApproval(userId, response))
                .isInstanceOf(PaymentException.class);
    }

    @Test
    @DisplayName("결제 승인 적용 실패: 결제 정보를 찾을 수 없음")
    void applyApproval_fail_payment_not_found() {
        // given
        TossPaymentResponse response = mock(TossPaymentResponse.class);
        User user = mock(User.class);

        given(response.getOrderId()).willReturn(orderId);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(user.isPremium()).willReturn(false);
        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentProcessingService.applyApproval(userId, response))
                .isInstanceOf(PaymentException.class);
    }
}