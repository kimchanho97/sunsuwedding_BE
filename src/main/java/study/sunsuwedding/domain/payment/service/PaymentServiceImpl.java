package study.sunsuwedding.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.payment.client.PaymentApprovalClient;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.dto.PaymentSaveResponse;
import study.sunsuwedding.domain.payment.dto.TossPaymentResponse;
import study.sunsuwedding.domain.payment.entity.Payment;
import study.sunsuwedding.domain.payment.exception.PaymentException;
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
            processingService.applyApproval(userId, response);
        } catch (Exception e) {
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
