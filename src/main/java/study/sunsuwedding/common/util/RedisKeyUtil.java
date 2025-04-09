package study.sunsuwedding.common.util;

public class RedisKeyUtil {

    private static final String PREFIX_PORTFOLIO = "cache:portfolio";
    private static final String PREFIX_FAVORITE = "cache:favorite";

    public static String portfolioCursorDefaultKey(Long cursor) {
        return PREFIX_PORTFOLIO + ":default:cursor:" + cursor;
    }

    public static String portfolioCursorByLocationKey(String location, Long cursor) {
        return PREFIX_PORTFOLIO + ":location:" + location + ":cursor:" + cursor;
    }

    public static String favoriteAddRequestKey(Long userId) {
        return PREFIX_FAVORITE + ":add:request:user:" + userId;
    }

    public static String favoriteDeleteRequestKey(Long userId) {
        return PREFIX_FAVORITE + ":delete:request:user:" + userId;
    }

    public static String favoriteAddChangedUserSetKey() {
        return PREFIX_FAVORITE + ":add:changed-users";
    }

    public static String favoriteDeleteChangedUserSetKey() {
        return PREFIX_FAVORITE + ":delete:changed-users";
    }
}

