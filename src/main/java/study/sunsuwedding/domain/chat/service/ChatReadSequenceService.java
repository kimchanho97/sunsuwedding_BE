package study.sunsuwedding.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.sunsuwedding.domain.chat.repository.ChatParticipantQueryRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatReadSequenceService {

    private final ChatParticipantQueryRepository chatParticipantQueryRepository;

    public Map<Long, Long> getLastReadSequences(String chatRoomCode) {
        return chatParticipantQueryRepository.findUserReadSeqMapByChatRoomCode(chatRoomCode);
    }
}
