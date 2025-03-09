package study.sunsuwedding.domain.portfolio.service;

import org.springframework.web.multipart.MultipartFile;
import study.sunsuwedding.domain.portfolio.dto.req.PortfolioRequest;
import study.sunsuwedding.domain.portfolio.dto.res.OwnPortfolioResponse;
import study.sunsuwedding.domain.portfolio.dto.res.PortfolioResponse;

import java.util.List;

public interface PortfolioService {

    void createPortfolio(Long userId, PortfolioRequest request, List<MultipartFile> images);

    PortfolioResponse getPortfolio(Long userId, Long portfolioId);

    void deletePortfolio(Long userId);

    OwnPortfolioResponse getOwnPortfolio(Long userId);

    void updatePortfolio(Long userId, PortfolioRequest request, List<String> existingImages, List<MultipartFile> newImages, List<String> deletedImages);
}
