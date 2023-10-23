package com.wanghz.myutil.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author whz
 * @date 2021/12/20 11:08
 */
public class DateUtils {

    public static final String NORM_DATE_PATTERN = "yyyy-MM-dd";
    public static final DateTimeFormatter NORM_DATE_FORMAT = DateTimeFormatter.ofPattern(NORM_DATE_PATTERN);

    public static final String NORM_TIME_PATTERN = "HH:mm:ss";
    public static final DateTimeFormatter NORM_TIME_FORMAT = DateTimeFormatter.ofPattern(NORM_TIME_PATTERN);

    public static final String NORM_DATETIME_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";
    public static final DateTimeFormatter NORM_DATETIME_MINUTE_FORMATTER = DateTimeFormatter.ofPattern(NORM_DATETIME_MINUTE_PATTERN);

    public static final String UTC_MS_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final DateTimeFormatter UTC_MS_WITH_ZONE_OFFSET_FORMAT = DateTimeFormatter.ofPattern(UTC_MS_WITH_ZONE_OFFSET_PATTERN);

    public static final String UTC_WITH_XXX_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";

    public static final DateTimeFormatter UTC_WITH_XXX_OFFSET_FORMAT = DateTimeFormatter.ofPattern(UTC_WITH_XXX_OFFSET_PATTERN);

    public static final DateTimeFormatter dtf_ZE = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ E");


    /**
     * LocalDateTime 转 Date
     */
    public static Date localDateToDate(LocalDateTime localDateTime) {
//        return new Date(localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date 转 LocalDate
     */
    public static LocalDate dateToLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Date 转 LocalDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
//      .atZone(ZoneOffset.ofHours(8))
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
