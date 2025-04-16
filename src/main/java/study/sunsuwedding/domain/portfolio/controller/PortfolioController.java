package study.sunsuwedding.domain.portfolio.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import static study.sunsuwedding.domain.portfolio.constant.PortfoliioConst.MAX_PORTFOLIO_IMAGES_COUNT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioQueryService portfolioQueryService;
    private final PortfolioService portfolioService;

    @GetMapping("/v1")
    public OffsetPaginationResponse<PortfolioListResponse> getPortfoliosV1EntityPaging(
            @AuthenticationPrincipal Long userId,
            @ModelAttribute PortfolioSearchRequest searchRequest,
            @PageableDefault(size = 10) Pageable pageable) {
        return portfolioQueryService.getPortfoliosV1EntityPaging(userId, searchRequest, pageable);
    }

    @GetMapping("/v2")
    public OffsetPaginationResponse<PortfolioListResponse> getPortfoliosV2DtoPaging(
            @AuthenticationPrincipal Long userId,
            @ModelAttribute PortfolioSearchRequest searchRequest,
            @PageableDefault(size = 10) Pageable pageable) {
        return portfolioQueryService.getPortfoliosV2DtoPaging(userId, searchRequest, pageable);
    }

    @GetMapping("/v3")
    public CursorPaginationResponse<PortfolioListResponse> getPortfoliosV3DtoCursorPaging(
            @AuthenticationPrincipal Long userId,
            @ModelAttribute PortfolioSearchRequest searchRequest,
            @RequestParam(required = false) Long cursor,
            @PageableDefault(size = 10) Pageable pageable) {
        return portfolioQueryService.getPortfoliosV3DtoCursorPaging(userId, searchRequest, cursor, pageable);
    }

    @GetMapping("/v4")
    public CursorPaginationResponse<PortfolioListResponse> getPortfoliosV4DtoCursorPaging(
            @AuthenticationPrincipal Long userId,
            @ModelAttribute PortfolioSearchRequest searchRequest,
            @RequestParam(required = false) Long cursor,
            @PageableDefault(size = 10) Pageable pageable) {
        return portfolioQueryService.getPortfoliosV4CursorCaching(userId, searchRequest, cursor, pageable);
    }

    @GetMapping("/{portfolioId}")
    public ApiResponse<PortfolioResponse> getPortfolio(@AuthenticationPrincipal Long userId, @PathVariable Long portfolioId) {
        return ApiResponse.success(portfolioService.getPortfolio(userId, portfolioId));
    }

    @PostMapping
    public ApiResponse<Void> addPortfolio(@AuthenticationPrincipal Long userId,
                                          @RequestPart("portfolio") @Valid PortfolioRequest request,
                                          @RequestPart("images") List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw PortfolioException.portfolioImageEmpty();
        }
        if (images.size() > MAX_PORTFOLIO_IMAGES_COUNT) {
            throw PortfolioException.portfolioImageLimitExceeded();
        }

        portfolioService.createPortfolio(userId, request, images);
        return ApiResponse.success(null);
    }

    @PutMapping
    public ApiResponse<Void> updatePortfolio(@AuthenticationPrincipal Long userId,
                                             @RequestPart("portfolio") @Valid PortfolioRequest request,
                                             @RequestParam(value = "existingImages", required = false) List<String> existingImages,  // 기존 이미지 (S3 URL)
                                             @RequestParam(value = "deletedImages", required = false) List<String> deletedImages, // 삭제할 이미지 (S3 URL)
                                             @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) {  // 신규 이미지 파일
        int totalImageCount = (existingImages != null ? existingImages.size() : 0) +
                (newImages != null ? newImages.size() : 0);

        if (totalImageCount == 0) {
            throw PortfolioException.portfolioImageEmpty();
        }
        if (totalImageCount > MAX_PORTFOLIO_IMAGES_COUNT) {
            throw PortfolioException.portfolioImageLimitExceeded();
        }

        portfolioService.updatePortfolio(userId, request, existingImages, newImages, deletedImages);
        return ApiResponse.success(null);
    }

    @DeleteMapping
    public ApiResponse<Void> deletePortfolio(@AuthenticationPrincipal Long userId) {
        portfolioService.deletePortfolio(userId);
        return ApiResponse.success(null);
    }

    @GetMapping("/me")
    public ApiResponse<OwnPortfolioResponse> getMyPortfolio(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success(portfolioService.getOwnPortfolio(userId));
    }
}
