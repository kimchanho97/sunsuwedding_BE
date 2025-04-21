package study.sunsuwedding.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import study.sunsuwedding.domain.chat.dto.LastReadSequenceRequest;
import study.sunsuwedding.domain.chat.service.ChatReadSequenceService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/chat/rooms")
public class ChatReadInternalController {

    private final ChatReadSequenceService chatReadSequenceService;

    @GetMapping("/{chatRoomCode}/last-read-sequences")
    public ResponseEntity<Map<Long, Long>> getReadSequencesByUserInChatRoom(@PathVariable String chatRoomCode) {
        Map<Long, Long> result = chatReadSequenceService.getReadSequencesByUserInChatRoom(chatRoomCode);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/last-read-sequences")
    public ResponseEntity<Map<String, Long>> getReadSequencesByChatRoomsForUser(@RequestBody LastReadSequenceRequest request) {
        Map<String, Long> result = chatReadSequenceService.getReadSequencesByChatRoomsForUser(request.chatRoomCodes(), request.userId());
        return ResponseEntity.ok(result);
    }

}
