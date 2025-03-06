package study.sunsuwedding.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.sunsuwedding.domain.portfolio.entity.PortfolioItem;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {
}
