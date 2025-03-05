package study.sunsuwedding.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatter {

    public static String formatDateInKorean(LocalDateTime time) {
        if (time == null) return "";
        return time.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
    }

    public static String formatDateTimeInKorean(LocalDateTime time) {
        if (time == null) return "";
        return time.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
    }
}
