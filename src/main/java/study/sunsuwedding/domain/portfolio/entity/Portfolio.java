package study.sunsuwedding.domain.portfolio.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import study.sunsuwedding.domain.user.entity.Planner;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "portfolio")
@Getter
@SQLDelete(sql = "UPDATE portfolio SET is_deleted = true, deleted_at = NOW() WHERE portfolio_id = ?")
@SQLRestriction("is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Portfolio {

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

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Long totalPrice;
    private Long contractCount;
    private Long avgPrice;
    private Long minPrice;
    private Long maxPrice;
    private Double avgStars;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;

}
