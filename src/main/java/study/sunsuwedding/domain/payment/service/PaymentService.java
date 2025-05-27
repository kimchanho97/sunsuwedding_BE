package study.sunsuwedding.domain.payment.service;

import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.dto.PaymentSaveResponse;

public interface PaymentService {

    PaymentSaveResponse save(Long userId);

    void approvePayment(Long userId, PaymentApproveRequest request);
}
