package study.sunsuwedding.domain.favorite.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import study.sunsuwedding.common.response.ApiResponse;
import study.sunsuwedding.domain.favorite.service.FavoriteService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<Void>> addFavorite(@AuthenticationPrincipal Long userId, @PathVariable Long portfolioId) {
        favoriteService.addFavorite(userId, portfolioId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(@AuthenticationPrincipal Long userId, @PathVariable Long portfolioId) {
        favoriteService.removeFavorite(userId, portfolioId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
