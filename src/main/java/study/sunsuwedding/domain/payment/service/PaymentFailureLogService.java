package study.sunsuwedding.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.dto.TossPaymentResponse;
import study.sunsuwedding.domain.payment.entity.PaymentFailureLog;
import study.sunsuwedding.domain.payment.repository.PaymentFailureLogRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentFailureLogService {

    private final PaymentFailureLogRepository logRepository;

    @Retryable(
            value = DataAccessException.class,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordDbWriteFailure(Long userId, TossPaymentResponse response, Exception exception) {
        log.warn("[Payment][Approval Failed] orderId={}, userId={}, reason={}",
                response.getOrderId(),
                userId,
                exception.getMessage(),
                exception);

        PaymentFailureLog failureLog = new PaymentFailureLog(
                response.getOrderId(),
                response.getPaymentKey(),
                userId,
                response.getTotalAmount(),
                PaymentFailureLog.FailureType.DB_WRITE_FAILED,
                exception.getMessage()
        );
        logRepository.save(failureLog);
    }

    @Retryable(
            value = DataAccessException.class,
            backoff = @Backoff(delay = 100, multiplier = 2, maxDelay = 1000)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordNetworkFailure(Long userId, PaymentApproveRequest request, Exception e) {
        log.warn("[Payment][Network Failure] orderId={}, userId={}, reason={}",
                request.getOrderId(),
                userId,
                e.getMessage(),
                e);

        PaymentFailureLog failureLog = new PaymentFailureLog(
                request.getOrderId(),
                request.getPaymentKey(),
                userId,
                request.getAmount(),
                PaymentFailureLog.FailureType.NETWORK_UNCERTAIN,
                e.getMessage());
        logRepository.save(failureLog);
    }
}
