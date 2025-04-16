package study.sunsuwedding.domain.favorite.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import study.sunsuwedding.common.response.ApiResponse;
import study.sunsuwedding.common.response.OffsetPaginationResponse;
import study.sunsuwedding.domain.favorite.service.FavoriteService;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioListResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{portfolioId}")
    public ApiResponse<Void> addFavorite(@AuthenticationPrincipal Long userId, @PathVariable Long portfolioId) {
        favoriteService.addFavorite(userId, portfolioId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{portfolioId}")
    public ApiResponse<Void> removeFavorite(@AuthenticationPrincipal Long userId, @PathVariable Long portfolioId) {
        favoriteService.removeFavorite(userId, portfolioId);
        return ApiResponse.success(null);
    }

    @GetMapping
    public OffsetPaginationResponse<PortfolioListResponse> getUserFavoritePortfolios(
            @AuthenticationPrincipal Long userId, @PageableDefault(size = 10) Pageable pageable) {
        return new OffsetPaginationResponse<>(favoriteService.getUserFavoritePortfolios(userId, pageable));
    }

}
