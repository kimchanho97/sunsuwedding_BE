package study.sunsuwedding.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.chat.dto.ChatRoomCreateResponse;
import study.sunsuwedding.domain.chat.dto.ChatRoomPartnerProfileRequest;
import study.sunsuwedding.domain.chat.dto.ChatRoomPartnerProfileResponse;
import study.sunsuwedding.domain.chat.entity.ChatRoom;
import study.sunsuwedding.domain.chat.repository.ChatParticipantQueryRepository;
import study.sunsuwedding.domain.chat.repository.ChatParticipantRepository;
import study.sunsuwedding.domain.chat.repository.ChatRoomRepository;
import study.sunsuwedding.domain.portfolio.exception.PortfolioException;
import study.sunsuwedding.domain.user.entity.Planner;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.PlannerRepository;
import study.sunsuwedding.domain.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;
    private final PlannerRepository plannerRepository;
    private final ChatParticipantQueryRepository chatParticipantQueryRepository;

    @Override
    public boolean validateChatRoom(String chatRoomCode, Long userId) {
        return chatParticipantRepository.existsByChatRoomCodeAndUserId(chatRoomCode, userId);
    }

    @Override
    @Transactional
    public ChatRoomCreateResponse createChatRoom(Long userId, Long plannerId) {
        User user = getUserById(userId);
        Planner planner = getPlannerById(plannerId);

        return chatRoomRepository.findExistingChatRoom(userId, plannerId)
                .map(ChatRoomCreateResponse::fromEntity)
                .orElseGet(() -> {
                    ChatRoom chatRoom = ChatRoom.create(user, planner);
                    chatRoomRepository.save(chatRoom);
                    return ChatRoomCreateResponse.fromEntity(chatRoom);
                });
    }

    @Override
    public List<Long> getParticipantUserIds(String chatRoomCode) {
        return chatParticipantRepository.findUserIdsByChatRoomCode(chatRoomCode);
    }

    @Override
    public List<ChatRoomPartnerProfileResponse> findPartnerProfiles(ChatRoomPartnerProfileRequest request) {
        return chatParticipantQueryRepository.findPartners(request.chatRoomCodes(), request.requesterId());
    }

    @Override
    public ChatRoomPartnerProfileResponse findPartnerProfile(String chatRoomCode, Long requesterId) {
        return chatParticipantQueryRepository.findPartner(chatRoomCode, requesterId)
                .orElseThrow(UserException::userNotFound);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserException::userNotFound);
    }

    private Planner getPlannerById(Long plannerId) {
        return plannerRepository.findById(plannerId)
                .orElseThrow(PortfolioException::plannerNotFound);
    }
}