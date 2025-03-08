package study.sunsuwedding.domain.portfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import study.sunsuwedding.domain.favorite.repository.FavoriteRepository;
import study.sunsuwedding.domain.portfolio.dto.req.PortfolioRequest;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioResponse;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;
import study.sunsuwedding.domain.portfolio.entity.PortfolioImage;
import study.sunsuwedding.domain.portfolio.exception.PortfolioException;
import study.sunsuwedding.domain.portfolio.repository.*;
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
    private final FavoriteRepository favoriteRepository;
    private final PortfolioImageRepository portfolioImageRepository;
    private final PortfolioItemRepository portfolioItemRepository;
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

        // 2. 포트폴리오 아이템 & 이미지 저장
        savePortfolioData(portfolio, request, images);
    }

    @Override
    @Transactional
    public void updatePortfolio(Long userId, PortfolioRequest request, List<MultipartFile> images) {
        Planner planner = getPlannerById(userId);
        Portfolio portfolio = getPortfolioByPlanner(planner);

        deleteExistingPortfolioData(portfolio);
        updatePortfolioData(request, portfolio);
        savePortfolioData(portfolio, request, images);
    }

    @Override
    public void deletePortfolio(Long userId) {
        Planner planner = getPlannerById(userId);
        Portfolio portfolio = getPortfolioByPlanner(planner);
        deleteExistingPortfolioData(portfolio);
        portfolioRepository.delete(portfolio);
    }

    @Override
    public PortfolioResponse getPortfolio(Long userId, Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findPortfolioWithDetails(portfolioId)
                .orElseThrow(PortfolioException::portfolioNotFound);

        if (userId == null) {
            return PortfolioResponse.fromEntity(portfolio, false);
        }

        // 찜 여부 확인
        boolean isLiked = favoriteRepository.existsByUserIdAndPortfolioId(userId, portfolioId);
        return PortfolioResponse.fromEntity(portfolio, isLiked);
    }

    private void updatePortfolioData(PortfolioRequest request, Portfolio portfolio) {
        portfolio.update(
                request.getPlannerName(),
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getCareer(),
                request.getPartnerCompany());
    }

    private void savePortfolioData(Portfolio portfolio, PortfolioRequest request, List<MultipartFile> images) {
        // 1. 포트폴리오 아이템 저장 (배치 처리)
        portfolioItemJdbcRepository.batchInsert(request.toPortfolioItems(portfolio));

        // 2. 이미지 S3 업로드 후, 엔티티로 변환 후 배치 저장
        List<S3UploadResultDto> uploadResults = s3ImageService.uploadImages(images);
        List<PortfolioImage> portfolioImages = uploadResults.stream()
                .map(result -> new PortfolioImage(portfolio, result.getFileUrl(), result.getFileName(), result.equals(uploadResults.get(0))))
                .toList();
        portfolioImageJdbcRepository.batchInsert(portfolioImages);
    }

    private void deleteExistingPortfolioData(Portfolio portfolio) {
        // 1. 기존 포트폴리오 아이템 삭제 -> 벌크 삭제
        portfolioItemRepository.deleteByPortfolioId(portfolio.getId());

        // 2. 기존 이미지 삭제 (S3 + DB) -> 벌크 삭제
        List<PortfolioImage> existingImages = portfolioImageRepository.findByPortfolio(portfolio);
        if (!existingImages.isEmpty()) {
            List<String> fileNames = existingImages.stream()
                    .map(PortfolioImage::getFileName)
                    .toList();

            s3ImageService.deleteImages(fileNames);
            portfolioImageRepository.deleteByPortfolioId(portfolio.getId());
        }
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

    private Portfolio getPortfolioByPlanner(Planner planner) {
        return portfolioRepository.findByPlanner(planner)
                .orElseThrow(PortfolioException::portfolioNotFound);
    }
}
