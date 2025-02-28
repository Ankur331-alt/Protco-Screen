package com.smart.rinoiot.common.utils;

import android.text.TextUtils;

import com.smart.rinoiot.common.bean.DeviceDpBean;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeviceControlUtils {

    /** 为物模型设置默认值 */
    public static List<DeviceDpBean> setDefaultVale(List<DeviceDpBean> targetArray) {
        if (targetArray == null) targetArray = new ArrayList<>();
        for (DeviceDpBean item: targetArray) {
            if (item.getValue() != null) continue;
            try {
                item.setValue("");
                JSONObject jsonObject = new JSONObject(item.getDpJson());
                String type = jsonObject.getJSONObject("dataType").getString("type");
                if (!TextUtils.isEmpty(type)) {
                    JSONObject specs = jsonObject.getJSONObject("dataType").getJSONObject("specs");
                    if ("int".equals(type)) {
                        item.setValue(Integer.parseInt(specs.getString(specs.keys().next())));
                    } else if ("bool".equals(type)) {
                        item.setValue(Integer.parseInt(specs.keys().next()) == 1);
                    } else if ("enum".equals(type)) {
                        item.setValue(specs.getString(specs.keys().next()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return targetArray;
    }
}
