package study.sunsuwedding.domain.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.sunsuwedding.domain.favorite.entity.Favorite;

import java.util.Set;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    @Query("SELECT f.portfolio.id FROM Favorite f WHERE f.user.id = :userId")
    Set<Long> findPortfolioIdsByUserId(@Param("userId") Long userId);
}
