package study.sunsuwedding.domain.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.sunsuwedding.domain.favorite.entity.Favorite;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;
import study.sunsuwedding.domain.user.entity.User;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserAndPortfolio(User user, Portfolio portfolio);

    Optional<Favorite> findByUserAndPortfolio(User user, Portfolio portfolio);
}
