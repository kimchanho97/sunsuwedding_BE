package study.sunsuwedding.domain.auth.service;

import study.sunsuwedding.domain.auth.dto.req.AuthLoginRequest;
import study.sunsuwedding.domain.user.entity.User;

public interface AuthService {

    User login(AuthLoginRequest request);
}
