package study.sunsuwedding.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;
import study.sunsuwedding.domain.user.entity.Planner;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    boolean existsByPlanner(Planner planner);

    @Query("""
            SELECT DISTINCT p FROM Portfolio p 
            LEFT JOIN FETCH p.items 
            LEFT JOIN FETCH p.images 
            WHERE p.id = :portfolioId
            """)
    Optional<Portfolio> findPortfolioWithDetails(@Param("portfolioId") Long portfolioId);

    Optional<Portfolio> findByPlanner(Planner planner);

    @Query("""
            SELECT DISTINCT p FROM Portfolio p 
            LEFT JOIN FETCH p.items 
            LEFT JOIN FETCH p.images 
            WHERE p.planner = :planner
            """)
    Optional<Portfolio> findPortfolioWithDetailsByPlanner(@Param("planner") Planner planner);
    
}
