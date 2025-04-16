package study.sunsuwedding.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import study.sunsuwedding.common.response.ApiResponse;
import study.sunsuwedding.domain.user.dto.req.UserSignUpRequest;
import study.sunsuwedding.domain.user.dto.res.UserInfoResponse;
import study.sunsuwedding.domain.user.dto.res.UserProfileImageResponse;
import study.sunsuwedding.domain.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponse<Void> signUp(@RequestBody @Valid UserSignUpRequest request) {
        userService.signUp(request);
        return ApiResponse.success(null);
    }

    @GetMapping("/info")
    public ApiResponse<UserInfoResponse> getUserInfo(@AuthenticationPrincipal Long userId) {
        UserInfoResponse response = userService.getUserInfo(userId);
        return ApiResponse.success(response);
    }

    @DeleteMapping
    public ApiResponse<Void> withdraw(@AuthenticationPrincipal Long userId) {
        userService.withdraw(userId);
        return ApiResponse.success(null);
    }

    @PostMapping("/profile-image")
    public ApiResponse<UserProfileImageResponse> changeProfileImage(@AuthenticationPrincipal Long userId,
                                                                    @RequestParam MultipartFile profileImage) {
        return ApiResponse.success(userService.updateProfileImage(userId, profileImage));
    }

    @DeleteMapping("/profile-image")
    public ApiResponse<Void> deleteProfileImage(@AuthenticationPrincipal Long userId) {
        userService.deleteProfileImage(userId);
        return ApiResponse.success(null);
    }

}
