package study.sunsuwedding.domain.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.entity.PaymentFailureLog;
import study.sunsuwedding.domain.payment.repository.PaymentFailureLogRepository;

@Service
@RequiredArgsConstructor
public class PaymentFailureLogService {

    private final PaymentFailureLogRepository paymentFailureLogRepository;

    @Transactional
    public void logFailure(Long userId, PaymentApproveRequest request, String reason) {
        paymentFailureLogRepository.save(new PaymentFailureLog(request.getOrderId(), request.getPaymentKey(), userId, reason));
    }

}
