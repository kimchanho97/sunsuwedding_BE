package study.sunsuwedding.domain.chat.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import study.sunsuwedding.domain.chat.entity.ChatRoom;

@Getter
@RequiredArgsConstructor
public class ChatRoomCreateResponse {

    private final String chatRoomCode;

    public static ChatRoomCreateResponse fromEntity(ChatRoom chatRoom) {
        return new ChatRoomCreateResponse(chatRoom.getChatRoomCode());
    }
}
