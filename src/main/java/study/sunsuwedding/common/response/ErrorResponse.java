package study.sunsuwedding.common.response;

import lombok.Getter;
import study.sunsuwedding.common.exception.BaseException;

@Getter
public class ErrorResponse {

    private final int code;
    private final String message;

    public ErrorResponse(BaseException e) {
        this.code = e.getCode();
        this.message = e.getMessage();
    }
}
