package study.sunsuwedding.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.entity.Payment;
import study.sunsuwedding.domain.payment.exception.PaymentException;
import study.sunsuwedding.domain.payment.exception.RetryExhaustedException;
import study.sunsuwedding.domain.payment.repository.PaymentRepository;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessingService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public void validateForApproval(Long userId, PaymentApproveRequest request) {
        validateUserNotPremium(userId);
        validatePaymentIsApprovable(request);
    }

    @Retryable(
            value = {TransientDataAccessException.class, QueryTimeoutException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 300, multiplier = 2, maxDelay = 1000)
    )
    @Transactional
    public void applyApproval(Long userId, String orderId, String paymentKey) {
        User user = getValidatedUser(userId);
        Payment payment = getPaymentByOrderId(orderId);

        user.upgrade();
        payment.markAsApproved(paymentKey);
    }

    @Recover
    public void recoverApplyApproval(Exception e, Long userId, String orderId, String paymentKey) {
        log.error("결제 DB 반영 최종 실패 (3회 재시도 후): orderId={}, error={}", orderId, e.getMessage());
        throw new RetryExhaustedException("결제 DB 업데이트 재시도 실패", e);
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
