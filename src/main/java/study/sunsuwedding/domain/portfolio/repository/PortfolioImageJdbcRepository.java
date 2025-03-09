package study.sunsuwedding.domain.portfolio.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import study.sunsuwedding.domain.portfolio.entity.PortfolioImage;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PortfolioImageJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = """
                INSERT INTO portfolio_image (portfolio_id, file_name, file_url, is_thumbnail) 
                VALUES (?, ?, ?, ?)
            """;

    public void batchInsert(List<PortfolioImage> portfolioImages) {
        jdbcTemplate.batchUpdate(INSERT_SQL, portfolioImages, portfolioImages.size(), (ps, portfolioImage) -> {
            ps.setLong(1, portfolioImage.getPortfolio().getId());
            ps.setString(2, portfolioImage.getFileName());
            ps.setString(3, portfolioImage.getFileUrl());
            ps.setBoolean(4, portfolioImage.getIsThumbnail());
        });
    }
}
