package study.sunsuwedding.domain.user.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import study.sunsuwedding.domain.user.exception.UserException;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Role {
    COUPLE("couple"),
    PLANNER("planner");

    private final String roleName;

    public static Role fromString(String role) {
        return Arrays.stream(values())
                .filter(value -> value.roleName.equalsIgnoreCase(role)) // 대소문자 구분 없이 변환
                .findFirst()
                .orElseThrow(UserException::invalidRole);
    }
}
