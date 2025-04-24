package study.sunsuwedding.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.entity.PaymentFailureLog;
import study.sunsuwedding.domain.payment.repository.PaymentFailureLogRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentFailureLogService {

    private final PaymentFailureLogRepository logRepository;

    @Transactional
    public void recordFailure(Long userId, PaymentApproveRequest request, Exception exception) {
        // 1) 경고 로그
        log.warn("[Payment][Approval Failed] orderId={}, userId={}, reason={}",
                request.getOrderId(),
                userId,
                exception.getMessage(),
                exception);

        // 2) DB 저장
        PaymentFailureLog failureLog = new PaymentFailureLog(
                request.getOrderId(),
                request.getPaymentKey(),
                userId,
                exception.getMessage()
        );
        logRepository.save(failureLog);
    }

}
