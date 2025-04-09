package study.sunsuwedding.domain.favorite.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class FavoriteBatchScheduler {

    private final FavoriteCacheService favoriteCacheService;
    private final FavoriteBatchProcessor favoriteBatchProcessor;

    @Scheduled(fixedDelay = 2 * 60 * 1000)
    public void syncFavoritesFromRedisToDatabase() {
        Set<Long> addUserIds = favoriteCacheService.getAddChangedUserIds();
        Set<Long> deleteUserIds = favoriteCacheService.getDeleteChangedUserIds();

        for (Long userId : addUserIds) {
            try {
                favoriteBatchProcessor.processAddRequests(userId);
            } catch (Exception e) {
                log.error("[찜 추가 동기화 실패] userId={}", userId, e);
            }
        }

        for (Long userId : deleteUserIds) {
            try {
                favoriteBatchProcessor.processDeleteRequests(userId);
            } catch (Exception e) {
                log.error("[찜 삭제 동기화 실패] userId={}", userId, e);
            }
        }
    }
}
