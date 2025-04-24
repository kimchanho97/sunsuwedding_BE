package study.sunsuwedding.domain.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.sunsuwedding.common.response.ApiResponse;
import study.sunsuwedding.domain.payment.dto.PaymentApproveRequest;
import study.sunsuwedding.domain.payment.dto.PaymentSaveRequest;
import study.sunsuwedding.domain.payment.service.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 데이터 저장
     */
    @PostMapping("/save")
    public ApiResponse<Void> save(@AuthenticationPrincipal Long userId, @Valid @RequestBody PaymentSaveRequest request) {
        paymentService.save(userId, request);
        return ApiResponse.success(null);
    }

    /**
     * 결제 승인 & 유저 등급 업그레이드
     */
    @PostMapping("/approve")
    public ApiResponse<Void> approve(@AuthenticationPrincipal Long userId, @Valid @RequestBody PaymentApproveRequest request) {
        paymentService.approvePayment(userId, request);
        return ApiResponse.success(null);
    }
}
