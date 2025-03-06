package study.sunsuwedding.domain.portfolio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<Page<PortfolioListResponse>>> getPofolios(
            @ModelAttribute PortfolioSearchRequest searchRequest,
            Pageable pageable
    ) {
        Page<PortfolioListResponse> portfolios = portfolioService.getPortfolios(searchRequest, pageable);
        return ResponseEntity.ok(ApiResponse.success(portfolios));
    }


}
