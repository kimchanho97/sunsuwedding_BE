package study.sunsuwedding.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.payment.dto.TossWebhookRequest;
import study.sunsuwedding.domain.payment.entity.Payment;
import study.sunsuwedding.domain.payment.entity.PaymentFailureLog;
import study.sunsuwedding.domain.payment.exception.PaymentException;
import study.sunsuwedding.domain.payment.repository.PaymentFailureLogRepository;
import study.sunsuwedding.domain.payment.repository.PaymentRepository;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossWebhookService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentFailureLogRepository failureLogRepository;

    @Transactional
    public void processWebhook(TossWebhookRequest payload) {
        String orderId = payload.getData().getOrderId();
        String status = payload.getData().getStatus();
        String paymentKey = payload.getData().getPaymentKey();

        // 1. 결제 완료 상태(DONE)만 처리
        if (!"DONE".equalsIgnoreCase(status)) {
            return;
        }

        // 2. 결제 정보 조회
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(PaymentException::paymentNotFound);

        // 3. 이미 처리된 경우 무시
        if (payment.isApproved()) {
            return;
        }

        // 4. 유저 정보 조회 및 결제 승인 처리
        User user = userRepository.findById(payment.getUser().getId())
                .orElseThrow(UserException::userNotFound);

        user.upgrade();
        payment.markAsApproved(paymentKey);
        failureLogRepository.findPaymentFailureLogByOrderId(orderId)
                .ifPresent(PaymentFailureLog::markAsRecovered);

        log.info("[웹훅][복구 완료] orderId={}, userId={}", orderId, user.getId());
    }
}
