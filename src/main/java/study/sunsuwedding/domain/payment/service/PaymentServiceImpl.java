package study.sunsuwedding.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.payment.client.PaymentApprovalClient;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.dto.PaymentSaveResponse;
import study.sunsuwedding.domain.payment.dto.TossPaymentResponse;
import study.sunsuwedding.domain.payment.entity.Payment;
import study.sunsuwedding.domain.payment.exception.PaymentException;
import study.sunsuwedding.domain.payment.exception.RetryExhaustedException;
import study.sunsuwedding.domain.payment.repository.PaymentRepository;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.UserRepository;

import java.util.UUID;

import static study.sunsuwedding.domain.payment.constant.PaymentConst.SUNSU_MEMBERSHIP_PRICE;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentApprovalClient approvalClient;
    private final PaymentProcessingService processingService;
    private final PaymentFailureLogService failureLogService;

    @Override
    @Transactional
    public PaymentSaveResponse save(Long userId) {
        User user = getValidatedUser(userId);

        String orderId = UUID.randomUUID().toString();
        long amount = SUNSU_MEMBERSHIP_PRICE;

        paymentRepository.save(new Payment(user, orderId, amount));

        return new PaymentSaveResponse(orderId, amount);
    }

    /**
     * 1) 검증 → 2) 외부 승인 → 3) 내부 업데이트
     */
    @Override
    public void approvePayment(Long userId, PaymentApproveRequest request) {
        // 1) 사전 검증
        processingService.validateForApproval(userId, request);

        // 2) Toss 승인 요청
        try {
            TossPaymentResponse response = approvalClient.approve(request);
            processSuccess(userId, response);
        } catch (PaymentException e) {
            if (!e.isTimeout()) {
                throw e;
            }
            handleApprovalTimeout(userId, request);
        }
    }

    private void handleApprovalTimeout(Long userId, PaymentApproveRequest request) {
        try {
            Thread.sleep(500);
            TossPaymentResponse response = approvalClient.getPaymentResponseByOrderId(request.getOrderId());

            if (response.isDone()) {
                processSuccess(userId, response);
            } else {
                throw PaymentException.paymentFailed();
            }

        } catch (Exception e) {
            failureLogService.recordNetworkFailure(userId, request, e);
            throw PaymentException.paymentUncertainStatus();
        }
    }

    private void processSuccess(Long userId, TossPaymentResponse response) {
        try {
            processingService.applyApproval(userId, response.getOrderId(), response.getPaymentKey());

        } catch (RetryExhaustedException e) {
            // 3번 재시도 후에도 실패한 경우
            log.warn("결제 DB 반영 실패 (재시도 소진): orderId={}", response.getOrderId());
            failureLogService.recordDbWriteFailure(userId, response, e);
            throw PaymentException.paymentCompletedButDelayed();

        } catch (DataIntegrityViolationException e) {
            // 재시도해도 의미 없는 에러 (즉시 실패 처리)
            log.error("결제 DB 반영 실패 (복구 불가능한 에러): orderId={}", response.getOrderId(), e);
            failureLogService.recordDbWriteFailure(userId, response, e);
            throw PaymentException.paymentCompletedButDelayed();

        } catch (Exception e) {
            // 예상치 못한 에러
            log.error("결제 DB 반영 실패 (예상치 못한 에러): orderId={}", response.getOrderId(), e);
            failureLogService.recordDbWriteFailure(userId, response, e);
            throw PaymentException.paymentCompletedButDelayed();
        }
    }

    private User getValidatedUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserException::userNotFound);

        if (user.isPremium()) {
            throw PaymentException.alreadyPremiumUser();
        }
        return user;
    }

}
