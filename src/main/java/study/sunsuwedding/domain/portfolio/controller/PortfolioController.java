package study.sunsuwedding.domain.portfolio.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import study.sunsuwedding.common.response.ApiResponse;
import study.sunsuwedding.common.response.CursorPaginationResponse;
import study.sunsuwedding.common.response.OffsetPaginationResponse;
import study.sunsuwedding.domain.portfolio.dto.req.PortfolioRequest;
import study.sunsuwedding.domain.portfolio.dto.req.PortfolioSearchRequest;
import study.sunsuwedding.domain.portfolio.dto.res.OwnPortfolioResponse;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioListResponse;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioResponse;
import study.sunsuwedding.domain.portfolio.exception.PortfolioException;
import study.sunsuwedding.domain.portfolio.service.PortfolioQueryService;
import study.sunsuwedding.domain.portfolio.service.PortfolioService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioQueryService portfolioQueryService;
    private final PortfolioService portfolioService;

    @GetMapping("/v1")
    public ResponseEntity<ApiResponse<OffsetPaginationResponse<PortfolioListResponse>>> getPortfoliosV1EntityPaging(
            @AuthenticationPrincipal Long userId,
            @ModelAttribute PortfolioSearchRequest searchRequest,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(portfolioQueryService.getPortfoliosV1EntityPaging(userId, searchRequest, pageable)));
    }

    @GetMapping("/v2")
    public ResponseEntity<ApiResponse<OffsetPaginationResponse<PortfolioListResponse>>> getPortfoliosV2DtoPaging(
            @AuthenticationPrincipal Long userId,
            @ModelAttribute PortfolioSearchRequest searchRequest,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(portfolioQueryService.getPortfoliosV2DtoPaging(userId, searchRequest, pageable)));
    }

    @GetMapping("/v3")
    public ResponseEntity<ApiResponse<CursorPaginationResponse<PortfolioListResponse>>> getPortfoliosV3DtoCursorPaging(
            @AuthenticationPrincipal Long userId,
            @ModelAttribute PortfolioSearchRequest searchRequest,
            @RequestParam(required = false) Long cursor,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(portfolioQueryService.getPortfoliosV3DtoCursorPaging(userId, searchRequest, cursor, pageable)));
    }

    @GetMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<PortfolioResponse>> getPortfolio(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long portfolioId) {
        return ResponseEntity.ok(ApiResponse.success(portfolioService.getPortfolio(userId, portfolioId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addPortfolio(
            @AuthenticationPrincipal Long userId,
            @RequestPart("portfolio") @Valid PortfolioRequest request,
            @RequestPart("images") List<MultipartFile> images) {
        // 이미지 유효성 검사(비어 있는지 확인)
        if (images == null || images.isEmpty()) {
            throw PortfolioException.portfolioImageEmpty();
        }
        if (images.size() > 5) {
            throw PortfolioException.portfolioImageLimitExceeded();
        }

        portfolioService.createPortfolio(userId, request, images);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Void>> updatePortfolio(
            @AuthenticationPrincipal Long userId,
            @RequestPart("portfolio") @Valid PortfolioRequest request,
            @RequestPart("images") List<MultipartFile> images) {
        // 이미지 유효성 검사(비어 있는지 확인)
        if (images == null || images.isEmpty()) {
            throw PortfolioException.portfolioImageEmpty();
        }
        if (images.size() > 5) {
            throw PortfolioException.portfolioImageLimitExceeded();
        }

        portfolioService.updatePortfolio(userId, request, images);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deletePortfolio(@AuthenticationPrincipal Long userId) {
        portfolioService.deletePortfolio(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<OwnPortfolioResponse>> getMyPortfolio(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.success(portfolioService.getOwnPortfolio(userId)));
    }
}
