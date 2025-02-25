package study.sunsuwedding.common.response;

import lombok.Getter;
import study.sunsuwedding.common.exception.BaseException;
import study.sunsuwedding.common.exception.ErrorCode;

@Getter
public class ErrorResponse {

    private final int code;
    private final String message;

    public ErrorResponse(BaseException e) {
        this.code = e.getCode();
        this.message = e.getMessage();
    }

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
