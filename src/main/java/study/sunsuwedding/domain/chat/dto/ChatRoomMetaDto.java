package study.sunsuwedding.domain.chat.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatRoomMetaDto {

    private String chatRoomCode;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Long lastMessageSeqId;

    @QueryProjection
    public ChatRoomMetaDto(String chatRoomCode, String lastMessage, LocalDateTime lastMessageAt, Long lastMessageSeqId) {
        this.chatRoomCode = chatRoomCode;
        this.lastMessage = lastMessage;
        this.lastMessageAt = lastMessageAt;
        this.lastMessageSeqId = lastMessageSeqId;
    }
}
