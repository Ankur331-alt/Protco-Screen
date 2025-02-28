package com.smart.rinoiot.common.rn;

import com.facebook.react.bridge.Promise;
import com.smart.rinoiot.common.base.BaseApplication;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.mqtt2.Manager.MqttManager;
import com.smart.rinoiot.common.mqtt2.Manager.TopicManager;
import com.smart.rinoiot.common.mqtt2.payload.EventPayload;

import java.util.List;

/**
 * @author tw
 * @time 2022/11/15 20:44
 * @description 群组设备数据交互 面板、app、设备交互
 */
public class GroupDeviceManager {
    private static final String TAG = GroupDeviceManager.class.getSimpleName();
    private static GroupDeviceManager instance;

    public static GroupDeviceManager getInstance() {
        if (instance == null) {
            instance = new GroupDeviceManager();
        }
        return instance;
    }

    /**
     * 群组设备数据交互
     */
    public void dataInteraction(List<EventPayload.Device> devices, Promise promise) {
        sendPanelInteraction(devices);
        if (promise != null) promise.resolve(true);
    }

    /**
     * wifi或蓝牙设备与面板数据交互
     * 在线状态（0离线(ble设备)，1在线）
     */
    public void sendPanelInteraction(List<EventPayload.Device> devices) {
        byte[] bytes = getCloudBleData(devices);
        MqttManager.getInstance().publish(TopicManager.publishDpToDevice(UserInfoManager.getInstance().getUserInfo(
                BaseApplication.getApplication()).id), bytes);
    }

    /**
     * 设备上报云端数据
     */
    public byte[] getCloudBleData(List<EventPayload.Device> devices) {
        byte[] bytes = null;
        if (devices != null && !devices.isEmpty()) {
            bytes = MqttManager.getInstance().getCloudWifiData(devices);
        }
        return bytes;

    }
}
