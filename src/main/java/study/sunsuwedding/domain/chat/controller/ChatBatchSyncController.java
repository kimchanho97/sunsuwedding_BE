package study.sunsuwedding.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.sunsuwedding.domain.chat.dto.ChatReadSeqSyncRequest;
import study.sunsuwedding.domain.chat.dto.ChatRoomMetaSyncRequest;
import study.sunsuwedding.domain.chat.service.ChatBatchSyncService;

import java.util.List;

@RestController
@RequestMapping("/internal/batch")
@RequiredArgsConstructor
public class ChatBatchSyncController {

    private final ChatBatchSyncService chatBatchSyncService;

    @PostMapping("/chat-room-meta")
    public ResponseEntity<Void> syncChatRoomMeta(@RequestBody List<ChatRoomMetaSyncRequest> requests) {
        chatBatchSyncService.batchUpdateChatRoomMeta(requests);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/last-read-sequences")
    public ResponseEntity<Void> syncLastReadSequences(@RequestBody List<ChatReadSeqSyncRequest> requests) {
        chatBatchSyncService.batchUpdateLastReadSequences(requests);
        return ResponseEntity.ok().build();
    }
}