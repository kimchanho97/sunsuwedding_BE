package study.sunsuwedding.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.sunsuwedding.domain.portfolio.entity.PortfolioItem;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {

    @Modifying
    @Query("DELETE FROM PortfolioItem pi WHERE pi.portfolio.id = :portfolioId")
    void deleteByPortfolioId(@Param("portfolioId") Long portfolioId);
}
