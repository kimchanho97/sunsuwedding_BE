package study.sunsuwedding.infra.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import study.sunsuwedding.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum S3ErrorCode implements ErrorCode {

    EMPTY_FILE(HttpStatus.BAD_REQUEST, 4001, "업로드할 파일이 존재하지 않습니다."),
    MISSING_FILE_EXTENSION(HttpStatus.BAD_REQUEST, 4002, "파일 확장자가 존재하지 않습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, 4003, "지원되지 않는 파일 확장자입니다."),
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 4004, "S3 파일 업로드 중 오류가 발생했습니다."),
    S3_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 4005, "S3 파일 삭제에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
