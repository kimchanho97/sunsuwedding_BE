package study.sunsuwedding.domain.chat.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import study.sunsuwedding.domain.chat.entity.ChatRoom;

@Getter
@RequiredArgsConstructor
public class ChatRoomCreateResponse {

    private final String chatRoomCode;
    private final boolean alreadyExists;

    public static ChatRoomCreateResponse fromEntity(ChatRoom chatRoom, boolean alreadyExists) {
        return new ChatRoomCreateResponse(chatRoom.getChatRoomCode(), alreadyExists);
    }
}
