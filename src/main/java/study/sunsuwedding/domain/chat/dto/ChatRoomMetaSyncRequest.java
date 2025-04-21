package study.sunsuwedding.domain.chat.dto;

public record ChatRoomMetaSyncRequest(
        String chatRoomCode,
        String lastMessage,
        String lastMessageAt,
        String lastMessageSeqId
) {
}