package study.sunsuwedding.domain.portfolio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.sunsuwedding.common.response.ApiResponse;
import study.sunsuwedding.domain.portfolio.dto.req.PortfolioSearchRequest;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioListResponse;
import study.sunsuwedding.domain.portfolio.service.PortfolioService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping
    public ResponseEntity<ApiResponse<Slice<PortfolioListResponse>>> getPortfolios(
            @AuthenticationPrincipal Long userId,
            @ModelAttribute PortfolioSearchRequest searchRequest,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Slice<PortfolioListResponse> portfolios = portfolioService.getPortfolios(userId, searchRequest, pageable);
        return ResponseEntity.ok(ApiResponse.success(portfolios));
    }


}
