package study.sunsuwedding.domain.auth.dto.res;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import study.sunsuwedding.domain.user.entity.User;

@Getter
@RequiredArgsConstructor
public class AuthLoginResponse {

    private final Long userId;

    public static AuthLoginResponse fromEntity(User user) {
        return new AuthLoginResponse(user.getId());
    }
}
