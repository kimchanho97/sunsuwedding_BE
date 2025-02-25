package study.sunsuwedding.domain.user.exception;

import study.sunsuwedding.common.exception.BusinessException;

public class UserException extends BusinessException {

    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }

    public static UserException userNotFound() {
        return new UserException(UserErrorCode.USER_NOT_FOUND);
    }

    public static UserException plannerNotFound() {
        return new UserException(UserErrorCode.PLANNER_NOT_FOUND);
    }

    public static UserException duplicateEmail() {
        return new UserException(UserErrorCode.DUPLICATE_EMAIL);
    }

    public static UserException emailNotFound() {
        return new UserException(UserErrorCode.EMAIL_NOT_FOUND);
    }

    public static UserException invalidRole() {
        return new UserException(UserErrorCode.INVALID_ROLE);
    }

    public static UserException incorrectPassword() {
        return new UserException(UserErrorCode.INCORRECT_PASSWORD);
    }

    public static UserException passwordMismatch() {
        return new UserException(UserErrorCode.PASSWORD_MISMATCH);
    }

    public static UserException alreadyPremiumUser() {
        return new UserException(UserErrorCode.ALREADY_PREMIUM_USER);
    }

    public static UserException deletedUser() {
        return new UserException(UserErrorCode.DELETED_USER);
    }
}

