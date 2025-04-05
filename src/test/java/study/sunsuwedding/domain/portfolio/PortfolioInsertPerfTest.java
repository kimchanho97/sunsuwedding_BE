package study.sunsuwedding.domain.portfolio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;
import study.sunsuwedding.domain.portfolio.entity.PortfolioImage;
import study.sunsuwedding.domain.portfolio.entity.PortfolioItem;
import study.sunsuwedding.domain.portfolio.repository.*;
import study.sunsuwedding.domain.user.entity.Planner;
import study.sunsuwedding.domain.user.repository.PlannerRepository;

import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
@Transactional
class PortfolioInsertPerfTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private PlannerRepository plannerRepository;
    @Autowired
    private PortfolioItemRepository portfolioItemRepository;
    @Autowired
    private PortfolioImageRepository portfolioImageRepository;
    @Autowired
    private PortfolioItemJdbcRepository portfolioItemJdbcRepository;
    @Autowired
    private PortfolioImageJdbcRepository portfolioImageJdbcRepository;

    Planner planner;

    @BeforeEach
    void setUp() {
        planner = new Planner("planner", "planner@test.com", "pw");
        plannerRepository.save(planner);
    }

    private void clearData() {
        jdbcTemplate.execute("DELETE FROM portfolio_image");
        jdbcTemplate.execute("DELETE FROM portfolio_item");
        jdbcTemplate.execute("DELETE FROM portfolio");
    }

    @Test
    void 배치_삽입_성능_테스트() {
        int warmupIterations = 0;
        int iterations = 1;
        long totalTime = 0;

        for (int i = 0; i < warmupIterations; i++) {
            clearData(); // 워밍업
            long start = System.nanoTime();
            insertTestData();
            long end = System.nanoTime();

            long duration = end - start;
            System.out.println("Warmup " + (i + 1) + ": " + (duration / 1_000_000) + " ms");
        }

        for (int i = 0; i < iterations; i++) {
            clearData();

            long start = System.nanoTime();
            insertTestData();
            long end = System.nanoTime();

            long duration = end - start;
            totalTime += duration;
            System.out.println("Run " + (i + 1) + ": " + (duration / 1_000_000) + " ms");
        }

        double avg = totalTime / (double) iterations;
        System.out.println("Avg time: " + (avg / 1_000_000) + " ms");
    }

    private void insertTestData() {
        Portfolio portfolio = Portfolio.builder()
                .planner(planner)
                .plannerName("테스터")
                .title("제목")
                .location("서울")
                .description("a".repeat(1000))
                .career("b".repeat(1000))
                .partnerCompany("c".repeat(1000))
                .totalPrice(1000000L)
                .contractedCount(10L)
                .averageRating(4.5)
                .build();
        portfolioRepository.save(portfolio);

        List<PortfolioItem> items = IntStream.range(0, 10)
                .mapToObj(i -> new PortfolioItem(portfolio, "아이템" + i, 10000L))
                .toList();

        List<PortfolioImage> images = IntStream.range(0, 10)
                .mapToObj(i -> new PortfolioImage(portfolio, "img" + i + ".jpg", "url" + i, i == 0))
                .toList();

        portfolioItemRepository.saveAll(items);
        portfolioImageRepository.saveAll(images);
//        portfolioItemJdbcRepository.batchInsert(items);
//        portfolioImageJdbcRepository.batchInsert(images);
    }
}
