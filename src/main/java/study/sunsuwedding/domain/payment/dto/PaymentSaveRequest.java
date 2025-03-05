package study.sunsuwedding.domain.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentSaveRequest {

    @NotEmpty(message = "orderId는 비어있으면 안됩니다.")
    @Size(max = 255, message = "orderId는 255자 이내여야 합니다.")
    private String orderId;

    @NotNull(message = "금액은 비어있으면 안됩니다.")
    @Min(value = 0, message = "금액은 양수여야 합니다.")
    private Long amount;
    
}
