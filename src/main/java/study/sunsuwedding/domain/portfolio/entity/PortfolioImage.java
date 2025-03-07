package study.sunsuwedding.domain.portfolio.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "portfolio_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_image_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    private String fileName; // S3에 저장된 파일명(Key)
    private String fileUrl; // S3에 저장된 이미지 URL

    @Column(nullable = false)
    private Boolean isThumbnail;

    @Builder
    public PortfolioImage(Portfolio portfolio, String fileName, String fileUrl, Boolean isThumbnail) {
        this.portfolio = portfolio;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.isThumbnail = isThumbnail;
    }

}
