package com.smart.rinoiot.common.mqtt2.payload;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smart.rinoiot.common.utils.LgUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @author tw
 * @time 2022/10/26 14:44
 * @description 蓝牙设备上报云端数据
 */
public class BleCloudManager {

    public static final String TAG = BleCloudManager.class.getSimpleName();

    public static BleCloudManager instance;

    public static BleCloudManager getInstance() {
        if (instance == null) {
            instance = new BleCloudManager();
        }
        return instance;
    }

    /**
     * 蓝牙设备 上报云端数据
     */
    public byte[] getCloudBleData(Object bleObject) {
        BleEventPayload mEventPayload = new BleEventPayload();
        mEventPayload.setData(bleObject);
        mEventPayload.setTs(System.currentTimeMillis() / 1000);
        mEventPayload.setMsgId(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 18));
        LgUtils.w(TAG + "   getCloudBleData  new Gson().toJson(mEventPayload)=" + new Gson().toJson(mEventPayload));
        return new Gson().toJson(mEventPayload).getBytes();
    }

    /**
     * 蓝牙设备 上报云端数据
     * {"msgId":"e57ef82959834a4","ts":1669361412,"data":{"work_mode":{"ts":1669361412,"value":"white"}},"type":"thing.property.report"}
     */
    public byte[] getCloudBleData(List<EventPayload.Device> devices) {
        JsonObject jsonObject = new JsonObject();
        for (EventPayload.Device item : devices) {
            if (item.getProperties() != null) {
                try {
                    JSONObject properties = new JSONObject(item.getProperties().toString());
                    Iterator<String> keys = properties.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        jsonFormatData(jsonObject, key, properties.get(key));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        BleEventPayload mEventPayload = new BleEventPayload();
        mEventPayload.setData(jsonObject);
        mEventPayload.setTs(System.currentTimeMillis() / 1000);
        mEventPayload.setAppReport(1);
        mEventPayload.setMsgId(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 18));
        LgUtils.w(TAG + "   getCloudBleData  new Gson().toJson(mEventPayload)=" + new Gson().toJson(mEventPayload));
        return new Gson().toJson(mEventPayload).getBytes();
    }

    /**
     * 根据类型转化数据
     */
    public void jsonFormatData(JsonObject jsonObject, String key, Object value) {
        if (value instanceof Number) {
            jsonObject.addProperty(key, (Number) value);
        } else if (value instanceof Boolean) {
            jsonObject.addProperty(key, (Boolean) value);
        } else if (value instanceof String) {
            jsonObject.addProperty(key, String.valueOf(value));
        } else {
            jsonObject.addProperty(key, new Gson().toJson(value));
        }
    }
}
