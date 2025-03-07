package study.sunsuwedding.domain.portfolio.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import study.sunsuwedding.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum PortfolioErrorCode implements ErrorCode {

    PORTFOLIO_NOT_FOUND("요청한 포트폴리오가 삭제되었거나 존재하지 않습니다.", 4000, HttpStatus.NOT_FOUND),
    PORTFOLIO_ALREADY_EXISTS("해당 플래너의 포트폴리오가 이미 존재합니다. 플래너당 하나의 포트폴리오만 생성할 수 있습니다.", 4001, HttpStatus.BAD_REQUEST),

    PORTFOLIO_IMAGE_NOT_FOUND("포트폴리오 이미지가 존재하지 않습니다.", 4002, HttpStatus.NOT_FOUND),
    PORTFOLIO_IMAGE_LIMIT_EXCEEDED("등록 가능한 이미지 개수(최대 5개)를 초과하였습니다.", 4003, HttpStatus.BAD_REQUEST),
    PORTFOLIO_IMAGE_UPLOAD_FAILED("포트폴리오 이미지 업로드 중 오류가 발생했습니다.", 4004, HttpStatus.INTERNAL_SERVER_ERROR),
    PORTFOLIO_IMAGE_ENCODING_FAILED("이미지 인코딩 중 오류가 발생했습니다.", 4005, HttpStatus.INTERNAL_SERVER_ERROR),

    PORTFOLIO_DIRECTORY_CREATION_FAILED("포트폴리오 폴더 생성 중 오류가 발생했습니다.", 4006, HttpStatus.INTERNAL_SERVER_ERROR),
    PORTFOLIO_DIRECTORY_CLEAN_FAILED("포트폴리오 폴더 정리 중 오류가 발생했습니다.", 4007, HttpStatus.INTERNAL_SERVER_ERROR),
    PORTFOLIO_IMAGE_DELETE_FAILED("포트폴리오 이미지 삭제 중 오류가 발생했습니다.", 4008, HttpStatus.INTERNAL_SERVER_ERROR),
    PORTFOLIO_IMAGE_PATH_ERROR("포트폴리오 이미지 경로 조회 중 오류가 발생했습니다.", 4009, HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final int code;
    private final HttpStatus httpStatus;
}
