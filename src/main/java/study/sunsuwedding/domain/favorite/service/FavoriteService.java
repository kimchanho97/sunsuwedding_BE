package study.sunsuwedding.domain.favorite.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioListResponse;

import java.util.Set;

public interface FavoriteService {

    void addFavorite(Long userId, Long portfolioId);

    void removeFavorite(Long userId, Long portfolioId);

    Slice<PortfolioListResponse> getUserFavoritePortfolios(Long userId, Pageable pageable);

    Set<Long> getCurrentFavoritePortfolioIds(Long userId);
}
