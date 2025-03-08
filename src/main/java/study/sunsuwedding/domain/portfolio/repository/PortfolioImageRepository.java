package study.sunsuwedding.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;
import study.sunsuwedding.domain.portfolio.entity.PortfolioImage;

import java.util.List;

public interface PortfolioImageRepository extends JpaRepository<PortfolioImage, Long> {

    @Modifying
    @Query("DELETE FROM PortfolioImage pi WHERE pi.portfolio.id = :portfolioId")
    void deleteByPortfolioId(@Param("portfolioId") Long portfolioId);

    List<PortfolioImage> findByPortfolio(Portfolio portfolio);
}