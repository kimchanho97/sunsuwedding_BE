package study.sunsuwedding.domain.portfolio.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import study.sunsuwedding.domain.portfolio.entity.PortfolioItem;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PortfolioItemJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = """
                INSERT INTO portfolio_item (portfolio_id, item_name, item_price, created_at, last_modified_at) 
                VALUES (?, ?, ?, NOW(), NOW())
            """;

    public void batchInsert(List<PortfolioItem> portfolioItems) {
        jdbcTemplate.batchUpdate(INSERT_SQL, portfolioItems, portfolioItems.size(),
                (ps, item) -> {
                    ps.setLong(1, item.getPortfolio().getId());
                    ps.setString(2, item.getItemName());
                    ps.setLong(3, item.getItemPrice());
                });
    }
}
