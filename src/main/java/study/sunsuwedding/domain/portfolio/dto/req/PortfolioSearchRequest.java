package study.sunsuwedding.domain.portfolio.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSearchRequest {
    private String name;
    private String location;
    private Long minPrice;
    private Long maxPrice;
}
