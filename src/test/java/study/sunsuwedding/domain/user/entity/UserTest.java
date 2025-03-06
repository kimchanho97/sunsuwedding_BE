package study.sunsuwedding.domain.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import study.sunsuwedding.domain.payment.exception.PaymentException;
import study.sunsuwedding.domain.user.constant.Grade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UserTest {

    @Test
    @DisplayName("유저 엔티티 생성 테스트")
    void createUser() {
        // given
        String username = "testUser";
        String email = "test@example.com";
        String password = "password123";

        // when
        User user = new Couple(username, email, password);

        // then
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getGrade()).isEqualTo(Grade.NORMAL);
        assertThat(user.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("유저 프리미엄 업그레이드 테스트")
    void upgradeUser() {
        // given
        User user = new Couple("testUser", "test@example.com", "password123");

        // when
        user.upgrade();

        // then
        assertThat(user.getGrade()).isEqualTo(Grade.PREMIUM);
        assertThat(user.getUpgradeAt()).isNotNull();
    }

    @Test
    @DisplayName("이미 프리미엄인 유저 업그레이드 시 예외 발생 테스트")
    void upgradeAlreadyPremiumUser() {
        // given
        User user = new Couple("testUser", "test@example.com", "password123");
        user.upgrade();

        // when & then
        assertThatThrownBy(user::upgrade)
                .isInstanceOf(PaymentException.class);
    }

    @Test
    @DisplayName("유저 프리미엄 상태 확인 테스트")
    void isPremium() {
        // given
        User normalUser = new Couple("testUser", "test@example.com", "password123");
        User premiumUser = new Couple("testUser", "test@example.com", "password123");
        premiumUser.upgrade();

        // when & then
        assertThat(normalUser.isPremium()).isFalse();
        assertThat(premiumUser.isPremium()).isTrue();
    }
}
