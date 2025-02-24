package study.sunsuwedding.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import study.sunsuwedding.domain.user.constant.Grade;


@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@SQLDelete(sql = "UPDATE user SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Grade grade;

    private boolean isActive;

    public User(String email, String username, String password, Grade grade, boolean isActive) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.grade = grade;
        this.isActive = isActive;
    }
}

