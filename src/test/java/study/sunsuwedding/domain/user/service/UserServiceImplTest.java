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
import study.sunsuwedding.domain.user.dto.req.UserSignUpRequest;
import study.sunsuwedding.domain.user.dto.res.UserInfoResponse;
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

    String email = "test@example.com";
    String username = "existingUser";
    String password = "securePass";

    User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = new Couple(username, email, passwordEncoder.encode(password));
        userRepository.save(savedUser);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("존재하는 유저 정보 조회 성공")
    void testGetUserInfo_Success() {
        // Given
        Long userId = savedUser.getId();

        // When
        UserInfoResponse response = userService.getUserInfo(userId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getUsername()).isEqualTo(username);
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getRole()).isEqualTo("couple");
        assertThat(response.getGrade()).isEqualTo("normal");
        assertThat(response.getUpgradeAt()).isEqualTo("");
    }

    @Test
    @DisplayName("존재하지 않는 유저 조회 시 예외 발생")
    void testGetUserInfo_UserNotFound() {
        // Given
        Long invalidUserId = 999L; // 존재하지 않는 유저 ID

        // When & Then
        assertThatThrownBy(() -> userService.getUserInfo(invalidUserId))
                .isInstanceOf(UserException.class);
    }


    @Test
    @DisplayName("중복된 이메일로 회원가입 시 예외 발생")
    void testSignUpWithDuplicateEmail() {
        // Given
        UserSignUpRequest request = new UserSignUpRequest(
                "couple", "newUser", email, "newPassword", "newPassword"
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
        User newUser = userRepository.findByEmail(newEmail)
                .orElseThrow(() -> new AssertionError("회원가입된 유저를 찾을 수 없습니다."));

        assertThat(newUser.getUsername()).isEqualTo(newUsername);
        assertThat(newUser.getEmail()).isEqualTo(newEmail);
        assertThat(passwordEncoder.matches(newPassword, newUser.getPassword())).isTrue(); // 비밀번호 암호화 확인
        assertThat(newUser.getIsDeleted()).isFalse();
        assertThat(newUser.getGrade()).isEqualTo(Grade.NORMAL);
    }
}
