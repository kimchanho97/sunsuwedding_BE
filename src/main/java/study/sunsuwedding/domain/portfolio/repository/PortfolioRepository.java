package study.sunsuwedding.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;
import study.sunsuwedding.domain.user.entity.Planner;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    boolean existsByPlanner(Planner planner);

    Optional<Portfolio> findByPlanner(Planner planner);

    @Query("""
            SELECT p FROM Portfolio p 
            JOIN FETCH p.items 
            WHERE p.id = :portfolioId
            """)
    Optional<Portfolio> findWithItemsByPortfolioId(@Param("portfolioId") Long portfolioId);

    @Query("""
            SELECT p FROM Portfolio p 
            JOIN FETCH p.items 
            WHERE p.planner = :planner
            """)
    Optional<Portfolio> findWithItemsByPlanner(@Param("planner") Planner planner);

}
