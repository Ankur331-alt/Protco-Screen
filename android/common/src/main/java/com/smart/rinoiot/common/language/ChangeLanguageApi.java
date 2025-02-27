package com.smart.rinoiot.common.language;

import android.content.Context;
import android.content.res.Resources;

/**
 * 修改语言接口
 */
public interface ChangeLanguageApi {
    /**
     * 是否支持手动修改语言
     *
     * @return
     */
    boolean isSupportHand();

    /**
     * 修改语言
     *
     * @param context
     */
    void changeLanguage(Context context);

    /**
     * 获取资源文件
     *
     * @return
     */
    Resources getResources(Resources res);
}
