package study.sunsuwedding.domain.portfolio.service;

import org.springframework.data.domain.Pageable;
import study.sunsuwedding.common.response.CursorPaginationResponse;
import study.sunsuwedding.common.response.OffsetPaginationResponse;
import study.sunsuwedding.domain.portfolio.dto.req.PortfolioSearchRequest;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioListResponse;

public interface PortfolioQueryService {

    OffsetPaginationResponse<PortfolioListResponse> getPortfoliosV1EntityPaging(Long userId, PortfolioSearchRequest searchRequest, Pageable pageable);

    OffsetPaginationResponse<PortfolioListResponse> getPortfoliosV2DtoPaging(Long userId, PortfolioSearchRequest searchRequest, Pageable pageable);

    CursorPaginationResponse<PortfolioListResponse> getPortfoliosV3DtoCursorPaging(Long userId, PortfolioSearchRequest searchRequest, Long cursor, Pageable pageable);

}
