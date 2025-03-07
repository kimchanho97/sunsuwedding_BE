package study.sunsuwedding.domain.portfolio.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import study.sunsuwedding.common.response.SliceResponse;
import study.sunsuwedding.domain.portfolio.dto.req.PortfolioSearchRequest;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioListResponse;

public interface PortfolioQueryService {

    Slice<PortfolioListResponse> getPortfoliosV1EntityPaging(Long userId, PortfolioSearchRequest searchRequest, Pageable pageable);

    Slice<PortfolioListResponse> getPortfoliosV2DtoPaging(Long userId, PortfolioSearchRequest searchRequest, Pageable pageable);

    SliceResponse<PortfolioListResponse> getPortfoliosV3DtoCursorPaging(Long userId, PortfolioSearchRequest searchRequest, Long cursor, Pageable pageable);

}
