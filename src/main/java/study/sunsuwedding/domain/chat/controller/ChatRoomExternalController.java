package study.sunsuwedding.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import study.sunsuwedding.common.response.ApiResponse;
import study.sunsuwedding.domain.chat.dto.ChatRoomPartnerProfileResponse;
import study.sunsuwedding.domain.chat.service.ChatRoomService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-room-partner")
public class ChatRoomExternalController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/{chatRoomCode}")
    public ApiResponse<ChatRoomPartnerProfileResponse> getPartnerProfile(
            @PathVariable String chatRoomCode,
            @RequestParam Long requesterId
    ) {
        ChatRoomPartnerProfileResponse response = chatRoomService.findPartnerProfile(chatRoomCode, requesterId);
        return ApiResponse.success(response);
    }
}
