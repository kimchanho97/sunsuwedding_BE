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

import java.util.List;
import java.util.Set;

import static study.sunsuwedding.domain.portfolio.entity.QPortfolio.portfolio;
import static study.sunsuwedding.domain.portfolio.entity.QPortfolioImage.portfolioImage;

@Repository
@RequiredArgsConstructor
public class FavoriteQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Slice<PortfolioListResponse> findFavoritePortfoliosByIds(Set<Long> portfolioIds, Pageable pageable) {
        List<PortfolioListResponse> content = queryFactory
                .select(new QPortfolioListResponse(
                        portfolio.id,
                        portfolioImage.fileUrl,
                        portfolio.title,
                        portfolio.plannerName,
                        portfolio.totalPrice,
                        portfolio.location,
                        portfolio.contractedCount,
                        portfolio.averageRating,
                        Expressions.constant(true)
                ))
                .from(portfolio)
                .join(portfolioImage)
                .on(portfolioImage.portfolio.eq(portfolio), portfolioImage.isThumbnail.isTrue())
                .where(portfolio.id.in(portfolioIds))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) content.removeLast();

        return new SliceImpl<>(content, pageable, hasNext);
    }

}
