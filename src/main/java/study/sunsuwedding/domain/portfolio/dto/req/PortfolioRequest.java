package study.sunsuwedding.domain.portfolio.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;
import study.sunsuwedding.domain.portfolio.entity.PortfolioItem;
import study.sunsuwedding.domain.user.entity.Planner;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioRequest {

    @NotEmpty(message = "포트폴리오 플래너 이름은 비어있으면 안됩니다.")
    @Size(max = 10, message = "포트폴리오 플래너 이름은 최대 10자까지 입력 가능합니다.")
    private String plannerName;

    @NotEmpty(message = "포트폴리오 한줄 소개는 비어있으면 안됩니다.")
    @Size(max = 100, message = "포트폴리오 한줄 소개는 최대 100자까지 입력 가능합니다.")
    private String title;

    @NotEmpty(message = "포트폴리오 상세 소개는 비어있으면 안됩니다.")
    @Size(max = 1000, message = "포트폴리오 한줄 소개는 최대 1000자까지 입력 가능합니다.")
    private String description;

    @NotEmpty(message = "포트폴리오 위치는 비어있으면 안됩니다.")
    @Size(max = 255, message = "포트폴리오 위치는 최대 255자까지 입력 가능합니다.")
    private String location;

    @NotEmpty(message = "포트폴리오 경력은 비어있으면 안됩니다.")
    @Size(max = 1000, message = "포트폴리오 경력은 최대 1000자까지 입력 가능합니다.")
    private String career;

    @NotEmpty(message = "포트폴리오 제휴 업체는 비어있으면 안됩니다.")
    @Size(max = 1000, message = "포트폴리오 제휴 업체는 최대 1000자까지 입력 가능합니다.")
    private String partnerCompany;

    @NotNull(message = "포트폴리오 가격 리스트는 비어있으면 안됩니다.")
    private List<PortfolioItemDto> items;

    public Portfolio toPortfolio(Planner planner) {
        return Portfolio.builder()
                .planner(planner)
                .plannerName(plannerName)
                .title(title)
                .location(location)
                .description(description)
                .career(career)
                .partnerCompany(partnerCompany)
                .totalPrice(getTotalPrice())
                .contractedCount(0L)
                .averageRating(0.0)
                .build();
    }

    public List<PortfolioItem> toPortfolioItems(Portfolio portfolio) {
        return items.stream()
                .map(itemDto -> new PortfolioItem(portfolio, itemDto.getItemTitle(), itemDto.getItemPrice()))
                .toList();
    }

    private Long getTotalPrice() {
        return items.stream()
                .map(PortfolioItemDto::getItemPrice)
                .reduce(0L, Long::sum);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortfolioItemDto {

        @NotEmpty(message = "포트폴리오 가격의 제목은 비어있으면 안됩니다.")
        @Size(max = 255, message = "포트폴리오 가격의 제목은 최대 255자까지 입력 가능합니다.")
        private String itemTitle;

        @NotEmpty(message = "포트폴리오 가격은 비어있으면 안됩니다.")
        @Min(value = 0, message = "포트폴리오 가격은 양수여야 합니다.")
        private Long itemPrice;
    }

}
