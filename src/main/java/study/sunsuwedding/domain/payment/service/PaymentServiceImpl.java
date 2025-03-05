package study.sunsuwedding.domain.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.sunsuwedding.domain.payment.dto.PaymentSaveRequest;
import study.sunsuwedding.domain.payment.entity.Payment;
import study.sunsuwedding.domain.payment.exception.PaymentException;
import study.sunsuwedding.domain.payment.repository.PaymentRepository;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.UserRepository;

import static study.sunsuwedding.domain.user.constant.Grade.PREMIUM;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Override
    public void save(Long userId, PaymentSaveRequest request) {
        User user = getValidatedUser(userId);

        // 승인이 실패한 결제 정보가 존재할 수 있으므로, 결제 정보가 존재하면 업데이트
        paymentRepository
                .findByUserId(userId)
                .ifPresentOrElse(
                        payment -> payment.update(request.getOrderId(), request.getAmount()), // 기존 결제 정보 업데이트
                        () -> paymentRepository.save(new Payment(user, request.getOrderId(), request.getAmount())) // 새로운 결제 정보 저장
                );
    }

    private User getValidatedUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserException::userNotFound);

        // 이미 프리미엄 회원인 경우
        if (user.getGrade() == PREMIUM) {
            throw PaymentException.alreadyPremiumUser();
        }

        return user;
    }
}
