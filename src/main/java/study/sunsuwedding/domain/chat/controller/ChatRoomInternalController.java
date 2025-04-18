package study.sunsuwedding.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import study.sunsuwedding.domain.chat.dto.ChatRoomCreateRequest;
import study.sunsuwedding.domain.chat.dto.ChatRoomCreateResponse;
import study.sunsuwedding.domain.chat.dto.ChatRoomParticipantsResponse;
import study.sunsuwedding.domain.chat.dto.ChatRoomValidationRequest;
import study.sunsuwedding.domain.chat.service.ChatRoomService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/chat-rooms")
public class ChatRoomInternalController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<ChatRoomCreateResponse> createChatRoom(@RequestBody ChatRoomCreateRequest request) {
        ChatRoomCreateResponse response = chatRoomService.createChatRoom(request.getUserId(), request.getPlannerId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateInternal(@RequestBody ChatRoomValidationRequest request) {
        boolean isValid = chatRoomService.validateChatRoom(request.getChatRoomCode(), request.getUserId());
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/{chatRoomCode}/participants")
    public ResponseEntity<ChatRoomParticipantsResponse> getParticipants(@PathVariable String chatRoomCode) {
        List<Long> participantUserIds = chatRoomService.getParticipantUserIds(chatRoomCode);
        return ResponseEntity.ok(new ChatRoomParticipantsResponse(participantUserIds));
    }

}

