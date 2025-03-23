package study.sunsuwedding.common.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CursorPaginationResponse<T> {

    private List<T> data;
    private Long nextCursor; // 다음 페이지의 커서 값 (없으면 null)

    public CursorPaginationResponse(List<T> content, Long nextCursor) {
        this.data = content;
        this.nextCursor = nextCursor;
    }
}
