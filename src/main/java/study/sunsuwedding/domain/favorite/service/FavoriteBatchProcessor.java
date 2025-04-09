package study.sunsuwedding.domain.favorite.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteBatchProcessor {

    private final FavoriteCacheService favoriteCacheService;
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_OR_UPDATE_SQL = """
            INSERT INTO favorite (user_id, portfolio_id, is_deleted, created_at, last_modified_at)
            VALUES (?, ?, false, NOW(), NOW())
            ON DUPLICATE KEY UPDATE is_deleted = false, last_modified_at = NOW()
            """;

    private static final String SOFT_DELETE_SQL_TEMPLATE = """
            UPDATE favorite SET is_deleted = true, last_modified_at = NOW()
            WHERE user_id = ? AND portfolio_id IN (%s)
            """;

    public void syncAllAddRequests(Map<Long, Set<Object>> addRequestsByUser) {
        List<Object[]> batchParams = new ArrayList<>();

        addRequestsByUser.forEach((userId, rawPortfolioIds) -> {
            Set<Long> portfolioIds = convertToLongSet(rawPortfolioIds);
            portfolioIds.forEach(pid -> batchParams.add(new Object[]{userId, pid}));
        });

        if (!batchParams.isEmpty()) {
            jdbcTemplate.batchUpdate(INSERT_OR_UPDATE_SQL, batchParams);
            addRequestsByUser.keySet().forEach(favoriteCacheService::clearAddRequestCache);
            log.info("[찜 추가 배치 완료] 전체 요청 수 = {}", batchParams.size());
        }
    }

    public void syncAllDeleteRequests(Map<Long, Set<Object>> deleteRequestsByUser) {
        deleteRequestsByUser.forEach((userId, rawPortfolioIds) -> {
            Set<Long> portfolioIds = convertToLongSet(rawPortfolioIds);
            if (portfolioIds.isEmpty()) return;

            String inClause = portfolioIds.stream().map(id -> "?").collect(Collectors.joining(", "));
            String sql = String.format(SOFT_DELETE_SQL_TEMPLATE, inClause);

            List<Object> args = new ArrayList<>();
            args.add(userId);
            args.addAll(portfolioIds);

            jdbcTemplate.update(sql, args.toArray());
            favoriteCacheService.clearDeleteRequestCache(userId);
            log.info("[찜 삭제 배치 완료] userId = {}, 삭제 수 = {}", userId, portfolioIds.size());
        });
    }

    private Set<Long> convertToLongSet(Set<Object> raw) {
        if (raw == null || raw.isEmpty()) return Collections.emptySet();
        return raw.stream()
                .filter(Objects::nonNull)
                .map(obj -> Long.parseLong(obj.toString()))
                .collect(Collectors.toSet());
    }
}
