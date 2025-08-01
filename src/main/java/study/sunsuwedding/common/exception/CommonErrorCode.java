package study.sunsuwedding.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),

    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, 401, "로그인이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403, "접근이 거부되었습니다."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, 409, "이미 존재하는 리소스입니다."),
    INVALID_REQUEST_FORMAT(HttpStatus.BAD_REQUEST, 400, "요청 형식이 잘못되었습니다."),
    SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
