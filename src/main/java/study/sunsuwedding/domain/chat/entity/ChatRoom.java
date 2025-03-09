package study.sunsuwedding.domain.chat.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import study.sunsuwedding.common.entity.BaseTimeEntity;
import study.sunsuwedding.domain.user.entity.Planner;
import study.sunsuwedding.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "chat_room")
@SQLDelete(sql = "UPDATE chat SET is_deleted = true, deleted_at = NOW() WHERE chat_id = ?")
@SQLRestriction("is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

    private LocalDateTime lastMessageAt; // 마지막 메시지 시간
    private String lastMessage; // 마지막 메시지

    @Column(nullable = false)
    private Boolean isDeleted;
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> participants = new ArrayList<>();

    public static ChatRoom create(User user, Planner planner) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.participants.add(new ChatParticipant(chatRoom, user));
        chatRoom.participants.add(new ChatParticipant(chatRoom, planner));
        return chatRoom;
    }
}
