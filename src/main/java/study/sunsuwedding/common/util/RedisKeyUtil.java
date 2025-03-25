package study.sunsuwedding.common.util;

public class RedisKeyUtil {

    public static String portfolioCursorDefaultKey(Long cursor) {
        return "cache:portfolio:default:cursor:" + cursor;
    }

    public static String portfolioCursorByLocationKey(String location, Long cursor) {
        return "cache:portfolio:location:" + location + ":cursor:" + cursor;
    }
}

