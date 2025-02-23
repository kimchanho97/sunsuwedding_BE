package study.sunsuwedding.user.exception;

import study.sunsuwedding.common.exception.BusinessException;

public class UserException extends BusinessException {

    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }

    public static UserException userNotFound() {
        return new UserException(UserErrorCode.USER_NOT_FOUND);
    }
}
