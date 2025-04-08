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
    PAYMENT_APPROVAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 3004, "결제 승인 처리 중 오류가 발생했습니다."),
    ALREADY_APPROVED(HttpStatus.BAD_REQUEST, 3005, "이미 승인된 결제입니다."),
    PAYMENT_STATUS_QUERY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 3006, "결제 상태 확인 중 오류가 발생했습니다."),
    PAYMENT_STATUS_NOT_CONFIRMED_YET(HttpStatus.ACCEPTED, 3007, "결제 승인 여부를 아직 확인할 수 없습니다. 일정 시간 후 자동으로 처리됩니다."),
    INTERNAL_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 3008, "결제 승인은 완료되었으나 등급 업그레이드 처리 중 문제가 발생했습니다. 곧 자동 복구됩니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
