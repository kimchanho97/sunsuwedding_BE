package study.sunsuwedding.domain.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.payment.client.PaymentApprovalClient;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.dto.PaymentSaveRequest;
import study.sunsuwedding.domain.payment.entity.Payment;
import study.sunsuwedding.domain.payment.exception.PaymentException;
import study.sunsuwedding.domain.payment.repository.PaymentRepository;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentApprovalClient paymentApprovalClient;

    @Override
    @Transactional
    public void save(Long userId, PaymentSaveRequest request) {
        User user = getValidatedUser(userId);
        paymentRepository.save(new Payment(user, request.getOrderId(), request.getAmount()));
    }

    @Override
    @Transactional
    public void approvePaymentAndUpgradeUser(Long userId, PaymentApproveRequest request) {
        User user = getValidatedUser(userId);
        Payment payment = getPaymentByOrderId(request.getOrderId());

        // 1. 검증: 결제 정보와 요청이 일치하는지 확인
        validatePaymentRequest(payment, request);
        // 2. 토스 페이먼츠 승인 요청
        paymentApprovalClient.approve(request);
        // 3. 유저 등급 업데이트
        user.upgrade();
        // 4. 결제 정보 승인 처리
        payment.markAsApproved(request.getPaymentKey());
    }

    private void validatePaymentRequest(Payment payment, PaymentApproveRequest request) {
        if (!payment.matches(request.getOrderId(), request.getAmount())) {
            throw PaymentException.paymentMismatch();
        }
    }

    private Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(PaymentException::paymentNotFound);
    }

    private User getValidatedUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserException::userNotFound);

        // 이미 프리미엄 회원인 경우
        if (user.isPremium()) {
            throw PaymentException.alreadyPremiumUser();
        }
        return user;
    }
}
