package study.sunsuwedding.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.chat.dto.*;
import study.sunsuwedding.domain.chat.entity.ChatRoom;
import study.sunsuwedding.domain.chat.repository.ChatParticipantQueryRepository;
import study.sunsuwedding.domain.chat.repository.ChatParticipantRepository;
import study.sunsuwedding.domain.chat.repository.ChatRoomQueryRepository;
import study.sunsuwedding.domain.chat.repository.ChatRoomRepository;
import study.sunsuwedding.domain.portfolio.exception.PortfolioException;
import study.sunsuwedding.domain.user.entity.Planner;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.PlannerRepository;
import study.sunsuwedding.domain.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;
    private final PlannerRepository plannerRepository;
    private final ChatParticipantQueryRepository chatParticipantQueryRepository;
    private final ChatRoomQueryRepository chatRoomQueryRepository;

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
                .map(room -> ChatRoomCreateResponse.fromEntity(room, true)) // 기존 방 존재
                .orElseGet(() -> {
                    ChatRoom newRoom = ChatRoom.create(user, planner);
                    chatRoomRepository.save(newRoom);
                    return ChatRoomCreateResponse.fromEntity(newRoom, false); // 새로 생성
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

    @Override
    public List<String> findChatRoomCodesByUserIdSorted(Long userId, int size) {
        return chatRoomQueryRepository.findChatRoomCodesByUserIdSorted(userId, size);
    }

    @Override
    public long countChatRoomsByUserId(Long userId) {
        return chatParticipantRepository.countByUserId(userId);
    }

    @Override
    public Map<String, ChatRoomMetaResponse> getChatRoomMetas(List<String> chatRoomCodes) {
        return chatRoomQueryRepository.findChatRoomMetas(chatRoomCodes).stream()
                .collect(Collectors.toMap(
                        ChatRoomMetaDto::getChatRoomCode,
                        dto -> new ChatRoomMetaResponse(dto.getLastMessage(), dto.getLastMessageAt(), dto.getLastMessageSeqId())
                ));
    }

    @Override
    public Map<String, ChatRoomMetaResponse> getAllChatRoomMetas() {
        return chatRoomQueryRepository.findAllChatRoomMetas().stream()
                .collect(Collectors.toMap(
                        ChatRoomMetaDto::getChatRoomCode,
                        dto -> new ChatRoomMetaResponse(dto.getLastMessage(), dto.getLastMessageAt(), dto.getLastMessageSeqId())
                ));
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