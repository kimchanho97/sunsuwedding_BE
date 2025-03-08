package study.sunsuwedding.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import study.sunsuwedding.common.entity.BaseTimeEntity;
import study.sunsuwedding.domain.payment.exception.PaymentException;
import study.sunsuwedding.domain.user.constant.Grade;

import java.time.LocalDateTime;

import static study.sunsuwedding.domain.user.constant.Grade.PREMIUM;


@Getter
@Entity
@Table(name = "users") // user 테이블은 MySQL에서 예약어이므로 users로 변경
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@SQLDelete(sql = "UPDATE users SET is_deleted = true, deleted_at = NOW() WHERE user_id = ?")
@SQLRestriction("is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Column(nullable = false)
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private LocalDateTime upgradeAt;
    private String fileName; // 프로필 이미지 파일명
    private String fileUrl; // 프로필 이미지 URL

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.grade = Grade.NORMAL;
        this.isDeleted = false;
    }

    @Transient
    public String getDtype() {
        DiscriminatorValue val = this.getClass().getAnnotation(DiscriminatorValue.class);
        return val == null ? null : val.value();
    }

    public void upgrade() {
        if (this.grade == PREMIUM) {
            throw PaymentException.alreadyPremiumUser();
        }
        this.grade = PREMIUM;
        this.upgradeAt = LocalDateTime.now();
    }

    public boolean isPremium() {
        return this.grade == PREMIUM;
    }

    public void changeProfileImage(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }
}

