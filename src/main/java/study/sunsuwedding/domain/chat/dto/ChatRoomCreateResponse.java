package study.sunsuwedding.domain.chat.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import study.sunsuwedding.domain.chat.entity.ChatRoom;

@Getter
@RequiredArgsConstructor
public class ChatRoomCreateResponse {

    private final Long chatRoomId;

    public static ChatRoomCreateResponse fromEntity(ChatRoom chatRoom) {
        return new ChatRoomCreateResponse(chatRoom.getId());
    }
}
