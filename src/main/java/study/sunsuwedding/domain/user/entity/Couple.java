package study.sunsuwedding.domain.user.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@DiscriminatorValue(value = "couple")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Couple extends User {

    public Couple(String username, String email, String password) {
        super(username, email, password);
    }
}
