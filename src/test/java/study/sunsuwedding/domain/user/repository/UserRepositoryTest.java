package study.sunsuwedding.domain.user.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.user.constant.Grade;
import study.sunsuwedding.domain.user.entity.Couple;
import study.sunsuwedding.domain.user.entity.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    CoupleRepository coupleRepository;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("ID로 사용자(Couple) 조회")
    void findByUser() {
        // Given
        Couple couple = Couple.builder()
                .username("CoupleUser")
                .email("couple@example.com")
                .password("securePass")
                .build();

        coupleRepository.save(couple);
        em.flush();
        em.clear();

        // When
        User foundCouple = userRepository.findById(couple.getId())
                .orElseThrow(() -> new AssertionError("사용자를 찾을 수 없습니다."));

        // Then
        assertThat(foundCouple.getUsername()).isEqualTo("CoupleUser");
        assertThat(foundCouple.getEmail()).isEqualTo("couple@example.com");
        assertThat(foundCouple.getPassword()).isEqualTo("securePass");
        assertThat(foundCouple.getGrade()).isEqualTo(Grade.NORMAL);
        assertThat(foundCouple.getIsDeleted()).isFalse();
        assertThat(foundCouple.getDeletedAt()).isNull();
        assertThat(foundCouple.getAvatarUrl()).isNull();
    }

}
