package study.sunsuwedding.domain.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossWebhookRequest {
    private String eventType;
    private String createdAt;
    private TossWebhookData data;

    @Getter
    @NoArgsConstructor
    public static class TossWebhookData {
        private String orderId;
        private String paymentKey;
        private String status;
        private String approvedAt;
    }
}
