package study.sunsuwedding.domain.user.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.sunsuwedding.domain.user.constant.Grade;

@Entity
@Getter
@DiscriminatorValue(value = "planner")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Planner extends User {

    @Builder
    public Planner(String email, String username, String password, Grade grade, boolean isActive) {
        super(email, username, password, grade, isActive);
    }
}
