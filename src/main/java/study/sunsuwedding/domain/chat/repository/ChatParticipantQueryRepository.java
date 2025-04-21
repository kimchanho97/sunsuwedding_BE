package study.sunsuwedding.domain.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.sunsuwedding.domain.chat.dto.ChatRoomPartnerProfileResponse;
import study.sunsuwedding.domain.chat.dto.QChatRoomPartnerProfileResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static study.sunsuwedding.domain.chat.entity.QChatParticipant.chatParticipant;
import static study.sunsuwedding.domain.chat.entity.QChatRoom.chatRoom;
import static study.sunsuwedding.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class ChatParticipantQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<ChatRoomPartnerProfileResponse> findPartners(List<String> chatRoomCodes, Long requesterId) {
        return queryFactory
                .select(new QChatRoomPartnerProfileResponse(
                        chatRoom.chatRoomCode,
                        user.id,
                        user.username,
                        user.fileUrl))
                .from(chatParticipant)
                .join(chatParticipant.chatRoom, chatRoom)
                .join(chatParticipant.user, user)
                .where(
                        chatRoom.chatRoomCode.in(chatRoomCodes),
                        user.id.ne(requesterId)
                )
                .fetch();
    }

    public Optional<ChatRoomPartnerProfileResponse> findPartner(String chatRoomCode, Long requesterId) {
        ChatRoomPartnerProfileResponse result = queryFactory
                .select(new QChatRoomPartnerProfileResponse(
                        chatRoom.chatRoomCode,
                        user.id,
                        user.username,
                        user.fileUrl
                ))
                .from(chatParticipant)
                .join(chatParticipant.chatRoom, chatRoom)
                .join(chatParticipant.user, user)
                .where(
                        chatRoom.chatRoomCode.eq(chatRoomCode),
                        user.id.ne(requesterId)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    public Map<Long, Long> findReadSequencesGroupedByUserInRoom(String chatRoomCode) {
        return queryFactory
                .select(chatParticipant.user.id, chatParticipant.lastReadSeqId)
                .from(chatParticipant)
                .join(chatParticipant.chatRoom, chatRoom)
                .join(chatParticipant.user, user)
                .where(chatRoom.chatRoomCode.eq(chatRoomCode))
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(chatParticipant.user.id),
                        tuple -> Optional.ofNullable(tuple.get(chatParticipant.lastReadSeqId)).orElse(0L)
                ));
    }

    public Map<String, Long> findReadSequencesGroupedByChatRoomForUser(List<String> chatRoomCodes, Long userId) {
        return queryFactory
                .select(chatRoom.chatRoomCode, chatParticipant.lastReadSeqId)
                .from(chatParticipant)
                .join(chatParticipant.chatRoom, chatRoom)
                .join(chatParticipant.user, user)
                .where(
                        chatRoom.chatRoomCode.in(chatRoomCodes),
                        user.id.eq(userId)
                )
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(chatRoom.chatRoomCode),
                        tuple -> Optional.ofNullable(tuple.get(chatParticipant.lastReadSeqId)).orElse(0L)
                ));
    }

}
