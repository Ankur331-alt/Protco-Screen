package com.smart.device.manager;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smart.device.bean.StockDpInfoBean;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.mqtt2.payload.EventPayload;
import com.smart.rinoiot.common.rn.GroupDeviceManager;
import com.smart.rinoiot.common.rn.SingleDeviceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author tw
 * @time 2022/12/14 14:24
 * @description 常用功能管理类
 */
public class StockDpUtils {
    private static StockDpUtils instance;

    private static final String TYPE_KEY = "type";
    private static final String ENUM_KEY = "enum";
    private static final String SPECS_KEY = "specs";
    private static final String ENUMS_KEY = "enums";
    private static final String VALUES_KEY = "values";
    private static final String DATA_TYPE_KEY = "dataType";

    public static StockDpUtils getInstance() {
        if (instance == null) {
            instance = new StockDpUtils();
        }
        return instance;
    }

    /**
     * 处理常用开关中dpjson数据
     */
    public void dealStockDpJsonData(
            DeviceDpBean deviceDpBean, StockDpInfoBean stockDpInfoBean
    ) throws JSONException {
        JSONObject dpJson = new JSONObject(deviceDpBean.getDpJson());
        if (dpJson != null && dpJson.has(DATA_TYPE_KEY)) {
            JSONObject dataType = dpJson.getJSONObject(DATA_TYPE_KEY);
            if (dataType != null) {
                if (dataType.has(TYPE_KEY)) {
                    //获取数据类型
                    stockDpInfoBean.setType(dataType.getString(TYPE_KEY));
                }
                if (dataType.has(SPECS_KEY)) {
                    //获取数据结构
                    JSONObject specs = dataType.getJSONObject(SPECS_KEY);
                    if (specs != null) {
                        Map<String, Object> mapDpJson = new HashMap<>();
                        if (TextUtils.equals(stockDpInfoBean.getType(), ENUM_KEY)) {
                            if (specs.has(ENUMS_KEY)) {
                                JSONArray enums = specs.getJSONArray(ENUMS_KEY);
                                if (enums != null && enums.length() > 0) {
                                    for (int i = 0; i < enums.length(); i++) {
                                        mapDpJson.put(enums.get(i).toString(), enums.get(i));
                                    }
                                }
                            }
                        } else {
                            for (Iterator<String> it = specs.keys(); it.hasNext(); ) {
                                String key = it.next();
                                mapDpJson.put(key, specs.get(key));
                            }

                        }
                        stockDpInfoBean.setMapDpJson(mapDpJson);
                    }
                }
            }
        }
    }

    /**
     * 处理常用开关中dpjson数据对应的图片
     */
    public void dealStockDpImageData(
            DeviceDpBean deviceDpBean, StockDpInfoBean stockDpInfoBean
    ) throws JSONException {
        JSONObject imageJson = new JSONObject(deviceDpBean.getImageJson());
        if (imageJson != null && imageJson.has(VALUES_KEY)) {
            JSONObject values = imageJson.getJSONObject(VALUES_KEY);
            if (values != null) {
                Map<String, Object> mapDpImage = new HashMap<>();
                for (Iterator<String> it = values.keys(); it.hasNext(); ) {
                    String key = it.next();
                    mapDpImage.put(key, values.get(key));
                }
                stockDpInfoBean.setMapDpImage(mapDpImage);
            }
        }
    }

    /**
     * 常用功能下发数据
     */
    public void commonPublishData(StockDpInfoBean stockDpInfoBean, Object value) {
        try {
            List<EventPayload.Device> devices = new ArrayList<>();
            EventPayload.Device device = new EventPayload.Device();
            device.setDeviceId(stockDpInfoBean.getDevId());
            JsonObject properties = jsonFormatData(stockDpInfoBean.getKey(), value);
            device.setProperties(properties);
            devices.add(device);
            if (devices.size() == 1) {
                //单设备
                DeviceInfoBean deviceInfoBean = CacheDataManager.getInstance().getDeviceInfo(
                        stockDpInfoBean.getDevId()
                );
                if (deviceInfoBean != null) {
                    if (deviceInfoBean.isCustomGroup()) {
                        //特殊情况下，群组设备中只有一个设备时，
                        GroupDeviceManager.getInstance().dataInteraction(devices, null);
                    } else {//单设备
                        SingleDeviceManager.getInstance().dataInteraction(
                                deviceInfoBean, devices, null
                        );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据类型转化数据
     */
    public JsonObject jsonFormatData(String key, Object value) throws Exception {
        JsonObject jsonObject = new JsonObject();
        if (value instanceof Number) {
            jsonObject.addProperty(key, (Number) value);
        } else if (value instanceof Boolean) {
            jsonObject.addProperty(key, (Boolean) value);
        } else if (value instanceof String) {
            jsonObject.addProperty(key, String.valueOf(value));
        } else {
            jsonObject.addProperty(key, new Gson().toJson(value));
        }
        return jsonObject;
    }

    public boolean isCommonFunc(DeviceInfoBean deviceInfoBean){
        return deviceInfoBean.getStockDpInfoVOList() == null ||
                deviceInfoBean.getStockDpInfoVOList().isEmpty();
    }
}
