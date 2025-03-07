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

    private Long id;
    private String image;
    private String title;
    private String plannerName;
    private Long price;
    private String location;
    private Long contractCount;
    private Double avgStars;
    private Boolean isLiked;

    @QueryProjection
    public PortfolioListResponse(Long id, String image, String title, String plannerName, Long price, String location, Long contractCount, Double avgStars, Boolean isLiked) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.plannerName = plannerName;
        this.price = price;
        this.location = location;
        this.contractCount = contractCount;
        this.avgStars = avgStars;
        this.isLiked = isLiked;
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
                portfolio.getContractCount(),
                portfolio.getAvgStars(),
                favoritePortfolioIds.contains(portfolio.getId())
        );
    }
}
