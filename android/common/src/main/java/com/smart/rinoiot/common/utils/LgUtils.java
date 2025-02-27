package com.smart.rinoiot.common.utils;

import android.app.Application;

import com.apkfuns.logutils.LogUtils;
import com.smart.rinoiot.common.BuildConfig;
import com.smart.rinoiot.common.R;

/**
 * 日志框架集成，以后使用都用它，方便后期维护，切换
 *
 * @Package: com.znkit.smart.mifei.utils
 * @ClassName: LgUtil
 * @Description: java类作用描述
 * @Author: xf
 * @CreateDate: 2020/12/9 5:36 PM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/12/9 5:36 PM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class LgUtils {
    public static void init(Application application) {
        LogUtils.getLogConfig()
                .configAllowLog(BuildConfig.DEBUG)
                .configTagPrefix(application.getResources().getString(R.string.rino_app_name))
                .configShowBorders(true)
                .configFormatTag("%d{HH:mm:ss:SSS} %t %c{-5}");
    }

    public static void d(Object debug) {
        LogUtils.d(debug);
    }

    public static void e(Object error) {
        LogUtils.e(error);
    }

    public static void i(Object info) {
        LogUtils.i(info);
    }

    public static void w(Object warn) {
        LogUtils.w(warn);
    }

    public static void v(Object v) {
        LogUtils.v(v);
    }
}
