package study.sunsuwedding.domain.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import study.sunsuwedding.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    // 로그인
    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, 2001, "이메일을 찾을 수 없습니다."),
    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, 2002, "비밀번호가 일치하지 않습니다."),

    // 회원가입
    DELETED_USER(HttpStatus.BAD_REQUEST, 2003, "이미 탈퇴한 회원입니다. 계정을 복구하시겠습니까?"),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, 2004, "해당 이메일로 가입된 계정이 이미 존재합니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, 2005, "역할(Role)은 'PLANNER' 또는 'COUPLE'만 가능합니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, 2006, "비밀번호와 비밀번호 확인 값이 일치해야 합니다."),

    // 유저 정보 조회
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 2007, "해당 사용자를 찾을 수 없습니다. 탈퇴했거나 존재하지 않는 계정입니다."),

    PLANNER_NOT_FOUND(HttpStatus.NOT_FOUND, 2001, "해당 플래너를 찾을 수 없습니다. 탈퇴했거나 존재하지 않는 계정입니다."),
    ALREADY_PREMIUM_USER(HttpStatus.BAD_REQUEST, 2007, "이미 프리미엄 회원입니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
