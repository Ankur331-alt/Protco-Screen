package com.smart.rinoiot.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

/**
 * 应用字体大小是否根据系统字体大小变化
 */
public class FontDisplayUtils {
    private static final String TAG = FontDisplayUtils.class.getSimpleName();

    /**
     * 保持字体大小不随系统设置变化（用在界面加载之前）
     * 要重写Activity的attachBaseContext()
     */
    public static Context attachBaseContext(Context context, float fontScale) {
        Configuration config = context.getResources().getConfiguration();
//        LgUtils.i(TAG + "    changeActivityFontScaleA " + config.fontScale + "fontScale  ==" + fontScale);
        //错误写法
//        if(config.fontScale != fontScale) {
//            config.fontScale = fontScale;
//            return context.createConfigurationContext(config);
//        } else {
//            return context;
//        }
        //正确写法
        config.fontScale = fontScale;
        return context.createConfigurationContext(config);
    }

    /**
     * 保持字体大小不随系统设置变化（用在界面加载之前）
     * 要重写Activity的getResources()
     */
    public static Resources getResources(Context context, Resources resources, float fontScale) {
        Configuration config = resources.getConfiguration();
//        LgUtils.i(TAG + " changeActivityFontScaleR " + config.fontScale + ", " + fontScale);
        if (config.fontScale != fontScale) {
            config.fontScale = fontScale;
            return context.createConfigurationContext(config).getResources();
        } else {
            return resources;
        }
    }

    /**
     * 保存字体大小，后通知界面重建，它会触发attachBaseContext，来改变字号
     */
    public static void recreate(Activity activity) {
//          activity.getWindow().getDecorView().requestLayout();
//          activity.getWindow().getDecorView().invalidate();
        //只有这句才有效，其它两句都无效
        activity.recreate();
    }
}
