package study.sunsuwedding.domain.user.service;

import org.springframework.web.multipart.MultipartFile;
import study.sunsuwedding.domain.user.dto.req.UserSignUpRequest;
import study.sunsuwedding.domain.user.dto.res.UserInfoResponse;
import study.sunsuwedding.domain.user.dto.res.UserProfileImageResponse;

public interface UserService {

    void signUp(UserSignUpRequest userSignUpRequest);

    UserInfoResponse getUserInfo(Long userId);

    void withdraw(Long userId);

    UserProfileImageResponse updateProfileImage(Long userId, MultipartFile profileImage);

    void deleteProfileImage(Long userId);
}
