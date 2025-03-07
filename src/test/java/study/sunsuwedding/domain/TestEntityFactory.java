package study.sunsuwedding.domain;

import org.springframework.test.util.ReflectionTestUtils;
import study.sunsuwedding.domain.favorite.entity.Favorite;
import study.sunsuwedding.domain.portfolio.entity.Portfolio;
import study.sunsuwedding.domain.user.entity.Couple;
import study.sunsuwedding.domain.user.entity.User;

public class TestEntityFactory {

    public static User createUser(Long id, String name, String email, String password) {
        User user = new Couple(name, email, password);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    public static Favorite createFavorite(Long id, User user, Portfolio portfolio) {
        Favorite favorite = new Favorite(user, portfolio);
        ReflectionTestUtils.setField(favorite, "id", id);
        return favorite;
    }
}