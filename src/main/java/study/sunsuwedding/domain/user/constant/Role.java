package study.sunsuwedding.domain.user.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    COUPLE("couple"),
    PLANNER("planner");

    private final String roleName;
}
