package study.sunsuwedding.domain.portfolio.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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
}
