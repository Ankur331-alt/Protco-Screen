package com.smart.rinoiot.common.rn;

import android.content.Context;

import com.google.gson.Gson;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseApplication;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;

import java.util.Locale;

public class RNLanguageUtils {

    /**
     * 获取国家名称
     */
    public static String getLanguage(Context context) {
        if (context == null) return "zh-Hans";
        Locale locale = context.getResources().getConfiguration().locale;
        String country = locale.getLanguage();
        LgUtils.w("多屏多语言    locale=" + new Gson().toJson(locale) + "   country=" + country);
        //简体中文和繁体中文处理
        if (country.startsWith("zh")) {
            if (locale.toLanguageTag().toLowerCase().contains("hk")) {
//                country = "zh-Hant-HK";
                country = "zh-Hant";
            } else if (locale.toLanguageTag().toLowerCase().contains("tw")) {
                country = "zh-Hant";
            } else {
                country = "zh-Hans";
            }
        } else if (country.startsWith("es")) {
            if (locale.toLanguageTag().toLowerCase().contains("us")) {
                country = "es-419";
            } else {
                country = "es";
            }
        } else if (country.startsWith("in")) {
            country = "id";
        } else if (country.startsWith("pt")) {
            if (locale.toLanguageTag().toLowerCase().contains("br")) {
                country = "pt-BR";
            } else {
                country = "pt";
            }
        }
        return country;
    }

    /**
     * 获取app版本号
     */
    public static String getLocalVersion() {
        String localVersion;
        try {
            localVersion = BaseApplication.getApplication().getPackageManager().
                    getPackageInfo(BaseApplication.getApplication().getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            localVersion = SharedPreferenceUtil.getInstance().get(Constant.IGNORE_UPDATE, "");
        }
        return localVersion;
    }

}
