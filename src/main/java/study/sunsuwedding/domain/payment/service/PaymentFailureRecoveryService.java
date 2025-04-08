package study.sunsuwedding.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.payment.entity.Payment;
import study.sunsuwedding.domain.payment.entity.PaymentFailureLog;
import study.sunsuwedding.domain.payment.exception.PaymentException;
import study.sunsuwedding.domain.payment.repository.PaymentFailureLogRepository;
import study.sunsuwedding.domain.payment.repository.PaymentRepository;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentFailureRecoveryService {

    private final PaymentFailureLogRepository failureLogRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    /**
     * [결제 실패 복구 배치]
     */
    @Transactional
    @Scheduled(fixedDelay = 60 * 1000)
    public void recoverFailedPayments() {
        List<PaymentFailureLog> failureLogs = failureLogRepository.findUnrecovered();

        for (PaymentFailureLog failureLogEntry : failureLogs) {
            try {
                // 1. 유저 & 결제 정보 재조회
                User user = userRepository.findById(failureLogEntry.getUserId())
                        .orElseThrow(UserException::userNotFound);

                Payment payment = paymentRepository.findByOrderId(failureLogEntry.getOrderId())
                        .orElseThrow(PaymentException::paymentNotFound);

                // 2. 재처리 (유저 업그레이드 & 결제 승인 마킹)
                user.upgrade();
                payment.markAsApproved(failureLogEntry.getPaymentKey());

                // 3. 복구 완료 처리
                failureLogEntry.markAsRecovered();
                log.info("[복구][성공] orderId={}, userId={}", failureLogEntry.getOrderId(), failureLogEntry.getUserId());
            } catch (Exception e) {
                log.warn("[복구][실패] orderId={}, userId={}, reason={}", failureLogEntry.getOrderId(), failureLogEntry.getUserId(), e.getMessage(), e);
            }
        }
    }
}
