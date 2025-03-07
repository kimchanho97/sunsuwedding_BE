package study.sunsuwedding.domain.portfolio.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import study.sunsuwedding.domain.portfolio.dto.req.PortfolioSearchRequest;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioListResponse;
import study.sunsuwedding.domain.portfolio.dto.res.QPortfolioListResponse;
import study.sunsuwedding.domain.portfolio.entity.PortfolioImage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static study.sunsuwedding.domain.favorite.entity.QFavorite.favorite;
import static study.sunsuwedding.domain.portfolio.entity.QPortfolio.portfolio;
import static study.sunsuwedding.domain.portfolio.entity.QPortfolioImage.portfolioImage;

@Repository
@RequiredArgsConstructor
public class PortfolioQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * DTO 조회
     */
    public Slice<PortfolioListResponse> findPortfoliosByDto(Long userId, PortfolioSearchRequest searchRequest, Pageable pageable) {
        Set<Long> favoritePortfolioIds = findFavoritePortfolioIds(userId);
        List<PortfolioListResponse> results = queryFactory
                .select(new QPortfolioListResponse(
                        portfolio.id,
                        portfolioImage.fileUrl,
                        portfolio.title,
                        portfolio.plannerName,
                        portfolio.totalPrice,
                        portfolio.location,
                        portfolio.contractCount,
                        portfolio.avgStars,
                        new CaseBuilder()
                                .when(portfolio.id.in(favoritePortfolioIds)).then(true)
                                .otherwise(false) // 좋아요 여부 계산
                ))
                .from(portfolio)
                .leftJoin(portfolioImage)
                .on(portfolioImage.portfolio.eq(portfolio), portfolioImage.isThumbnail.isTrue())
                .where(
                        nameContains(searchRequest.getName()),
                        locationEq(searchRequest.getLocation()),
                        priceBetween(searchRequest.getMinPrice(), searchRequest.getMaxPrice())
                )
                .orderBy(portfolio.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1) // `limit + 1` 개 조회하여 다음 페이지가 있는지 확인
                .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.removeLast();
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

    // Entity 조회
    public List<PortfolioImage> findPortfolioImagesByEntity(PortfolioSearchRequest searchRequest, Pageable pageable) {
        return queryFactory
                .selectFrom(portfolioImage) // PortfolioImage 기준으로 조회
                .join(portfolioImage.portfolio, portfolio)
                .on(portfolioImage.isThumbnail.isTrue()) // 썸네일 이미지만 조회
                .fetchJoin()
                .where(
                        nameContains(searchRequest.getName()),
                        locationEq(searchRequest.getLocation()),
                        priceBetween(searchRequest.getMinPrice(), searchRequest.getMaxPrice())
                )
                .orderBy(portfolio.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    // 좋아요 목록 조회 (Set<Long>으로 반환)
    public Set<Long> findFavoritePortfolioIds(Long userId) {
        if (userId == null) {
            return new HashSet<>();
        }
        return new HashSet<>(queryFactory
                .select(favorite.portfolio.id)
                .from(favorite)
                .where(favorite.user.id.eq(userId))
                .fetch());
    }

    private BooleanExpression nameContains(String name) {
        return StringUtils.hasText(name) ? portfolio.plannerName.contains(name) : null;
    }

    private BooleanExpression locationEq(String location) {
        return StringUtils.hasText(location) ? portfolio.location.eq(location) : null;
    }

    private BooleanExpression priceBetween(Long minPrice, Long maxPrice) {
        if (minPrice == null && maxPrice == null) return null;
        if (minPrice == null) return portfolio.totalPrice.loe(maxPrice);
        if (maxPrice == null) return portfolio.totalPrice.goe(minPrice);
        return portfolio.totalPrice.between(minPrice, maxPrice);
    }
}
