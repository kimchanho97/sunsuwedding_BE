package study.sunsuwedding.domain.payment.exception;

import study.sunsuwedding.common.exception.BusinessException;
import study.sunsuwedding.common.exception.ErrorCode;

import static study.sunsuwedding.domain.payment.exception.PaymentErrorCode.*;

public class PaymentException extends BusinessException {

    public PaymentException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static PaymentException alreadyPremiumUser() {
        return new PaymentException(ALREADY_PREMIUM_USER);
    }

    public static PaymentException paymentNotFound() {
        return new PaymentException(PAYMENT_NOT_FOUND);
    }

    public static PaymentException paymentMismatch() {
        return new PaymentException(PAYMENT_MISMATCH);
    }

    public static PaymentException paymentApprovalFailed() {
        return new PaymentException(PAYMENT_APPROVAL_FAILED);
    }

    public static PaymentException alreadyApproved() {
        return new PaymentException(ALREADY_APPROVED);
    }

    public static PaymentException statusQueryFailed() {
        return new PaymentException(PAYMENT_STATUS_QUERY_FAILED);
    }

    public static PaymentException statusNotConfirmedYet() {
        return new PaymentException(PAYMENT_STATUS_NOT_CONFIRMED_YET);
    }

    public static PaymentException internalProcessingFailed() {
        return new PaymentException(INTERNAL_PROCESSING_FAILED);
    }

}
