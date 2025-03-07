package study.sunsuwedding.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.chat.dto.ChatRoomCreateResponse;
import study.sunsuwedding.domain.chat.entity.ChatRoom;
import study.sunsuwedding.domain.chat.repository.ChatRoomRepository;
import study.sunsuwedding.domain.portfolio.exception.PortfolioException;
import study.sunsuwedding.domain.user.entity.Planner;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.PlannerRepository;
import study.sunsuwedding.domain.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final PlannerRepository plannerRepository;

    @Override
    @Transactional
    public ChatRoomCreateResponse createChatRoom(Long userId, Long plannerId) {
        User user = getUserById(userId);
        Planner planner = getPlannerById(plannerId);

        // 기존 채팅방 존재 여부 확인
        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findExistingChatRoom(userId, plannerId);
        if (existingChatRoom.isPresent()) {
            return ChatRoomCreateResponse.fromEntity(existingChatRoom.get());
        }

        // 채팅방 생성 (엔티티가 직접 생성 책임을 가짐)
        ChatRoom chatRoom = ChatRoom.create(user, planner);
        chatRoomRepository.save(chatRoom);
        return ChatRoomCreateResponse.fromEntity(chatRoom);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserException::userNotFound);
    }

    private Planner getPlannerById(Long userId) {
        return plannerRepository.findById(userId)
                .orElseThrow(PortfolioException::plannerNotFound);
    }
}
