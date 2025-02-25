package study.sunsuwedding.domain.user.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.user.constant.Grade;
import study.sunsuwedding.domain.user.dto.UserSignUpRequest;
import study.sunsuwedding.domain.user.entity.Couple;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.UserRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;
    @Autowired
    PasswordEncoder passwordEncoder;

    String duplicatedEmail = "test@example.com";
    String existingUsername = "existingUser";
    String existingPassword = "securePass";

    @BeforeEach
    void setUp() {
        Couple existingUser = Couple.builder()
                .email(duplicatedEmail)
                .username(existingUsername)
                .password(passwordEncoder.encode(existingPassword)) // 비밀번호 암호화
                .build();
        userRepository.save(existingUser);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("중복된 이메일로 회원가입 시 예외 발생")
    void testSignUpWithDuplicateEmail() {
        // Given
        UserSignUpRequest request = new UserSignUpRequest(
                "couple", "newUser", duplicatedEmail, "newPassword", "newPassword"
        );

        // When & Then
        assertThatThrownBy(() -> userService.signUp(request))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("비밀번호 불일치 시 예외 발생")
    void testSignUpWithPasswordMismatch() {
        // Given
        UserSignUpRequest request = new UserSignUpRequest(
                "couple", "newUser", "unique@example.com", "newPassword", "wrongPassword"
        );

        // When & Then
        assertThatThrownBy(() -> userService.signUp(request))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("정상적으로 회원가입이 진행된다.")
    void testSuccessfulSignUp() {
        // Given
        String newEmail = "unique@example.com";
        String newUsername = "newUser";
        String newPassword = "newPassword";

        UserSignUpRequest request = new UserSignUpRequest(
                "couple", newUsername, newEmail, newPassword, newPassword
        );

        // When
        userService.signUp(request);
        em.flush();
        em.clear();

        // Then
        User savedUser = userRepository.findByEmail(newEmail)
                .orElseThrow(() -> new AssertionError("회원가입된 유저를 찾을 수 없습니다."));

        assertThat(savedUser.getUsername()).isEqualTo(newUsername);
        assertThat(savedUser.getEmail()).isEqualTo(newEmail);
        assertThat(passwordEncoder.matches(newPassword, savedUser.getPassword())).isTrue(); // 비밀번호 암호화 확인
        assertThat(savedUser.getIsDeleted()).isFalse();
        assertThat(savedUser.getGrade()).isEqualTo(Grade.NORMAL);
    }
}
