package study.sunsuwedding.domain.user.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@DiscriminatorValue(value = "planner")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Planner extends User {

    @Builder
    public Planner(String username, String email, String password) {
        super(username, email, password);
    }
}
