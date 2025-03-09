package study.sunsuwedding.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;
import study.sunsuwedding.domain.user.entity.Planner;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    boolean existsByPlanner(Planner planner);

    Optional<Portfolio> findByPlanner(Planner planner);

}
