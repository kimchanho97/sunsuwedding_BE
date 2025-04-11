package study.sunsuwedding.domain.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomCreateRequest {

    private Long userId;
    private Long plannerId;

}
