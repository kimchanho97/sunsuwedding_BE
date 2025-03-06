package study.sunsuwedding.domain.portfolio.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import study.sunsuwedding.domain.portfolio.dto.req.PortfolioSearchRequest;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;

import java.util.List;

import static study.sunsuwedding.domain.portfolio.entity.QPortfolio.portfolio;

@Repository
@RequiredArgsConstructor
public class PortfolioQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Portfolio> findPortfolios(PortfolioSearchRequest searchRequest, Pageable pageable) {
        List<Portfolio> results = queryFactory
                .selectFrom(portfolio)
                .where(
                        nameContains(searchRequest.getName()),
                        locationEq(searchRequest.getLocation()),
                        priceBetween(searchRequest.getMinPrice(), searchRequest.getMaxPrice())
                )
                .orderBy(portfolio.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(portfolio.count())
                .from(portfolio)
                .where(
                        nameContains(searchRequest.getName()),
                        locationEq(searchRequest.getLocation()),
                        priceBetween(searchRequest.getMinPrice(), searchRequest.getMaxPrice())
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
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
