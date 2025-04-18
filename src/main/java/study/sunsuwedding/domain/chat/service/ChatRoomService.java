package study.sunsuwedding.domain.chat.service;

import study.sunsuwedding.domain.chat.dto.ChatRoomCreateResponse;

import java.util.List;

public interface ChatRoomService {

    ChatRoomCreateResponse createChatRoom(Long userId, Long plannerId);

    boolean validateChatRoom(String chatRoomCode, Long userId);

    List<Long> getParticipantUserIds(String chatRoomCode);
}
