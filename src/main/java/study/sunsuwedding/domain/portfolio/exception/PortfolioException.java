package study.sunsuwedding.domain.portfolio.exception;

import study.sunsuwedding.common.exception.BusinessException;
import study.sunsuwedding.common.exception.ErrorCode;

import static study.sunsuwedding.domain.portfolio.exception.PortfolioErrorCode.*;

public class PortfolioException extends BusinessException {

    public PortfolioException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static PortfolioException notFound() {
        return new PortfolioException(PORTFOLIO_NOT_FOUND);
    }

    public static PortfolioException alreadyExists() {
        return new PortfolioException(PORTFOLIO_ALREADY_EXISTS);
    }

    public static PortfolioException imageNotFound() {
        return new PortfolioException(PORTFOLIO_IMAGE_NOT_FOUND);
    }

    public static PortfolioException imageLimitExceeded() {
        return new PortfolioException(PORTFOLIO_IMAGE_LIMIT_EXCEEDED);
    }

    public static PortfolioException imageUploadFailed() {
        return new PortfolioException(PORTFOLIO_IMAGE_UPLOAD_FAILED);
    }


}
