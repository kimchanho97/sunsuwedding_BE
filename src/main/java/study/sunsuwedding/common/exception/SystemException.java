package study.sunsuwedding.common.exception;

import lombok.Getter;

@Getter
public class SystemException extends BaseException {

    public SystemException(ErrorCode errorCode) {
        super(errorCode);
    }
}
