package study.sunsuwedding.infra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import study.sunsuwedding.common.exception.CommonErrorCode;
import study.sunsuwedding.common.response.ErrorResponse;
import study.sunsuwedding.infra.security.SessionAuthenticationFilter;

import java.io.IOException;
import java.util.Collections;

import static study.sunsuwedding.common.exception.CommonErrorCode.FORBIDDEN;
import static study.sunsuwedding.common.exception.CommonErrorCode.UNAUTHORIZED_ACCESS;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 1. CSRF 보호 비활성화 (세션 기반에서는 보안적으로 필요)
        // 2. iframe 정책 설정 유지
        // 3. CORS 설정 유지
        // 4. 세션 정책 변경: 세션 기반 인증이므로 `IF_REQUIRED`
        // 5. form 로긴 해제 (UsernamePasswordAuthenticationFilter 비활성화)
        // 6. 로그인 인증창이 뜨지 않게 비활성화

        http
                .csrf(CsrfConfigurer::disable)
                .headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .cors((cors) -> cors.configurationSource(configurationSource()))
                .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(FormLoginConfigurer::disable)
                .httpBasic(HttpBasicConfigurer::disable);

        // 7. 커스텀 필터 적용 (SessionAuthenticationFilter 추가)
        http.addFilterBefore(
                new SessionAuthenticationFilter(authenticationManager(http)),
                UsernamePasswordAuthenticationFilter.class
        );

        // 8. 인증 실패 처리 (401 Unauthorized)
        http.exceptionHandling((exceptionHandling) ->
                exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
                    handleExceptionResponse(response, UNAUTHORIZED_ACCESS);
                })
        );

        // 9. 인가(Authorization) 실패 처리 (403 Forbidden)
        http.exceptionHandling((exceptionHandling) ->
                exceptionHandling.accessDeniedHandler((request, response, accessDeniedException) -> {
                    handleExceptionResponse(response, FORBIDDEN);
                })
        );

        // 10. 인증, 권한 필터 설정
        http
                .securityMatcher("/api/**") // /api/** 경로에 대해서만 보안 설정
                .authorizeHttpRequests((authorize) ->
                                authorize
                                        .requestMatchers(
                                                new AntPathRequestMatcher("/test"),
                                                new AntPathRequestMatcher("/api/auth/login"),
                                                new AntPathRequestMatcher("/api/auth/logout"),
                                                new AntPathRequestMatcher("/api/user/signup"),
                                                new AntPathRequestMatcher("/api/portfolio", "GET"),
                                                new AntPathRequestMatcher("/api/portfolio/{portfolioId}", "GET")
                                        ).permitAll()
                                        .requestMatchers(
                                                new AntPathRequestMatcher("/api/chat/room")
                                        ).hasAuthority("couple")
                                        .requestMatchers(
                                                new AntPathRequestMatcher("/api/portfolio", "POST"),
                                                new AntPathRequestMatcher("/api/portfolio", "PUT"),
                                                new AntPathRequestMatcher("/api/portfolio", "DELETE"),
                                                new AntPathRequestMatcher("/api/portfolio/me")
//                                        new AntPathRequestMatcher("/api/quotation/**")
                                        ).hasAuthority("planner")
                                        .requestMatchers(
                                                new AntPathRequestMatcher("/api/user", "DELETE"),
                                                new AntPathRequestMatcher("/api/user/info", "GET"),
                                                new AntPathRequestMatcher("/api/user/profile-image", "POST"),
                                                new AntPathRequestMatcher("/api/payment/**"),
                                                new AntPathRequestMatcher("/api/favorite/**")
                                        ).authenticated()
                                        .anyRequest().permitAll()
                );

        return http.build();
    }

    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

//        configuration.addAllowedOriginPattern("http://localhost:3000");

        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));  // 모든 Origin 허용
        configuration.setAllowCredentials(true);  // 쿠키 포함 요청 허용 (세션 기반 인증 필요)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private void handleExceptionResponse(HttpServletResponse response, CommonErrorCode errorCode) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(errorCode.getHttpStatus().value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(new ErrorResponse(errorCode)));
    }
}