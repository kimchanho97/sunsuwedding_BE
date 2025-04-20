package study.sunsuwedding.domain.chat.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import study.sunsuwedding.common.entity.BaseTimeEntity;
import study.sunsuwedding.domain.user.entity.User;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Entity
@Table(
        name = "chat_participant",
        uniqueConstraints = @UniqueConstraint(columnNames = {"chat_room_id", "user_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE chat_participant SET is_lefted = true, left_at = NOW() WHERE chat_participant_id = ?")
@SQLRestriction("is_lefted = false")
public class ChatParticipant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_participant_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Long lastReadSeqId; // 마지막으로 읽은 메시지 시퀀스 ID

    @Column(nullable = false)
    private Boolean isLefted; // 채팅방 나가기 시 true로 변경
    private LocalDateTime leftAt; // 채팅방 나간 시간

    public ChatParticipant(ChatRoom chatRoom, User user) {
        this.chatRoom = chatRoom;
        this.user = user;
        this.isLefted = false;
    }
}
