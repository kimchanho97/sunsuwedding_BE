package study.sunsuwedding.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TossPaymentResponse {

    private String status;
    private String paymentKey;
    private String orderId;
    private Long totalAmount;
    
    public boolean isDone() {
        return "DONE".equalsIgnoreCase(this.status);
    }
}
