package study.sunsuwedding.domain.payment.client;

import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;

public interface PaymentApprovalClient {

    void approve(PaymentApproveRequest request);
}
