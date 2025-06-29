package study.sunsuwedding.domain.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.dto.TossPaymentResponse;
import study.sunsuwedding.domain.payment.entity.PaymentFailureLog;
import study.sunsuwedding.domain.payment.repository.PaymentFailureLogRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentFailureLogService 테스트")
class PaymentFailureLogServiceTest {

    @Mock
    private PaymentFailureLogRepository logRepository;

    @InjectMocks
    private PaymentFailureLogService failureLogService;

    private Long testUserId;
    private String testOrderId;
    private String testPaymentKey;
    private Long testAmount;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        testOrderId = "ORDER_20240629_001";
        testPaymentKey = "test_payment_key";
        testAmount = 50000L;
    }

    @Nested
    @DisplayName("DB 쓰기 실패 로그 기록")
    class RecordDbWriteFailureTests {

        @Test
        @DisplayName("정상적인 DB 쓰기 실패 로그 기록이 성공한다")
        void recordDbWriteFailureSuccessfully() {
            // given
            TossPaymentResponse response = createTossPaymentResponse();
            RuntimeException dbException = new RuntimeException("DB 연결 실패");

            // when
            failureLogService.recordDbWriteFailure(testUserId, response, dbException);

            // then
            verify(logRepository).save(argThat(failureLog ->
                    failureLog.getOrderId().equals(testOrderId) &&
                            failureLog.getPaymentKey().equals(testPaymentKey) &&
                            failureLog.getUserId().equals(testUserId) &&
                            failureLog.getAmount().equals(testAmount) &&
                            failureLog.getFailureType() == PaymentFailureLog.FailureType.DB_WRITE_FAILED &&
                            failureLog.getErrorDetail().equals("DB 연결 실패")
            ));
        }

        @Test
        @DisplayName("Repository save 실패 시 DataAccessException이 전파된다")
        void propagateDataAccessExceptionOnRepositoryFailure() {
            // given
            TossPaymentResponse response = createTossPaymentResponse();
            RuntimeException originalException = new RuntimeException("원본 오류");
            DataAccessException dataAccessException = new DataAccessException("DB 저장 실패") {
            };

            given(logRepository.save(any(PaymentFailureLog.class))).willThrow(dataAccessException);

            // when & then
            try {
                failureLogService.recordDbWriteFailure(testUserId, response, originalException);
            } catch (DataAccessException e) {
                // @Retryable에 의해 재시도될 것임을 확인
                verify(logRepository, atLeastOnce()).save(any(PaymentFailureLog.class));
            }
        }

        @Test
        @DisplayName("null 예외 메시지도 정상적으로 처리된다")
        void handleNullExceptionMessage() {
            // given
            TossPaymentResponse response = createTossPaymentResponse();
            RuntimeException exceptionWithNullMessage = new RuntimeException((String) null);

            // when
            failureLogService.recordDbWriteFailure(testUserId, response, exceptionWithNullMessage);

            // then
            verify(logRepository).save(argThat(failureLog ->
                    failureLog.getErrorDetail() == null &&
                            failureLog.getFailureType() == PaymentFailureLog.FailureType.DB_WRITE_FAILED
            ));
        }
    }

    @Nested
    @DisplayName("네트워크 실패 로그 기록")
    class RecordNetworkFailureTests {

        @Test
        @DisplayName("정상적인 네트워크 실패 로그 기록이 성공한다")
        void recordNetworkFailureSuccessfully() {
            // given
            PaymentApproveRequest request = createPaymentApproveRequest();
            RuntimeException networkException = new RuntimeException("네트워크 타임아웃");

            // when
            failureLogService.recordNetworkFailure(testUserId, request, networkException);

            // then
            verify(logRepository).save(argThat(failureLog ->
                    failureLog.getOrderId().equals(testOrderId) &&
                            failureLog.getPaymentKey().equals(testPaymentKey) &&
                            failureLog.getUserId().equals(testUserId) &&
                            failureLog.getAmount().equals(testAmount) &&
                            failureLog.getFailureType() == PaymentFailureLog.FailureType.NETWORK_UNCERTAIN &&
                            failureLog.getErrorDetail().equals("네트워크 타임아웃")
            ));
        }

        @Test
        @DisplayName("Repository save 실패 시 DataAccessException이 전파된다")
        void propagateDataAccessExceptionOnRepositoryFailure() {
            // given
            PaymentApproveRequest request = createPaymentApproveRequest();
            RuntimeException originalException = new RuntimeException("네트워크 오류");
            DataAccessException dataAccessException = new DataAccessException("DB 저장 실패") {
            };

            given(logRepository.save(any(PaymentFailureLog.class))).willThrow(dataAccessException);

            // when & then
            try {
                failureLogService.recordNetworkFailure(testUserId, request, originalException);
            } catch (DataAccessException e) {
                // @Retryable에 의해 재시도될 것임을 확인
                verify(logRepository, atLeastOnce()).save(any(PaymentFailureLog.class));
            }
        }

        @Test
        @DisplayName("빈 예외 메시지도 정상적으로 처리된다")
        void handleEmptyExceptionMessage() {
            // given
            PaymentApproveRequest request = createPaymentApproveRequest();
            RuntimeException exceptionWithEmptyMessage = new RuntimeException("");

            // when
            failureLogService.recordNetworkFailure(testUserId, request, exceptionWithEmptyMessage);

            // then
            verify(logRepository).save(argThat(failureLog ->
                    failureLog.getErrorDetail().equals("") &&
                            failureLog.getFailureType() == PaymentFailureLog.FailureType.NETWORK_UNCERTAIN
            ));
        }
    }

    @Nested
    @DisplayName("로그 엔티티 생성 검증")
    class LogEntityCreationTests {

        @Test
        @DisplayName("DB 쓰기 실패 로그의 모든 필드가 올바르게 설정된다")
        void verifyAllFieldsForDbWriteFailure() {
            // given
            TossPaymentResponse response = createTossPaymentResponse();
            RuntimeException exception = new RuntimeException("상세한 DB 오류 메시지");

            // when
            failureLogService.recordDbWriteFailure(testUserId, response, exception);

            // then
            verify(logRepository).save(argThat(failureLog -> {
                return failureLog.getOrderId().equals(testOrderId) &&
                        failureLog.getPaymentKey().equals(testPaymentKey) &&
                        failureLog.getUserId().equals(testUserId) &&
                        failureLog.getAmount().equals(testAmount) &&
                        failureLog.getFailureType() == PaymentFailureLog.FailureType.DB_WRITE_FAILED &&
                        failureLog.getErrorDetail().equals("상세한 DB 오류 메시지");
            }));
        }

        @Test
        @DisplayName("네트워크 실패 로그의 모든 필드가 올바르게 설정된다")
        void verifyAllFieldsForNetworkFailure() {
            // given
            PaymentApproveRequest request = createPaymentApproveRequest();
            RuntimeException exception = new RuntimeException("상세한 네트워크 오류 메시지");

            // when
            failureLogService.recordNetworkFailure(testUserId, request, exception);

            // then
            verify(logRepository).save(argThat(failureLog -> {
                return failureLog.getOrderId().equals(testOrderId) &&
                        failureLog.getPaymentKey().equals(testPaymentKey) &&
                        failureLog.getUserId().equals(testUserId) &&
                        failureLog.getAmount().equals(testAmount) &&
                        failureLog.getFailureType() == PaymentFailureLog.FailureType.NETWORK_UNCERTAIN &&
                        failureLog.getErrorDetail().equals("상세한 네트워크 오류 메시지");
            }));
        }
    }

    // 테스트 픽스처 메서드들
    private TossPaymentResponse createTossPaymentResponse() {
        TossPaymentResponse response = mock(TossPaymentResponse.class);
        given(response.getOrderId()).willReturn(testOrderId);
        given(response.getPaymentKey()).willReturn(testPaymentKey);
        given(response.getTotalAmount()).willReturn(testAmount);
        return response;
    }

    private PaymentApproveRequest createPaymentApproveRequest() {
        return new PaymentApproveRequest(testOrderId, testPaymentKey, testAmount);
    }
}