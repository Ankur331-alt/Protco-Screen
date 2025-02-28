package com.smart.rinoiot.common.utils;

import android.os.SystemClock;

/**
 * @author tw
 * @time 2022/11/24 10:57
 * @description 常用功能防抖
 */
public class AntiShakeUtils {
    private static final long COLD_TIME_CLICK = 500L;
    private static final long COLD_TIME_LONG_CLICK = 1500L;

    private static long sLastResponseClick = 0;

    public static boolean canResponseClick() {
        long currentTime = SystemClock.elapsedRealtime();
        return (currentTime - sLastResponseClick) >= COLD_TIME_CLICK;
    }

    public static void updateLastClickTime() {
        sLastResponseClick = SystemClock.elapsedRealtime();
    }

    public static boolean canResponseLongClick() {
        long currentTime = SystemClock.elapsedRealtime();
        return (currentTime - sLastResponseClick) >= COLD_TIME_LONG_CLICK;
    }

}
