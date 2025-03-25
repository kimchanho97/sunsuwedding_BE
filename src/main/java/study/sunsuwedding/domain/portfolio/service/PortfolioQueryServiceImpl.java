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

    private final PortfolioQueryRepository portfolioQueryRepository;
    private final PortfolioCacheService portfolioCacheService;

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

    /**
     * V4: V3 + Redis 캐싱
     */
    @Override
    public CursorPaginationResponse<PortfolioListResponse> getPortfoliosV4CursorCaching(
            Long userId, PortfolioSearchRequest searchRequest, Long cursor, Pageable pageable) {

        Optional<String> cacheKeyOpt = resolveCacheKey(userId, searchRequest, cursor);
        // 1. 캐시 조회
        if (cacheKeyOpt.isPresent()) {
            String cacheKey = cacheKeyOpt.get();
            CursorPaginationResponse<PortfolioListResponse> cached = portfolioCacheService.get(cacheKey);
            if (cached != null) return cached;
        }

        // 2. DB 조회
        Slice<PortfolioListResponse> slice = portfolioQueryRepository.findPortfoliosByCursor(userId, searchRequest, cursor, pageable);
        List<PortfolioListResponse> content = new ArrayList<>(slice.getContent());
        Long nextCursor = slice.hasNext() ? content.removeLast().getPortfolioId() : null;
        CursorPaginationResponse<PortfolioListResponse> response = new CursorPaginationResponse<>(content, nextCursor);

        // 3. 캐시 저장
        cacheKeyOpt.ifPresent(key -> portfolioCacheService.save(key, response));
        return response;
    }

    private Optional<String> resolveCacheKey(Long userId, PortfolioSearchRequest request, Long cursor) {
        if (userId != null || cursor == null) {
            return Optional.empty(); // 로그인 유저 or 첫 페이지는 캐싱 X
        }
        if (isDefaultSearch(request)) {
            return Optional.of(RedisKeyUtil.portfolioCursorDefaultKey(cursor));
        }
        if (isLocationOnly(request)) {
            return Optional.of(RedisKeyUtil.portfolioCursorByLocationKey(request.getLocation(), cursor));
        }
        return Optional.empty(); // 이름/가격 조건 포함 → 캐싱 X
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
