package study.sunsuwedding.domain.chat.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import study.sunsuwedding.domain.chat.dto.ChatReadSequenceUpdateDto;
import study.sunsuwedding.domain.chat.dto.ChatRoomMetaSyncRequest;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatBatchJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchUpdateChatRoomMeta(List<ChatRoomMetaSyncRequest> requests) {
        String sql = """
                    UPDATE chat_room
                    SET last_message = ?, last_message_at = ?, last_message_seq_id = ?
                    WHERE chat_room_code = ?
                """;

        jdbcTemplate.batchUpdate(sql, requests, requests.size(), (ps, req) -> {
            ps.setString(1, req.lastMessage());
            ps.setObject(2, LocalDateTime.parse(req.lastMessageAt()));
            ps.setLong(3, Long.parseLong(req.lastMessageSeqId()));
            ps.setString(4, req.chatRoomCode());
        });
    }

    public void batchUpdateLastReadSeq(List<ChatReadSequenceUpdateDto> resolvedRequests) {
        String sql = """
                    UPDATE chat_participant
                    SET last_read_seq_id = ?
                    WHERE chat_room_id = ? AND user_id = ?
                """;

        jdbcTemplate.batchUpdate(sql, resolvedRequests, resolvedRequests.size(), (ps, req) -> {
            ps.setLong(1, req.lastReadSeqId());
            ps.setLong(2, req.chatRoomId());
            ps.setLong(3, req.userId());
        });
    }
}
