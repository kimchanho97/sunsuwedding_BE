package study.sunsuwedding.domain.payment.client;

import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.dto.TossPaymentResponse;

public interface PaymentApprovalClient {

    TossPaymentResponse approve(PaymentApproveRequest request);

    TossPaymentResponse getPaymentResponseByOrderId(String orderId);
}
