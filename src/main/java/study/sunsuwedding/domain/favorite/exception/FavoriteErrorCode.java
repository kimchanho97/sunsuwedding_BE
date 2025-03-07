package study.sunsuwedding.domain.favorite.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import study.sunsuwedding.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum FavoriteErrorCode implements ErrorCode {

    FAVORITE_ALREADY_EXISTS("이미 찜한 포트폴리오입니다.", 8001, HttpStatus.BAD_REQUEST),
    FAVORITE_NOT_FOUND("해당 찜하기 정보를 찾을 수 없습니다.", 8002, HttpStatus.NOT_FOUND);

    private final String message;
    private final int code;
    private final HttpStatus httpStatus;
}
