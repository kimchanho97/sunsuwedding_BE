package study.sunsuwedding.common.s3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import study.sunsuwedding.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum S3ErrorCode implements ErrorCode {

    EMPTY_FILE(HttpStatus.BAD_REQUEST, 4001, "파일이 비어있거나 파일 이름이 없습니다."),
    IO_EXCEPTION_ON_IMAGE_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, 4002, "이미지 업로드 중 오류가 발생했습니다."),
    NO_FILE_EXTENSION(HttpStatus.BAD_REQUEST, 4003, "파일 확장자가 없습니다."),
    PUT_OBJECT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, 4004, "S3에 파일을 업로드하는 중 오류가 발생했습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, 4004, "허용되지 않는 파일 확장자입니다."),
    DELETE_OBJECT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, 4005, "S3에 파일을 삭제하는 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
