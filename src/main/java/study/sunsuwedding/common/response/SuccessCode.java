package study.sunsuwedding.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    OK(HttpStatus.OK, 2000, "success");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
