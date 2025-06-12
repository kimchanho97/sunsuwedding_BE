package study.sunsuwedding.domain.payment.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import study.sunsuwedding.domain.payment.constant.PaymentConst;
import study.sunsuwedding.domain.payment.dto.PaymentSaveResponse;
import study.sunsuwedding.domain.payment.entity.Payment;
import study.sunsuwedding.domain.payment.exception.PaymentException;
import study.sunsuwedding.domain.payment.repository.PaymentRepository;
import study.sunsuwedding.domain.user.entity.Couple;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("결제 정보 저장")
    void save_payment_success() {
        // given
        Long userId = 1L;
        Couple testUser = new Couple("testCouple", "couple@example.com", "password123");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        PaymentSaveResponse response = paymentService.save(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualTo(PaymentConst.SUNSU_MEMBERSHIP_PRICE);
    }

    @Test
    @DisplayName("결제 정보 저장 실패 - 존재하지 않는 유저")
    void save_payment_fail_user_not_found() {
        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.save(userId))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("결제 정보 저장 실패 - 이미 프리미엄 유저")
    void save_payment_fail_already_premium() {
        // given
        Long userId = 1L;
        Couple testUser = new Couple("testCouple", "couple@example.com", "password123");
        testUser.upgrade();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // when & then
        assertThatThrownBy(() -> paymentService.save(userId))
                .isInstanceOf(PaymentException.class);
    }
}