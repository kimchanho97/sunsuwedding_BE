package study.sunsuwedding.domain.payment.exception;

import study.sunsuwedding.common.exception.BusinessException;
import study.sunsuwedding.common.exception.ErrorCode;

import static study.sunsuwedding.domain.payment.exception.PaymentErrorCode.ALREADY_PREMIUM_USER;

public class PaymentException extends BusinessException {

    public PaymentException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static PaymentException alreadyPremiumUser() {
        return new PaymentException(ALREADY_PREMIUM_USER);
    }
}
