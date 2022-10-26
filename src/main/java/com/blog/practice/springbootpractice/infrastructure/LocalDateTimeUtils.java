package com.blog.practice.springbootpractice.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class LocalDateTimeUtils {
    public static final String DEFAULT_PATTERNS = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_PATTERNS);

    public static LocalDateTime toLocalDateTime(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, FORMATTER);
        } catch (Exception e) {
            String message = "파라미터 datetime 문자열 파싱이 싪패하였습니다. dateTime=" + dateTime;
            log.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    public static String toStringDateTime(LocalDateTime localDateTime) {
        try {
            return FORMATTER.format(localDateTime);
        } catch (Exception e) {
            String message = "파라미터 localDateTime 파싱이 실패하였씁니다. localDateTime =" + localDateTime;
            log.error(message);
            throw new IllegalArgumentException(message);
        }
    }

}
