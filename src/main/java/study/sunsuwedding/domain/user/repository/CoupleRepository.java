package study.sunsuwedding.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.sunsuwedding.domain.user.entity.Couple;

public interface CoupleRepository extends JpaRepository<Couple, Long> {
}
