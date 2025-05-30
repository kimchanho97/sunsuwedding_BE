package study.sunsuwedding.domain.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.sunsuwedding.common.response.ApiResponse;
import study.sunsuwedding.domain.user.dto.res.UserInfoResponse;
import study.sunsuwedding.domain.user.entity.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<UserInfoResponse> login(@RequestBody @Valid AuthLoginRequest request, HttpServletRequest httpRequest) {
        User loginedUser = authService.login(request);

        HttpSession session = httpRequest.getSession();
        session.setAttribute(SessionConst.USER_ID, loginedUser.getId());
        session.setAttribute(SessionConst.USER_ROLE, loginedUser.getDtype());

        UserInfoResponse response = UserInfoResponse.fromEntity(loginedUser);
        return ApiResponse.success(response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 무효화
        }
        return ApiResponse.success(null);
    }

}
