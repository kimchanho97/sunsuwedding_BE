package study.sunsuwedding.domain.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import study.sunsuwedding.domain.payment.client.PaymentApprovalClient;
import study.sunsuwedding.domain.payment.constant.PaymentConst;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.dto.PaymentSaveResponse;
import study.sunsuwedding.domain.payment.dto.TossPaymentResponse;
import study.sunsuwedding.domain.payment.entity.Payment;
import study.sunsuwedding.domain.payment.exception.PaymentException;
import study.sunsuwedding.domain.payment.repository.PaymentRepository;
import study.sunsuwedding.domain.user.entity.Couple;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentServiceImpl 테스트")
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentApprovalClient approvalClient;

    @Mock
    private PaymentProcessingService processingService;

    @Mock
    private PaymentFailureLogService failureLogService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Long testUserId;
    private PaymentApproveRequest testApproveRequest;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        testApproveRequest = new PaymentApproveRequest(
                "ORDER_20240629_001",
                "test_payment_key",
                50000L
        );
    }

    @Nested
    @DisplayName("결제 정보 저장")
    class SaveTests {

        @Test
        @DisplayName("정상적인 사용자 ID로 결제 정보 저장이 성공한다")
        void savePaymentSuccessfully() {
            // given
            User testUser = createTestUser(false);
            given(userRepository.findById(testUserId)).willReturn(Optional.of(testUser));
            given(paymentRepository.save(any(Payment.class))).willAnswer(invocation -> invocation.getArgument(0));

            // when
            PaymentSaveResponse response = paymentService.save(testUserId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getAmount()).isEqualTo(PaymentConst.SUNSU_MEMBERSHIP_PRICE);
            assertThat(response.getOrderId()).isNotNull();
            verify(paymentRepository).save(any(Payment.class));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 저장 시 UserException이 발생한다")
        void throwUserExceptionWhenUserNotFound() {
            // given
            given(userRepository.findById(testUserId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentService.save(testUserId))
                    .isInstanceOf(UserException.class);
            verify(paymentRepository, never()).save(any(Payment.class));
        }

        @Test
        @DisplayName("이미 프리미엄 사용자일 때 PaymentException이 발생한다")
        void throwPaymentExceptionWhenAlreadyPremium() {
            // given
            User premiumUser = createTestUser(true);
            given(userRepository.findById(testUserId)).willReturn(Optional.of(premiumUser));

            // when & then
            assertThatThrownBy(() -> paymentService.save(testUserId))
                    .isInstanceOf(PaymentException.class);
            verify(paymentRepository, never()).save(any(Payment.class));
        }
    }

    @Nested
    @DisplayName("결제 승인")
    class ApprovePaymentTests {

        @Test
        @DisplayName("정상적인 결제 승인이 성공한다")
        void approvePaymentSuccessfully() {
            // given
            TossPaymentResponse successResponse = mock(TossPaymentResponse.class);
            given(approvalClient.approve(testApproveRequest)).willReturn(successResponse);

            // when
            paymentService.approvePayment(testUserId, testApproveRequest);

            // then
            verify(processingService).validateForApproval(testUserId, testApproveRequest);
            verify(approvalClient).approve(testApproveRequest);
            verify(processingService).applyApproval(testUserId, successResponse);
        }

        @Test
        @DisplayName("검증 실패 시 외부 승인 요청을 하지 않는다")
        void skipApprovalWhenValidationFails() {
            // given
            doThrow(PaymentException.paymentFailed()).when(processingService)
                    .validateForApproval(testUserId, testApproveRequest);

            // when & then
            assertThatThrownBy(() -> paymentService.approvePayment(testUserId, testApproveRequest))
                    .isInstanceOf(PaymentException.class);

            verify(processingService).validateForApproval(testUserId, testApproveRequest);
            verify(approvalClient, never()).approve(any());
            verify(processingService, never()).applyApproval(any(), any());
        }

        @Test
        @DisplayName("타임아웃이 아닌 결제 실패 시 즉시 예외가 발생한다")
        void throwExceptionImmediatelyForNonTimeoutFailure() {
            // given
            PaymentException nonTimeoutException = PaymentException.paymentFailed();
            given(approvalClient.approve(testApproveRequest)).willThrow(nonTimeoutException);

            // when & then
            assertThatThrownBy(() -> paymentService.approvePayment(testUserId, testApproveRequest))
                    .isInstanceOf(PaymentException.class);

            verify(processingService).validateForApproval(testUserId, testApproveRequest);
            verify(approvalClient).approve(testApproveRequest);
            verify(approvalClient, never()).getPaymentResponseByOrderId(any());
            verify(processingService, never()).applyApproval(any(), any());
        }

        @Test
        @DisplayName("타임아웃 발생 후 재조회에서 완료 상태이면 성공 처리한다")
        void handleTimeoutWithSuccessfulRetry() {
            // given
            PaymentException timeoutException = PaymentException.paymentTimeout();
            TossPaymentResponse doneResponse = mock(TossPaymentResponse.class);
            given(doneResponse.isDone()).willReturn(true);

            given(approvalClient.approve(testApproveRequest)).willThrow(timeoutException);
            given(approvalClient.getPaymentResponseByOrderId(testApproveRequest.getOrderId()))
                    .willReturn(doneResponse);

            // when
            paymentService.approvePayment(testUserId, testApproveRequest);

            // then
            verify(processingService).validateForApproval(testUserId, testApproveRequest);
            verify(approvalClient).approve(testApproveRequest);
            verify(approvalClient).getPaymentResponseByOrderId(testApproveRequest.getOrderId());
            verify(processingService).applyApproval(testUserId, doneResponse);
        }

        @Test
        @DisplayName("타임아웃 발생 후 재조회에서 미완료 상태이면 실패 처리한다")
        void handleTimeoutWithIncompleteStatus() {
            // given
            PaymentException timeoutException = PaymentException.paymentTimeout();
            TossPaymentResponse pendingResponse = mock(TossPaymentResponse.class);
            given(pendingResponse.isDone()).willReturn(false);

            given(approvalClient.approve(testApproveRequest)).willThrow(timeoutException);
            given(approvalClient.getPaymentResponseByOrderId(testApproveRequest.getOrderId()))
                    .willReturn(pendingResponse);

            // when & then
            assertThatThrownBy(() -> paymentService.approvePayment(testUserId, testApproveRequest))
                    .isInstanceOf(PaymentException.class);

            verify(processingService).validateForApproval(testUserId, testApproveRequest);
            verify(approvalClient).approve(testApproveRequest);
            verify(approvalClient).getPaymentResponseByOrderId(testApproveRequest.getOrderId());
            verify(processingService, never()).applyApproval(any(), any());
        }

        @Test
        @DisplayName("타임아웃 발생 후 재조회에서 예외 발생 시 불확실 상태로 처리한다")
        void handleTimeoutWithRetryException() {
            // given
            PaymentException timeoutException = PaymentException.paymentTimeout();
            RuntimeException retryException = new RuntimeException("네트워크 오류");

            given(approvalClient.approve(testApproveRequest)).willThrow(timeoutException);
            given(approvalClient.getPaymentResponseByOrderId(testApproveRequest.getOrderId()))
                    .willThrow(retryException);

            // when & then
            assertThatThrownBy(() -> paymentService.approvePayment(testUserId, testApproveRequest))
                    .isInstanceOf(PaymentException.class);

            verify(processingService).validateForApproval(testUserId, testApproveRequest);
            verify(approvalClient).approve(testApproveRequest);
            verify(approvalClient).getPaymentResponseByOrderId(testApproveRequest.getOrderId());
            verify(failureLogService).recordNetworkFailure(testUserId, testApproveRequest, retryException);
            verify(processingService, never()).applyApproval(any(), any());
        }

        @Test
        @DisplayName("승인 성공 후 내부 처리 실패 시 지연 완료로 처리한다")
        void handleSuccessWithInternalProcessingFailure() {
            // given
            TossPaymentResponse successResponse = mock(TossPaymentResponse.class);
            RuntimeException internalException = new RuntimeException("DB 처리 오류");

            given(approvalClient.approve(testApproveRequest)).willReturn(successResponse);
            doThrow(internalException).when(processingService)
                    .applyApproval(testUserId, successResponse);

            // when & then
            assertThatThrownBy(() -> paymentService.approvePayment(testUserId, testApproveRequest))
                    .isInstanceOf(PaymentException.class);

            verify(processingService).validateForApproval(testUserId, testApproveRequest);
            verify(approvalClient).approve(testApproveRequest);
            verify(processingService).applyApproval(testUserId, successResponse);
            verify(failureLogService).recordDbWriteFailure(testUserId, successResponse, internalException);
        }
    }

    // 테스트 픽스처 메서드들
    private User createTestUser(boolean isPremium) {
        Couple user = new Couple("testCouple", "couple@example.com", "password123");
        if (isPremium) {
            user.upgrade();
        }
        return user;
    }
}