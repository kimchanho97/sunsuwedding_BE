package study.sunsuwedding.domain.chat.dto;

import java.util.List;

public record LastReadSequenceRequest(
        List<String> chatRoomCodes,
        Long userId
) {
}
