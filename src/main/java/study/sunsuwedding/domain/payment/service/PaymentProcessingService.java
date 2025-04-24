package study.sunsuwedding.domain.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.dto.TossPaymentResponse;
import study.sunsuwedding.domain.payment.entity.Payment;
import study.sunsuwedding.domain.payment.exception.PaymentException;
import study.sunsuwedding.domain.payment.repository.PaymentRepository;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public void validateForApproval(Long userId, PaymentApproveRequest request) {
        validateUserNotPremium(userId);
        validatePaymentIsApprovable(request);
    }

    @Transactional
    public void applyApproval(Long userId, TossPaymentResponse response) {
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
