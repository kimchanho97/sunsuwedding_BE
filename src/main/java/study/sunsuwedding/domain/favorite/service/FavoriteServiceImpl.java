package study.sunsuwedding.domain.favorite.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.favorite.repository.FavoriteQueryRepository;
import study.sunsuwedding.domain.favorite.repository.FavoriteRepository;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioListResponse;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteQueryRepository favoriteQueryRepository;
    private final FavoriteCacheService favoriteCacheService;

    @Override
    public void addFavorite(Long userId, Long portfolioId) {
        favoriteCacheService.saveAddRequestToCache(userId, portfolioId);
    }

    @Override
    public void removeFavorite(Long userId, Long portfolioId) {
        favoriteCacheService.saveDeleteRequestToCache(userId, portfolioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<PortfolioListResponse> getUserFavoritePortfolios(Long userId, Pageable pageable) {
        Set<Long> finalFavoriteIds = getCurrentFavoritePortfolioIds(userId);
        if (finalFavoriteIds.isEmpty()) {
            return new SliceImpl<>(Collections.emptyList(), pageable, false);
        }
        
        return favoriteQueryRepository.findFavoritePortfoliosByIds(finalFavoriteIds, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> getCurrentFavoritePortfolioIds(Long userId) {
        if (userId == null) return Collections.emptySet();

        Set<Long> dbFavoriteIds = favoriteRepository.findPortfolioIdsByUserId(userId);
        Set<Object> cachedAddIds = favoriteCacheService.getAddRequestPortfolios(userId);
        Set<Object> cachedDeleteIds = favoriteCacheService.getDeleteRequestPortfolios(userId);

        Set<Long> finalFavorites = new HashSet<>(dbFavoriteIds);
        cachedAddIds.forEach(p -> finalFavorites.add(Long.parseLong(p.toString())));
        cachedDeleteIds.forEach(p -> finalFavorites.remove(Long.parseLong(p.toString())));
        return finalFavorites;
    }

}
