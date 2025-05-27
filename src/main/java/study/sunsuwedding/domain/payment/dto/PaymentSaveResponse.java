package study.sunsuwedding.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSaveResponse {

    private String orderId;
    private Long amount;
}
