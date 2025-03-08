package study.sunsuwedding.domain.auth;

import study.sunsuwedding.domain.user.entity.User;

public interface AuthService {

    User login(AuthLoginRequest request);
}
