package study.sunsuwedding.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMetaResponse {

    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Long lastMessageSeqId;
}
