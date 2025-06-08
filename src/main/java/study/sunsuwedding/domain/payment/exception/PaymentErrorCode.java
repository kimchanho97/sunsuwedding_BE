package study.sunsuwedding.domain.payment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import study.sunsuwedding.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

    // 결제 데이터 저장
    ALREADY_PREMIUM_USER(HttpStatus.BAD_REQUEST, 3001, "이미 프리미엄 회원이므로 결제를 진행할 수 없습니다."),

    // 결제 승인
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, 3002, "결제 정보가 존재하지 않습니다."),
    PAYMENT_MISMATCH(HttpStatus.BAD_REQUEST, 3003, "결제 요청 정보가 기존 결제 정보와 일치하지 않습니다."),

    ALREADY_APPROVED(HttpStatus.BAD_REQUEST, 3004, "이미 승인된 결제입니다."),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, 3007, "결제 처리에 실패했습니다."),
    PAYMENT_TIMEOUT(HttpStatus.ACCEPTED, 3007, "결제 처리 중입니다. 잠시 후 다시 확인해주세요."),
    PAYMENT_UNCERTAIN(HttpStatus.ACCEPTED, 3006, "결제 처리 중입니다. 잠시 후 다시 확인해주세요.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
