package com.smart.rinoiot.common.language;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import com.smart.rinoiot.common.utils.SharedPreferenceUtil;

import java.util.Locale;

public class ChangeLanguageManager {
    private static ChangeLanguageManager instance;
    private ChangeLanguageApi changeLanguageApi;
    public final static String LANGUAGE = "language";

    public static ChangeLanguageManager getInstance() {
        if (instance == null) {
            instance = new ChangeLanguageManager();
        }
        return instance;
    }


    public void changeLanguage(Context context) {
        if (context==null) return;
        String sta = SharedPreferenceUtil.getInstance().get(LANGUAGE, "en");
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(sta);
        res.updateConfiguration(conf, dm);
    }

    public Resources getResources(Resources resources) {
        String sta =SharedPreferenceUtil.getInstance().get(LANGUAGE, "en");
        if (Build.VERSION.SDK_INT < 24) {
            resources.getConfiguration().setLocale(new Locale(sta));
        } else if (resources.getConfiguration().getLocales().size() > 0) {
            LocaleList list = new LocaleList(new Locale[]{new Locale(sta)});
            resources.getConfiguration().setLocales(list);
        }

        return resources;
    }


    public boolean isSupportHand() {
        return changeLanguageApi != null && changeLanguageApi.isSupportHand();
    }
}
