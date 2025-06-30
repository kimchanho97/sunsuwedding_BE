package study.sunsuwedding.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.payment.dto.TossWebhookRequest;
import study.sunsuwedding.domain.payment.entity.Payment;
import study.sunsuwedding.domain.payment.entity.PaymentFailureLog;
import study.sunsuwedding.domain.payment.repository.PaymentFailureLogRepository;
import study.sunsuwedding.domain.payment.repository.PaymentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossWebhookService {

    private final PaymentRepository paymentRepository;
    private final PaymentFailureLogRepository failureLogRepository;
    private final PaymentProcessingService processingService;

    @Transactional
    public void processWebhook(TossWebhookRequest payload) {
        try {
            String orderId = payload.getData().getOrderId();
            String status = payload.getData().getStatus();
            String paymentKey = payload.getData().getPaymentKey();

            log.info("[웹훅] 수신: orderId={}, status={}, paymentKey={}", orderId, status, paymentKey);

            // 1. 결제 완료 상태(DONE)만 처리
            if (!"DONE".equalsIgnoreCase(status)) {
                return;
            }

            // 2. 결제 정보 조회
            Payment payment = paymentRepository.findByOrderId(orderId).orElse(null);
            if (payment == null) {
                return;
            }

            // 3. 이미 처리된 경우 무시 (멱등성 보장)
            if (payment.isApproved()) {
                return;
            }

            // 4. PaymentProcessingService를 통한 결제 승인 처리
            Long userId = payment.getUser().getId();
            processingService.applyApproval(userId, orderId, paymentKey);

            // 5. 실패 로그 복구 처리
            failureLogRepository.findPaymentFailureLogByOrderId(orderId)
                    .ifPresent(PaymentFailureLog::markAsRecovered);

            log.info("[웹훅] 복구 완료: orderId={}, userId={}", orderId, userId);

        } catch (Exception e) {
            log.error("[웹훅] 예상치 못한 오류: orderId={}", payload.getData().getOrderId(), e);
        }
    }
}
