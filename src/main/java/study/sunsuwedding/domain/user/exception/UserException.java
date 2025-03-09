package study.sunsuwedding.domain.user.exception;

import study.sunsuwedding.common.exception.BusinessException;

import static study.sunsuwedding.domain.user.exception.UserErrorCode.*;

public class UserException extends BusinessException {

    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }

    public static UserException emailNotFound() {
        return new UserException(EMAIL_NOT_FOUND);
    }

    public static UserException incorrectPassword() {
        return new UserException(INCORRECT_PASSWORD);
    }

    public static UserException deletedUser() {
        return new UserException(DELETED_USER);
    }

    public static UserException duplicateEmail() {
        return new UserException(DUPLICATE_EMAIL);
    }

    public static UserException invalidRole() {
        return new UserException(INVALID_ROLE);
    }

    public static UserException passwordMismatch() {
        return new UserException(PASSWORD_MISMATCH);
    }

    public static UserException userNotFound() {
        return new UserException(USER_NOT_FOUND);
    }

    public static UserException noProfileImage() {
        return new UserException(NO_PROFILE_IMAGE);
    }

}

