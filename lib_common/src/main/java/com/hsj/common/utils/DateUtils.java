package com.hsj.common.utils;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @Author:hsj
 * @Date:2019-07-23
 * @Class:DateUtils
 * @Desc:时间格式化工具类
 */
public final class DateUtils {

    //////////////////////////////////////////////////////////////
    // DateUtils 功能如下：
    //      1、格式化时间戳
    //      2、格式化系统当前时间
    //      3、格式化的时间转时间戳
    //      4、定时器：CountDownTimer;
    //////////////////////////////////////////////////////////////

    private DateUtils()  {
        throw new IllegalAccessError("DateUtils can't be instance");
    }

    /**
     * 一分钟的毫秒值
     */
    private static final long ONE_MINUTE = 60 * 1000;

    /**
     * 一小时的毫秒值
     */
    private static final long ONE_HOUR = 60 * ONE_MINUTE;

    /**
     * 一天的毫秒值
     */
    private static final long ONE_DAY = 24 * ONE_HOUR;

    /**
     * 两天天的毫秒值
     */
    private static final long TWO_DAY = 48 * ONE_HOUR;

    /**
     * 三天的毫秒值
     */
    private static final long THREE_DAY = 72 * ONE_HOUR;

    /**
     * 一月的毫秒值，用于判断上次的更新时间
     */
    private static final long ONE_MONTH = 30 * TWO_DAY;

    /**
     * 一年的毫秒值，用于判断上次的更新时间
     */
    private static final long ONE_YEAR = 12 * ONE_MONTH;

    /**
     * 格式化指定时间
     *
     * @param desFormat 指定格式
     * @param desTime   指定时间
     * @return 返回格式
     * @exception IllegalArgumentException if desFormat or desTime is invalid
     */
    public static String formatTime(String desFormat, long desTime) {
        if (TextUtils.isEmpty(desFormat) || desTime <= 0) return null;
        SimpleDateFormat format = new SimpleDateFormat(desFormat, Locale.CHINA);
        return format.format(desTime);
    }

    /**
     * 根据目标格式和目标格式化时间获取时间戳
     *
     * @param desFormat 目标格式
     * @param desTime   目标时间
     * @return 获取时间戳
     * @exception IllegalArgumentException if desFormat or desTime is invalid
     */
    public static long getTimeStamp(String desFormat, String desTime) {
        if (TextUtils.isEmpty(desFormat) || TextUtils.isEmpty(desTime)) return -2;
        SimpleDateFormat sdf = new SimpleDateFormat(desFormat, Locale.CHINA);
        Date date;
        try {
            date = sdf.parse(desTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        return date == null ? -1 : date.getTime();
    }

    /**
     * 社区时间的文字描述：（HH为24小时制，hh为12小时制）
     * "刚刚"、"x分钟前"、"今天/昨天/前天 HH:mm"、"MM月dd日 HH:mm"（今年）、"yyyy年MM月dd日 HH:mm"（非今年）
     *
     * @param timeStamp 时间戳
     * @return 返回格式化
     * @exception IllegalArgumentException if timeStamp is invalid
     */
    public static String formatTimeForZone(long timeStamp) {
        if (timeStamp <= 0) return null;
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - timeStamp;
        String updateAtValue = "";
        if (timePassed < ONE_MINUTE) {
            //一分钟内
            updateAtValue = "刚刚";
        } else if (timePassed < ONE_HOUR) {
            //几分钟内
            long timeIntoFormat = timePassed / ONE_MINUTE;
            updateAtValue = timeIntoFormat + "分钟前";
        } else if (timePassed < TWO_DAY) {
            //两天内
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date date = calendar.getTime();
            long timeIntoFormat = date.getTime();
            if (timeStamp < timeIntoFormat) {
                //昨天
                updateAtValue = "昨天 " + formatTime("HH:mm", timeStamp);
            } else {
                //今天
                updateAtValue = "今天 " + formatTime("HH:mm", timeStamp);
            }
        } else if (timePassed < THREE_DAY) {
            //在前天
            updateAtValue = "前天 " + formatTime("HH:mm", timeStamp);
        } else if (timePassed < ONE_YEAR) {
            //一年以内
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.YEAR, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date date = calendar.getTime();
            long timeIntoFormat = date.getTime();
            if (timeStamp < timeIntoFormat) {
                //去年
                updateAtValue = formatTime("yyyy年MM月dd日 HH:mm", timeStamp);
            } else {
                //今年
                updateAtValue = formatTime("MM月dd日 HH:mm", timeStamp);
            }
        } else {
            //大于一年
            updateAtValue = formatTime("yyyy年MM月dd日 HH:mm", timeStamp);
        }
        return updateAtValue;
    }

}