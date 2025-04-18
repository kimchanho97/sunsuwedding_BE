package study.sunsuwedding.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomParticipantsResponse {
    private List<Long> participantUserIds;
}
