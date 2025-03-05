package study.sunsuwedding.domain.payment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import study.sunsuwedding.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

    ALREADY_PREMIUM_USER(HttpStatus.BAD_REQUEST, 3000, "이미 프리미엄 회원입니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
