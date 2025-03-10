package study.sunsuwedding.domain.portfolio.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.sunsuwedding.common.entity.BaseTimeEntity;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "portfolio_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private Long itemPrice;

    public PortfolioItem(Portfolio portfolio, String itemName, Long itemPrice) {
        this.portfolio = portfolio;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }

}
