package study.sunsuwedding.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CursorPaginationResponse<T> {

    private List<T> data;
    private Long nextCursor; // 다음 페이지의 커서 값 (없으면 null)

    public CursorPaginationResponse(Slice<T> slice, Long nextCursor) {
        this.data = slice.getContent();
        this.nextCursor = nextCursor;
    }
}
