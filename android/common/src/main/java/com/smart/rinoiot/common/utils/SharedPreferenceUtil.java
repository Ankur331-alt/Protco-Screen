package com.smart.rinoiot.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

/**
 * sharedPreference 工具类
 */
public final class SharedPreferenceUtil {

    private static final String FILE_NAME = "shareData"; //文件名
    @SuppressLint("StaticFieldLeak")
    private static SharedPreferenceUtil mInstance;
    private Context context;

    private SharedPreferenceUtil() {
    }

    public void init(Context context) {
        this.context = context;
    }

    public static SharedPreferenceUtil getInstance() {
        if (mInstance == null) {
            synchronized (SharedPreferenceUtil.class) {
                if (mInstance == null) {
                    mInstance = new SharedPreferenceUtil();
                }
            }
        }
        return mInstance;
    }

    /** 清除指定缓存数据 */
    public void remove(String key) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * 存入键值对
     *
     * @param key 键
     * @param value 值
     */
    public void put(String key, Object value) {
        //判断类型
        String type = value.getClass().getSimpleName();
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (type) {
            case "Integer":
                editor.putInt(key, (Integer) value);
                break;
            case "Boolean":
                editor.putBoolean(key, (Boolean) value);
                break;
            case "Float":
                editor.putFloat(key, (Float) value);
                break;
            case "Long":
                editor.putLong(key, (Long) value);
                break;
            case "String":
                editor.putString(key, (String) value);
                break;
        }
        editor.apply();
    }

    /**
     * 读取键的值，若无则返回默认值
     *
     * @param key 键
     * @param defValue 默认值
     * @return
     */
    @NonNull
    public <T> T get(String key, T defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String type = defValue.getClass().getSimpleName();
        switch (type) {
            case "Integer":
                return (T) (Integer) sharedPreferences.getInt(key, (Integer) defValue);
            case "Boolean":
                return (T) (Boolean) sharedPreferences.getBoolean(key, (Boolean) defValue);
            case "Float":
                return (T) (Float) sharedPreferences.getFloat(key, (Float) defValue);
            case "Long":
                return (T) (Long) sharedPreferences.getLong(key, (Long) defValue);
            case "String":
                return (T) sharedPreferences.getString(key, (String) defValue);
        }
        return defValue;
    }

    /** 清除所有缓存数据 */
    public void removeSharedPreferences() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
}

