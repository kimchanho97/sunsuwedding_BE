package study.sunsuwedding.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.chat.dto.ChatReadSeqSyncRequest;
import study.sunsuwedding.domain.chat.dto.ChatReadSequenceUpdateDto;
import study.sunsuwedding.domain.chat.dto.ChatRoomMetaSyncRequest;
import study.sunsuwedding.domain.chat.repository.ChatBatchJdbcRepository;
import study.sunsuwedding.domain.chat.repository.ChatRoomQueryRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatBatchSyncService {

    private final ChatBatchJdbcRepository chatBatchJdbcRepository;
    private final ChatRoomQueryRepository chatRoomQueryRepository;

    public void batchUpdateChatRoomMeta(List<ChatRoomMetaSyncRequest> requests) {
        log.info("📦 [배치 시작] ChatRoomMetaSyncRequest {}건", requests.size());
        requests.forEach(req -> log.debug("➡️ chatRoomCode={}, lastMessage={}, lastMessageAt={}, lastMessageSeqId={}",
                req.chatRoomCode(), req.lastMessage(), req.lastMessageAt(), req.lastMessageSeqId()));

        chatBatchJdbcRepository.batchUpdateChatRoomMeta(requests);

        log.info("✅ [배치 완료] ChatRoomMeta {}건 업데이트 완료", requests.size());
    }

    public void batchUpdateLastReadSequences(List<ChatReadSeqSyncRequest> requests) {
        log.info("📦 [배치 시작] ChatReadSeqSyncRequest {}건", requests.size());
        requests.forEach(req -> log.debug("➡️ chatRoomCode={}, userId={}, lastReadSeqId={}",
                req.chatRoomCode(), req.userId(), req.lastReadSeqId()));

        // 1. chatRoomCode → chatRoomId 매핑
        List<String> chatRoomCodes = requests.stream()
                .map(ChatReadSeqSyncRequest::chatRoomCode)
                .distinct()
                .toList();
        Map<String, Long> chatRoomCodeToIdMap = chatRoomQueryRepository.findChatRoomCodeToIdMap(chatRoomCodes);

        // 2. 변환
        List<ChatReadSequenceUpdateDto> sequenceUpdateDtos = requests.stream()
                .map(req -> new ChatReadSequenceUpdateDto(
                        chatRoomCodeToIdMap.get(req.chatRoomCode()),
                        req.userId(),
                        req.lastReadSeqId()))
                .filter(req -> req.chatRoomId() != null)
                .toList();

        // 3. 배치 업데이트
        chatBatchJdbcRepository.batchUpdateLastReadSeq(sequenceUpdateDtos);

        log.info("✅ [배치 완료] ChatParticipant {}건 lastReadSeqId 업데이트 완료", sequenceUpdateDtos.size());
    }
}
