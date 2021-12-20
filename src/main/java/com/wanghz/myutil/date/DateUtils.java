package com.wanghz.myutil.date;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author whz
 * @date 2021/12/20 11:08
 */
public class DateUtils {

    public static final DateTimeFormatter dtf_datetime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter dtf_datetimeSSSZ = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ E");
    public static final DateTimeFormatter dtf_date = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter dtf_time = DateTimeFormatter.ofPattern("HH:mm:ss");

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
