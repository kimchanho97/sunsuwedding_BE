package study.sunsuwedding.domain.portfolio.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;
import study.sunsuwedding.domain.portfolio.entity.PortfolioImage;

import java.util.List;

@Getter
@NoArgsConstructor
public class PortfolioResponse {

    private List<String> images;
    private String title;
    private String plannerName;
    private Long plannerId;
    private Long contractCount;
    private PortfolioItemsDto priceInfo;
    private String location;
    private String description;
    private String career;
    private String partnerCompany;
    private Double avgStars;
    private Boolean isLiked;

    public static PortfolioResponse fromEntity(Portfolio portfolio, boolean isLiked) {
        PortfolioResponse response = new PortfolioResponse();
        response.title = portfolio.getTitle();
        response.plannerName = portfolio.getPlannerName();
        response.plannerId = portfolio.getPlanner().getId();
        response.contractCount = portfolio.getContractedCount();
        response.location = portfolio.getLocation();
        response.description = portfolio.getDescription();
        response.career = portfolio.getCareer();
        response.partnerCompany = portfolio.getPartnerCompany();
        response.avgStars = portfolio.getAverageRating();
        response.isLiked = isLiked;

        // Portfolio Images 변환
        response.images = portfolio.getImages().stream()
                .map(PortfolioImage::getFileUrl)
                .toList();

        // Portfolio Items 변환
        List<PortfolioItemDto> items = portfolio.getItems().stream()
                .map(item -> new PortfolioItemDto(item.getItemName(), item.getItemPrice()))
                .toList();

        response.priceInfo = new PortfolioItemsDto(
                portfolio.getTotalPrice(),
                items
        );

        return response;
    }

    @Getter
    @NoArgsConstructor
    public static class PortfolioItemsDto {
        private Long totalPrice;
        private List<PortfolioItemDto> items;

        public PortfolioItemsDto(Long totalPrice, List<PortfolioItemDto> items) {
            this.totalPrice = totalPrice;
            this.items = items;
        }
    }
}
