package study.sunsuwedding.domain.payment.client;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.dto.TossPaymentResponse;
import study.sunsuwedding.domain.payment.exception.PaymentException;

import java.time.Duration;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentApprovalClient implements PaymentApprovalClient {

    @Value("${payment.toss.secret}")
    private String secretKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.tosspayments.com")
            .clientConnector(new ReactorClientHttpConnector(
                    HttpClient.create()
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000) // 연결 타임아웃 3초
                            .responseTimeout(Duration.ofSeconds(7)) // 응답 타임아웃 7초
            ))
            .build();

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
