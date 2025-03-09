package study.sunsuwedding.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody @Valid UserSignUpRequest request) {
        userService.signUp(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 유저 정보 조회
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(@AuthenticationPrincipal Long userId) {
        UserInfoResponse response = userService.getUserInfo(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("")
    public ResponseEntity<ApiResponse<Void>> withdraw(@AuthenticationPrincipal Long userId) {
        userService.withdraw(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 프로필 이미지 등록
     */
    @PostMapping("/profile-image")
    public ResponseEntity<ApiResponse<UserProfileImageResponse>> changeProfileImage(
            @AuthenticationPrincipal Long userId, @RequestParam MultipartFile profileImage) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateProfileImage(userId, profileImage)));
    }

    /**
     * 프로필 이미지 삭제
     */
    @DeleteMapping("/profile-image")
    public ResponseEntity<ApiResponse<Void>> deleteProfileImage(@AuthenticationPrincipal Long userId) {
        userService.deleteProfileImage(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
