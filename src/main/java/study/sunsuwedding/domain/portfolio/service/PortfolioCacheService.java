package study.sunsuwedding.domain.portfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import study.sunsuwedding.common.response.CursorPaginationResponse;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioListResponse;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

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

    public void evictAll() {
        ScanOptions options = ScanOptions.scanOptions()
                .match("cache:portfolio:*")
                .count(1000)
                .build();

        Set<String> keysToDelete = new HashSet<>();
        try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory()
                .getConnection()
                .scan(options)) {

            while (cursor.hasNext()) {
                keysToDelete.add(new String(cursor.next()));
            }
        }

        if (!keysToDelete.isEmpty()) {
            redisTemplate.delete(keysToDelete);
        }
    }
}
