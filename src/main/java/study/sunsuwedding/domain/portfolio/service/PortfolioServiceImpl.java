package study.sunsuwedding.domain.portfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.portfolio.dto.req.PortfolioSearchRequest;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioListResponse;
import study.sunsuwedding.domain.portfolio.entity.PortfolioImage;
import study.sunsuwedding.domain.portfolio.repository.PortfolioQueryRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioQueryRepository portfolioQueryRepository;

    @Override
    public Slice<PortfolioListResponse> getPortfolios(Long userId, PortfolioSearchRequest searchRequest, Pageable pageable) {
        return portfolioQueryRepository.findPortfoliosByDto(userId, searchRequest, pageable);
    }

    public Slice<PortfolioListResponse> getPortfoliosByEntity(Long userId, PortfolioSearchRequest searchRequest, Pageable pageable) {
        List<PortfolioImage> portfolioImages = portfolioQueryRepository.findPortfolioImagesByEntity(searchRequest, pageable);
        Set<Long> favoritePortfolioIds = portfolioQueryRepository.findFavoritePortfolioIds(userId);

        List<PortfolioListResponse> content = portfolioImages.stream()
                .map(portfolioImage ->
                        PortfolioListResponse.fromEntity(portfolioImage, favoritePortfolioIds))
                .toList();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.removeLast();
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

}
