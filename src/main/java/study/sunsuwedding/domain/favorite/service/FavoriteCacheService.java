package study.sunsuwedding.domain.favorite.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import study.sunsuwedding.common.util.RedisKeyUtil;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveAddRequestToCache(Long userId, Long portfolioId) {
        String deleteKey = getDeleteRequestKey(userId);
        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(deleteKey, portfolioId))) {
            redisTemplate.opsForSet().remove(deleteKey, portfolioId);
            redisTemplate.opsForSet().remove(RedisKeyUtil.favoriteDeleteChangedUserSetKey(), userId);
            return;
        }

        redisTemplate.opsForSet().add(getAddRequestKey(userId), portfolioId);
        redisTemplate.opsForSet().add(RedisKeyUtil.favoriteAddChangedUserSetKey(), userId);
    }

    public void saveDeleteRequestToCache(Long userId, Long portfolioId) {
        String addKey = getAddRequestKey(userId);
        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(addKey, portfolioId))) {
            redisTemplate.opsForSet().remove(addKey, portfolioId);
            redisTemplate.opsForSet().remove(RedisKeyUtil.favoriteAddChangedUserSetKey(), userId);
            return;
        }

        redisTemplate.opsForSet().add(getDeleteRequestKey(userId), portfolioId);
        redisTemplate.opsForSet().add(RedisKeyUtil.favoriteDeleteChangedUserSetKey(), userId);
    }

    public Set<Object> getAddRequestPortfolios(Long userId) {
        Set<Object> result = redisTemplate.opsForSet().members(getAddRequestKey(userId));
        return result != null ? result : Collections.emptySet();
    }

    public Set<Object> getDeleteRequestPortfolios(Long userId) {
        Set<Object> result = redisTemplate.opsForSet().members(getDeleteRequestKey(userId));
        return result != null ? result : Collections.emptySet();
    }

    public void clearAddRequestCache(Long userId) {
        redisTemplate.delete(getAddRequestKey(userId));
        redisTemplate.opsForSet().remove(RedisKeyUtil.favoriteAddChangedUserSetKey(), userId);
    }

    public void clearDeleteRequestCache(Long userId) {
        redisTemplate.delete(getDeleteRequestKey(userId));
        redisTemplate.opsForSet().remove(RedisKeyUtil.favoriteDeleteChangedUserSetKey(), userId);
    }

    public Set<Long> getAddChangedUserIds() {
        Set<Object> raw = redisTemplate.opsForSet().members(RedisKeyUtil.favoriteAddChangedUserSetKey());
        return convertToLongSet(raw != null ? raw : Collections.emptySet());
    }

    public Set<Long> getDeleteChangedUserIds() {
        Set<Object> raw = redisTemplate.opsForSet().members(RedisKeyUtil.favoriteDeleteChangedUserSetKey());
        return convertToLongSet(raw != null ? raw : Collections.emptySet());
    }

    private String getAddRequestKey(Long userId) {
        return RedisKeyUtil.favoriteAddRequestKey(userId);
    }

    private String getDeleteRequestKey(Long userId) {
        return RedisKeyUtil.favoriteDeleteRequestKey(userId);
    }

    private Set<Long> convertToLongSet(Set<Object> raw) {
        return raw.stream()
                .filter(Objects::nonNull)
                .map(obj -> Long.parseLong(obj.toString()))
                .collect(Collectors.toSet());
    }
}
