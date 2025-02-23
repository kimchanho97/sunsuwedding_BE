package study.sunsuwedding.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import study.sunsuwedding.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 2000, "서비스를 탈퇴했거나 가입하지 않은 유저의 요청입니다."),
    ;

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
