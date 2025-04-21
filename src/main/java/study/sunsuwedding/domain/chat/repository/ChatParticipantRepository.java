package study.sunsuwedding.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.sunsuwedding.domain.chat.entity.ChatParticipant;

import java.util.List;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    @Query("""
                SELECT COUNT(cp) > 0 
                FROM ChatParticipant cp
                JOIN cp.chatRoom cr
                JOIN cp.user u
                WHERE cr.chatRoomCode = :chatRoomCode
                  AND u.id = :userId
            """)
    boolean existsByChatRoomCodeAndUserId(@Param("chatRoomCode") String chatRoomCode, @Param("userId") Long userId);

    @Query("""
                SELECT u.id
                FROM ChatParticipant cp
                JOIN cp.chatRoom cr
                JOIN cp.user u
                WHERE cr.chatRoomCode = :chatRoomCode
            """)
    List<Long> findUserIdsByChatRoomCode(@Param("chatRoomCode") String chatRoomCode);

    @Query("""
                SELECT COUNT(cp)
                FROM ChatParticipant cp
                JOIN cp.user u
                WHERE u.id = :userId
            """)
    long countByUserId(@Param("userId") Long userId);


}
