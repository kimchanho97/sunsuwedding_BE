package study.sunsuwedding.domain.portfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import study.sunsuwedding.common.response.CursorPaginationResponse;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioListResponse;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PortfolioCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final Duration TTL = Duration.ofMinutes(3);

    public void save(String key, CursorPaginationResponse<PortfolioListResponse> value) {
        redisTemplate.opsForValue().set(key, value, TTL);
    }

    public CursorPaginationResponse<PortfolioListResponse> get(String key) {
        return (CursorPaginationResponse<PortfolioListResponse>) redisTemplate.opsForValue().get(key);
    }

}
