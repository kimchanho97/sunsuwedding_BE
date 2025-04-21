package study.sunsuwedding.domain.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.sunsuwedding.domain.chat.dto.ChatRoomMetaDto;
import study.sunsuwedding.domain.chat.dto.QChatRoomMetaDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static study.sunsuwedding.domain.chat.entity.QChatParticipant.chatParticipant;
import static study.sunsuwedding.domain.chat.entity.QChatRoom.chatRoom;
import static study.sunsuwedding.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class ChatRoomQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<String> findChatRoomCodesByUserIdSorted(Long userId, int size) {
        return queryFactory
                .select(chatRoom.chatRoomCode)
                .from(chatParticipant)
                .join(chatParticipant.chatRoom, chatRoom)
                .join(chatParticipant.user, user)
                .where(chatParticipant.user.id.eq(userId))
                .orderBy(chatRoom.lastMessageAt.desc().nullsLast())
                .limit(size)
                .fetch();
    }

    public List<ChatRoomMetaDto> findChatRoomMetas(List<String> chatRoomCodes) {
        return queryFactory
                .select(new QChatRoomMetaDto(
                        chatRoom.chatRoomCode,
                        chatRoom.lastMessage,
                        chatRoom.lastMessageAt,
                        chatRoom.lastMessageSeqId))
                .from(chatRoom)
                .where(chatRoom.chatRoomCode.in(chatRoomCodes))
                .fetch();
    }

    public Map<String, Long> findChatRoomCodeToIdMap(List<String> chatRoomCodes) {
        return queryFactory
                .select(chatRoom.chatRoomCode, chatRoom.id)
                .from(chatRoom)
                .where(chatRoom.chatRoomCode.in(chatRoomCodes))
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(chatRoom.chatRoomCode),
                        tuple -> tuple.get(chatRoom.id)
                ));
    }
}
