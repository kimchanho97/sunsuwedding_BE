package study.sunsuwedding.domain.chat.dto;

import java.util.List;

public record ChatRoomPartnerProfileRequest(
        Long requesterId,
        List<String> chatRoomCodes
) {
}
