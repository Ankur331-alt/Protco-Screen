package com.smart.rinoiot.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.bean.DisplayDateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    /**
     * 一小时，3600秒
     */
    private static final long ONE_HOUR = 60 * 60;
    /**
     * 一分钟，60秒
     */
    private static final long ONE_MINUTE = 60;

    private static final  String[] CHINESE_NUMBERS = {
            "一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
            "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八",
            "十九", "二十", "二十一", "二十二", "二十三", "二十四", "二十五",
            "二十六", "二十七", "二十八", "二十九", "三十", "三十一"
    };

    /**
     * 当前年
     */
    public static int getCurrentDateYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.YEAR);
    }

    /**
     * 当前月
     */
    public static int getCurrentDateMonth() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.MONTH);
    }

    /**
     * 当前日
     */
    public static int getCurrentDateDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 当前时
     */
    public static int getCurrentDateHour() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 当前分
     */
    public static int getCurrentDateMinute() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.MINUTE);
    }

    /**
     * 星期几或者周几
     */
    public static String getCurrentDateWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String getCurrentDate(){
        Date date = new Date();
        Locale locale = Locale.getDefault();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
        if(locale.equals(Locale.CHINESE)){
            // Get an instance of the Calendar class and set the time to the provided date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // Get the day of the month
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            // Format the date and replace the day of the month with the Chinese equivalent
            String formattedDate = sdf.format(date);
            return formattedDate.replace(
                    Integer.toString(dayOfMonth), CHINESE_NUMBERS[dayOfMonth - 1]
            ).concat("日");
        }else{
            return sdf.format(new Date());
        }
    }

    public static DisplayDateTime getDisplayDateTime() {
        String date = getCurrentDate();
        return new DisplayDateTime(date, getCurrentTime());
    }

    public static String getDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String getDayAndYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static int getMonthOfDay(int year, int month) {
        int day;
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            day = 29;
        } else {
            day = 28;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return day;

        }

        return 0;
    }

    /**
     * 字符串时间转化date
     */
    @SuppressLint("SimpleDateFormat")
    public static Date getDate(String strTime) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            date = sdf.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 时间戳按规定格式转化成String
     */
    @SuppressLint("SimpleDateFormat")
    public static String getStringFromDate(long time, String pattern) {
        Date date = new Date(time);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * String按规定格式转化成时间戳
     */
    public static long getTimeFromString(String time, String pattern) {
        Date date = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
            date = sdf.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert date != null;
        return date.getTime();
    }

    /**
     * 判断指定时间是否超过12小时
     */
    public static boolean is24Time(String time) {
        return getTimeFromString(time, "HH:mm") > getTimeFromString("12:00", "HH:mm");
    }

    /**
     * String按规定格式转化成12小时制
     */
    public static String get12Time(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
            return sdf.format(new Date(getTimeFromString(time, "HH:mm")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * String按规定格式转化成24小时制
     */
    public static String get24Time(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return sdf.format(new Date(getTimeFromString(time, "hh:mm") + 1000 * 60 * 60 * 12));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /*将字符串转为时间戳*/
    public static long getStringToDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

    /*将字符串转为时间戳*/
    public static long getStringToDateLine(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
        return date.getTime();
    }

    /*将字符串转为时间戳, 英文方式*/
    public static long getStringToDateLineEn(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        Date date;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
        return date.getTime();
    }

    /**
     * 字符串时间转化date  年月日时分秒
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDateTime(String strTime) {
        try {
            long time = Long.parseLong(strTime);
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 字符串时间转化date 年月日
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDateTimeYMD(String strTime) {
        try {
            if (TextUtils.isEmpty(strTime)) return "";
            if (TextUtils.equals(strTime, "0")) return "";

            long time = Long.parseLong(strTime);
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 字符串时间转化date 年月日, 英文方式
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDateTimeYMDEn(String strTime) {
        try {
            if (TextUtils.isEmpty(strTime)) return "";
            if (TextUtils.equals(strTime, "0")) return "";

            long time = Long.parseLong(strTime);
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 字符串时间转化date 获取时分秒
     */
    public static String getDateHour(long strTime) {
        Date date = new Date(strTime);
        SimpleDateFormat sdf = new SimpleDateFormat(" HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * 获取当天的指定时间
     *
     * @param currentHour 设置时间点
     */
    public static long getSpecifyTime(int currentHour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, 0);//-1 前一天 0：当天； 1：后一天
        cal.set(Calendar.HOUR_OF_DAY, currentHour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 字符串时间转化date 获取时分
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDateHourMinute(long strTime) {
        Date date = new Date(strTime);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * 字符串时间转化date 年月日
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDateTimeYMD(long strTime) {
        try {
            Date date = new Date(strTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取指定天的开始时间
     */
    public static long getSpecifyStart(String strTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(Long.parseLong(strTime)));
        cal.add(Calendar.DAY_OF_YEAR, 0);//-1 前一天 0：当天； 1：后一天
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取指定天的结束时间
     */
    public static long getSpecifyEnd(String strTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(Long.parseLong(strTime)));
        cal.add(Calendar.DAY_OF_YEAR, 0);//-1 前一天 0：当天； 1：后一天
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTimeInMillis();
    }

    /**
     * 当前时间是否在同一天，从00:00---11:59
     *
     * @param beforeTime  上一条数据中的时间
     * @param currentTime 当前这条数据的时间
     */
    public static boolean isSameDay(long beforeTime, long currentTime) {
        boolean isSameFlag = false;
        if (beforeTime >= getSpecifyStart(String.valueOf(currentTime)) && beforeTime <= getSpecifyEnd(String.valueOf(currentTime))) {
            isSameFlag = true;
        }
        return isSameFlag;
    }

    /**
     * 秒数转化成可显示的时间，如1时1分1秒
     */
    public static String changeToTime(Context context, long second) {
        StringBuilder result = new StringBuilder();
        long remainder = second;
        if (remainder >= ONE_HOUR) {
            int hour = (int) (remainder / ONE_HOUR);
            result.append(hour > 9 ? hour : "0" + hour).append(context.getString(R.string.rino_common_hour_unit));
            remainder = remainder % ONE_HOUR;
        }
        if (remainder >= ONE_MINUTE) {
            int minute = (int) (remainder / ONE_MINUTE);
            result.append(minute > 9 ? minute : "0" + minute).append(context.getString(R.string.rino_common_minutes_unit));
            remainder = remainder % ONE_MINUTE;
        }
        if (remainder > 0) {
            result.append(remainder > 9 ? remainder : "0" + remainder).append(context.getString(R.string.rino_common_second_unit));
        }
        return result.toString();
    }

    /**
     * 注销时间设置
     */
    public static String logoutCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * 注销一周时间设置
     */
    public static String logoutWeekTime() {
        long weekTime = getSpecifyTime(0) + 7 * 24 * 3600 * 1000;
        Date date = new Date(weekTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return sdf.format(date);
    }
}
