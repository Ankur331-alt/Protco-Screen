package com.smart.rinoiot.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.smart.rinoiot.common.R;


public class TextUtil {

    /**
     * @param houseFlag  true：新房tab列表
     * @param resourceId 资源文件id
     * @param textView   控件
     * @param mContext   上下文
     *                   设置textview 右边图片
     */
    public static void setTvDrawableRight(Context mContext, TextView textView, int resourceId, boolean houseFlag) {
        Drawable drawable = mContext.getResources().getDrawable(resourceId);
        if (houseFlag) textView.setCompoundDrawablePadding(DpUtils.dip2px(8));
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    //textview设置左边图片
    @SuppressLint("UseCompatLoadingForDrawables")
    public static void setTvDrawableLeft(Context mContext, int resourceId, TextView textView) {
        if (resourceId == 0) {
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            return;
        }
        Drawable drawable = mContext.getResources().getDrawable(resourceId);
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    //textview设置左边图片
    @SuppressLint("UseCompatLoadingForDrawables")
    public static void setTvDrawableLeft(Drawable drawable, TextView textView) {
        if (drawable == null) {
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            return;
        }
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }


    /**
     * @param resourceId 资源文件id
     * @param textView   控件
     * @param mContext   上下文
     * @deprecated 设置textview 左边图片
     */
    public static void setTvDrawableLeft(Context mContext, TextView textView, int resourceId, int drawablePadding) {
        Drawable drawable = mContext.getResources().getDrawable(resourceId);
        textView.setCompoundDrawablePadding(DpUtils.dip2px(drawablePadding));
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    /**
     * @param resourceId 资源文件id
     * @param textView   控件
     * @param mContext   上下文
     * @param like       是否关注 0：取消关注（故事列表），1：关注 ；2：故事详情
     * @deprecated 设置textview 左边图片
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public static void setTvDrawableLeft(Context mContext, int resourceId, TextView textView, int like) {
        if (resourceId == 0) {
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            return;
        }
        Drawable drawable = mContext.getResources().getDrawable(resourceId);
        if (like == 1) {
            drawable.setTint(mContext.getResources().getColor(R.color.main_theme_color));
        } else {
            if (like == 0) {
                drawable.setTint(mContext.getResources().getColor(R.color.black_10));
            } else {
                drawable.setTint(mContext.getResources().getColor(R.color.white));
            }
        }
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    /**
     * @param resourceId 资源文件id
     * @param textView   控件
     * @param mContext   上下文
     * @deprecated 设置textview 左边图片
     */
    //textview设置左边图片
    @SuppressLint("UseCompatLoadingForDrawables")
    public static void setTvDrawableRight(Context mContext, int resourceId, TextView textView) {
        if (resourceId == 0) {
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            return;
        }
        Drawable drawable = mContext.getResources().getDrawable(resourceId);
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    /**
     * @param resourceId 资源文件id
     * @param textView   控件
     * @param mContext   上下文
     * @param like       是否关注 0：取消关注（故事列表），1：关注 ；2：故事详情
     * @deprecated 设置textview 左边图片
     */
    public static void setTvDrawableTop(Context mContext, int resourceId, TextView textView, int like) {
        if (resourceId == 0) {
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            return;
        }
        Drawable drawable = mContext.getResources().getDrawable(resourceId);
        if (like == 1) {
            drawable.setTint(mContext.getResources().getColor(R.color.main_theme_color));
        } else {
            if (like == 0) {
                drawable.setTint(mContext.getResources().getColor(R.color.black_10));
            } else {
                drawable.setTint(mContext.getResources().getColor(R.color.white));
            }
        }
        textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
    }

    /**
     * @param drawable drawable
     * @param textView   控件
     * @param mContext   上下文
     * @deprecated 设置textview 左边图片
     */
    public static void setTvDrawableTop(Context mContext, Drawable drawable, TextView textView) {
        if (drawable == null) {
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            return;
        }
        textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
    }
}
