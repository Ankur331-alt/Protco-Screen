package com.smart.rinoiot.common.manager;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.bean.CountryBean;
import com.smart.rinoiot.common.utils.AESUtil;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.common.utils.FileUtils;
import com.smart.rinoiot.common.utils.ServiceProductUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author tw
 * @time 2022/11/21 13:47
 * @description 根据国家获取接口域名+mqtt域名及端口
 */
public class CountryAndDomainManager {
    private static CountryAndDomainManager instance;

    public static CountryAndDomainManager getInstance() {
        if (instance == null) {
            instance = new CountryAndDomainManager();
        }
        return instance;
    }

    /**
     * 根据国家areaId 获取对应的domain+mqtt端口及ip地址
     */
    public void getAreaIdByDomain(Context context, String areaId) {
        String json = CacheDataManager.getInstance().getCountryDomainJson();
        if (TextUtils.isEmpty(json)) {//接口返回为空，取本地json数据
            json = FileUtils.readDataCenter(context, Constant.DATA_CENTER_FILE_NAME);
            if (TextUtils.isEmpty(json)) {
                json = getJson("cityAndDomain.json", context);
            }
        }
        try {
            JSONObject dataCenter = new JSONObject(json);
            if (dataCenter.has(areaId)) {//存在这个key
                JSONObject jsonObject = dataCenter.getJSONObject(areaId);
                String domain = "", mqttUrl = "";
                if (jsonObject.has("domain")) {
                    domain = AESUtil.decryptECB(jsonObject.getString("domain"), AESUtil.decryptKey);
                }
                if (jsonObject.has("mqttUrl")) {
                    mqttUrl = AESUtil.decryptECB(jsonObject.getString("mqttUrl"), AESUtil.decryptKey);
                }
                ServiceProductUtils.getInstance().setServiceProductData(domain, mqttUrl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取本地的国家列表
     */
    public void getLocalCountryList(Context context) {
        String countryAssetsName;
        if (AppUtil.getLanguage(context).contains("zh")) {
            countryAssetsName = "country_zh.json";
        } else {
            countryAssetsName = "country_en.json";
        }
        String json = getJson(countryAssetsName, context);
        List<CountryBean> data = new Gson().fromJson(json, new TypeToken<List<CountryBean>>() {
        }.getType());
        if (data != null && data.size() > 0) {
            CacheDataManager.getInstance().saveCountryList(data);
            for (CountryBean item : data) {
                if (AppUtil.getLanguage(context).contains("zh")) {
                    if (TextUtils.equals(item.getCountryName(), "中国")) {
                        CacheDataManager.getInstance().saveCurrentCountry(item);
                        break;
                    }
                } else {
                    if (TextUtils.equals(item.getCountryName(), "United States of America")) {
                        CacheDataManager.getInstance().saveCurrentCountry(item);
                        break;
                    }
                }
            }
        }
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
}
