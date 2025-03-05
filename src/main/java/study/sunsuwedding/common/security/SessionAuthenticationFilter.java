package study.sunsuwedding.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import study.sunsuwedding.domain.auth.constant.SessionConst;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class SessionAuthenticationFilter extends BasicAuthenticationFilter {

    public SessionAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 1. 세션 확인 (false: 세션이 없으면 null 반환)
        HttpSession session = request.getSession(false);

        if (session != null) {
            // 2. 세션에서 userId, role 정보 가져오기
            Long userId = (Long) session.getAttribute(SessionConst.USER_ID);
            String role = (String) session.getAttribute(SessionConst.USER_ROLE);

            // 3. userId, role 정보가 존재하면 인증 객체 생성 및 SecurityContextHolder에 저장
            if (userId != null && role != null) {
                Authentication authentication = createAuthentication(userId, role);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 4. 다음 필터 체인 실행 (Spring Security가 이 후 인증 검증 수행)
        chain.doFilter(request, response);
    }

    private Authentication createAuthentication(Long userId, String role) {
        // 사용자 역할(role) 기반 권한 설정
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

        // SecurityContextHolder에 저장될 Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(userId, null, authorities);
    }
}
