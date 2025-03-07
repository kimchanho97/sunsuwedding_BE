package study.sunsuwedding.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.sunsuwedding.domain.chat.entity.ChatRoom;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr " +
            "JOIN ChatParticipant cp1 ON cp1.chatRoom.id = cr.id AND cp1.user.id = :userId " +
            "JOIN ChatParticipant cp2 ON cp2.chatRoom.id = cr.id AND cp2.user.id = :plannerId ")
    Optional<ChatRoom> findExistingChatRoom(@Param("userId") Long userId, @Param("plannerId") Long plannerId);
}
