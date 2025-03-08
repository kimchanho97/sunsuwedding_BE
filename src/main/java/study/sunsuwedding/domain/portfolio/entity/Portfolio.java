package study.sunsuwedding.domain.portfolio.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import study.sunsuwedding.common.entity.BaseTimeEntity;
import study.sunsuwedding.domain.user.entity.Planner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "portfolio")
@Getter
@SQLDelete(sql = "UPDATE portfolio SET is_deleted = true, deleted_at = NOW() WHERE portfolio_id = ?")
@SQLRestriction("is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Portfolio extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "planner_id")
    private Planner planner;

    @Column(nullable = false)
    private String plannerName;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false, length = 1000)
    private String career;

    @Column(nullable = false, length = 1000)
    private String partnerCompany;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
    private List<PortfolioItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
    private List<PortfolioImage> images = new ArrayList<>();

    private Long totalPrice;
    private Long contractCount;
    private Double avgStars;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;

    @Builder
    public Portfolio(Planner planner, String plannerName, String title, String location, String description, String career, String partnerCompany, Long totalPrice, Long contractCount, Long avgPrice, Long minPrice, Long maxPrice, Double avgStars) {
        this.planner = planner;
        this.plannerName = plannerName;
        this.title = title;
        this.location = location;
        this.description = description;
        this.career = career;
        this.partnerCompany = partnerCompany;
        this.totalPrice = totalPrice;
        this.contractCount = contractCount;
        this.avgStars = avgStars;
    }

    public void update(String plannerName, String title, String description, String location, String career, String partnerCompany) {
        this.plannerName = plannerName;
        this.title = title;
        this.description = description;
        this.location = location;
        this.career = career;
        this.partnerCompany = partnerCompany;
    }
}
