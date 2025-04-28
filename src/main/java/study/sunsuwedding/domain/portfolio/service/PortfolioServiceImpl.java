package study.sunsuwedding.domain.portfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import study.sunsuwedding.domain.favorite.service.FavoriteService;
import study.sunsuwedding.domain.portfolio.dto.req.PortfolioRequest;
import study.sunsuwedding.domain.portfolio.dto.res.OwnPortfolioResponse;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioResponse;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;
import study.sunsuwedding.domain.portfolio.entity.PortfolioImage;
import study.sunsuwedding.domain.portfolio.exception.PortfolioException;
import study.sunsuwedding.domain.portfolio.repository.*;
import study.sunsuwedding.domain.user.entity.Planner;
import study.sunsuwedding.domain.user.repository.PlannerRepository;
import study.sunsuwedding.infra.storage.S3ImageService;
import study.sunsuwedding.infra.storage.S3UploadResultDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PlannerRepository plannerRepository;
    private final FavoriteService favoriteService;
    private final PortfolioImageRepository portfolioImageRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final PortfolioItemJdbcRepository portfolioItemJdbcRepository;
    private final PortfolioImageJdbcRepository portfolioImageJdbcRepository;
    private final S3ImageService s3ImageService;
    private final PortfolioCacheService portfolioCacheService;

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
    public void updatePortfolio(Long userId, PortfolioRequest request, List<String> existingImages, List<MultipartFile> newImages, List<String> deletedImages) {
        Planner planner = getPlannerById(userId);
        Portfolio portfolio = getPortfolioByPlanner(planner);
        // 1. 포트폴리오 기본 정보 업데이트
        updatePortfolio(request, portfolio);

        // 2. 삭제할 이미지 처리
        if (!CollectionUtils.isEmpty(deletedImages)) {
            List<String> fileNamesToDelete = portfolioImageRepository.findFileNamesByUrls(deletedImages);
            if (!fileNamesToDelete.isEmpty()) {
                s3ImageService.deleteImages(fileNamesToDelete); // S3에서 이미지 삭제
                portfolioImageRepository.deleteByFileNames(fileNamesToDelete); // DB에서 이미지 삭제 (IN 절 사용)
            }
        }

        // 3. 새로운 이미지 업로드 및 DB 저장
        List<PortfolioImage> allImages = new ArrayList<>();
        if (!CollectionUtils.isEmpty(existingImages)) {
            List<PortfolioImage> existingPortfolioImages = portfolioImageRepository.findByPortfolio(portfolio);
            allImages.addAll(existingPortfolioImages);
        }

        if (!CollectionUtils.isEmpty(newImages)) {
            List<S3UploadResultDto> uploadResults = s3ImageService.uploadImages(newImages);
            List<PortfolioImage> newPortfolioImages = uploadResults.stream()
                    .map(result -> new PortfolioImage(portfolio, result.getFileName(), result.getFileUrl(), false))
                    .toList();
            portfolioImageJdbcRepository.batchInsert(newPortfolioImages);
            allImages.addAll(newPortfolioImages);
        }

        // 4. 기존 섬네일 해제 + 새로운 섬네일 설정
        allImages.forEach(PortfolioImage::clearThumbnail); // 기존 썸네일 해제
        allImages.getFirst().setThumbnail(); // 첫 번째 이미지 썸네일 지정

        // 5. 포트폴리오 아이템 업데이트 (기존 삭제 후 새로 저장)
        portfolioItemRepository.deleteByPortfolioId(portfolio.getId());
        portfolioItemJdbcRepository.batchInsert(request.toPortfolioItems(portfolio));

        // 6. 캐시 전체 무효화
        portfolioCacheService.evictAll();
    }

    @Override
    @Transactional
    public void deletePortfolio(Long userId) {
        Planner planner = getPlannerById(userId);
        Portfolio portfolio = getPortfolioByPlanner(planner);
        // deleteExistingPortfolioData(portfolio);
        portfolioRepository.delete(portfolio);
        // portfolioCacheService.evictAll(); // 캐시 전체 무효화
    }

    @Override
    public PortfolioResponse getPortfolio(Long userId, Long portfolioId) {
        // MultipleBagFetchException 방지를 위해 PortfolioItem은 fetch join, PortfolioImage는 지연 로딩 처리
        Portfolio portfolio = portfolioRepository.findWithItemsByPortfolioId(portfolioId)
                .orElseThrow(PortfolioException::portfolioNotFound);

        boolean isFavorited = false;
        if (userId != null) {
            Set<Long> favoriteIds = favoriteService.getCurrentFavoritePortfolioIds(userId);
            isFavorited = favoriteIds.contains(portfolioId);
        }
        return PortfolioResponse.fromEntity(portfolio, isFavorited);
    }

    @Override
    public OwnPortfolioResponse getOwnPortfolio(Long userId) {
        Planner planner = getPlannerById(userId);

        // MultipleBagFetchException 방지를 위해 PortfolioItem은 fetch join, PortfolioImage는 지연 로딩 처리
        return portfolioRepository.findWithItemsByPlanner(planner)
                .map(OwnPortfolioResponse::fromEntity)
                .orElse(null); // 포트폴리오가 없으면 null 반환
    }

    private void updatePortfolio(PortfolioRequest request, Portfolio portfolio) {
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
                .map(result -> new PortfolioImage(portfolio, result.getFileName(), result.getFileUrl(), result.equals(uploadResults.get(0))))
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
