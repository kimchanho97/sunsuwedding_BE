package study.sunsuwedding.domain.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;
import study.sunsuwedding.domain.portfolio.entity.PortfolioImage;

import java.util.List;

public interface PortfolioImageRepository extends JpaRepository<PortfolioImage, Long> {

    @Modifying
    @Query("DELETE FROM PortfolioImage pi WHERE pi.portfolio.id = :portfolioId")
    void deleteByPortfolioId(@Param("portfolioId") Long portfolioId);

    List<PortfolioImage> findByPortfolio(Portfolio portfolio);

    // fileUrl 리스트로 fileName 리스트 조회 (S3 삭제용)
    @Query("SELECT p.fileName FROM PortfolioImage p WHERE p.fileUrl IN :fileUrls")
    List<String> findFileNamesByUrls(@Param("fileUrls") List<String> fileUrls);

    // fileName 리스트로 DB 삭제 (IN 절 사용)
    @Modifying
    @Query("DELETE FROM PortfolioImage p WHERE p.fileName IN :fileNames")
    void deleteByFileNames(@Param("fileNames") List<String> fileNames);
}