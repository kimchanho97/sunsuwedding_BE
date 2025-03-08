package study.sunsuwedding.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OffsetPaginationResponse<T> {

    private List<T> data;
    private boolean hasNext; // 다음 페이지가 있는지 여부

    public OffsetPaginationResponse(Slice<T> slice) {
        this.data = slice.getContent();
        this.hasNext = slice.hasNext();
    }
}
