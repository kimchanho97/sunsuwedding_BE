package study.sunsuwedding.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.sunsuwedding.domain.chat.service.ChatReadSequenceService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/chat/rooms")
public class ChatReadInternalController {

    private final ChatReadSequenceService chatReadSequenceService;

    @GetMapping("/{chatRoomCode}/last-read-sequences")
    public ResponseEntity<Map<Long, Long>> getLastReadSequences(@PathVariable String chatRoomCode) {
        Map<Long, Long> result = chatReadSequenceService.getLastReadSequences(chatRoomCode);
        return ResponseEntity.ok(result);
    }
}
