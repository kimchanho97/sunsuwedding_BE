package study.sunsuwedding.domain.portfolio.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;
import study.sunsuwedding.domain.portfolio.entity.PortfolioImage;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OwnPortfolioResponse {

    private String plannerName;
    private List<PortfolioItemDto> items;
    private List<String> images;
    private String title;
    private String description;
    private String location;
    private String career;
    private String partnerCompany;

    public static OwnPortfolioResponse fromEntity(Portfolio portfolio) {
        return new OwnPortfolioResponse(
                portfolio.getPlannerName(),
                portfolio.getItems().stream()
                        .map(item -> new PortfolioItemDto(item.getItemName(), item.getItemPrice()))
                        .toList(),
                portfolio.getImages().stream()
                        .map(PortfolioImage::getFileUrl)
                        .toList(),
                portfolio.getTitle(),
                portfolio.getDescription(),
                portfolio.getLocation(),
                portfolio.getCareer(),
                portfolio.getPartnerCompany()
        );
    }
}
