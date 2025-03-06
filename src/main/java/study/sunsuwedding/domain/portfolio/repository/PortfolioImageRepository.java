package study.sunsuwedding.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.sunsuwedding.domain.portfolio.entity.PortfolioImage;

public interface PortfolioImageRepository extends JpaRepository<PortfolioImage, Long> {
}
