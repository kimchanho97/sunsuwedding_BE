package study.sunsuwedding.domain.chat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomCreateRequest {

    @NotNull(message = "plannerId는 비어있으면 안됩니다.")
    private Long plannerId;

}
