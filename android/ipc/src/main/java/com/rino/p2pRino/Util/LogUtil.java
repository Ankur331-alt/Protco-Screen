package com.rino.p2pRino.Util;

import android.util.Log;

public  class LogUtil {

    public static String className;

    /**
     * 获取文件名、方法名、所在行数
     * @param sElements StackTraceElement
     */
    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName().split("\\.")[0];
    }

    public static int v(String msg) {
        getMethodNames(new Throwable().getStackTrace());
        return Log.v(className, msg);
    }

    public static int d(String msg) {
        getMethodNames(new Throwable().getStackTrace());
        return Log.d(className, msg);
    }

    public static int i(String msg) {
        getMethodNames(new Throwable().getStackTrace());
        return Log.i(className, msg);
    }

    public static int w(String msg) {
        getMethodNames(new Throwable().getStackTrace());
        return Log.w(className, msg);
    }

    public static int e(String msg) {
        getMethodNames(new Throwable().getStackTrace());
        return Log.e(className, msg);
    }

}
