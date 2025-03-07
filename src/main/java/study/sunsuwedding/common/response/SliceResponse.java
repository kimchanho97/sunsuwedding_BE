package study.sunsuwedding.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SliceResponse<T> {
    private List<T> data;
    private Long nextCursor; // ✅ 다음 페이지 조회를 위한 커서 값

    public SliceResponse(Slice<T> slice, Long nextCursor) {
        this.data = slice.getContent();
        this.nextCursor = nextCursor;
    }
}
