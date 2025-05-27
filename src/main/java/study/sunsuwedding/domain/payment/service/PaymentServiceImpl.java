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
        // 1) 사전 검증 (read-only 트랜잭션)
        processingService.validateForApproval(userId, request);

        // 2) Toss 승인 요청 (트랜잭션 없음)
        TossPaymentResponse response = approvalClient.approve(request);
        if (!response.isDone()) {
            response = approvalClient.getPaymentStatusByOrderId(request.getOrderId());
            if (!response.isDone()) {
                throw PaymentException.statusNotConfirmedYet();
            }
        }

        // 3) 내부 DB 반영 (write 트랜잭션)
        try {
            processingService.applyApproval(userId, response);
        } catch (Exception ex) {
            failureLogService.recordFailure(userId, request, ex);
            throw PaymentException.internalProcessingFailed();
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
