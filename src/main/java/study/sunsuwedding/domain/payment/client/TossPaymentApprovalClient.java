package study.sunsuwedding.domain.payment.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.exception.PaymentException;

import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TossPaymentApprovalClient implements PaymentApprovalClient {

    @Value("${payment.toss.secret}")
    private String secretKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.tosspayments.com")
            .build();

    @Override
    public void approve(PaymentApproveRequest request) {
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

        Map<String, Object> requestBody = Map.of(
                "paymentKey", request.getPaymentKey(),
                "orderId", request.getOrderId(),
                "amount", request.getAmount()
        );

        webClient.post()
                .uri("/v1/payments/confirm")
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, basicAuth);
                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                })
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.error(PaymentException.paymentApprovalFailed()))
                .toBodilessEntity() // 상태 코드만 확인하고 응답 바디를 사용하지 않음
                .block();
    }

}
