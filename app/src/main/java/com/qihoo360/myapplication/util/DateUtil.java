package com.qihoo360.myapplication.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * DateUtil
 *
 * @author hujunjie
 * @version 1.0
 * @since 15-3-16 下午2:24
 */
public class DateUtil {

    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 根据格式得到格式化后的时间
     *
     * @param currDate 要格式化的时间
     * @param format   时间格式，如yyyy-MM-dd HH:mm:ss
     * @return String 返回格式化后的时间，格式由参数<code>format</code>定义，如yyyy-MM-dd HH:mm:ss
     * @see SimpleDateFormat#format(Date)
     */
    public static String getFormatDateTime(java.util.Date currDate, String format) {
        SimpleDateFormat fmt;
        try {
            fmt = new SimpleDateFormat(format, Locale.getDefault());
            return fmt.format(currDate);
        } catch (Exception e) {
            fmt = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
            try {
                return fmt.format(currDate);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public static String getTime(long millionSeconds) {
        return getFormatDateTime(new Date(millionSeconds), "yyyy-MM-dd HH:mm");
    }
}
