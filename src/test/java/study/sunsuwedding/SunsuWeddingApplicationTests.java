package study.sunsuwedding;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
class SunsuWeddingApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("CI/CD 파이프라인 테스트 - 의도적 실패")
    void cicdTest() {
        fail("CI/CD 테스트용 의도적 실패");
    }
}
