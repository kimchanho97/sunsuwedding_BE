package study.sunsuwedding.domain.portfolio.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.sunsuwedding.domain.portfolio.dto.req.PortfolioSearchRequest;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioListResponse;

public interface PortfolioService {

    Page<PortfolioListResponse> getPortfolios(PortfolioSearchRequest searchRequest, Pageable pageable);
}
