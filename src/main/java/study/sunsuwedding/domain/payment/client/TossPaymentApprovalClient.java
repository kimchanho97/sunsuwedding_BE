package study.sunsuwedding.domain.payment.client;

import io.netty.handler.timeout.ReadTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.dto.TossPaymentResponse;
import study.sunsuwedding.domain.payment.exception.PaymentException;

import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
public class TossPaymentApprovalClient implements PaymentApprovalClient {

    private final String secretKey;
    private final WebClient webClient;

    public TossPaymentApprovalClient(@Qualifier("tossWebClient") WebClient webClient,
                                     @Value("${payment.toss.secret}") String secretKey) {
        this.webClient = webClient;
        this.secretKey = secretKey;
    }

    @Override
    public TossPaymentResponse approve(PaymentApproveRequest request) {
        Map<String, Object> requestBody = Map.of(
                "paymentKey", request.getPaymentKey(),
                "orderId", request.getOrderId(),
                "amount", request.getAmount()
        );

        return webClient.post()
                .uri("/v1/payments/confirm")
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, getBasicAuth());
                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                })
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> Mono.error(PaymentException.paymentFailed()))
                .bodyToMono(TossPaymentResponse.class)
                .onErrorMap(ReadTimeoutException.class, e -> PaymentException.paymentTimeout())
                .onErrorMap(Exception.class, e -> PaymentException.paymentFailed())
                .block();
    }

    @Override
    public TossPaymentResponse getPaymentResponseByOrderId(String orderId) {
        return webClient.get()
                .uri("/v1/payments/orders/{orderId}", orderId)
                .headers(headers -> headers.add(HttpHeaders.AUTHORIZATION, getBasicAuth()))
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> Mono.error(PaymentException.paymentFailed()))
                .bodyToMono(TossPaymentResponse.class)
                .onErrorMap(Exception.class, e -> PaymentException.paymentFailed())
                .block();
    }

    private String getBasicAuth() {
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
    }

}
