package study.sunsuwedding.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.sunsuwedding.domain.chat.repository.ChatParticipantQueryRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatReadSequenceService {

    private final ChatParticipantQueryRepository chatParticipantQueryRepository;

    public Map<Long, Long> getReadSequencesByUserInChatRoom(String chatRoomCode) {
        return chatParticipantQueryRepository.findReadSequencesGroupedByUserInRoom(chatRoomCode);
    }

    public Map<String, Long> getReadSequencesByChatRoomsForUser(List<String> chatRoomCodes, Long userId) {
        return chatParticipantQueryRepository.findReadSequencesGroupedByChatRoomForUser(chatRoomCodes, userId);
    }
}
