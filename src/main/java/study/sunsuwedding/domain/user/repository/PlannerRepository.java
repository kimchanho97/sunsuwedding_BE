package study.sunsuwedding.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.sunsuwedding.domain.user.entity.Planner;

public interface PlannerRepository extends JpaRepository<Planner, Long> {
}
