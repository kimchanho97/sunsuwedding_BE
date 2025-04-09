package study.sunsuwedding.domain.portfolio.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;
import study.sunsuwedding.domain.portfolio.entity.PortfolioImage;

import java.util.Set;

@Getter
@NoArgsConstructor
public class PortfolioListResponse {

    private Long portfolioId;
    private String thumbnail;
    private String title;
    private String plannerName;
    private Long totalPrice;
    private String location;
    private Long contractedCount;
    private Double averageRating;
    private Boolean isFavorited;

    @QueryProjection
    public PortfolioListResponse(Long portfolioId, String thumbnail, String title, String plannerName, Long totalPrice, String location, Long contractedCount, Double averageRating, Boolean isFavorited) {
        this.portfolioId = portfolioId;
        this.thumbnail = thumbnail;
        this.title = title;
        this.plannerName = plannerName;
        this.totalPrice = totalPrice;
        this.location = location;
        this.contractedCount = contractedCount;
        this.averageRating = averageRating;
        this.isFavorited = isFavorited;
    }

    public PortfolioListResponse(PortfolioListResponse source) {
        this.portfolioId = source.portfolioId;
        this.thumbnail = source.thumbnail;
        this.title = source.title;
        this.plannerName = source.plannerName;
        this.totalPrice = source.totalPrice;
        this.location = source.location;
        this.contractedCount = source.contractedCount;
        this.averageRating = source.averageRating;
        this.isFavorited = source.isFavorited;
    }

    public static PortfolioListResponse fromEntity(PortfolioImage portfolioImage, Set<Long> favoritePortfolioIds) {
        Portfolio portfolio = portfolioImage.getPortfolio(); // PortfolioImage에서 Portfolio 가져오기
        return new PortfolioListResponse(
                portfolio.getId(),
                portfolioImage.getFileUrl(),
                portfolio.getTitle(),
                portfolio.getPlannerName(),
                portfolio.getTotalPrice(),
                portfolio.getLocation(),
                portfolio.getContractedCount(),
                portfolio.getAverageRating(),
                favoritePortfolioIds.contains(portfolio.getId())
        );
    }

    public void setFavorited(boolean isFavorited) {
        this.isFavorited = isFavorited;
    }
}
