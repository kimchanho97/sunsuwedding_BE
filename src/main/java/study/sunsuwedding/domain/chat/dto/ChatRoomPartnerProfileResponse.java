package study.sunsuwedding.domain.chat.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomPartnerProfileResponse {

    private String chatRoomCode;
    private Long partnerUserId;
    private String partnerName;
    private String avatarUrl;

    @QueryProjection
    public ChatRoomPartnerProfileResponse(String chatRoomCode, Long partnerUserId, String partnerName, String avatarUrl) {
        this.chatRoomCode = chatRoomCode;
        this.partnerUserId = partnerUserId;
        this.partnerName = partnerName;
        this.avatarUrl = avatarUrl;
    }
}