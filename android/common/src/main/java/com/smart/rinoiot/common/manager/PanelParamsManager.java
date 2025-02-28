package com.smart.rinoiot.common.manager;

import android.os.Bundle;

import com.google.gson.Gson;
import com.smart.rinoiot.common.bean.DeviceInfoBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author tw
 * @time 2022/12/14 18:11
 * @description 面板传参统一处理
 */
public class PanelParamsManager {
    private static PanelParamsManager instance;

    public static PanelParamsManager getInstance() {
        if (instance == null) {
            instance = new PanelParamsManager();
        }
        return instance;
    }

    /**
     * 除定义外的字段，别的数据都通过透传
     */
    public void panelParamsRawData(Bundle bundle, DeviceInfoBean deviceInfoBean, String rnDirName) {
        if (bundle == null) return;
        bundle.putString("protocolType", deviceInfoBean.getProtocolType());
        bundle.putBoolean("isShare", false);//当前设备是否为分享过来的设备
        bundle.putBoolean("isAppOnline", false);//当前APP是否在线
        bundle.putString("firmwareVersion", deviceInfoBean.getFirmwareVersion());//固件版本号
        bundle.putInt("connectType", -1);//0=云端1=局域网2=蓝牙
        bundle.putInt("connectStatus", -1);//0=连接中1=未连接2=已连接
        bundle.putString("panelId", rnDirName.replaceAll(deviceInfoBean.getProductId(), ""));
        List<String> list = getFilterData();
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(deviceInfoBean));
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String key = it.next();
                if (!list.contains(key)) {
                    if (jsonObject.get(key) instanceof Integer) {
                        bundle.putInt(key, (int) jsonObject.get(key));
                    } else if (jsonObject.get(key) instanceof Double) {
                        bundle.putDouble(key, (Double) jsonObject.get(key));
                    } else if (jsonObject.get(key) instanceof Float) {
                        bundle.putFloat(key, (Float) jsonObject.get(key));
                    } else if (jsonObject.get(key) instanceof Boolean) {
                        bundle.putBoolean(key, (Boolean) jsonObject.get(key));
                    } else if (jsonObject.get(key) instanceof String) {
                        bundle.putString(key, String.valueOf(jsonObject.get(key)));
                    } else {
                        bundle.putString(key, new Gson().toJson(jsonObject.get(key)));
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取自定义字段和单独传给面板的字段集合
     */
    private List<String> getFilterData() {
        List<String> list = new ArrayList<>();
        list.add("id");
        list.add("name");
        list.add("productId");
        list.add("isGroup");
//        list.add("imageUrl");
        list.add("select");
        list.add("userId");
        list.add("groupId");
        list.add("groupOffLineFlag");
        list.add("isCustomGroup");
        list.add("dpInfoVOList");
        list.add("onlineStatus");
        list.add("protocolType");
        list.add("firmwareVersion");
        return list;
    }
}
