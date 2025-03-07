package study.sunsuwedding.domain.favorite.exception;

import study.sunsuwedding.common.exception.BusinessException;
import study.sunsuwedding.common.exception.ErrorCode;

import static study.sunsuwedding.domain.favorite.exception.FavoriteErrorCode.FAVORITE_ALREADY_EXISTS;
import static study.sunsuwedding.domain.favorite.exception.FavoriteErrorCode.FAVORITE_NOT_FOUND;

public class FavoriteException extends BusinessException {

    public FavoriteException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static FavoriteException favoriteAlreadyExists() {
        return new FavoriteException(FAVORITE_ALREADY_EXISTS);
    }

    public static FavoriteException favoriteNotFound() {
        return new FavoriteException(FAVORITE_NOT_FOUND);
    }

}
