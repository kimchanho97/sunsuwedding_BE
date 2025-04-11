package study.sunsuwedding.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomValidationRequest {

    private Long chatRoomId;
    private Long userId;
}