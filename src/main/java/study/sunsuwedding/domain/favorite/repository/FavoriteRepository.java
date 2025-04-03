package study.sunsuwedding.domain.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.sunsuwedding.domain.favorite.entity.Favorite;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndPortfolioId(Long userId, Long portfolioId);

    boolean existsByUserIdAndPortfolioId(Long userId, Long portfolioId);
}
