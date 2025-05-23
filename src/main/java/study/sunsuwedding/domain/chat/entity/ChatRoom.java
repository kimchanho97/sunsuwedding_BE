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
import java.util.UUID;

@Getter
@Entity
@Table(name = "chat_room")
@SQLDelete(sql = "UPDATE chat_room SET is_deleted = true, deleted_at = NOW() WHERE chat_room_id = ?")
@SQLRestriction("is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    private String lastMessage; // 마지막 메시지
    private LocalDateTime lastMessageAt; // 마지막 메시지 시간
    private Long lastMessageSeqId; // 마지막 메시지 시퀀스 ID

    @Column(nullable = false)
    private Boolean isDeleted;
    private LocalDateTime deletedAt;

    @Column(name = "chat_room_code", unique = true, nullable = false, updatable = false)
    private String chatRoomCode;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> participants = new ArrayList<>();

    public static ChatRoom create(User user, Planner planner) {
        ChatRoom chatRoom = new ChatRoom(false);
        chatRoom.participants.add(new ChatParticipant(chatRoom, user));
        chatRoom.participants.add(new ChatParticipant(chatRoom, planner));
        return chatRoom;
    }

    private ChatRoom(Boolean isDeleted) {
        this.isDeleted = isDeleted;
        this.chatRoomCode = UUID.randomUUID().toString();
        this.lastMessage = "";
        this.lastMessageAt = LocalDateTime.now();
        this.lastMessageSeqId = 0L;
    }
}
