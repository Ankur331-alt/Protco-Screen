package com.smart.rinoiot.common.rn;

import com.facebook.react.bridge.Promise;
import com.smart.rinoiot.common.BleConfigConstant;
import com.smart.rinoiot.common.base.BaseApplication;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.ble.BleScanConnectManager;
import com.smart.rinoiot.common.manager.UserInfoManager;
import com.smart.rinoiot.common.mqtt2.Manager.MqttManager;
import com.smart.rinoiot.common.mqtt2.Manager.TopicManager;
import com.smart.rinoiot.common.mqtt2.payload.BleCloudManager;
import com.smart.rinoiot.common.mqtt2.payload.EventPayload;
import com.smart.rinoiot.common.utils.LgUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author tw
 * @time 2022/11/15 20:44
 * @description 单设备数据交互 面板、app、设备交互
 */
public class SingleDeviceManager {
    private static final String TAG = SingleDeviceManager.class.getSimpleName();
    private static SingleDeviceManager instance;

    public static SingleDeviceManager getInstance() {
        if (instance == null) {
            instance = new SingleDeviceManager();
        }
        return instance;
    }

    /**
     * 单设备数据交互
     */
    public void dataInteraction(DeviceInfoBean deviceInfoBean, List<EventPayload.Device> devices, Promise promise) {
        if(null == deviceInfoBean) {
            return;
        }

        //在线状态（0离线，1在线）
        if (deviceInfoBean.getOnlineStatus() == 0) {
            try {
                /// String dataJson = MqttManager.getInstance().createJsonData(devices);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", BleConfigConstant.THING_PROPERTY_SET);
                LgUtils.i("bluetooth devicePublish --> data = " + jsonObject.toString());
                BleScanConnectManager.getInstance().sendMessage(deviceInfoBean.getUuid(), jsonObject.toString(), data1 -> {
                    if (promise!=null) {
                        promise.resolve(true);
                    }
                }, null );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (deviceInfoBean.getOnlineStatus() == 1) {
            sendPanelInteraction(deviceInfoBean, devices);
            if (promise!=null) {
                promise.resolve(true);
            }
        }
    }

    /**
     * wifi或蓝牙设备与面板数据交互
     * 在线状态（0离线(ble设备)，1在线）
     */
    public void sendPanelInteraction(DeviceInfoBean deviceInfoBean, List<EventPayload.Device> devices) {
        byte[] bytes = getCloudBleData(deviceInfoBean, devices);
        if (deviceInfoBean == null) {
            return;
        }

        LgUtils.w(TAG + "  设备与面板交互   deviceInfoBean.getOnlineStatus()=" + deviceInfoBean.getOnlineStatus());
        if (deviceInfoBean.getOnlineStatus() == 0) {
            MqttManager.getInstance()
                    .publish(TopicManager.publishCloudDpToDevice(deviceInfoBean.getUuid()), bytes);
        } else {
            String userId = UserInfoManager.getInstance()
                    .getUserInfo(BaseApplication.getApplication()).id;
            MqttManager.getInstance()
                    .publish(TopicManager.publishDpToDevice(userId), bytes);
        }
    }

    /**
     * 设备上报云端数据
     */
    public byte[] getCloudBleData(DeviceInfoBean deviceInfoBean, List<EventPayload.Device> devices) {
        byte[] bytes = null;
        if (deviceInfoBean != null) {
            //蓝牙设备数据格式
            if (deviceInfoBean.getOnlineStatus() == 0) {
                if (devices != null && !devices.isEmpty()) {
                    bytes = BleCloudManager.getInstance().getCloudBleData(devices);
                }
                /// MqttManager.getInstance().publish(TopicManager.publishCloudDpToDevice(deviceInfoBean.getUuid()), bytes);
            } else {
                // wifi设备数据格式
                /// if (devices != null && !devices.isEmpty()) {
                ///    bytes = MqttManager.getInstance().getCloudWifiData(devices);
                /// }
                /// else {
                ///    bytes = MqttManager.getInstance().createJsonData(deviceInfoBean.getId(), object, null).getBytes();
                /// }
            }
        }
        return bytes;

    }
}
