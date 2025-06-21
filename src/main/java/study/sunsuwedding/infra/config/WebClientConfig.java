package study.sunsuwedding.infra.config;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean("tossWebClient")
    public WebClient tossWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000) // 연결 타임아웃 3초
                .responseTimeout(Duration.ofSeconds(7)); // 응답 타임아웃 7초

        return WebClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
