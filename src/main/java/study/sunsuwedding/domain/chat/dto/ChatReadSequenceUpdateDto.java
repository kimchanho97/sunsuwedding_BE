package study.sunsuwedding.domain.chat.dto;

public record ChatReadSequenceUpdateDto(
        Long chatRoomId,
        Long userId,
        Long lastReadSeqId
) {
}
