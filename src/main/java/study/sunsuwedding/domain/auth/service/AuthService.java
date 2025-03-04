package study.sunsuwedding.domain.auth.service;

import study.sunsuwedding.domain.auth.dto.req.AuthLoginRequest;
import study.sunsuwedding.domain.auth.dto.res.AuthLoginResponse;

public interface AuthService {

    AuthLoginResponse login(AuthLoginRequest request);
}
