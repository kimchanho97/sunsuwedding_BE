package study.sunsuwedding.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {

    private final int code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(SuccessCode.OK.getCode(), SuccessCode.OK.getMessage(), data);
    }
}
