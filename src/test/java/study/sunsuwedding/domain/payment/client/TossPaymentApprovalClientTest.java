package study.sunsuwedding.domain.payment.client;

import io.netty.handler.timeout.ReadTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.dto.TossPaymentResponse;
import study.sunsuwedding.domain.payment.exception.PaymentException;

import java.util.Base64;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TossPaymentApprovalClientTest {

    @Mock
    private WebClient webClient;

    // WebClient의 fluent API를 모의하기 위한 Mock 객체들
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    private TossPaymentApprovalClient tossPaymentApprovalClient;

    @BeforeEach
    void setUp() {
        tossPaymentApprovalClient = new TossPaymentApprovalClient(webClient, "test_secret_key");

        // POST 요청 체인 설정
        lenient().when(webClient.post()).thenReturn(requestBodyUriSpec);
        lenient().when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        lenient().when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);
        lenient().when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // GET 요청 체인 설정
        lenient().when(webClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.headers(any(Consumer.class))).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // onStatus는 항상 자기 자신을 반환하도록 설정
        lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    }

    @Nested
    @DisplayName("결제 승인 테스트")
    class ApprovePaymentTests {

        @Test
        @DisplayName("정상적인 결제 승인 요청이 성공한다")
        void shouldApprovePaymentSuccessfully() {
            // given
            PaymentApproveRequest request = PaymentApproveRequest.builder()
                    .paymentKey("test_payment_key")
                    .orderId("test_order_id")
                    .amount(10000L)
                    .build();

            TossPaymentResponse expectedResponse = TossPaymentResponse.builder()
                    .status("DONE")
                    .paymentKey("test_payment_key")
                    .orderId("test_order_id")
                    .totalAmount(10000L)
                    .build();

            when(responseSpec.bodyToMono(TossPaymentResponse.class))
                    .thenReturn(Mono.just(expectedResponse));

            // when
            TossPaymentResponse actualResponse = tossPaymentApprovalClient.approve(request);

            // then
            assertThat(actualResponse)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedResponse);

            // WebClient 호출 검증
            verify(webClient).post();
            verify(requestBodyUriSpec).uri("/v1/payments/confirm");
            verify(requestBodySpec).headers(any(Consumer.class));
            verify(requestBodySpec).bodyValue(argThat(body -> {
                Map<String, Object> requestBody = (Map<String, Object>) body;
                return "test_payment_key".equals(requestBody.get("paymentKey")) &&
                        "test_order_id".equals(requestBody.get("orderId")) &&
                        10000L == (long) requestBody.get("amount");
            }));
        }

        @Test
        @DisplayName("ReadTimeoutException 발생 시 PaymentException(timeout=true)으로 변환된다")
        void shouldConvertReadTimeoutExceptionToPaymentTimeoutException() {
            // given
            PaymentApproveRequest request = createDefaultRequest();

            when(responseSpec.bodyToMono(TossPaymentResponse.class))
                    .thenReturn(Mono.error(ReadTimeoutException.INSTANCE));

            // when & then
            assertThatThrownBy(() -> tossPaymentApprovalClient.approve(request))
                    .isInstanceOf(PaymentException.class)
                    .satisfies(exception -> {
                        PaymentException paymentException = (PaymentException) exception;
                        assertThat(paymentException.isTimeout()).isTrue();
                    });
        }

        @Test
        @DisplayName("HTTP 에러 상태 코드 발생 시 PaymentException(timeout=false)으로 변환된다")
        void shouldConvertHttpErrorToPaymentFailedException() {
            // given
            PaymentApproveRequest request = createDefaultRequest();

            when(responseSpec.bodyToMono(TossPaymentResponse.class))
                    .thenReturn(Mono.error(PaymentException.paymentFailed()));

            // when & then
            assertThatThrownBy(() ->
                    tossPaymentApprovalClient.approve(request))
                    .isInstanceOf(PaymentException.class)
                    .satisfies(exception -> {
                        PaymentException paymentException = (PaymentException) exception;
                        assertThat(paymentException.isTimeout()).isFalse();
                    });
        }

        @Test
        @DisplayName("일반 Exception 발생 시 PaymentException(timeout=false)으로 변환된다")
        void shouldConvertGeneralExceptionToPaymentFailedException() {
            // given
            PaymentApproveRequest request = createDefaultRequest();

            when(responseSpec.bodyToMono(TossPaymentResponse.class))
                    .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

            // when & then
            assertThatThrownBy(() -> tossPaymentApprovalClient.approve(request))
                    .isInstanceOf(PaymentException.class)
                    .satisfies(exception -> {
                        PaymentException paymentException = (PaymentException) exception;
                        assertThat(paymentException.isTimeout()).isFalse();
                    });
        }

        private PaymentApproveRequest createDefaultRequest() {
            return PaymentApproveRequest.builder()
                    .paymentKey("test_payment_key")
                    .orderId("test_order_id")
                    .amount(10000L)
                    .build();
        }
    }

    @Nested
    @DisplayName("주문 ID로 결제 정보 조회 테스트")
    class GetPaymentByOrderIdTests {

        @Test
        @DisplayName("주문 ID로 결제 정보 조회가 성공한다")
        void shouldGetPaymentResponseByOrderIdSuccessfully() {
            // given
            String orderId = "test_order_id";
            TossPaymentResponse expectedResponse = TossPaymentResponse.builder()
                    .status("DONE")
                    .paymentKey("test_payment_key")
                    .orderId(orderId)
                    .totalAmount(10000L)
                    .build();

            when(responseSpec.bodyToMono(TossPaymentResponse.class))
                    .thenReturn(Mono.just(expectedResponse));

            // when
            TossPaymentResponse actualResponse = tossPaymentApprovalClient
                    .getPaymentResponseByOrderId(orderId);

            // then
            assertThat(actualResponse)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedResponse);

            // WebClient 호출 검증
            verify(webClient).get();
            verify(requestHeadersUriSpec).uri("/v1/payments/orders/{orderId}", orderId);
            verify(requestHeadersSpec).headers(any(Consumer.class));
        }

        @Test
        @DisplayName("존재하지 않는 주문 ID 조회 시 PaymentException이 발생한다")
        void shouldThrowPaymentExceptionWhenOrderNotFound() {
            // given
            String orderId = "non_existent_order_id";

            when(responseSpec.bodyToMono(TossPaymentResponse.class))
                    .thenReturn(Mono.error(PaymentException.paymentFailed()));

            // when & then
            assertThatThrownBy(() ->
                    tossPaymentApprovalClient.getPaymentResponseByOrderId(orderId))
                    .isInstanceOf(PaymentException.class)
                    .satisfies(exception -> {
                        PaymentException paymentException = (PaymentException) exception;
                        assertThat(paymentException.isTimeout()).isFalse();
                    });
        }
    }

    @Nested
    @DisplayName("인증 헤더 테스트")
    class AuthenticationHeaderTests {

        @Test
        @DisplayName("Basic Auth 헤더가 올바르게 생성된다")
        void shouldGenerateCorrectBasicAuthHeader() {
            // given
            PaymentApproveRequest request = PaymentApproveRequest.builder()
                    .paymentKey("test_payment_key")
                    .orderId("test_order_id")
                    .amount(10000L)
                    .build();

            TossPaymentResponse mockResponse = TossPaymentResponse.builder()
                    .status("DONE")
                    .build();

            when(responseSpec.bodyToMono(TossPaymentResponse.class))
                    .thenReturn(Mono.just(mockResponse));

            // when
            tossPaymentApprovalClient.approve(request);

            // then
            verify(requestBodySpec).headers(argThat(headerConsumer -> {
                HttpHeaders headers = new HttpHeaders();
                headerConsumer.accept(headers);

                String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
                String expectedAuth = "Basic " + Base64.getEncoder()
                        .encodeToString("test_secret_key:".getBytes());

                return expectedAuth.equals(authHeader) &&
                        MediaType.APPLICATION_JSON_VALUE.equals(
                                headers.getFirst(HttpHeaders.CONTENT_TYPE));
            }));
        }
    }
}