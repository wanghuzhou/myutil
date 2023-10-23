package com.utiltest;

import com.wanghz.myutil.date.DateUtils;
import org.junit.Test;

import java.time.ZonedDateTime;

/**
 * @author whz
 * @date 2021/12/20 11:37
 */
public class DateTest {
    @Test
    public void test1() {
//        String s = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String s = ZonedDateTime.now().format(DateUtils.dtf_ZE);
        System.out.println(s);
    }
}
