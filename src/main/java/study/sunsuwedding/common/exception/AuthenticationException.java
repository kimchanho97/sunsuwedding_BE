package study.sunsuwedding.common.exception;

import lombok.Getter;

@Getter
public class AuthenticationException extends BaseException {

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
