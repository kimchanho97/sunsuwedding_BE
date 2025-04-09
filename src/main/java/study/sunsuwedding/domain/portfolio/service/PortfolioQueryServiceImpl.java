package study.sunsuwedding.domain.portfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import study.sunsuwedding.common.response.CursorPaginationResponse;
import study.sunsuwedding.common.response.OffsetPaginationResponse;
import study.sunsuwedding.common.util.RedisKeyUtil;
import study.sunsuwedding.domain.favorite.repository.FavoriteRepository;
import study.sunsuwedding.domain.favorite.service.FavoriteCacheService;
import study.sunsuwedding.domain.favorite.service.FavoriteService;
import study.sunsuwedding.domain.portfolio.dto.req.PortfolioSearchRequest;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioListResponse;
import study.sunsuwedding.domain.portfolio.entity.PortfolioImage;
import study.sunsuwedding.domain.portfolio.repository.PortfolioQueryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioQueryServiceImpl implements PortfolioQueryService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteCacheService favoriteCacheService;
    private final PortfolioQueryRepository portfolioQueryRepository;
    private final PortfolioCacheService portfolioCacheService;
    private final FavoriteService favoriteService;

    /**
     * V1: 엔티티 조회 후 DTO 변환 + 오프셋 기반 페이징
     */
    @Override
    public OffsetPaginationResponse<PortfolioListResponse> getPortfoliosV1EntityPaging(Long userId, PortfolioSearchRequest searchRequest, Pageable pageable) {
        List<PortfolioImage> portfolioImages = portfolioQueryRepository.findPortfolioImagesByEntity(searchRequest, pageable);
        Set<Long> favoritePortfolioIds = portfolioQueryRepository.findFavoritePortfolioIds(userId);

        List<PortfolioListResponse> content = portfolioImages.stream()
                .map(portfolioImage ->
                        PortfolioListResponse.fromEntity(portfolioImage, favoritePortfolioIds))
                .collect(Collectors.toCollection(ArrayList::new));

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.removeLast();
        }
        return new OffsetPaginationResponse<>(new SliceImpl<>(content, pageable, hasNext));
    }

    /**
     * V2: DTO 조회 + 오프셋 기반 페이징
     */
    @Override
    public OffsetPaginationResponse<PortfolioListResponse> getPortfoliosV2DtoPaging(Long userId, PortfolioSearchRequest searchRequest, Pageable pageable) {
        return new OffsetPaginationResponse<>(portfolioQueryRepository.findPortfoliosByDto(userId, searchRequest, pageable));
    }

    /**
     * V3: DTO 조회 + 커서 기반 페이징
     */
    @Override
    public CursorPaginationResponse<PortfolioListResponse> getPortfoliosV3DtoCursorPaging(
            Long userId, PortfolioSearchRequest searchRequest, Long cursor, Pageable pageable) {
        Slice<PortfolioListResponse> slice = portfolioQueryRepository.findPortfoliosByCursor(userId, searchRequest, cursor, pageable);

        // 다음 페이지 커서 계산 (다음 페이지가 없으면 null) -> 현재 content는 size + 1
        List<PortfolioListResponse> content = new ArrayList<>(slice.getContent());
        Long nextCursor = slice.hasNext() ? content.removeLast().getPortfolioId() : null;
        return new CursorPaginationResponse<>(content, nextCursor);
    }

    @Override
    public CursorPaginationResponse<PortfolioListResponse> getPortfoliosV4CursorCaching(
            Long userId, PortfolioSearchRequest searchRequest, Long cursor, Pageable pageable) {

        Optional<String> cacheKeyOpt = generateCacheKeyIfApplicable(searchRequest, cursor);

        // 1. 캐시 조회
        if (cacheKeyOpt.isPresent()) {
            String cacheKey = cacheKeyOpt.get();
            CursorPaginationResponse<PortfolioListResponse> cached = portfolioCacheService.get(cacheKey);
            if (cached != null) {
                if (userId == null) {
                    return cached; // 비회원은 개인화 필요 없음
                }
                List<PortfolioListResponse> personalized = personalizeFavoriteStatus(cached.getData(), userId);
                return new CursorPaginationResponse<>(personalized, cached.getNextCursor());
            }
        }

        // 2. DB 조회 (userId = null → 비개인화된 기본 쿼리)
        Slice<PortfolioListResponse> slice = portfolioQueryRepository.findPortfoliosByCursor(null, searchRequest, cursor, pageable);
        List<PortfolioListResponse> rawContent = new ArrayList<>(slice.getContent());
        Long nextCursor = slice.hasNext() ? rawContent.removeLast().getPortfolioId() : null;

        CursorPaginationResponse<PortfolioListResponse> response = new CursorPaginationResponse<>(rawContent, nextCursor);

        // 3. 캐시 저장 (비로그인 유저 기준)
        cacheKeyOpt.ifPresent(key -> portfolioCacheService.save(key, response));

        // 4. 개인화 처리 (로그인 유저만)
        if (userId == null) return response;
        List<PortfolioListResponse> personalized = personalizeFavoriteStatus(rawContent, userId);
        return new CursorPaginationResponse<>(personalized, nextCursor);
    }

    private List<PortfolioListResponse> personalizeFavoriteStatus(List<PortfolioListResponse> list, Long userId) {
        Set<Long> favoriteIds = favoriteService.getCurrentFavoritePortfolioIds(userId);

        // 캐시 원본을 건드리지 않기 위해 깊은 복사
        return list.stream()
                .map(p -> {
                    PortfolioListResponse copy = new PortfolioListResponse(p); // 복사 생성자 or 수동 복사
                    copy.setFavorited(favoriteIds.contains(p.getPortfolioId()));
                    return copy;
                })
                .toList();
    }

    private Optional<String> generateCacheKeyIfApplicable(PortfolioSearchRequest request, Long cursor) {
        if (cursor == null) {
            return Optional.empty(); // 첫 페이지 → 캐싱 X
        }
        if (isDefaultSearch(request)) {
            return Optional.of(RedisKeyUtil.portfolioCursorDefaultKey(cursor));
        }
        if (isLocationOnly(request)) {
            return Optional.of(RedisKeyUtil.portfolioCursorByLocationKey(request.getLocation(), cursor));
        }
        return Optional.empty(); // 기타 조건 포함 시 캐싱 X
    }

    private boolean isDefaultSearch(PortfolioSearchRequest request) {
        return !StringUtils.hasText(request.getName()) &&
                !StringUtils.hasText(request.getLocation()) &&
                request.getMinPrice() == null &&
                request.getMaxPrice() == null;
    }

    private boolean isLocationOnly(PortfolioSearchRequest request) {
        return StringUtils.hasText(request.getLocation()) &&
                !StringUtils.hasText(request.getName()) &&
                request.getMinPrice() == null &&
                request.getMaxPrice() == null;
    }

}
