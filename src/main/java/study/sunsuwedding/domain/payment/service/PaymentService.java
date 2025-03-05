package study.sunsuwedding.domain.payment.service;

import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.dto.PaymentSaveRequest;

public interface PaymentService {

    void save(Long userId, PaymentSaveRequest request);

    void approveAndUserGradeUpdate(Long userId, PaymentApproveRequest request);
}
