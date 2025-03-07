package study.sunsuwedding.domain.portfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import study.sunsuwedding.domain.portfolio.dto.req.PortfolioRequest;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;
import study.sunsuwedding.domain.portfolio.entity.PortfolioImage;
import study.sunsuwedding.domain.portfolio.exception.PortfolioException;
import study.sunsuwedding.domain.portfolio.repository.PortfolioImageJdbcRepository;
import study.sunsuwedding.domain.portfolio.repository.PortfolioItemJdbcRepository;
import study.sunsuwedding.domain.portfolio.repository.PortfolioRepository;
import study.sunsuwedding.domain.user.entity.Planner;
import study.sunsuwedding.domain.user.repository.PlannerRepository;
import study.sunsuwedding.infra.storage.S3ImageService;
import study.sunsuwedding.infra.storage.S3UploadResultDto;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PlannerRepository plannerRepository;
    private final PortfolioItemJdbcRepository portfolioItemJdbcRepository;
    private final PortfolioImageJdbcRepository portfolioImageJdbcRepository;
    private final S3ImageService s3ImageService;

    @Override
    @Transactional
    public void createPortfolio(Long userId, PortfolioRequest request, List<MultipartFile> images) {
        Planner planner = getPlannerById(userId);
        checkIfAlreadyRegisteredPortfolio(planner);
        // 1. 포트폴리오 저장 (JPA)
        Portfolio portfolio = portfolioRepository.save(request.toPortfolio(planner));

        // 2. 포트폴리오 아이템 배치 저장 (JdbcTemplate)
        portfolioItemJdbcRepository.batchInsert(request.toPortfolioItems(portfolio));

        // 3. 이미지 업로드 → DTO 반환 후, 엔티티로 변환 후 배치 저장
        List<S3UploadResultDto> uploadResults = s3ImageService.uploadImages(images);
        List<PortfolioImage> portfolioImages = uploadResults.stream()
                .map(result -> new PortfolioImage(portfolio, result.getFileUrl(), result.getFileName(), result.equals(uploadResults.get(0))))
                .toList();
        portfolioImageJdbcRepository.batchInsert(portfolioImages);
    }

    private void checkIfAlreadyRegisteredPortfolio(Planner planner) {
        if (portfolioRepository.existsByPlanner(planner)) {
            throw PortfolioException.portfolioAlreadyExists();
        }
    }

    private Planner getPlannerById(Long userId) {
        return plannerRepository.findById(userId)
                .orElseThrow(PortfolioException::plannerNotFound);
    }
}
