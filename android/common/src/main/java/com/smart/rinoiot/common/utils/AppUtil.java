package com.smart.rinoiot.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Browser;
import android.provider.Settings;
import android.text.TextUtils;

import com.amap.api.location.AMapLocationClientOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.R;
import com.smart.rinoiot.common.base.BaseApplication;
import com.smart.rinoiot.common.bean.CountryBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * app工具类
 */
public class AppUtil {
    @SuppressLint("StaticFieldLeak")
    private static AppUtil mInstance;
    private Context context;

    public void init(Context context) {
        this.context = context;
    }

    public static AppUtil getInstance() {
        if (mInstance == null) {
            synchronized (SharedPreferenceUtil.class) {
                if (mInstance == null) {
                    mInstance = new AppUtil();
                }
            }
        }
        return mInstance;
    }

    private static final Map<String, String> countryCache = new HashMap<String, String>() {{
        put("zh-CN", "China");
        put("en_US", "United States of America");
    }};

    /**
     * 获取国家所属区域
     */
    public static String getRegion(Context context) {
        String countryZipCode = "";
        String region = "AZ";
        Locale locale = context.getResources().getConfiguration().locale;
        countryZipCode = locale.getCountry();
        List<CountryBean> countryList = getCountryList(context);
        for (CountryBean countryBean : countryList) {
            if (countryBean.getRegion() != null && countryBean.getCountryCode().equals(countryZipCode)) {
                region = countryBean.getRegion();
                break;
            }
        }
        return TextUtils.isEmpty(region) ? "AZ" : region;
    }

    /**
     * 获取国家码
     */
    public static String getCountryCode(Context context) {
        String countryZipCode = "";
        String countryCode = "1";
        Locale locale = context.getResources().getConfiguration().locale;
        countryZipCode = locale.getLanguage();
        List<CountryBean> countryList = getCountryList(context);
        for (CountryBean countryBean : countryList) {
            if (countryBean.getRegion() != null && countryBean.getCountryCode().equals(countryZipCode)) {
                countryCode = countryBean.getPhoneCode();
                break;
            }
        }
        return countryCode;
    }

    /**
     * 获取当前语言 简体中文和繁体中文已处理
     */
    public String getLanguageData() {
        String str = AppUtil.getLanguage(context);
        if (TextUtils.isEmpty(str)) {
            str = "zh_CN";
        } else {
            if (str.startsWith("ja")) {
                str = "ja_JP";
            } else if (str.startsWith("ko")) {
                str = "ko_KR";
            } else if (str.startsWith("it")) {
                str = "it_IT";
            } else if (str.startsWith("es")) {
                str = "es_AR";
            } else if (str.startsWith("en")) {
                str = "en_US";
            } else if (str.startsWith("fr")) {
                str = "fr_FR";
            } else if (str.startsWith("de")) {
                str = "de_DE";
            }
        }
        return str;
    }

    /**
     * 获取国家名称
     */
    public static String getCountryName(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String country = countryCache.get(locale.toString());
        return country == null ? context.getString(R.string.rino_user_country_the_uniteds_states) : country;
    }

    /**
     * 获取当前手机时区，如“Asia/Shanghai”
     */
    public static String getSystemTimeZone() {
        TimeZone timeZone = TimeZone.getDefault();
        return timeZone.getID();
    }

    /**
     * 获取当前手机语言
     */
    public static String getLanguage(Context context) {
        if (context == null) return "";
        Locale locale = context.getResources().getConfiguration().locale;
        String country = locale.getLanguage();
        //简体中文和繁体中文处理
        if (country.startsWith("zh")) {
            if (locale.toLanguageTag().toLowerCase().contains("hant")) {
                country = "zh_TW";
            } else {
                country = "zh_CN";
            }
        }
        return country;
    }

    /**
     * 获取当前手机语言
     */
    public static String getSystemLanguage(Context context) {
        if (context == null) return "";
        Locale locale = context.getResources().getConfiguration().locale;
        return locale.getLanguage();
    }

    /**
     * 获取本地的国家列表
     */
    public static List<CountryBean> getCountryList(Context context) {
        Type listType = new TypeToken<LinkedList<CountryBean>>() {
        }.getType();
        String json = getJson("country.json", context);
        return new Gson().fromJson(json, listType);
    }

    /**
     * 根据asset中的文件名，获取json内容
     */
    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 获取应用程序名称
     */
    public static synchronized String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据国家简写，对应匹配英文国家名
     */
    public static String getCountyParams(Context context) {
        String country = "";
        String strCountry = SharedPreferenceUtil.getInstance().get(Constant.COUNTRY_REGION, "");
        if (TextUtils.isEmpty(strCountry)) {
            strCountry = "US";
        }
        List<CountryBean> list = getCountryList(context);
        if (list == null || list.isEmpty()) return strCountry;
        for (CountryBean item : list) {
            if (TextUtils.equals(strCountry, item.getCountryCode())) {
                country = item.getCountryName();
                break;
            }
        }
        return country;
    }

    /**
     * 检测是否为邮箱
     */
    public static boolean isEmail(String text) {
        String check = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(text);
        return matcher.matches();
    }

//    /** 检测是否为手机号 */
//    public static boolean isPhoneNumber(String phone) {
//        String check = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
//        Pattern pattern = Pattern.compile(check);
//        Matcher matcher = pattern.matcher(phone);
//        return matcher.matches();
//    }

    /**
     * 检测是否为手机号
     */
    public static boolean isPhoneNumber(String phoneNumber) {
        boolean isValid = false;
        /*
         * 可接受的电话格式有：
         */
        String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
        /*
         * 可接受的电话格式有：
         */
        String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(phoneNumber);

        Pattern pattern2 = Pattern.compile(expression2);
        Matcher matcher2 = pattern2.matcher(phoneNumber);
        if (matcher.matches() || matcher2.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * 检测是否同时包含数字和字母
     */
    public static boolean isContainsNumberAndLetter(String text) {
        String check = ".*[a-zA-Z].*[0-9]|.*[0-9].*[a-zA-Z]";
        Pattern pattern = Pattern.compile(check);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    /**
     * 截取byte数组
     *
     * @param src begin(起始位置) count(长度)
     * @return byte[]
     */
    public static byte[] subBytes(byte[] src, int begin, int count) {
        try {
            byte[] bs = new byte[count];
            if (begin + count - begin >= 0)
                System.arraycopy(src, begin, bs, 0, begin + count - begin);
            return bs;
        } catch (Exception e) {
            e.getMessage();
            LgUtils.w("onScanResult:  解析11：subBytes 异常  " + e.getMessage());
        }
        return null;
    }

    /**
     * byte数组转String, 尾部去0
     */
    public static String byteArrayToString(byte[] buffer) {
        try {
            int length = buffer.length;
            for (int i = 0; i < buffer.length; ++i) {
                if (buffer[i] == 0) {
                    length = i;
                    break;
                }
            }
            return new String(buffer, 0, length, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            LgUtils.w("onScanResult:  解析11：byteArrayToString 异常  " + e.getMessage());
            return "";
        }
    }

    /**
     * 需要过滤网关校验的接口集合
     */
    public static boolean authSkipUrlProvider(String strUrl) {
        if (strUrl.contains("business-app/v1/country/list")
                || strUrl.contains("business-app/v1/country/getTimezoneList")
                || strUrl.contains("business-app/v1/country/getInfo")
                || strUrl.contains("business-app/v1/city/list")
                || strUrl.contains("business-app/v1/verifyCode/send")
                || strUrl.contains("business-app/v1/verifyCode/checkVerifyCode")
                || strUrl.contains("business-app/v1/user/registry")
                || strUrl.contains("auth/oauth/token")
                || strUrl.contains("business-app/v1/user/resetPassword")
                || strUrl.contains("business-app/v1/user/profile")) {
            return true;
        }
        return false;
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

    /**
     * 切换高德地图的api语言
     */
    public static AMapLocationClientOption.GeoLanguage changeMapLanguage(Context context) {
        return getLanguage(context).contains("zh") ? AMapLocationClientOption.GeoLanguage.ZH : AMapLocationClientOption.GeoLanguage.EN;
    }

    /**
     * 跳转浏览器
     */
    public static void setBrowser(Context context, String url) {
        if (!url.contains("http://") && !url.contains("https://")) {
            url = "http://" + url;
        }
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        intent.putExtra(Constant.IS_BROWSER, true);
        context.startActivity(intent);
    }

    /**
     * Returns the device identifier of the android device
     *
     * @return the device identifier of the android device
     */
    public String getAndroidDeviceId() {
        if (context != null) {
            return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return Build.ID;
    }
}
