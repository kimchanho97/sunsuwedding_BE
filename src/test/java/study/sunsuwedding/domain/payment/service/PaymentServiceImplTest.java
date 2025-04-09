package study.sunsuwedding.domain.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import study.sunsuwedding.domain.payment.client.PaymentApprovalClient;
import study.sunsuwedding.domain.payment.dto.PaymentSaveRequest;
import study.sunsuwedding.domain.payment.entity.Payment;
import study.sunsuwedding.domain.payment.repository.PaymentRepository;
import study.sunsuwedding.domain.user.entity.Couple;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.repository.UserRepository;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentApprovalClient paymentApprovalClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User user;
    private Payment payment;

    @BeforeEach
    void setUp() {
        user = new Couple("testUser", "test@example.com", "password123");
        payment = new Payment(user, "order-123", 50000L);
    }

    @Test
    @DisplayName("결제 정보 저장 테스트 - 새 결제 생성")
    void saveNewPayment() {
        // given
        PaymentSaveRequest request = new PaymentSaveRequest("order-123", 50000L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user)); // user 객체 반환

        // when
        paymentService.save(1L, request);

        // then
        verify(paymentRepository).save(any(Payment.class));
    }
}
