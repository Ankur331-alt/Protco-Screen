package com.smart.rinoiot.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

public class ActivityUtils {

    /**
     * @param context
     * @param t       参数为Map 或者 对象
     * @param clazz
     * @param <T>
     */
    public static <T> void startActivity(Context context, T t, Class clazz) {
        if (context == null) return;
        Intent intent = new Intent(context, clazz);
        if (t != null) {
            intent.putExtra("parmars", (Serializable) t);
        }
        context.startActivity(intent);

    }

    /**
     * @param context 跨包隐式跳转
     * @param t       参数为Map 或者 对象
     * @param path   也买你全路径
     * @param <T>
     */
    public static <T> void startActivity(Context context, T t, String path) {
        if (context == null) return;
       Intent intent = new Intent();
        intent.setClassName(context, path);
        if (t != null) {
            intent.putExtra("parmars", (Serializable) t);
        }
        context.startActivity(intent);

    }

    public static <T> void startActivityForResult(Context context, T t, Class clazz, int requestCode) {
        if (context == null) return;
        Intent intent = new Intent(context, clazz);
        if (t != null) {
            intent.putExtra("parmars", (Serializable) t);
        }
        ((Activity) context).startActivityForResult(intent, requestCode);

    }

    public static <T> T getParmars(Intent intent, T t) {
        if (intent != null && intent.getSerializableExtra("parmars")!=null) {
            return (T) intent.getSerializableExtra("parmars");
        }
        return null;
    }


}
