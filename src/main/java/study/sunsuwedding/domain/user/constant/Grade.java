package study.sunsuwedding.domain.user.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Grade {
    NORMAL("normal"),
    PREMIUM("premium");

    private final String gradeName;

}
