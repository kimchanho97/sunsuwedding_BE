package study.sunsuwedding.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import study.sunsuwedding.common.response.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        log.error("BaseException", e);

        return ResponseEntity
                .status(e.getHttpStatus())
                .body(new ErrorResponse(e));
    }
}
