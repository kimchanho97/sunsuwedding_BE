package study.sunsuwedding.domain.portfolio.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import study.sunsuwedding.domain.portfolio.dto.req.PortfolioSearchRequest;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioListResponse;

public interface PortfolioService {

    Slice<PortfolioListResponse> getPortfolios(Long userId, PortfolioSearchRequest searchRequest, Pageable pageable);
}
