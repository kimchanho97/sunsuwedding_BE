package study.sunsuwedding.domain.favorite.repository;


import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioListResponse;
import study.sunsuwedding.domain.portfolio.dto.res.QPortfolioListResponse;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static study.sunsuwedding.domain.favorite.entity.QFavorite.favorite;
import static study.sunsuwedding.domain.portfolio.entity.QPortfolio.portfolio;
import static study.sunsuwedding.domain.portfolio.entity.QPortfolioImage.portfolioImage;

@Repository
@RequiredArgsConstructor
public class FavoriteQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Slice<PortfolioListResponse> findUserFavoritePortfolios(Long userId, Pageable pageable) {
        // 유저가 찜한 포트폴리오 ID 목록 조회
        Set<Long> favoritePortfolioIds = new HashSet<>(queryFactory
                .select(favorite.portfolio.id)
                .from(favorite)
                .where(favorite.user.id.eq(userId))
                .fetch());

        // 찜한 포트폴리오가 없으면 바로 빈 Slice 반환
        if (favoritePortfolioIds.isEmpty()) {
            return new SliceImpl<>(Collections.emptyList(), pageable, false);
        }

        // 포트폴리오 + 썸네일 이미지 조회 (전체 포트폴리오 목록)
        List<PortfolioListResponse> content = queryFactory
                .select(new QPortfolioListResponse(
                        portfolio.id,
                        portfolioImage.fileUrl,
                        portfolio.title,
                        portfolio.plannerName,
                        portfolio.totalPrice,
                        portfolio.location,
                        portfolio.contractCount,
                        portfolio.avgStars,
                        Expressions.constant(true)
                ))
                .from(portfolio)
                .join(portfolioImage).on(portfolioImage.portfolio.eq(portfolio)
                        .and(portfolioImage.isThumbnail.isTrue()))
                .where(portfolio.id.in(favoritePortfolioIds)) // 찜한 포트폴리오만 필터링
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1) // `limit + 1` 개 조회하여 다음 페이지가 있는지 확인
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.removeLast(); // 마지막 요소를 제거
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

}
