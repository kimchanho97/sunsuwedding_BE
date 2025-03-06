package study.sunsuwedding.domain.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import study.sunsuwedding.domain.payment.client.PaymentApprovalClient;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.dto.PaymentSaveRequest;
import study.sunsuwedding.domain.payment.entity.Payment;
import study.sunsuwedding.domain.payment.exception.PaymentException;
import study.sunsuwedding.domain.payment.repository.PaymentRepository;
import study.sunsuwedding.domain.user.constant.Grade;
import study.sunsuwedding.domain.user.entity.Couple;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentApprovalClient paymentApprovalClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User user;
    private Payment payment;

    @BeforeEach
    void setUp() {
        user = new Couple("testUser", "test@example.com", "password123");
        payment = new Payment(user, "order-123", 50000L);
    }

    @Test
    @DisplayName("결제 정보 저장 테스트 - 새 결제 생성")
    void saveNewPayment() {
        // given
        PaymentSaveRequest request = new PaymentSaveRequest("order-123", 50000L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user)); // user 객체 반환
        when(paymentRepository.findByUserId(anyLong())).thenReturn(Optional.empty()); // 결제 정보 없음

        // when
        paymentService.save(1L, request);

        // then
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("결제 정보 저장 테스트 - 기존 결제 업데이트")
    void updateExistingPayment() {
        // given
        PaymentSaveRequest request = new PaymentSaveRequest("order-456", 70000L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(paymentRepository.findByUserId(anyLong())).thenReturn(Optional.of(payment)); // 결제 정보 존재

        // when
        paymentService.save(1L, request);

        // then
        assertThat(payment.getOrderId()).isEqualTo("order-456");
        assertThat(payment.getPayedAmount()).isEqualTo(70000L);
    }

    @Test
    @DisplayName("결제 승인 및 유저 업그레이드 테스트")
    void approvePaymentAndUpgradeUser() {
        // given
        PaymentApproveRequest request = new PaymentApproveRequest("order-123", "payment-key-123", 50000L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(paymentRepository.findByUserId(anyLong())).thenReturn(Optional.of(payment));
        doNothing().when(paymentApprovalClient).approve(request);

        // when
        paymentService.approvePaymentAndUpgradeUser(1L, request);

        // then
        assertThat(user.getGrade()).isEqualTo(Grade.PREMIUM);
        assertThat(payment.getPaymentKey()).isEqualTo("payment-key-123");
        assertThat(payment.getPayedAt()).isNotNull();
    }

    @Test
    @DisplayName("결제 승인 시 요청 정보 불일치 예외 테스트")
    void approvePaymentMismatchThrowsException() {
        // given
        PaymentApproveRequest request = new PaymentApproveRequest("wrong-order", "payment-key-123", 50000L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(paymentRepository.findByUserId(anyLong())).thenReturn(Optional.of(payment));

        // when & then
        assertThatThrownBy(() -> paymentService.approvePaymentAndUpgradeUser(1L, request))
                .isInstanceOf(PaymentException.class);
    }
}
