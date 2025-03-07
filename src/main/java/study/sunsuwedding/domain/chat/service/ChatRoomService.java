package study.sunsuwedding.domain.chat.service;

import study.sunsuwedding.domain.chat.dto.ChatRoomCreateResponse;

public interface ChatRoomService {

    ChatRoomCreateResponse createChatRoom(Long userId, Long plannerId);
}
