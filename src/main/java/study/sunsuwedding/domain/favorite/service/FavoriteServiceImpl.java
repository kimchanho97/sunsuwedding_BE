package study.sunsuwedding.domain.favorite.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.favorite.entity.Favorite;
import study.sunsuwedding.domain.favorite.exception.FavoriteException;
import study.sunsuwedding.domain.favorite.repository.FavoriteQueryRepository;
import study.sunsuwedding.domain.favorite.repository.FavoriteRepository;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioListResponse;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;
import study.sunsuwedding.domain.portfolio.exception.PortfolioException;
import study.sunsuwedding.domain.portfolio.repository.PortfolioRepository;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final FavoriteRepository favoriteRepository;
    private final FavoriteQueryRepository favoriteQueryRepository;

    @Override
    @Transactional
    public void addFavorite(Long userId, Long portfolioId) {
        User user = getUserById(userId);
        Portfolio portfolio = getPortfolioById(portfolioId);

        // 좋아요 누른 적 있는지 확인
        checkIfAlreadyFavorited(user, portfolio);
        favoriteRepository.save(new Favorite(user, portfolio));
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long portfolioId) {
        User user = getUserById(userId);
        Portfolio portfolio = getPortfolioById(portfolioId);

        Favorite favorite = favoriteRepository.findByUserAndPortfolio(user, portfolio)
                .orElseThrow(FavoriteException::favoriteNotFound);

        favoriteRepository.delete(favorite);
    }

    @Override
    public Slice<PortfolioListResponse> getUserFavoritePortfolios(Long userId, Pageable pageable) {
        return favoriteQueryRepository.findUserFavoritePortfolios(userId, pageable);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserException::userNotFound);
    }

    private Portfolio getPortfolioById(Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(PortfolioException::portfolioNotFound);
    }

    private void checkIfAlreadyFavorited(User user, Portfolio portfolio) {
        boolean alreadyFavorited = favoriteRepository.existsByUserAndPortfolio(user, portfolio);
        if (alreadyFavorited) {
            throw FavoriteException.favoriteAlreadyExists();
        }
    }
}
