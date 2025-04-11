package study.sunsuwedding.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.sunsuwedding.domain.chat.entity.ChatParticipant;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    @Query("""
                SELECT COUNT(cp) > 0 
                FROM ChatParticipant cp 
                WHERE cp.chatRoom.id = :chatRoomId 
                AND cp.user.id = :userId
            """)
    boolean existsByChatRoomIdAndUserId(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);
}
