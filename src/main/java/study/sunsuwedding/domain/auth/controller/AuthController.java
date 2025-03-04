package study.sunsuwedding.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.sunsuwedding.common.response.ApiResponse;
import study.sunsuwedding.domain.auth.constant.SessionConst;
import study.sunsuwedding.domain.auth.dto.req.AuthLoginRequest;
import study.sunsuwedding.domain.auth.dto.res.AuthLoginResponse;
import study.sunsuwedding.domain.auth.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthLoginResponse>> login(@RequestBody @Valid AuthLoginRequest request, HttpServletRequest httpRequest) {
        AuthLoginResponse response = authService.login(request);

        HttpSession session = httpRequest.getSession();
        session.setAttribute(SessionConst.USER_ID, response.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 무효화
        }
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
