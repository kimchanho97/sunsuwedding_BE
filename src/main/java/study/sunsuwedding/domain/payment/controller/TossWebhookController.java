package study.sunsuwedding.domain.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.sunsuwedding.domain.payment.dto.TossWebhookRequest;
import study.sunsuwedding.domain.payment.service.TossWebhookService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/webhook/toss")
public class TossWebhookController {

    private final TossWebhookService webhookService;

    @PostMapping
    public ResponseEntity<Void> handleWebhook(@RequestBody TossWebhookRequest request) {
        webhookService.processWebhook(request);
        return ResponseEntity.ok().build(); // Toss는 응답 바디 없이 200 OK만 필요
    }
}