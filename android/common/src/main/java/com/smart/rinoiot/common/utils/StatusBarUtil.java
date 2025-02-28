package com.smart.rinoiot.common.utils;

import android.app.Activity;
import android.os.Build;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.gyf.immersionbar.ImmersionBar;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.R;

/**
 * 状态来工具类
 *
 * @Package: com.znkit.smart.mifei.utils
 * @ClassName: StatusBarUtil
 * @Description: java类作用描述
 * @Author: xf
 * @CreateDate: 2020/12/10 3:17 PM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/12/10 3:17 PM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class StatusBarUtil {
    public static void setNormalStatusBar(Activity activity) {
//        ImmersionBar.with(activity).fitsSystemWindows(true).statusBarColor(R.color.toolbar_bg)
        ImmersionBar.with(activity).fitsSystemWindows(false).statusBarColor(R.color.toolbar_bg)
                .statusBarDarkFont(Constant.IS_DARK_FONT, 0.2f)
                .init();
    }

    public static void setNormalStatusBar(Activity activity, int resourceId, boolean isDarkFont) {
        ImmersionBar.with(activity).fitsSystemWindows(true).statusBarColor(resourceId)
                .statusBarDarkFont(isDarkFont, 0.2f)
                .init();
    }

    public static void setFullStatusBar(Activity activity) {
        ImmersionBar.with(activity).fitsSystemWindows(false)
                .statusBarDarkFont(Constant.IS_DARK_FONT, 0.2f)
                .init();
    }

    public static void setTransparentFullStatusBar(Activity activity) {
        ImmersionBar.with(activity).fitsSystemWindows(false).statusBarColor(R.color.toolbar_bg)
                .statusBarDarkFont(Constant.IS_DARK_FONT, 0.2f)
                .init();
    }

    public static void setTransparentFullStatusBar(Activity activity, boolean isDarkFont) {
        ImmersionBar.with(activity).fitsSystemWindows(false).statusBarColor(R.color.toolbar_bg)
                .statusBarDarkFont(isDarkFont, 0.2f)
                .init();
    }

    public static void setTransparentNormalStatusBar(Activity activity, int resourceId) {
        ImmersionBar.with(activity).fitsSystemWindows(true).statusBarColor(resourceId)
                .statusBarDarkFont(Constant.IS_DARK_FONT, 0.2f)
                .init();
    }

    public static void setNormalStatusBar(Activity activity, int resourceId) {
        ImmersionBar.with(activity).fitsSystemWindows(false).statusBarColor(resourceId)
                .statusBarDarkFont(Constant.IS_DARK_FONT, 0.2f)
                .init();
    }

    /**
     * 积分商城 详情
     */
    public static void setTransparentFullIntegralStatusBar(Activity activity,boolean isDarkFont) {
        ImmersionBar.with(activity).fitsSystemWindows(false).statusBarColor(R.color.transparent)
                .statusBarDarkFont(isDarkFont, 0.2f)
                .init();
    }
    /**
     * 烧烤炉首页
     */
    public static void setTransparentFullTopWindStatusBar(Activity activity) {
        ImmersionBar.with(activity).fitsSystemWindows(false).statusBarColor(R.color.transparent)
                .statusBarDarkFont(Constant.IS_DARK_FONT, 0.2f)
                .init();
    }

    /**
     * 视频播放
     */
    public static void setTransparentFullVideoStatusBar(Activity activity) {
        ImmersionBar.with(activity).fitsSystemWindows(false).statusBarColor(R.color.transparent)
                .statusBarDarkFont(false, 0.2f)
                .init();
    }

    /**
     * webview播放
     */
    public static void setTransparentFullWebviewStatusBar(Activity activity) {
        ImmersionBar.with(activity).fitsSystemWindows(false).statusBarColor(R.color.transparent)
                .statusBarDarkFont(true, 0.2f)
                .init();
    }

    public static void setFullStatusBarColor(Activity activity, int color, boolean isDark) {
        ImmersionBar.with(activity).fitsSystemWindows(false).statusBarColor(color)
                .statusBarDarkFont(isDark, 0.2f)
                .init();
    }

    /**
     * Hides the status bar
     * @param activity the activity with the bar we want to hide
     */
    @SuppressWarnings("all")
    public static void hideStatusBar(@NonNull Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = activity.getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }
    }
}