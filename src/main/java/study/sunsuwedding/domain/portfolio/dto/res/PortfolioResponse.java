package study.sunsuwedding.domain.portfolio.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;
import study.sunsuwedding.domain.portfolio.entity.PortfolioImage;

import java.util.List;

@Getter
@NoArgsConstructor
public class PortfolioResponse {

    private Long plannerId;
    private String plannerName;
    private List<String> images; // S3 URL 리스트
    private String title;
    private Long contractedCount;
    private Long totalPrice;
    private List<PortfolioItemDto> items;
    private String location;
    private String description;
    private String career;
    private String partnerCompany;
    private Double averageRating;
    private Boolean isFavorited;

    public static PortfolioResponse fromEntity(Portfolio portfolio, boolean isFavorited) {
        PortfolioResponse response = new PortfolioResponse();
        response.plannerId = portfolio.getPlanner().getId();
        response.plannerName = portfolio.getPlannerName();
        response.images = portfolio.getImages().stream()
                .map(PortfolioImage::getFileUrl)
                .toList();
        response.title = portfolio.getTitle();
        response.contractedCount = portfolio.getContractedCount();
        response.totalPrice = portfolio.getTotalPrice();
        response.items = portfolio.getItems().stream()
                .map(item -> new PortfolioItemDto(item.getItemName(), item.getItemPrice()))
                .toList();
        response.location = portfolio.getLocation();
        response.description = portfolio.getDescription();
        response.career = portfolio.getCareer();
        response.partnerCompany = portfolio.getPartnerCompany();
        response.averageRating = portfolio.getAverageRating();
        response.isFavorited = isFavorited;
        return response;
    }
}
