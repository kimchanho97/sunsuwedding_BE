package study.sunsuwedding.domain.portfolio.exception;

import study.sunsuwedding.common.exception.BusinessException;
import study.sunsuwedding.common.exception.ErrorCode;

import static study.sunsuwedding.domain.portfolio.exception.PortfolioErrorCode.*;

public class PortfolioException extends BusinessException {

    public PortfolioException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static PortfolioException portfolioNotFound() {
        return new PortfolioException(PORTFOLIO_NOT_FOUND);
    }

    public static PortfolioException portfolioImageEmpty() {
        return new PortfolioException(PORTFOLIO_IMAGE_EMPTY);
    }

    public static PortfolioException plannerNotFound() {
        return new PortfolioException(PLANNER_NOT_FOUND);
    }

    public static PortfolioException portfolioAlreadyExists() {
        return new PortfolioException(PORTFOLIO_ALREADY_EXISTS);
    }

    public static PortfolioException portfolioImageNotFound() {
        return new PortfolioException(PORTFOLIO_IMAGE_NOT_FOUND);
    }

    public static PortfolioException portfolioImageLimitExceeded() {
        return new PortfolioException(PORTFOLIO_IMAGE_LIMIT_EXCEEDED);
    }

    public static PortfolioException portfolioImageUploadFailed() {
        return new PortfolioException(PORTFOLIO_IMAGE_UPLOAD_FAILED);
    }

    public static PortfolioException portfolioImageEncodingFailed() {
        return new PortfolioException(PORTFOLIO_IMAGE_ENCODING_FAILED);
    }
}
