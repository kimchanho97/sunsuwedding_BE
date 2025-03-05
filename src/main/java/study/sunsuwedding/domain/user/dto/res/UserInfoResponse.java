package study.sunsuwedding.domain.user.dto.res;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import study.sunsuwedding.common.util.DateFormatter;
import study.sunsuwedding.domain.user.entity.User;

@Getter
@RequiredArgsConstructor
public class UserInfoResponse {

    private final Long userId;
    private final String username;
    private final String email;
    private final String role;
    private final String grade;
    private final String payedAt;

    public static UserInfoResponse fromEntity(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDtype(),
                user.getGrade().getGradeName(),
                DateFormatter.formatDateInKorean(user.getUpgradeAt()));
    }
}
