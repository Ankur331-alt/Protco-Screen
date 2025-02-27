package com.smart.rinoiot.common.utils;

import android.app.Application;
import android.util.DisplayMetrics;

/**
 * Android dp sp单位转换工具类
 *
 * @Package: com.wq.lib.base.utils
 * @ClassName: DpUtils.java
 * @Author: xf
 * @CreateDate: 2020/3/29 17:47
 * @UpdateUser: 更新者：xf
 * @UpdateDate: 2020/3/29 17:47
 * @Version: 1.0
 */
public class DpUtils {
    private static float scale;
    private static float fontScale;
    private static float width;
    private static float height;

    public static void init(Application application) {
        DisplayMetrics displayMetrics = application.getResources().getDisplayMetrics();
        scale = displayMetrics.density;
        fontScale = displayMetrics.scaledDensity;
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
    }

    /**
     * 获取屏幕宽度
     *
     * @return float
     * @method getScreenWidth
     * @date: 2020/5/18 2:44 PM
     * @author: xf
     */
    public static float getScreenWidth() {
        return width;
    }

    /**
     * 获取屏幕高度
     *
     * @return float
     * @method getScreenHeight
     * @date: 2020/5/18 2:44 PM
     * @author: xf
     */
    public static float getScreenHeight() {
        return height;
    }

    /**
     * @param
     * @return
     * @method px2dip
     * @description px转dp
     * @date: 2020/3/29 17:47
     * @author: xf
     */
    public static int px2dip(float pxValue) {
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @param
     * @return
     * @method dip2px
     * @description dp转像素
     * @date: 2020/3/29 17:47
     * @author: xf
     */
    public static int dip2px(float dpValue) {
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static float px2sp(float pxValue) {
        return (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static float sp2px(float spValue) {
        return (spValue * fontScale + 0.5f);
    }

    /**
     * 将sp值转换为dp值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int dp2sp(float spValue) {
        return  px2dip(sp2px(spValue));
    }

}