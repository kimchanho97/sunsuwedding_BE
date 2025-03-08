package study.sunsuwedding.domain.portfolio.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import study.sunsuwedding.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum PortfolioErrorCode implements ErrorCode {

    PORTFOLIO_IMAGE_EMPTY(HttpStatus.BAD_REQUEST, 4000, "포트폴리오 이미지가 비어있습니다."),
    PLANNER_NOT_FOUND(HttpStatus.NOT_FOUND, 4001, "해당 플래너를 찾을 수 없습니다. 탈퇴했거나 존재하지 않는 계정입니다."),
    PORTFOLIO_NOT_FOUND(HttpStatus.NOT_FOUND, 4002, "요청한 포트폴리오가 삭제되었거나 존재하지 않습니다."),
    PORTFOLIO_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, 4003, "해당 플래너의 포트폴리오가 이미 존재합니다. 플래너당 하나의 포트폴리오만 생성할 수 있습니다."),
    PORTFOLIO_IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, 4004, "등록 가능한 이미지 개수(최대 5개)를 초과하였습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
