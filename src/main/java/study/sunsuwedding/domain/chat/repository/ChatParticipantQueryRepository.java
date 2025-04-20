package study.sunsuwedding.domain.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.sunsuwedding.domain.chat.dto.ChatRoomPartnerProfileResponse;
import study.sunsuwedding.domain.chat.dto.QChatRoomPartnerProfileResponse;

import java.util.List;
import java.util.Optional;

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

}
