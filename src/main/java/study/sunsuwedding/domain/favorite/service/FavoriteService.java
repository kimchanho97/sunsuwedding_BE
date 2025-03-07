package study.sunsuwedding.domain.favorite.service;

public interface FavoriteService {

    void addFavorite(Long userId, Long portfolioId);

    void removeFavorite(Long userId, Long portfolioId);
}
