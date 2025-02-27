package com.smart.rinoiot.common.mqtt2.Manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.apkfuns.logutils.LogUtils;
import com.dsh.matter.model.scanner.DeviceSharePayload;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.api.CommonApiService;
import com.smart.rinoiot.common.bean.AppQrLogInMsgBean;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.GroupBean;
import com.smart.rinoiot.common.bean.GroupDeviceItemBean;
import com.smart.rinoiot.common.datastore.DataSourceManager;
import com.smart.rinoiot.common.device.RinoDataPoint;
import com.smart.rinoiot.common.event.DeviceEvent;
import com.smart.rinoiot.common.event.DeviceNetworkEvent;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.manager.EventManager;
import com.smart.rinoiot.common.manager.PanelCacheDeviceDataManager;
import com.smart.rinoiot.common.matter.MtrDeviceControlManager;
import com.smart.rinoiot.common.matter.MtrDeviceDataUtils;
import com.smart.rinoiot.common.matter.model.MtrDiscoverableNode;
import com.smart.rinoiot.common.matter.model.MtrDiscoverableNodesEvent;
import com.smart.rinoiot.common.matter.model.MtrDiscoverableNodesStatus;
import com.smart.rinoiot.common.mqtt2.DeviceProperties;
import com.smart.rinoiot.common.mqtt2.DevicePropertiesFuture;
import com.smart.rinoiot.common.mqtt2.MqttConstant;
import com.smart.rinoiot.common.mqtt2.MqttConstant.Property;
import com.smart.rinoiot.common.mqtt2.PropertiesChangeMsg;
import com.smart.rinoiot.common.mqtt2.payload.EventPayload;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.rn.BundleJSONConverter;
import com.smart.rinoiot.common.rn.RNConstant;
import com.smart.rinoiot.common.rn.comfun.CommonEventManager;
import com.smart.rinoiot.common.utils.AppUtil;
import com.smart.rinoiot.common.utils.JSONDataFormatUtils;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.SharedPreferenceUtil;
import com.smart.rinoiot.common.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author tw
 * @time 2023/4/10 19:36
 * @description mqtt 2.0 数据转化位1.0的结构
 */
public class MqttConvertManager {

    private static final String TAG = "MqttConvertManager";

    private static final String PAYLOAD_KEY = "payload";
    private static final String DEVICES_KEY = "devices";
    private static final String CLIENT_ID_KEY = "clientId";
    private static final String DEVICE_ID_KEY = "deviceId";

    private static MqttConvertManager instance;

    private static final List<String> MQTT_DATA_POINTS = Arrays.asList(
            RinoDataPoint.RINO_SWITCH_DP.getValue(),
            RinoDataPoint.RINO_ONLINE_DP.getValue(),
            RinoDataPoint.RINO_SWITCH_1_DP.getValue(),
            RinoDataPoint.RINO_SWITCH_2_DP.getValue(),
            RinoDataPoint.RINO_SWITCH_3_DP.getValue(),
            RinoDataPoint.RINO_SWITCH_4_DP.getValue(),
            RinoDataPoint.RINO_SWITCH_LED_DP.getValue(),
            RinoDataPoint.RINO_SPIN_SWITCH_DP.getValue(),
            RinoDataPoint.RINO_MTR_SWITCH_DP.getValue(),
            RinoDataPoint.RINO_COLOR_TEMP_DP.getValue(),
            RinoDataPoint.RINO_PAINT_COLOUR_DP.getValue(),
            RinoDataPoint.RINO_BRIGHTNESS_DP.getValue(),
            RinoDataPoint.RINO_BRIGHT_VALUE_DP.getValue(),
            RinoDataPoint.RINO_COLOR_DP.getValue(),
            RinoDataPoint.RINO_COLOUR_DATA_DP.getValue(),
            RinoDataPoint.RINO_PAINT_COLOUR_DP.getValue()
    );

    /**
     * Device properties futures
     */
    private final HashMap<String, DevicePropertiesFuture> mFutures = new HashMap<>();

    public static MqttConvertManager getInstance() {
        if (instance == null) {
            instance = new MqttConvertManager();
        }
        return instance;
    }

    // *******************************数据上报转化******************************************
    /**
     * mqtt业务数据处理
     *
     * @param mqttMsgData the mqtt data
     */
    public void mqttMsgDealData(String mqttMsgData) {
        if (!TextUtils.isEmpty(mqttMsgData)) {
            try {
                JSONObject jsonObject = new JSONObject(mqttMsgData);
                String code = "", mqttDataMsg = "";
                if (jsonObject.has(Property.CODE_KEY)) {
                    code = jsonObject.getString(Property.CODE_KEY);
                }
                if (jsonObject.has(Property.DATA_KEY)) {
                    mqttDataMsg = jsonObject.getJSONObject(Property.DATA_KEY).toString();
                }

                if (TextUtils.equals(code, MqttConstant.MQTT_OTA_PROGRESS)) {
                    //Ota升级
                    mqttOtaUpgradeNotify(mqttDataMsg);
                } else if (TextUtils.equals(code, MqttConstant.MQTT_PROPERTY_CHANGE)) {
                    // 设备属性变化通知
                    mqttDevicePropertiesChangeNotify(mqttDataMsg);
                } else if (TextUtils.equals(code, MqttConstant.MQTT_DEVICE_STATUS)) {
                    // 设备状态改变通知
                    mqttDeviceStatusChangeNotify(mqttDataMsg);
                } else if (TextUtils.equals(code, MqttConstant.MQTT_BIND_RESULT)) {
                    // 绑定结果通知
                    mqttBindDeviceResultNotify(mqttDataMsg);
                } else if(TextUtils.equals(code, MqttConstant.MQTT_MATTER_PAIR)) {
                    matterPairMessageHandler(mqttDataMsg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "mqttMsgDealData: Cause= " + e.getLocalizedMessage());
            }
        }
    }

    /**
     * Handles the matter find messages
     * @param mqttDataMsg the message data.
     */
    private void matterPairMessageHandler(String mqttDataMsg) {
        Log.d(TAG, "matterPairMessageHandler: " + mqttDataMsg);
        if(StringUtil.isBlank(mqttDataMsg)){
            return;
        }

        String androidDeviceId = AppUtil.getInstance().getAndroidDeviceId();
        try{
            JSONObject jsonObject = new JSONObject(mqttDataMsg);
            if(!jsonObject.has(CLIENT_ID_KEY)){
                return;
            }

            String clientId = jsonObject.getString(CLIENT_ID_KEY);
            if(StringUtil.isBlank(clientId)){
                return;
            }

            if(!clientId.contentEquals(androidDeviceId)){
                return;
            }

            List<MtrDiscoverableNode> discoverableNodes = new ArrayList<>();
            JSONArray devicesArray = jsonObject.getJSONArray(DEVICES_KEY);
            if(devicesArray.length() == 0){
                EventBus.getDefault().post(new MtrDiscoverableNodesEvent(
                        MtrDiscoverableNodesStatus.NOT_DISCOVERED
                ));
                return;
            }

            for (int i = 0; i < devicesArray.length(); i++) {
                JSONObject node = devicesArray.getJSONObject(i);
                MtrDiscoverableNode discoverableNode = toDiscoverableNode(node);
                if(null == discoverableNode){
                    continue;
                }
                discoverableNodes.add(discoverableNode);
            }

            // post devices
            EventBus.getDefault().post(new MtrDiscoverableNodesEvent(
                    MtrDiscoverableNodesStatus.DISCOVERED, discoverableNodes
            ));
        }catch (Exception exception){
            EventBus.getDefault().post(new MtrDiscoverableNodesEvent(
                    MtrDiscoverableNodesStatus.ERROR
            ));
        }
    }

    /**
     * Converts the JSON objects to discoverable nodes
     * @param node node
     * @return a MtrDiscoverableNode
     */
    private MtrDiscoverableNode toDiscoverableNode(JSONObject node) {
        if(null == node){
            return null;
        }

        try {
            String payload = node.getString(PAYLOAD_KEY);
            String deviceId = node.getString(DEVICE_ID_KEY);
            DeviceSharePayload sharePayload = new Gson().fromJson(payload, DeviceSharePayload.class);
            return new MtrDiscoverableNode(deviceId, sharePayload);
        }catch (Exception exception){
            Log.e(TAG, "toDiscoverableNode: " + exception.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Handles app qr login messages
     *
     * @param payload payload
     */
    public void appQrLoginMessageHandler(String payload){
        if(TextUtils.isEmpty(payload)) {
            return;
        }

        try{
            // convert payload to java bean
            Type type = new TypeToken<AppQrLogInMsgBean>(){}.getType();
            AppQrLogInMsgBean appQrLogInMsgBean = new Gson().fromJson(payload, type);
            if(null == appQrLogInMsgBean){
                return;
            }

            // notify subscribers.
            EventBus.getDefault().post(appQrLogInMsgBean);
        }catch (Exception exception) {
            String msg = TAG + " Failed to convert payload. Cause: "+exception.getLocalizedMessage();
            LogUtils.e(msg);
        }
    }

    /**
     * 设备升级进度通知
     * {
     * "id":"45lkj3551234001",//消息ID
     * "ts":1626197189,//时间戳（秒）
     * "code":"ota_progress",//通知编码
     * "data":{
     * //0,成功；错误码，-1：下载超时；-2：文件不存在；-3：签名过期；-4:MD5不匹配；-5：更新固件失败
     * "resCode":0,
     * "deviceId":"设备ID",
     * "type":"download",//download: 下载中 ， burning: 升级烧录中, done:升级成功,fail:失败
     * "percent":80, //下载进度 0~100 ；
     * "version":"1.0.0"//升级的版本号
     * "resMsg":"",//错误信息
     * }
     * }
     */
    public void mqttOtaUpgradeNotify(String otaUpgradeMsg) {
        LgUtils.w(TAG + "   mqtt -->  mqttOtaUpgradeNotify  otaUpgradeMsg=" + otaUpgradeMsg);
        if (!TextUtils.isEmpty(otaUpgradeMsg)) {
            EventBus.getDefault().post(new DeviceEvent(DeviceEvent.Type.OTA_UPGRADE, otaUpgradeMsg));
        }
    }

    /**
     * 设备状态变化
     * {
     * "id":"45lkj3551234001",//消息ID
     * "ts":1626197189,//时间戳（秒）
     * "code":"status",//通知编码
     * "data":{
     * "deviceId":"设备ID",
     * "online":1  //在线状态 0：离线；1：在线
     * }//数据结构体
     * }
     */
    public void mqttDeviceStatusChangeNotify(String deviceStatusChangeMsg) throws Exception {
        Log.d(TAG, "mqttDeviceStatusChangeNotify: message= " + deviceStatusChangeMsg);
        JSONObject jsonObjectStatus = new JSONObject(deviceStatusChangeMsg);
        if (jsonObjectStatus.has(Property.ONLINE_KEY)) {
            int online = jsonObjectStatus.getInt(Property.ONLINE_KEY);
            String deviceId = "";
            if (jsonObjectStatus.has(Property.DEVICE_ID_KEY)) {
                deviceId = jsonObjectStatus.getString(Property.DEVICE_ID_KEY);
            }

            // Build device online status change prop message.
            DeviceProperties deviceProperties =  new DeviceProperties(
                    deviceId,
                    new HashMap<String, Object>(){{
                        put(RinoDataPoint.RINO_ONLINE_DP.getValue(), online == 1);
                    }}
            );
            PropertiesChangeMsg message = new PropertiesChangeMsg(
                    Collections.singletonList(deviceProperties)
            );

            updateDeviceProperties(message);

            // 0：离线；1：在线
            if (online == 1 || online == 0) {
                EventBus.getDefault().post(new DeviceEvent(DeviceEvent.Type.DEVICE_CHANGED, online));
                sendDeviceStatusNotifyPanel(deviceId, online == 1);
            }
        }
    }

    /**
     * 绑定结果通知
     * 2.0
     * {
     * "id":"45lkj3551234001",//消息ID
     * "ts":1626197189,//时间戳（秒）
     * "code":"bind_result",//通知编码
     * "data":{
     * "devices":[
     * {
     * "deviceId":"设备ID",
     * "uuid": "设备uuid",
     * "gatewayId":"网关设备ID",
     * "type":"add",//add：绑定；remove:解绑
     * "result":"success", //success:成功；fail:失败
     * "msg":"绑定成功" //绑定结果描述
     * }
     * ]
     * }//数据结构体
     * }
     */
    public void mqttBindDeviceResultNotify(String deviceBindResultMsg) throws Exception {
        LgUtils.w(TAG + "   mqtt -->  mqttBindDeviceResultNotify deviceBindResultMsg=" + deviceBindResultMsg);
        JSONObject jsonObjectBindResult = new JSONObject(deviceBindResultMsg);
        if (!jsonObjectBindResult.has(Property.DEVICES_KEY)) {
            return;
        }

        JSONArray devices = jsonObjectBindResult.getJSONArray(Property.DEVICES_KEY);
        if (devices.length() == 0) {
            return;
        }

        for (int i = 0; i < devices.length(); i++) {
            JSONObject deviceItem = devices.getJSONObject(i);
            //说明是面板中网关子设备配网
            if (deviceItem != null && deviceItem.has(Property.GATEWAY_ID_KEY)
                    && !TextUtils.isEmpty(deviceItem.getString(Property.GATEWAY_ID_KEY))
                    && !TextUtils.equals(deviceItem.getString(Property.GATEWAY_ID_KEY), "null")) {
                sendBindDeviceStatusNotifyPanel(devices);
            } else {
                if (deviceItem == null || !deviceItem.has(Property.TYPE_KEY)) {
                    continue;
                }

                //绑定
                String type = deviceItem.getString(Property.TYPE_KEY);
                if (TextUtils.equals(type, "add") && deviceItem.has(Property.UUID_KEY)) {
                    String uuid = deviceItem.getString(Property.UUID_KEY);
                    //绑定状态
                    if (!deviceItem.has(Property.RESULT_KEY)) {
                        continue;
                    }

                    //绑定成功
                    if (TextUtils.equals(deviceItem.getString(Property.RESULT_KEY), "success")) {
                        EventBus.getDefault().post(
                                new DeviceNetworkEvent(
                                        DeviceNetworkEvent.Type.BIND_SUCCESS,
                                        true,
                                        "",
                                        uuid
                                )
                        );
                        EventBus.getDefault().post(
                                new DeviceEvent(DeviceEvent.Type.DEVICE_BIND_SUCCESS, "success")
                        );
                    } else {
                        EventBus.getDefault().post(
                                new DeviceNetworkEvent(
                                        DeviceNetworkEvent.Type.BIND_FAIL,
                                        false,
                                        "",
                                        uuid
                                )
                        );
                    }
                } else if (TextUtils.equals(type, "remove")) {
                    //移除
                    EventBus.getDefault().post(
                            new DeviceEvent(DeviceEvent.Type.DEVICE_REMOVE, "remove")
                    );
                }
            }
        }
    }


    /**
     * 设备属性变化通知
     * {
     * "id":"45lkj3551234001",//消息ID
     * "ts":1626197189,//时间戳（秒）
     * "code":"property_change",//通知编码
     * "data":{
     * "devices":[
     * {
     * "deviceId":"设备ID",
     * "properties":{
     * "color":"red",  //颜色  红色
     * "brightness":80 //亮度  80
     * }
     * }
     * ]
     * }//数据结构体
     * }
     */
    public void mqttDevicePropertiesChangeNotify(String devicePropertiesChangeMsg) throws Exception {
        LgUtils.w(TAG + "   mqtt -->  mqttDevicePropertiesChangeNotify devicePropertiesChangeMsg=" + devicePropertiesChangeMsg);
        try {
            PropertiesChangeMsg changeMsg = new Gson().fromJson(
                    devicePropertiesChangeMsg, PropertiesChangeMsg.class
            );
            updateDeviceProperties(changeMsg);
        }catch (Exception exception) {
            Log.d(TAG, "mqttDevicePropertiesChangeNotify: Failed to convert =" + exception.getLocalizedMessage());
        }
        JSONObject jsonObjectPropertiesChange = new JSONObject(devicePropertiesChangeMsg);
        if (jsonObjectPropertiesChange.has(Property.DEVICES_KEY)) {
            JSONArray devices = jsonObjectPropertiesChange.getJSONArray(Property.DEVICES_KEY);
            //mqtt 1.0
            String json = mqttDevicePropertiesJsonFormat(devices);
            sendPropertiesChangeNotifyPanel(json, 1);
            //mqtt 2.0
            if (devices.length() > 0) {
                String json2 = devices.toString();
                sendPropertiesChangeNotifyPanel(json2, 2);
            }
        }
    }

    /**
     * Updates the device properties
     * @param propertiesMsg the changed device properties
     */
    public void updateDeviceProperties(PropertiesChangeMsg propertiesMsg) {
        Log.d(TAG, "updateDeviceProperties: msg=" + new Gson().toJson(propertiesMsg));
        if(null == propertiesMsg){
            return;
        }

        List<DeviceProperties> devices = propertiesMsg.getDevices();
        if(null == devices || devices.isEmpty()){
            return;
        }

        // update the states
        devices.forEach(deviceProperties -> {
            Map<String, Object> properties = deviceProperties.getProperties();
            if (null == properties || properties.isEmpty()) {
                return;
            }

            Map<String, Object> validProperties = properties.entrySet().stream()
                    .filter(entry -> MQTT_DATA_POINTS.contains(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (validProperties.isEmpty()) {
                return;
            }

            Log.d(TAG, "updateDeviceProperties: valid= " + new Gson().toJson(validProperties));
            addProperties(new DeviceProperties(
                    deviceProperties.getDeviceId(), validProperties
            ));
        });
    }

    /**
     * Caches the device properties.
     * @param deviceProperties device properties
     */
    private void addProperties(DeviceProperties deviceProperties) {
        if(null == deviceProperties){
            return;
        }

        Log.d(TAG, "addProperties: properties=" + new Gson().toJson(deviceProperties));
        String deviceId = deviceProperties.getDeviceId();
        if (mFutures.containsKey(deviceId)) {
            Log.d(TAG, "addProperties: " + deviceId + " exists.");
            DevicePropertiesFuture future = mFutures.get(deviceId);
            if(null != future) {
                future.getProperties().putAll(deviceProperties.getProperties());
                Disposable disposable = createDisposableFuture(deviceId);
                future.updateDisposable(disposable);
            }
        } else {
            Log.d(TAG, "addProperties: no record of " + deviceId);
            Disposable disposable = createDisposableFuture(deviceId);
            DevicePropertiesFuture future = new DevicePropertiesFuture(deviceProperties, disposable);
            mFutures.put(deviceId, future);
            Log.d(TAG, "addProperties: future=" + new Gson().toJson(future));
        }
    }

    private Disposable createDisposableFuture(String deviceId){
        return Completable.complete()
                .delay(2500, TimeUnit.MILLISECONDS)
                .doOnComplete(() -> {
                    // retrieve the states
                    DevicePropertiesFuture future = mFutures.remove(deviceId);
                    if(null == future){
                        return;
                    }

                    // remove record from cache and dispose the delay disposable
                    if(future.getDisposable() != null){
                        future.getDisposable().dispose();
                    }

                    // send it.
                    DataSourceManager.getInstance().updateDeviceStates(
                            future.getDeviceId(), future.getProperties()
                    );
                })
                .subscribeOn(Schedulers.io())
                .subscribe(
                        ()->{},
                        error-> Log.e(TAG, "addProperties: " + error.getLocalizedMessage())
                );
    }

    /**
     * mqtt上报开关dp点数据
     * {
     * * "id":"45lkj3551234001",//消息ID
     * * "ts":1626197189,//时间戳（秒）
     * * "code":"property_change",//通知编码
     * * "data":{
     * * "devices":[
     * * {
     * * "deviceId":"设备ID",
     * * "properties":{
     * * "color":"red",  //颜色  红色
     * * "brightness":80 //亮度  80
     * * }
     * * }
     * * ]
     * * }//数据结构体
     * * }
     */
    public boolean isSwitch(String reportMqttData) throws Exception {
        boolean isSwitchFlag = false;
        JSONObject dpDataBean = new JSONObject(reportMqttData);
        JSONArray devices = dpDataBean.getJSONArray(Property.DEVICES_KEY);
        for (int i = 0; i < devices.length(); i++) {
            JSONObject jsonObject = devices.getJSONObject(i);
            if (jsonObject != null && jsonObject.has(Property.PROPERTIES_KEY)) {
                if (jsonObject.get(Property.PROPERTIES_KEY) instanceof JSONObject) {
                    JSONObject properties = jsonObject.getJSONObject(Property.PROPERTIES_KEY);
                    isSwitchFlag = mqttDeviceSwitchJsonFormat(properties);
                }

                if (isSwitchFlag) {
                    break;
                }
            }
        }

        return isSwitchFlag;
    }

    /**
     * 设备快捷开关 mqtt 2.0
     */
    public boolean mqttDeviceSwitchJsonFormat(JSONObject properties) throws Exception {
        boolean isSwitchFlag = false;
        Iterator<String> keys = properties.keys();
        while (keys.hasNext()) {
            String next = keys.next();
            Object value = properties.get(next);
            if (value instanceof Boolean) {
                isSwitchFlag = true;
                break;
            }
        }
        return isSwitchFlag;
    }

    /**
     * 设备属性变化 主要用于mqtt 2.0期数据转化位1.0
     * mqtt 1.0:
     * [{"deviceId":"1641330103605059584","properties":[{"switch":{"ts":1.681127303E9,"value":false}}]}]
     * <p>
     * mqtt 2.0:
     * [
     * * {
     * * "deviceId":"设备ID",
     * * "properties":{
     * * "color":"red",  //颜色  红色
     * * "brightness":80 //亮度  80
     * * }
     * * }
     * * ]
     */
    public String mqttDevicePropertiesJsonFormat(JSONArray devices) throws Exception {
        String propertiesChange = "";
        if (devices != null && devices.length() > 0) {
            //mqtt 1.0
            JSONArray devicesMqtt1 = new JSONArray();
            for (int i = 0; i < devices.length(); i++) {
                JSONObject item = devices.getJSONObject(i);
                JSONObject mqttTemp = new JSONObject();
                if (item.has(Property.DEVICE_ID_KEY)) {
                    mqttTemp.put(Property.DEVICE_ID_KEY, item.getString(Property.DEVICE_ID_KEY));
                }
                if (item.has(Property.PROPERTIES_KEY)) {
                    JSONArray propertiesTemp = new JSONArray();
                    JSONObject properties = item.getJSONObject(Property.PROPERTIES_KEY);
                    Iterator<String> keys = properties.keys();
                    while (keys.hasNext()) {
                        JSONObject propertiesItemTemp = new JSONObject();
                        JSONObject propertiesItemTemp2 = new JSONObject();
                        String next = keys.next();
                        propertiesItemTemp2.put(Property.TIMESTAMP_KEY, System.currentTimeMillis() / 1000);
                        propertiesItemTemp2.put(Property.VALUE_KEY, properties.get(next));
                        propertiesItemTemp.put(next, propertiesItemTemp2);
                        propertiesTemp.put(propertiesItemTemp);
                    }
                    mqttTemp.put(Property.PROPERTIES_KEY, propertiesTemp);
                }
                devicesMqtt1.put(mqttTemp);
            }
            propertiesChange = devicesMqtt1.toString();
        }
        return propertiesChange;
    }

    /**
     * 当设备属性状态变化时，发送数据给面板
     *
     * @param json     发送json数据
     * @param mqttType 1:1.0;2:2.0
     */
    public void sendPropertiesChangeNotifyPanel(String json, int mqttType) throws Exception {
        WritableArray writableArray = BundleJSONConverter.jsonToReact(new JSONArray(json));
        /// getWritableArray(JavaOnlyArray.from(devicePayload.getData().getDevices()));//Arguments.createArray();
        LgUtils.i(TAG + "   sendPropertiesChangeNotifyPanel   mqtt --> 发数据给rn --> " + json);
        String notifyType = mqttType == 1 ? RNConstant.DEVICE_DATA_POINT_UPDATE : RNConstant.DEVICE_DATA_POINT_UPDATE_V2;
        Boolean commonFunctionPanel = SharedPreferenceUtil.getInstance().get(Constant.COMMON_FUNCTION_PANEL, false);
        if (commonFunctionPanel) {
            CommonEventManager.getInstance().sendData(notifyType, writableArray);
        } else {
            EventManager.getInstance().sendData(notifyType, writableArray);
        }
    }

    /**
     * 面板中网关绑定网关子设备逻辑
     * 2.0
     * [
     * {
     * "deviceId":"设备ID",
     * "uuid": "设备uuid",
     * "gatewayId":"网关设备ID",
     * "type":"add",//add：绑定；remove:解绑
     * "result":"success", //success:成功；fail:失败
     * "msg":"绑定成功" //绑定结果描述
     * }
     * ]
     * 1.0:
     * [{"deviceId":"1646069091649863680","gatewayId":"1643849625483382784",
     * "header":{"bind":true,"status":1.0}},
     * {"deviceId":"1646069092319666176","gatewayId":"1643849625483382784",
     * "header":{"bind":true,"status":1.0}}]
     */
    public void sendBindDeviceStatusNotifyPanel(JSONArray bindDeviceJSONArray) throws Exception {
        JSONArray mqttJSONArrayTemp = new JSONArray();
        if (bindDeviceJSONArray == null || bindDeviceJSONArray.length() == 0) {
            return;
        }
        for (int i = 0; i < bindDeviceJSONArray.length(); i++) {
            String gatewayId = "";
            JSONObject jsonObject = bindDeviceJSONArray.getJSONObject(i);
            JSONObject bindJsonItem = new JSONObject();
            JSONObject bindJsonHeader = new JSONObject();
            if (jsonObject != null && jsonObject.has(Property.DEVICE_ID_KEY)) {
                bindJsonItem.put(Property.DEVICE_ID_KEY, jsonObject.getString(Property.DEVICE_ID_KEY));
            }
            if (jsonObject != null && jsonObject.has(Property.GATEWAY_ID_KEY)) {
                gatewayId = jsonObject.getString(Property.GATEWAY_ID_KEY);
                bindJsonItem.put(Property.GATEWAY_ID_KEY, gatewayId);
            }
            if (jsonObject != null && jsonObject.has(Property.TYPE_KEY)) {
                String type = jsonObject.getString(Property.TYPE_KEY);
                //绑定
                if (TextUtils.equals(type, "add")) {
                    //绑定状态
                    if (jsonObject.has(Property.RESULT_KEY)) {
                        //绑定成功
                        if (TextUtils.equals(jsonObject.getString(Property.RESULT_KEY), "success")) {
                            bindJsonHeader.put(Property.BIND_KEY, true);
                            bindJsonHeader.put(Property.STATUS_KEY, 1);
                            bindJsonItem.put(Property.HEADER_KEY, bindJsonHeader);
                            EventBus.getDefault().post(new DeviceEvent(DeviceEvent.Type.DEVICE_BIND_SUCCESS, "success"));
                        }
                    }
                } else if (TextUtils.equals(type, "remove")) {
                    //绑定成功
                    if (TextUtils.equals(jsonObject.getString(Property.RESULT_KEY), "success")) {
                        sendGateWaySubDeviceUnBindNotifyPanel(gatewayId);
                        List<DeviceInfoBean> panelDeviceDataList = PanelCacheDeviceDataManager.getInstance().getPanelDeviceDataList();
                        if (panelDeviceDataList != null && !panelDeviceDataList.isEmpty()) {
                            DeviceInfoBean infoBean = panelDeviceDataList.get(panelDeviceDataList.size() - 1);
                            //网关设备
                            if (infoBean != null && infoBean.getGateway() == 1) {
                                LgUtils.w(TAG + "   当前在网关面板中,不需要移除");
                                return;
                            }
                        }

                        EventBus.getDefault().post(new DeviceEvent(DeviceEvent.Type.DEVICE_REMOVE, "remove"));
                        return;
                    }
                }
            }
            mqttJSONArrayTemp.put(bindJsonItem);
        }
        sendBindDeviceNotifyPanel(mqttJSONArrayTemp.toString(), 1);
        sendBindDeviceNotifyPanel(bindDeviceJSONArray.toString(), 2);
    }

    /**
     * 当网关设备绑定网关子设备时，发送数据给面板
     *
     * @param json     发送json数据
     * @param mqttType 1:1.0;2:2.0
     */
    public void sendBindDeviceNotifyPanel(String json, int mqttType) throws Exception {
        WritableArray writableArray = BundleJSONConverter.jsonToReact(new JSONArray(json));
        /// getWritableArray(JavaOnlyArray.from(devicePayload.getData().getDevices()));//Arguments.createArray();
        LgUtils.i(TAG + "   sendBindDeviceNotifyPanel   mqtt --> 发数据给rn --> " + json);
        String notifyType = mqttType == 1 ? RNConstant.DEVICE_DATA_POINT_UPDATE : RNConstant.DEVICE_GATEWAY_BIND_V2;
        EventManager.getInstance().sendData(notifyType, writableArray);
    }

    /**
     * 设备在线离线通知，发送数据给面板
     *
     * @param deviceStatus 设备在线状态
     */
    public void sendDeviceStatusNotifyPanel(String deviceId, boolean deviceStatus) throws Exception {
        //deviceStatus=true:如果改设备是在线状态，不管是单设备还是群组，都是在线
        if (!deviceStatus) {
            deviceStatus = groupDeviceStatusNotify(deviceId, deviceStatus);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Property.IS_ONLINE_KEY, deviceStatus);
        WritableMap writableMap = BundleJSONConverter.jsonToReact(jsonObject);
        /// getWritableArray(JavaOnlyArray.from(devicePayload.getData().getDevices()));
        /// Arguments.createArray();
        LgUtils.i(TAG + "   sendDeviceStatusNotifyPanel   mqtt --> 发数据给rn --> " + jsonObject);
        EventManager.getInstance().sendData(RNConstant.DEVICE_INFO_UPDATE, writableMap);
    }

    /**
     * mqtt上报设备状态变化，群组设备在线状态处理逻辑
     */
    public boolean groupDeviceStatusNotify(String deviceId, boolean groupDeviceStatus) {
        DeviceInfoBean deviceInfoBean = null;
        //当前面板是否为群组面板，
        List<DeviceInfoBean> panelDeviceDataList = PanelCacheDeviceDataManager.getInstance().getPanelDeviceDataList();
        LgUtils.w(TAG + "    groupDeviceStatusNotify  panelDeviceDataList=" + panelDeviceDataList);
        if (panelDeviceDataList != null && !panelDeviceDataList.isEmpty()) {
            for (DeviceInfoBean item : panelDeviceDataList) {
                deviceInfoBean = item;
                //当前操作的面板包含群组面板
                if (deviceInfoBean != null && !TextUtils.isEmpty(deviceInfoBean.getGroupId())) {
                    break;
                }
            }
        }
        if (deviceInfoBean != null) {
            GroupBean groupDeviceInfo = CacheDataManager.getInstance().getGroupDeviceInfo(deviceInfoBean.getGroupId());
            if (groupDeviceInfo != null) {
                List<GroupDeviceItemBean> deviceList = groupDeviceInfo.getDeviceList();
                if (deviceList != null && !deviceList.isEmpty()) {
                    boolean isExit = false;
                    for (GroupDeviceItemBean item : deviceList) {
                        //该设备在这个群组中
                        if (item != null && TextUtils.equals(item.getId(), deviceId)) {
                            isExit = true;
                            break;
                        }
                    }
                    if (isExit) {
                        boolean status = false;
                        for (GroupDeviceItemBean item : deviceList) {
                            //改设备在这个群组中
                            if(item == null) {
                                continue;
                            }
                            if (TextUtils.equals(item.getId(), deviceId)) {
                                continue;
                            }
                            DeviceInfoBean deviceInfo = CacheDataManager.getInstance().getDeviceInfo(item.getId());
                            if (deviceInfo.getOnlineStatus() == 1) {
                                status = true;
                                break;
                            }
                        }
                        if (status) {
                            groupDeviceStatus = true;
                        }
                    }
                }
            }
        }
        return groupDeviceStatus;
    }

    //*************************数据下发格式转化***************************************

    /**
     * 下发单个开发dp点数据格式转化
     * {
     * "deviceId":"xxx",//设备ID
     * "data":{
     * "color":"red",  //颜色  红色
     * "brightness":80 //亮度  80
     * ......
     * }
     * }
     */
    public JsonObject sendSwitchPublish(DeviceInfoBean deviceInfoBean, boolean isOpen) {
        //data节点下的数据
        JsonObject dpBean = new JsonObject();
        if (deviceInfoBean == null) {
            return dpBean;
        }
        List<DeviceDpBean> deviceDpBeans = deviceInfoBean.getSwitchDpInfoVOList();
        if (deviceDpBeans == null || deviceDpBeans.isEmpty()) {
            return dpBean;
        }
        for (DeviceDpBean deviceDpBean : deviceDpBeans) {
            if (deviceDpBean.getValue() instanceof Boolean) {
                dpBean.addProperty(deviceDpBean.getKey(), !isOpen);
            }
        }
        return dpBean;
    }

    /**
     * Publishes device commands
     *
     * @param deviceId device identifier
     * @param command the device command
     */
    public boolean publish(String deviceId, Map<String, Object> command) {
        if(null == deviceId){
            throw new IllegalArgumentException("Device identifier cannot be null");
        }

        Map<String, Object> params = new HashMap<>(2);
        params.put(Property.DATA_KEY, command);
        params.put(Property.DEVICE_ID_KEY, deviceId);
        try {
            MqttNetworkManager.getInstance().mqttPropsIssue(params, new CallbackListener<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    Log.d(TAG, "onSuccess: " + data);
                }

                @Override
                public void onError(String code, String error) {
                    Log.e(TAG, "onError: code= " + code + " | error= " +  error);
                }
            });
            return true;
        }catch (Exception ex) {
            Log.e(TAG, "publish: failed to pub device command= " + ex.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Publishes device commands
     *
     * @param deviceId device identifier
     * @param command the device command
     * @return an observable
     */
    public Observable<Boolean> publishDps(String deviceId, Map<String, Object> command) {
        if(null == deviceId){
            throw new IllegalArgumentException("Device identifier cannot be null");
        }

        Map<String, Object> params = new HashMap<>(2);
        params.put(Property.DATA_KEY, command);
        params.put(Property.DEVICE_ID_KEY, deviceId);

        return Observable.create(emitter -> {
            CallbackListener<Boolean> listener = new CallbackListener<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    Log.d(TAG, "onSuccess: " + data);
                    if(null != data){
                        emitter.onNext(data);
                    }
                    emitter.onComplete();
                }

                @Override
                public void onError(String code, String error) {
                    Log.e(TAG, "onError: code= " + code + " | error= " +  error);
                    emitter.onError(new Exception(error));
                }
            };
            MqttNetworkManager.getInstance().mqttPropsIssue(params, listener);
        });
    }

    /**
     * 单个设备和群组设备开关显示
     */
    public boolean getSwitchDpStatus(DeviceInfoBean deviceInfoBean) {
        if (null == deviceInfoBean) {
            return false;
        }
        boolean isOpen = false;
        if (TextUtils.isEmpty(deviceInfoBean.getGroupId())) {
            isOpen = getSwitchSingleDpStatus(deviceInfoBean);
        } else {
            List<DeviceInfoBean> infoBeans = CacheDataManager.getInstance().getGroupDeviceListData(deviceInfoBean.getGroupId());
            if (infoBeans == null || infoBeans.isEmpty()) {
                return false;
            }
            for (DeviceInfoBean info : infoBeans) {
                isOpen = getSwitchSingleDpStatus(info);
                if (isOpen) {
                    break;
                }
            }
        }
        return isOpen;
    }

    /**
     * 获取开关状态，兼容多个
     */
    public boolean getSwitchSingleDpStatus(DeviceInfoBean deviceInfoBean) {
        if (deviceInfoBean == null || deviceInfoBean.getSwitchDpInfoVOList() == null) {
            return false;
        }

        if(deviceInfoBean.getSwitchDpInfoVOList().isEmpty()) {
            return false;
        }

        if(deviceInfoBean.getOnlineStatus() == 0) {
            return false;
        }

        //快捷开关
        boolean isOpen = false;
        for (DeviceDpBean item : deviceInfoBean.getSwitchDpInfoVOList()) {
            if (item.getValue() instanceof Boolean) {
                isOpen = (boolean) item.getValue();
                if (isOpen) {
                    break;
                }
            }
        }
        return isOpen;
    }

    /**
     * 面板内设备数据下发时，将 mqtt 1.0 数据结构转化为mqtt 2.0
     * [{"properties":{"switch":false},"deviceId":"1630469334162354176"},
     * {"properties":{"switch":false},"deviceId":"1633286617540206592"}]
     * <p>
     * 转化为mqtt 2.0数据结构
     * {
     * "deviceId":"xxx",//设备ID
     * "data":{
     * "color":"red",  //颜色  红色
     * "brightness":80 //亮度  80
     * ......
     * }
     * }
     * <p>
     * 或群组
     * {
     * "groupId":"xxx",//群组ID
     * "data":{
     * "color":"red",  //颜色  红色
     * "brightness":80 //亮度  80
     * ......
     * }
     * }
     */
    public void panelDevicePropertiesSendPublish(
            Context mContext, ReadableArray readableArray, CallbackListener<Boolean> callbackListener
    ) {
        Map<String, Object> params = new HashMap<>(2);
        try {
            if (null == readableArray || readableArray.size() <= 0) {
                throw new IllegalArgumentException("Invalid arguments passed");
            }

            JSONArray jsonArray = BundleJSONConverter.reactToJSON(readableArray);
            if (jsonArray.length() == 0) {
                throw new IllegalArgumentException("Invalid arguments passed");
            }

            JSONObject propertiesJson = jsonArray.getJSONObject(0);
            if (propertiesJson.has(Property.PROPERTIES_KEY)) {
                //设备属性
                JSONObject properties = propertiesJson.getJSONObject(Property.PROPERTIES_KEY);
                params.put(Property.DATA_KEY, JSONDataFormatUtils.orgJsonToGoogleJson(properties));

                if (jsonArray.length() > 1) {
                    //群组
                    handleDeviceGroupProperties(jsonArray, params, callbackListener);
                } else if(propertiesJson.has(Property.DEVICE_ID_KEY)) {
                    String deviceId = propertiesJson.getString(Property.DEVICE_ID_KEY);
                    DeviceInfoBean deviceInfoBean = CacheDataManager.getInstance().getDeviceInfo(deviceId);
                    if(null == deviceInfoBean || null == mContext){
                        return;
                    }
                    if(MtrDeviceDataUtils.isMatterDevice(deviceInfoBean)) {
                        //说明时matter设备
                        MtrDeviceControlManager.getInstance(mContext).matterControlEntrance(readableArray);
                    }else{
                        //单设备
                        params.put(Property.DEVICE_ID_KEY, deviceId);
                        MqttNetworkManager.getInstance().mqttPropsIssue(params, callbackListener);
                    }
                }else{
                    //单设备数据下发
                    MqttNetworkManager.getInstance().mqttPropsIssue(params, callbackListener);
                }
            } else if (propertiesJson.has(Property.TRIGGER_KEY)) {
                handleTriggerEvents(propertiesJson, params, callbackListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            callbackListener.onError("panelDevicePropertiesSendPublish", e.getLocalizedMessage());
            LgUtils.w(TAG + " panelDevicePropertiesSendPublish. Cause=" + e.getMessage());
        }
    }

    /**
     * Handles device group control properties
     *
     * @param jsonArray data array
     * @param params the params
     * @param callbackListener callback listener
     */
    private void handleDeviceGroupProperties(
            JSONArray jsonArray,
            Map<String, Object> params,
            CallbackListener<Boolean> callbackListener
    ) throws Exception {
        //群组
        DeviceInfoBean deviceInfo = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            if(null == item || !item.has(Property.DEVICE_ID_KEY)) {
                continue;
            }
            deviceInfo = CacheDataManager.getInstance()
                    .getDevIdByDeviceInfo(item.getString(Property.DEVICE_ID_KEY));
            if (deviceInfo != null) {
                break;
            }
        }

        if (deviceInfo != null) {
            params.put(Property.GROUP_ID_KEY, deviceInfo.getGroupId());
        }

        //群组设备数据下发
        MqttNetworkManager.getInstance().mqttGroupPropsIssue(params, callbackListener);
    }

    /**
     * Handles the trigger events
     *
     * @param propertiesJson command properties
     * @param params trigger params
     * @param callbackListener listener
     */
    private void handleTriggerEvents(
            JSONObject propertiesJson,
            Map<String, Object> params,
            CallbackListener<Boolean> callbackListener
    ) throws JSONException {
        JSONObject trigger = propertiesJson.getJSONObject(Property.TRIGGER_KEY);
        if (propertiesJson.has(Property.DEVICE_ID_KEY)) {
            String deviceId = propertiesJson.getString(Property.DEVICE_ID_KEY);
            params.put(Property.DEVICE_ID_KEY, deviceId);
        }

        String type = "", eventType = "", uuid = "";
        if (trigger.has(Property.TYPE_KEY)) {
            type = trigger.getString(Property.TYPE_KEY);
        }
        if (trigger.has(Property.EVENT_TYPE_KEY)) {
            eventType = trigger.getString(Property.EVENT_TYPE_KEY);
        }

        //网关设备绑定
        if (TextUtils.equals(type, Property.APP_EVENT_KEY) && TextUtils.equals(eventType, "FIND_BIND")) {
            //包含uuid，说明配单个子设备
            if (trigger.has(Property.UUID_KEY)) {
                uuid = trigger.getString(Property.UUID_KEY);
            }
            if (TextUtils.isEmpty(uuid)) {
                params.put(Property.SECOND_KEY, 60);
                params.put(Property.FIND_KEY, true);
                //单设备数据下发
                MqttNetworkManager.getInstance().mqttScanIssue(params, callbackListener);
            } else {
                params.put(Property.UUID_KEY, uuid);
                //单设备数据下发
                MqttNetworkManager.getInstance().mqttSubAdd(params, callbackListener);
            }
        }
    }

    /**
     * mqtt 2.0直接下发
     * {
     * "deviceId":"xxx",//设备ID
     * "data":{
     * "color":"red",  //颜色  红色
     * "brightness":80 //亮度  80
     * ......
     * }
     * }
     * <p>
     * 或群组
     * {
     * "groupId":"xxx",//群组ID
     * "data":{
     * "color":"red",  //颜色  红色
     * "brightness":80 //亮度  80
     * ......
     * }
     * }
     */
    public void panelDevicePropertiesSendPublish(Context mContext, ReadableMap readableMap, Promise promise) {
        CallbackListener<Boolean> callbackListener = new CallbackListener<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                if (promise != null) {
                    promise.resolve(data);
                }
            }

            @Override
            public void onError(String code, String error) {
                if (promise != null) {
                    promise.resolve(false);
                }
            }
        };
        Map<String, Object> params = new HashMap<>();
        if (readableMap != null) {
            if (readableMap.hasKey(Property.DATA_KEY)) {
                //设备属性数据
                params.put(Property.DATA_KEY, readableMap.getMap(Property.DATA_KEY));
            }
            if (readableMap.hasKey(Property.GROUP_ID_KEY)) {
                //说明时群组设备
                params.put(Property.GROUP_ID_KEY, readableMap.getString(Property.GROUP_ID_KEY));
                //群组设备数据下发
                MqttNetworkManager.getInstance().mqttGroupPropsIssue(params, callbackListener);

            } else if (readableMap.hasKey(Property.DEVICE_ID_KEY)) {
                //单设备控制
                String deviceId = readableMap.getString(Property.DEVICE_ID_KEY);
                params.put(Property.DEVICES_KEY, deviceId);
                DeviceInfoBean deviceInfoBean = CacheDataManager.getInstance().getDeviceInfo(deviceId);
                if (MtrDeviceDataUtils.isMatterDevice(deviceInfoBean) && mContext != null) {
                    //说明时matter设备
                    MtrDeviceControlManager.getInstance(mContext).matterControlEntrance(readableMap);
                    if (promise != null) {
                        promise.resolve(true);
                    }
                    return;
                }
                //单设备数据下发
                MqttNetworkManager.getInstance().mqttPropsIssue(params, callbackListener);
            }
        }
    }

    /**
     * 固件升级下发mqtt数据 数据格式
     * {
     * "deviceId":"xxx",//设备ID
     * "fileSize": 708482,
     * "md5sum": "36eb5951179db14a63****a37a9322a2",
     * "url": "<a href="https://ota-1255858890.cos.ap-guangzhou.myqcloud.com">Url</a>",
     * "version": "0.2"
     * }
     */
    public void deviceOtaUpgradeSendPublish(JSONObject otaJsonData, String devId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put(Property.DEVICE_ID_KEY, devId);
        if (otaJsonData != null) {
            Iterator<String> keys = otaJsonData.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                params.put(next, otaJsonData.get(next));
            }
        }
        MqttNetworkManager.getInstance().mqttOtaIssue(params);
    }

    /**
     * 设备属性上报下发mqtt数据 数据格式 蓝牙设备+matter设备操作成功，下发上报属性
     * {
     * "deviceId":"xxx",//设备ID
     * "data":{
     * "color":"red",  //颜色  红色
     * "brightness":80 //亮度  80
     * ......
     * }
     * 蓝牙设备结构
     * {
     * "code" : 0,
     * "ts" : 1609462497,
     * "msgId" : "cf1ff87a3ebb4c3",
     * "data" : {
     * "switch" : {
     * "value" : false,
     * "ts" : 1609462497
     * },
     * "dynamic" : {
     * "value" : "2013",
     * "ts" : 1609462497
     * }
     * },
     * "type" : "thing.property.get.response"
     * }
     * <p>
     * }
     */
    public void propsReportSendPublish(JSONObject reportJsonData, String devId) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put(Property.DEVICE_ID_KEY, devId);
            params.put(Property.DATA_KEY, JSONDataFormatUtils.orgJsonToGoogleJson(reportJsonData));
            MqttNetworkManager.getInstance().mqttPropsReport(params);
        } catch (Exception e) {
            e.printStackTrace();
            LgUtils.w(TAG + "   propsReportSendPublish  Exception e=" + e.getMessage());
        }
    }

    /**
     * 群组设备或者单设备判断是否存在开关dp点数据
     * @param deviceInfoBean device info
     * @return true if a switch data point exists false otherwise
     */
    public boolean getSwitchDpExit(DeviceInfoBean deviceInfoBean) {
        if (deviceInfoBean == null) {
            return false;
        }

        boolean isOpen = false;
        if (TextUtils.isEmpty(deviceInfoBean.getGroupId())) {
            isOpen = getShortcutSwitchSingleDpData(deviceInfoBean);
        } else {
            List<DeviceInfoBean> infoBeans = CacheDataManager.getInstance().getGroupDeviceListData(deviceInfoBean.getGroupId());
            if (infoBeans == null || infoBeans.isEmpty()) {
                return false;
            }
            for (DeviceInfoBean info : infoBeans) {
                isOpen = getShortcutSwitchSingleDpData(info);
                if (isOpen) {
                    break;
                }
            }
        }
        return isOpen;
    }

    /**
     * 单设备 快捷开关是否存在
     */
    public boolean getShortcutSwitchSingleDpData(DeviceInfoBean deviceInfoBean) {
        /// LgUtils.w(TAG + " getShortcutSwitchSingleDpData  开关dp点数据不为空");
        return deviceInfoBean != null && deviceInfoBean.getOnlineStatus() == 1 && deviceInfoBean.getSwitchDpInfoVOList() != null && !deviceInfoBean.getSwitchDpInfoVOList().isEmpty();
    }

    /**
     * 群组设备或者单设备判断是否存在常用功能点数据
     */
    public boolean getCommonFunExit(DeviceInfoBean deviceInfoBean) {
        boolean isOpen = false;
        if (deviceInfoBean != null) {
            if (TextUtils.isEmpty(deviceInfoBean.getGroupId())) {
                isOpen = getCommonFunSingleDpData(deviceInfoBean);
            } else {
                List<DeviceInfoBean> infoBeans = CacheDataManager.getInstance().getGroupDeviceListData(deviceInfoBean.getGroupId());
                if (infoBeans == null || infoBeans.isEmpty()) {
                    return false;
                }
                for (DeviceInfoBean info : infoBeans) {
                    isOpen = getCommonFunSingleDpData(info);
                    if (isOpen) {
                        break;
                    }
                }
            }

        }

        return isOpen;
    }

    /**
     * 单设备 常用功能是否存在
     */
    public boolean getCommonFunSingleDpData(DeviceInfoBean deviceInfoBean) {
        boolean isOpen = true;
        if (deviceInfoBean == null || deviceInfoBean.getStockDpInfoVOList() == null || deviceInfoBean.getStockDpInfoVOList().isEmpty()) {
            LgUtils.w(TAG + "  getCommonFunSingleDpData 开关dp点数据为空");
            return isOpen;
        }
        return isOpen;
    }

    /**
     * 如果是快捷开关
     * {
     * "id":"45lkj3551234001",//消息ID
     * "ts":1626197189,//时间戳（秒）
     * "code":"property_change",//通知编码
     * "data":{
     * "devices":[
     * {
     * "deviceId":"设备ID",
     * "properties":{
     * "color":"red",  //颜色  红色
     * "brightness":80 //亮度  80
     * }
     * }
     * ]
     * }//数据结构体
     * }
     */
    public List<DeviceInfoBean> updateDeviceModelData(List<DeviceInfoBean> data, String reportMqttData) {
        List<DeviceInfoBean> tempData = new ArrayList<>();
        List<DeviceInfoBean> tempDataAll = new ArrayList<>();
        List<DeviceInfoBean> allDeviceList = CacheDataManager.getInstance().getAllDeviceList(CacheDataManager.getInstance().getCurrentHomeId());
        try {
            JSONObject dpDataBean = new JSONObject(reportMqttData);
            JSONArray devices = dpDataBean.getJSONArray(Property.DEVICES_KEY);
            for (int i = 0; i < devices.length(); i++) {
                JSONObject jsonObject = devices.getJSONObject(i);
                if (allDeviceList != null && !allDeviceList.isEmpty() && jsonObject.has(Property.DEVICE_ID_KEY)) {
                    for (DeviceInfoBean item : allDeviceList) {
                        if (!item.isCustomGroup() && TextUtils.equals(item.getId(), jsonObject.getString(Property.DEVICE_ID_KEY))) {
                            List<DeviceDpBean> stockDpInfoVOList = item.getStockDpInfoVOList();
                            //常用功能
                            if (stockDpInfoVOList != null && !stockDpInfoVOList.isEmpty()) {
                                deviceModelData(stockDpInfoVOList, jsonObject);
                            }
                            List<DeviceDpBean> switchDpInfoVOList = item.getSwitchDpInfoVOList();
                            //快捷开关
                            if (switchDpInfoVOList != null && !switchDpInfoVOList.isEmpty()) {
                                deviceModelData(switchDpInfoVOList, jsonObject);
                            }
                            /// break;
                        }

                    }
                    for (DeviceInfoBean item : data) {
                        if (!item.isCustomGroup() && TextUtils.equals(item.getId(), jsonObject.getString(Property.DEVICE_ID_KEY))) {
                            List<DeviceDpBean> stockDpInfoVOList = item.getStockDpInfoVOList();
                            //常用功能
                            if (stockDpInfoVOList != null && !stockDpInfoVOList.isEmpty()) {
                                deviceModelData(stockDpInfoVOList, jsonObject);
                            }
                            List<DeviceDpBean> switchDpInfoVOList = item.getSwitchDpInfoVOList();
                            //快捷开关
                            if (switchDpInfoVOList != null && !switchDpInfoVOList.isEmpty()) {
                                deviceModelData(switchDpInfoVOList, jsonObject);
                            }
                            /// break;
                        }

                    }
                }
            }
            if (allDeviceList != null && !allDeviceList.isEmpty()) {
                for (DeviceInfoBean item : allDeviceList) {
                    if (item.isCustomGroup()) {
                        GroupBean groupDeviceInfo = CacheDataManager.getInstance().getGroupDeviceInfo(item.getGroupId());
                        if (groupDeviceInfo != null) {
                            item = CacheDataManager.getInstance().getGroupDeviceSingleData(groupDeviceInfo, allDeviceList);
                        }
                    }
                    tempDataAll.add(item);
                }
            }
            if (data != null && !data.isEmpty()) {
                for (DeviceInfoBean item : data) {
                    if (item.isCustomGroup()) {
                        GroupBean groupDeviceInfo = CacheDataManager.getInstance().getGroupDeviceInfo(item.getGroupId());
                        if (groupDeviceInfo != null) {
                            item = CacheDataManager.getInstance().getGroupDeviceSingleData(groupDeviceInfo, allDeviceList);
                        }
                    }
                    tempData.add(item);
                }
            }

            CacheDataManager.getInstance().saveAllDeviceList(CacheDataManager.getInstance().getCurrentHomeId(), tempDataAll);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tempData;
    }


    /**
     * 获取mqtt发送的设备id
     */
    public List<String> getMqttByDevIdList(String reportMqttData) {
        List<String> listDevId = new ArrayList<>();
        try {
            JSONObject dpDataBean = new JSONObject(reportMqttData);
            JSONArray devices = dpDataBean.getJSONArray(Property.DEVICES_KEY);
            for (int i = 0; i < devices.length(); i++) {
                JSONObject jsonObject = devices.getJSONObject(i);
                if (jsonObject.has(Property.DEVICE_ID_KEY)) {
                    listDevId.add(jsonObject.getString(Property.DEVICE_ID_KEY));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listDevId;
    }

    private void deviceModelData(List<DeviceDpBean> stockDpInfoVOList, JSONObject jsonObject) throws Exception {
        for (DeviceDpBean deviceDpBean : stockDpInfoVOList) {
            JSONObject properties = jsonObject.getJSONObject(Property.PROPERTIES_KEY);
            Iterator<String> keys = properties.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                if (TextUtils.equals(deviceDpBean.getKey(), next)) {
                    deviceDpBean.setValue(properties.get(next));
                }
            }
        }
    }

    /**
     * 根据mqtt上报的设备属性()开关状态，修改对应设备的开关状态
     */
    public List<Integer> getUpdateDeviceStatusList(String mqttData, List<DeviceInfoBean> currentDeviceList) {
        List<Integer> positionList = new ArrayList<>();
        List<String> mqttByDevIdList = MqttConvertManager.getInstance().getMqttByDevIdList(mqttData);
        List<DeviceInfoBean> deviceInfoBeans = MqttConvertManager.getInstance().updateDeviceModelData(currentDeviceList, mqttData);
        for (String devId : mqttByDevIdList) {
            for (int i = 0; i < deviceInfoBeans.size(); i++) {
                DeviceInfoBean infoBean = deviceInfoBeans.get(i);
                //单设备
                if (TextUtils.isEmpty(infoBean.getGroupId()) && TextUtils.equals(devId, infoBean.getId())) {
                    if (positionList.contains(i)) {
                        continue;
                    }
                    positionList.add(i);
                }
                //根据设备id，获取包含所有设备id的群组
                List<String> groupIds = CacheDataManager.getInstance().getDevIdByGroupIds(devId);
                if (groupIds != null && !groupIds.isEmpty()) {
                    for (String groupId : groupIds) {
                        if (TextUtils.equals(infoBean.getGroupId(), groupId)) {
                            if (positionList.contains(i)) {
                                continue;
                            }
                            positionList.add(i);
                        }
                    }
                }
            }
        }
        LgUtils.w(TAG + " getUpdateDeviceStatusList  需要修改的设备状态位置集合  positionList=" + new Gson().toJson(positionList));
        return positionList;
    }

    /**
     * 网关子设备解绑，发送数据给面板
     */
    public void sendGateWaySubDeviceUnBindNotifyPanel(String gatewayId) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Property.PAGE_KEY, "gateway-device-setting");
            jsonObject.put(Property.GATEWAY_ID_KEY, gatewayId);
            WritableMap writableMap = BundleJSONConverter.jsonToReact(jsonObject);
            LgUtils.i(TAG + "   sendGateWaySubDeviceUnBindNotifyPanel   解绑网关子设备 发数据给rn --> " + jsonObject);
            EventManager.getInstance().sendData(RNConstant.GATEWAY_SUB_DEVICE_UNBIND, writableMap);
        } catch (Exception e) {
            e.printStackTrace();
            LgUtils.i(TAG + "   sendGateWaySubDeviceUnBindNotifyPanel   解绑网关子设备 --> 发数据给rn --> " + e.getMessage());
        }
    }

    private String createJsonData(@Nullable String deviceId, Object trigger, Object properties) {
        EventPayload.Device device = new EventPayload.Device();
        device.setDevId(deviceId);
        if (trigger != null) {
            device.setTrigger(trigger);
        }
        if (properties != null) {
            device.setProperties(properties);
        }
        return createJsonData(device);
    }

    private String createJsonData(EventPayload.Device device) {
        List<EventPayload.Device> devices = new ArrayList<>();
        devices.add(device);
        return createJsonData(devices);
    }

    private String createJsonData(List<EventPayload.Device> devices) {
        EventPayload mEventPayload = new EventPayload();
        EventPayload.ItemData data1 = new EventPayload.ItemData();
        data1.setDevices(devices);
        mEventPayload.setData(data1);
        mEventPayload.setTs(System.currentTimeMillis() / 1000);
        mEventPayload.setMsgId(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 18));
        return new Gson().toJson(mEventPayload);
    }
}
