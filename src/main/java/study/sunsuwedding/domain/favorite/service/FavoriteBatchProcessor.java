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

    private static final String INSERT_OR_UPDATE_SQL =
            "INSERT INTO favorite (user_id, portfolio_id, is_deleted, created_at, last_modified_at) " +
                    "VALUES (?, ?, false, NOW(), NOW()) " +
                    "ON DUPLICATE KEY UPDATE is_deleted = false, last_modified_at = NOW()";

    private static final String SOFT_DELETE_SQL_TEMPLATE =
            "UPDATE favorite SET is_deleted = true, last_modified_at = NOW() " +
                    "WHERE user_id = ? AND portfolio_id IN (%s)";

    public void processAddRequests(Long userId) {
        Set<Long> portfolioIds = convertToLongSet(favoriteCacheService.getAddRequestPortfolios(userId));

        if (portfolioIds.isEmpty()) return;

        List<Object[]> batchParams = portfolioIds.stream()
                .map(portfolioId -> new Object[]{userId, portfolioId})
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(INSERT_OR_UPDATE_SQL, batchParams);
        favoriteCacheService.clearAddRequestCache(userId);
        log.info("[찜 추가 배치 완료] userId={}, size={}", userId, portfolioIds.size());
    }

    public void processDeleteRequests(Long userId) {
        Set<Long> portfolioIds = convertToLongSet(favoriteCacheService.getDeleteRequestPortfolios(userId));

        if (portfolioIds.isEmpty()) return;

        String inSql = portfolioIds.stream().map(id -> "?").collect(Collectors.joining(", "));
        String sql = String.format(SOFT_DELETE_SQL_TEMPLATE, inSql);

        List<Object> args = new ArrayList<>();
        args.add(userId);
        args.addAll(portfolioIds);

        jdbcTemplate.update(sql, args.toArray());
        favoriteCacheService.clearDeleteRequestCache(userId);
        log.info("[찜 삭제 배치 완료] userId={}, size={}", userId, portfolioIds.size());
    }

    private Set<Long> convertToLongSet(Set<Object> raw) {
        if (raw == null || raw.isEmpty()) return Collections.emptySet();
        return raw.stream()
                .filter(Objects::nonNull)
                .map(obj -> Long.parseLong(obj.toString()))
                .collect(Collectors.toSet());
    }
}
