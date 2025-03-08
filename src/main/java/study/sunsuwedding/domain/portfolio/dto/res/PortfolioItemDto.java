package study.sunsuwedding.domain.portfolio.dto.res;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PortfolioItemDto {

    private final String itemName;
    private final Long itemPrice;
}
