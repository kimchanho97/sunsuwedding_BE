package study.sunsuwedding.domain.chat.dto;

public record ChatReadSeqSyncRequest(
        String chatRoomCode,
        Long userId,
        Long lastReadSeqId
) {
}