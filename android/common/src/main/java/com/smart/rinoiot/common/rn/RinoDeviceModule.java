package com.smart.rinoiot.common.rn;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.ble.BleScanConnectManager;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.mqtt2.Manager.MqttConvertManager;
import com.smart.rinoiot.common.mqtt2.payload.EventPayload;
import com.smart.rinoiot.common.utils.LgUtils;
import com.smart.rinoiot.common.utils.PageActivityPathUtils;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @ProjectName: Rino
 * @Package: com.smart.rino.hybrid.modules
 * @ClassName: RinoDeviceModule
 * @Description: java类作用描述
 * @Author: ZhangStar
 * @Emali: ZhangStar666@gmail.com
 * @CreateDate: 2022/9/14 15:57
 * @UpdateUser: 更新者：
 * @UpdateDate: 2022/9/14 15:57
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class RinoDeviceModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private static final String TAG = "RinoDeviceModule";

    public RinoDeviceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        if (reactContext != null) {
            reactContext.addLifecycleEventListener(this);
        }
    }

    @NonNull
    @Override
    public String getName() {
        return "RinoDeviceModule";
    }

    /**
     * 下发数据给到设备，待补充逻辑
     *
     * @param data    具体类型和和王严同步
     * @param promise callback
     */
    @ReactMethod
    void devicePublish(ReadableArray data, Promise promise) {
        if (data==null||data.size()==0){
            promise.resolve(false);
            return;
        }

        String devId = data.getMap(0).getString("deviceId");
        if (TextUtils.isEmpty(devId)) {
            promise.resolve(false);
            return;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<EventPayload.Device>>() {}.getType();
        List<EventPayload.Device> devices = gson.fromJson(gson.toJson(data.toArrayList()), type);
        Log.d(TAG, "devicePublish: " + devId + " | devices=" + new Gson().toJson(devices));
        //单设备
        if (devices.size() == 1) {
            DeviceInfoBean deviceInfoBean = CacheDataManager.getInstance().getDeviceInfo(devId);
            if (null == deviceInfoBean) {
                promise.resolve(false);
                return;
            }

            //特殊情况下，群组设备中只有一个设备时，
            if (deviceInfoBean.isCustomGroup()) {
                GroupDeviceManager.getInstance().dataInteraction(devices, promise);
            } else {
                if(null == getCurrentActivity()) {
                    promise.resolve(false);
                    return;
                }

                MqttConvertManager.getInstance().panelDevicePropertiesSendPublish(
                        getCurrentActivity(),
                        data,
                        new CallbackListener<Boolean>() {
                            @Override
                            public void onSuccess(Boolean data) {
                                promise.resolve(data);
                            }

                            @Override
                            public void onError(String code, String error) {
                                promise.resolve(false);
                            }
                        }
                );
            }
        } else if (devices.size() > 1) {
            //群组设备
            GroupDeviceManager.getInstance().dataInteraction(devices, promise);
        }
    }

    /**
     * 面板中双模设备wifi配网
     * 场景：WIFI+蓝牙设备，蓝牙配网成功，进入面板，在进行wifi配网
     *
     * @param promise callback
     */
    @ReactMethod
    void deviceActivatorConnectCloud(String deviceId, Promise promise) {
        if (TextUtils.isEmpty(deviceId)) {
            LgUtils.w("面板中双模设备wifi配网   设备数据为空");
            return;
        }
        if (getCurrentActivity() == null) {
            LgUtils.w("面板中双模设备wifi配网   getCurrentActivity=null");
            return;
        }
        Intent intent = new Intent();
        intent.setClassName(getCurrentActivity(), PageActivityPathUtils.ADD_DEVICE_ACTIVITY);
        intent.putExtra(Constant.PANEL_CONFIG_TYPE, true);
        intent.putExtra(
                Constant.PANEL_CONFIG_DATA,
                new Gson().toJson(CacheDataManager.getInstance().getPanelWifiConfigData(deviceId))
        );
        getCurrentActivity().startActivity(intent);
        promise.resolve(true);
    }

    /**
     * 设备蓝牙连接
     *
     * @param promise callback
     */
    @ReactMethod
    void deviceBleConnect(String deviceId, Promise promise) {
        if (TextUtils.isEmpty(deviceId)) {
            LgUtils.w("设备蓝牙连接   设备数据为空");
            return;
        }

        DeviceInfoBean deviceInfo = CacheDataManager.getInstance().getDeviceInfo(deviceId);
        if (deviceInfo == null) {
            LgUtils.w("设备蓝牙连接   设备数据为空");
            return;
        }
        BleScanConnectManager.getInstance()
                .sendMessage(
                        deviceInfo.getUuid(),
                        "", null, null
                );
        promise.resolve(true);
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {

    }

}
