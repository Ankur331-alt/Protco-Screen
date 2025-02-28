package com.smart.rinoiot.common.manager;

import android.text.TextUtils;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.google.gson.Gson;
import com.smart.rinoiot.common.BleConfigConstant;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.ble.BleScanConnectManager;
import com.smart.rinoiot.common.mqtt2.Manager.MqttConvertManager;
import com.smart.rinoiot.common.rn.BundleJSONConverter;
import com.smart.rinoiot.common.rn.RNConstant;
import com.smart.rinoiot.common.utils.JSONDataFormatUtils;
import com.smart.rinoiot.common.utils.LgUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * @author tw
 * @time 2022/11/15 20:44
 * @description 蓝牙设备配网 上报和下发
 */
public class BleDeviceReportSendManager {
    private static final String TAG = BleDeviceReportSendManager.class.getSimpleName();
    private static BleDeviceReportSendManager instance;

    public static BleDeviceReportSendManager getInstance() {
        if (instance == null) {
            instance = new BleDeviceReportSendManager();
        }
        return instance;
    }

    /**
     * 蓝牙设备通过下面数据格式跟设备交互
     * 面板走mqtt 1.0 协议时，
     * 蓝牙设备返回数据
     * {"properties":{"switch":false},"deviceId":"1633286617540206592"}]
     * mqtt 2.0
     * {
     * "deviceId":"xxx",//设备ID
     * "data":{
     * "color":"red",  //颜色  红色
     * "brightness":80 //亮度  80
     * ......
     * }
     * }
     * 蓝牙
     * <p>
     * "data" : {
     * "switch" :false,
     * "dynamic" : 2013
     * },
     * "type" : "thing.property.get.response"
     * }
     */
    public void dataInteraction(Object object, Promise promise) {
        try {
            String deviceId = "";
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObjectData = new JSONObject();
            if (object instanceof ReadableArray) {
                JSONArray jsonArray = BundleJSONConverter.reactToJSON((ReadableArray) object);
                if (jsonArray != null && jsonArray.length() > 0) {
                    JSONObject jsonDpData = jsonArray.getJSONObject(0);
                    if (jsonDpData != null && jsonDpData.has("deviceId")) {
                        deviceId = jsonDpData.getString("deviceId");
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        panelControlBleDevice(jsonObjectData, jsonArray.getJSONObject(i), 1);
                    }
                }
            } else if (object instanceof ReadableMap) {
                panelControlBleDevice(jsonObjectData, BundleJSONConverter.reactToJSON((ReadableMap) object), 2);
            }
            if (!TextUtils.isEmpty(deviceId)) {
                DeviceInfoBean deviceInfo = CacheDataManager.getInstance().getDeviceInfo(deviceId);
                if (deviceInfo != null && deviceInfo.getOnlineStatus() == 0
                        && DeviceProtocolManager.getInstance().supportBleProtocol(deviceInfo)) {//在线状态（0离线，1在线）
                    jsonObject.put("type", BleConfigConstant.THING_PROPERTY_SET);
                    jsonObject.put("data", jsonObjectData);
                    BleScanConnectManager.getInstance().sendMessage(deviceInfo.getUuid(),
                            new Gson().toJson(jsonObject.toString()), data1 -> {
                                if (promise != null) promise.resolve(true);
                            }, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LgUtils.w(TAG + "   dataInteraction  Exception E=" + e.getMessage());
            if (promise != null) promise.resolve(false);
        }
    }

    /**
     * 面板通过app，跟设备直接走蓝牙协议
     *
     * @param dataType 1:mqtt 1.0   2:mqtt  2.0
     */
    public void panelControlBleDevice(JSONObject tempJson, JSONObject properties, int dataType) throws Exception {
        JSONObject jsonObjectData = JSONDataFormatUtils.specialJsonFormatData(dataType, properties);
        if (jsonObjectData != null) {
            Iterator<String> keys = jsonObjectData.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObjectData.get(key);
                tempJson.put(key, value);
            }
        }
    }

    /**
     * 蓝牙设备操作成功，将数据发送给面板
     * {"msgId":"e57ef82959834a4","ts":1669361412,"data":{"work_mode":{"ts":1669361412,"value":"white"}},
     * "type":"thing.property.report"}
     * <p>
     * mqtt 2.0数据结构
     * [{
     * "deviceId":"设备ID",
     * "properties":{
     * "color":"red",  //颜色  红色
     * "brightness":80 //亮度  80
     * }}]
     */
    public void bleDeviceOperateSuccessNotify(String result, String uuid) throws Exception {
        JSONObject jsonObject = new JSONObject(result);
        DeviceInfoBean deviceInfoBean = CacheDataManager.getInstance().getDeviceInfoByUuid(uuid);
        if (jsonObject == null || deviceInfoBean == null) {
            LgUtils.i(TAG + " onCharacteristicChanged   bluetooth --> 找不到uuid对应的设备" + "  uuid=" + uuid);
            return;
        }
        if (jsonObject.has("type") && TextUtils.equals(jsonObject.getString("type"), "thing.property.report")) {
            bleDeviceOperateSuccessSendPublish(deviceInfoBean.getId(), jsonObject);//蓝牙设备属性上报
        }
        bleDeviceOperateSuccessNotifyPanel(1, deviceInfoBean.getId(), jsonObject);//mqtt 1.0面板
        bleDeviceOperateSuccessNotifyPanel(2, deviceInfoBean.getId(), jsonObject);//mqtt 2.0面板

    }

    /**
     * 蓝牙设备操作成功，发送数据给面板，mqtt 1.0  +mqtt 2.0 面板
     *
     * @param mqttType 1:mqtt 1.0   2:mqtt 2.0
     */
    public void bleDeviceOperateSuccessNotifyPanel(int mqttType, String deviceId, JSONObject jsonObject) throws Exception {
        if (jsonObject != null && jsonObject.has("data")) {
            JSONObject data = jsonObject.getJSONObject("data");
            String notifyType = mqttType == 1 ? RNConstant.DEVICE_DATA_POINT_UPDATE : RNConstant.DEVICE_DATA_POINT_UPDATE_V2;
            JSONArray jsonArray = new JSONArray();
            JSONObject json = new JSONObject();
            json.put("deviceId", deviceId);
            if (mqttType == 1) {
                json.put("properties", new JSONArray().put(data));
            } else {
                JSONObject arrayObjectItem = new JSONObject();
                JSONObject propertiesItem = new JSONObject();
                Iterator<String> keys = data.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject jsonObject2 = data.getJSONObject(key);
                    if (jsonObject2.has("value")) {
                        propertiesItem.put(key, jsonObject2.get("value"));
                    }
                }
                json.put("properties", propertiesItem);
            }
            jsonArray.put(json);
            WritableArray writableArray = BundleJSONConverter.jsonToReact(jsonArray);
            LgUtils.i(TAG + " onCharacteristicChanged   bluetooth --> 发数据给rn --> " + jsonArray + "  deviceId=" + deviceId);
            EventManager.getInstance().sendData(notifyType, writableArray);
        }
    }

    /**
     * 蓝牙设备操作成功，下发mqtt数据，上报一下
     * {"msgId":"e57ef82959834a4","ts":1669361412,"data":{"work_mode":{"ts":1669361412,"value":"white"}},
     * "type":"thing.property.report"}
     * <p>
     * 转化为
     * {
     * "deviceId":"xxx",//设备ID
     * "data":{
     * "color":"red",  //颜色  红色
     * "brightness":80 //亮度  80
     * ......
     * }
     * }
     */
    public void bleDeviceOperateSuccessSendPublish(String deviceId, JSONObject jsonObject) throws Exception {
        JSONObject data = new JSONObject();
        if (jsonObject != null && jsonObject.has("data")) {
            JSONObject data1 = jsonObject.getJSONObject("data");
            Iterator<String> keys = data1.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject jsonObject2 = data1.getJSONObject(key);
                if (jsonObject2.has("value")) {
                    data.put(key, jsonObject2.get("value"));
                }
            }
        }
        MqttConvertManager.getInstance().propsReportSendPublish(data, deviceId);
    }

    /**
     * Matter 操作成功后，发送数据通知面板 兼容mqtt 1.0  mqtt 2.0
     * mqtt 1.0
     * [{"deviceId":"1623580479275642880","properties":{"switch":false}}]
     * * [{"deviceId":"1643915916117426176","properties":[{"switch":{"ts":1.680868979E9,"value":true}}]}]
     * <p>
     * mqtt 2.0
     * <p>
     * {
     * "deviceId":"xxx",//设备ID
     * "data":{
     * "color":"red",  //颜色  红色
     * "brightness":80 //亮度  80
     * }}
     */
    public void matterOperateSuccessNotify(int mqttType, String devId, JSONObject tempData) throws Exception {
        matterDeviceOperateSuccessSendPublish(devId, tempData);//matter设备数据上报
        JSONArray data = new JSONArray();
//        for (EventPayload.Device item : devices) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceId", devId);
        JSONArray properties = new JSONArray();
        if (mqttType == 1) {//mqtt 1.0
            JSONObject propertiesItem = new JSONObject();
            if (tempData != null) {
                Iterator<String> keys = tempData.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = tempData.get(key);
                    if (mqttType == 1) {//mqtt 1.0
                        JSONObject propertiesItem2 = new JSONObject();
                        propertiesItem2.put("ts", System.currentTimeMillis() / 1000);
                        propertiesItem2.put("value", value);
                        propertiesItem.put(key, propertiesItem2);

                    } else if (mqttType == 2) {//mqtt 2.0
                        propertiesItem.put(key, value);
                    }
                    properties.put(propertiesItem);
                }
            }
            jsonObject.put("properties", properties);
        }
        data.put(jsonObject);
//        }
        WritableArray writableArray = BundleJSONConverter.jsonToReact(new JSONArray(data.toString()));
        LgUtils.w(TAG + "   sendDataPANEL   item.getDeviceId()=" + devId
                + "  writableArray=" + new Gson().toJson(writableArray.toArrayList()));
        String notifyType = mqttType == 1 ? RNConstant.DEVICE_DATA_POINT_UPDATE : RNConstant.DEVICE_DATA_POINT_UPDATE_V2;
        EventManager.getInstance().sendData(notifyType, writableArray);
    }

    /**
     * matter设备上报mqtt
     */
    public void matterDeviceOperateSuccessSendPublish(String deviceId, JSONObject jsonObject) throws Exception {
        JSONObject data = new JSONObject();
        Iterator<String> keys = jsonObject.keys();
        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
            String key = it.next();
            Object value = jsonObject.get(key);
            data.put(key, value);
        }
        MqttConvertManager.getInstance().propsReportSendPublish(data, deviceId);
    }
}
