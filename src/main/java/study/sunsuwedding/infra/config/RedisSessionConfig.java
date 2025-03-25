package study.sunsuwedding.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.FlushMode;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(
        maxInactiveIntervalInSeconds = 1800, // 세션 유지 시간 30분
        flushMode = FlushMode.IMMEDIATE
)
public class RedisSessionConfig {
}

