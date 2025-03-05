package study.sunsuwedding.domain.user.service;

import study.sunsuwedding.domain.user.dto.req.UserSignUpRequest;
import study.sunsuwedding.domain.user.dto.res.UserInfoResponse;

public interface UserService {

    void signUp(UserSignUpRequest userSignUpRequest);

    UserInfoResponse getUserInfo(Long userId);

    void withdraw(Long userId);
    
}
