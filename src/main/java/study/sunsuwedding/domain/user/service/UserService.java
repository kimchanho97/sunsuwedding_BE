package study.sunsuwedding.domain.user.service;

import study.sunsuwedding.domain.user.dto.req.UserSignUpRequest;

public interface UserService {

    void signUp(UserSignUpRequest userSignUpRequest);
}
