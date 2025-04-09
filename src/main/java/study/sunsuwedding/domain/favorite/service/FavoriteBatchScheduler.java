package study.sunsuwedding.domain.favorite.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class FavoriteBatchScheduler {

    private final FavoriteCacheService favoriteCacheService;
    private final FavoriteBatchProcessor favoriteBatchProcessor;

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void syncFavoritesFromRedisToDatabase() {
        Map<Long, Set<Object>> addRequestsByUser = new HashMap<>();
        for (Long userId : favoriteCacheService.getAddChangedUserIds()) {
            addRequestsByUser.put(userId, favoriteCacheService.getAddRequestPortfolios(userId));
        }

        Map<Long, Set<Object>> deleteRequestsByUser = new HashMap<>();
        for (Long userId : favoriteCacheService.getDeleteChangedUserIds()) {
            deleteRequestsByUser.put(userId, favoriteCacheService.getDeleteRequestPortfolios(userId));
        }

        try {
            favoriteBatchProcessor.syncAllAddRequests(addRequestsByUser);
        } catch (Exception e) {
            log.error("[찜 추가 배치 실패]", e);
        }

        try {
            favoriteBatchProcessor.syncAllDeleteRequests(deleteRequestsByUser);
        } catch (Exception e) {
            log.error("[찜 삭제 배치 실패]", e);
        }
    }
}