package study.sunsuwedding.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.payment.client.PaymentApprovalClient;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.dto.PaymentSaveRequest;
import study.sunsuwedding.domain.payment.dto.TossPaymentResponse;
import study.sunsuwedding.domain.payment.entity.Payment;
import study.sunsuwedding.domain.payment.exception.PaymentException;
import study.sunsuwedding.domain.payment.repository.PaymentRepository;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentApprovalClient paymentApprovalClient;
    private final PaymentFailureLogService paymentFailureLogService;

    @Override
    @Transactional
    public void save(Long userId, PaymentSaveRequest request) {
        User user = getValidatedUser(userId);
        paymentRepository.save(new Payment(user, request.getOrderId(), request.getAmount()));
    }

    @Override
    public void approvePaymentAndUpgradeUser(Long userId, PaymentApproveRequest request) {
        // 1. 유저 & 결제 정보 사전 검증
        validateUserAndPayment(userId, request);
        // 2. Toss 승인 요청 (트랜잭션 외부)
        TossPaymentResponse response = paymentApprovalClient.approve(request);
        // 3. 승인 상태 확인
        if (!response.isDone()) {
            TossPaymentResponse checkedStatus = paymentApprovalClient.getPaymentStatusByOrderId(request.getOrderId());
            if (!checkedStatus.isDone()) {
                throw PaymentException.statusNotConfirmedYet();
            }
            response = checkedStatus;
        }
        // 4. 결제 승인 후 내부 처리 (결제 정보 저장, 유저 업그레이드)
        try {
            approveAndUpgradeInternally(userId, response);
        } catch (Exception e) {
            log.warn("[결제][승인 후 DB 처리 실패] orderId={}, userId={}, reason={}, exception={}",
                    request.getOrderId(), userId, e.getMessage(), e);
            paymentFailureLogService.logFailure(userId, request, e.getMessage());
            throw PaymentException.internalProcessingFailed();
        }
    }

    @Transactional(readOnly = true)
    protected void validateUserAndPayment(Long userId, PaymentApproveRequest request) {
        validateUserNotPremium(userId);
        validatePaymentIsApprovable(request);
    }

    @Transactional
    protected void approveAndUpgradeInternally(Long userId, TossPaymentResponse response) {
        User user = getValidatedUser(userId);
        Payment payment = getPaymentByOrderId(response.getOrderId());

        user.upgrade();
        payment.markAsApproved(response.getPaymentKey());
    }

    private void validateUserNotPremium(Long userId) {
        boolean isPremium = userRepository.findById(userId)
                .map(User::isPremium)
                .orElseThrow(UserException::userNotFound);

        if (isPremium) {
            throw PaymentException.alreadyPremiumUser();
        }
    }

    private void validatePaymentIsApprovable(PaymentApproveRequest request) {
        Payment payment = getPaymentByOrderId(request.getOrderId());
        if (payment.getPaymentKey() != null && payment.getPaidAt() != null) {
            throw PaymentException.alreadyApproved();
        }
        if (!payment.matches(request.getAmount())) {
            throw PaymentException.paymentMismatch();
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

    private Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(PaymentException::paymentNotFound);
    }
}
