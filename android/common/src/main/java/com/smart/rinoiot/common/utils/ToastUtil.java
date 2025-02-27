package com.smart.rinoiot.common.utils;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.smart.rinoiot.common.R;

import es.dmoral.toasty.MyToast;
import es.dmoral.toasty.Toasty;

/**
 * toast工具类，统一弹出用该类，方便后期维护
 *
 * @Package: com.znkit.smart.mifei.utils
 * @ClassName: ToastUtil
 * @Description: java类作用描述
 * @Author: xf
 * @CreateDate: 2020/12/9 5:18 PM
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/12/9 5:18 PM
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ToastUtil {
    private static Application application;

    public static void init(Application context) {
        application = context;
        MyToast.setDefaultInfoColor("#000000");
        MyToast.setDefaultSuccessColor("#88000000");
        MyToast.setDefaultErrorColor("#88000000");
        MyToast.init(context, false, true);
    }

    public static void showMsg(String msg) {
        if (TextUtils.isEmpty(msg) || TextUtils.equals("Connection reset", msg)) return;
        info(msg);
    }

    public static void showLongMsg(String msg) {
        if (TextUtils.isEmpty(msg) || TextUtils.equals("Connection reset", msg)) return;
        infoL(msg);
    }

    public static void showMsg(int res) {
        if (TextUtils.equals("Connection reset", application.getResources().getString(res))) return;
        try {
            info(application.getString(res));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showErrorMsg(int res) {
        info(application.getString(res));
    }

    public static void showErrorMsg(String error) {
        if (TextUtils.isEmpty(error)) return;
        info(error);
    }

    public static void showSuccessMsg(int res) {
        try {
            info(application.getString(res));
        } catch (Exception e) {
            info(String.valueOf(res));
            e.printStackTrace();
        }
    }

    public static void showSuccessMsg(String msg) {
        if (TextUtils.isEmpty(msg)) return;
        info(msg);
    }

    /**
     * 去掉图片展示
     */
    public static void info(String text) {
        runSafe(() -> Toasty.info(application, text, 0, false).show());
    }

    /**
     * 去掉图片展示
     */
    public static void infoL(String text) {
        runSafe(() -> Toasty.info(application, text, 1, false).show());
    }

    private static void runSafe(final Runnable runnable) {
        if (isMainThread()) {
            try {
                runnable.run();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        } else {
            getMainHanlder().post(() -> {
                try {
                    runnable.run();
                } catch (Exception var2) {
                    var2.printStackTrace();
                }

            });
        }

    }

    private static Handler mainHanlder;

    private static Handler getMainHanlder() {
        if (mainHanlder == null) {
            mainHanlder = new Handler(Looper.getMainLooper());
        }

        return mainHanlder;
    }

    private static boolean isMainThread() {
        long threadId = Thread.currentThread().getId();
        if (application == null) {
            return false;
        } else {
            long mainThreadId = application.getMainLooper().getThread().getId();
            return threadId == mainThreadId;
        }
    }

    public static void customSystem(Context context,String msg,int duration) {
        Toast toast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.toast_custom_layout, null);
        TextView toast_text = view.findViewById(R.id.toast_text);
        toast_text.setText(msg);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(view);
        toast.setDuration(duration);
        toast.show();
    }
}
