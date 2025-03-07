package study.sunsuwedding.domain.portfolio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import study.sunsuwedding.common.response.ApiResponse;
import study.sunsuwedding.common.response.SliceResponse;
import study.sunsuwedding.domain.portfolio.dto.req.PortfolioSearchRequest;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioListResponse;
import study.sunsuwedding.domain.portfolio.service.PortfolioQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioQueryService portfolioQueryService;

    @GetMapping("/v1")
    public ResponseEntity<ApiResponse<Slice<PortfolioListResponse>>> getPortfoliosV1EntityPaging(
            @AuthenticationPrincipal Long userId,
            @ModelAttribute PortfolioSearchRequest searchRequest,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(portfolioQueryService.getPortfoliosV1EntityPaging(userId, searchRequest, pageable)));
    }

    @GetMapping("/v2")
    public ResponseEntity<ApiResponse<Slice<PortfolioListResponse>>> getPortfoliosV2DtoPaging(
            @AuthenticationPrincipal Long userId,
            @ModelAttribute PortfolioSearchRequest searchRequest,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(portfolioQueryService.getPortfoliosV2DtoPaging(userId, searchRequest, pageable)));
    }

    @GetMapping("/v3")
    public ResponseEntity<ApiResponse<SliceResponse<PortfolioListResponse>>> getPortfoliosV3DtoCursorPaging(
            @AuthenticationPrincipal Long userId,
            @ModelAttribute PortfolioSearchRequest searchRequest,
            @RequestParam(required = false) Long cursor,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(portfolioQueryService.getPortfoliosV3DtoCursorPaging(userId, searchRequest, cursor, pageable)));
    }


}
