package com.smart.rinoiot.common.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static boolean isEmail(String text) {
        String check = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(text);
        return matcher.matches();

    }

    public static String convertNumber(int num) {
        if (num < 1000) {
            return String.valueOf(num);
        }
        return String.format(Locale.getDefault(), "%.2f", num / 1000f) + "K";
    }

    public static String convertTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm yyyy.MM.dd");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 判断内容是否为空
     */
    public static boolean strIsNull(String str) {
        return TextUtils.isEmpty(str) || TextUtils.equals(str, "null")
                || TextUtils.equals(str, "0") || TextUtils.equals(str, "0.0");
    }

    /**
     * 食谱营养成分返回数据保留一位小数
     */
    public static double saveDecimal(String str) {
        if (strIsNull(str)) {
            return 0;
        }
        BigDecimal bigDecimal = BigDecimal.valueOf(Double.parseDouble(str));
        return bigDecimal.setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }


    public static boolean isTuYaUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.contains("images.tuyacn.com");

    }

    @SuppressWarnings("all")
    public static boolean isNumber(String str) {
        // 通过Matcher进行字符串匹配
        Pattern pattern = Pattern.compile("-?[0-9]+(\\\\.[0-9]+)?");
        Matcher m = pattern.matcher(str);
        // 如果正则匹配通过 m.matches() 方法返回 true ，反之 false
        return m.matches();
    }
    public static boolean isNull(String str) {
        return (null == str);
    }

    /**
     * Checks if a string is blank or empty
     * @param str string value
     * @return true if the string in null or blank.
     */
    public static boolean isBlank(String str) {
        return (isNull(str) || str.contentEquals(""));
    }

    /**
     * Checks if a string is not blank
     * @param str the string value
     * @return true if the string is not blank or null
     */
    public static boolean isNotBlank(String str) {
        return (!isNull(str) && str.length() > 0);
    }

    public static boolean hasBlank(String ...attrs) {
        List<String> strings = Arrays.asList(attrs);
        if(strings.isEmpty()){
            return true;
        }

        Optional<String> first = strings.stream().filter(StringUtil::isBlank).findFirst();
        return first.isPresent();
    }
}
