package com.smart.rinoiot.common.manager;

import com.facebook.react.bridge.WritableMap;
import com.google.gson.Gson;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.event.DeviceEvent;
import com.smart.rinoiot.common.rn.BundleJSONConverter;
import com.smart.rinoiot.common.rn.RNConstant;
import com.smart.rinoiot.common.utils.LgUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.List;

/**
 * @author tw
 * @time 2022/10/13 17:23
 * @description 设备信息发生变化是通知面板
 */
public class UpdateDeviceInfoManager {
    private static final String TAG = UpdateDeviceInfoManager.class.getSimpleName();
    private static UpdateDeviceInfoManager instance;
    private boolean panelWifiConfigFlag;//从面板进入wifi配网，配网成功，只需要发送wifi通知，蓝牙状态通知不需要发送

    public void setPanelWifiConfigFlag(boolean panelWifiConfigFlag) {
        this.panelWifiConfigFlag = panelWifiConfigFlag;
    }

    public boolean isPanelWifiConfigFlag() {
        return panelWifiConfigFlag;
    }

    public static UpdateDeviceInfoManager getInstance() {
        if (instance == null) {
            instance = new UpdateDeviceInfoManager();
        }
        return instance;
    }

    /**
     * 修改名称 发送消息给面板
     */
    public void sendPanelRenameNotice(String name) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            WritableMap writableMap = BundleJSONConverter.jsonToReact(jsonObject);
            LgUtils.i(TAG + "    设备修改名称 --> 发数据给rn --> name:" + name);
            EventManager.getInstance().sendData(RNConstant.DEVICE_INFO_UPDATE, writableMap);
        } catch (Exception e) {
            LgUtils.i(TAG + "    设备修改名称 --> 发数据给rn --> error:" + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 从面板进入wifi配网 发送消息给面板   0=单蓝牙，1=WiFi未连接，2=WiFi已连接
     */
    public void sendPanelNetworkTypeNotice() {
        EventBus.getDefault().post(new DeviceEvent(DeviceEvent.Type.PANEL_WIFI_CONFIG_SUCCESS, "panel_wifi_Config"));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("networkType", 2);
            jsonObject.put("isOnline", true);
            jsonObject.put("bleConnectStatus", 0);
            WritableMap writableMap = BundleJSONConverter.jsonToReact(jsonObject);
            LgUtils.i(TAG + "    从面板进入wifi配网 --> 发数据给rn -->");
            EventManager.getInstance().sendData(RNConstant.DEVICE_INFO_UPDATE, writableMap);
        } catch (Exception e) {
            LgUtils.i(TAG + "    从面板进入wifi配网 --> 发数据给rn --> error:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 进入面板，设备蓝牙连接状态
     *
     * @param status 0=未连接，1=连接中，2=已连接
     *               BluetoothProfile.STATE_DISCONNECTED = 0;BluetoothProfile.STATE_CONNECTING = 1;
     *               BluetoothProfile.STATE_CONNECTED = 2;BluetoothProfile.STATE_DISCONNECTING = 3;
     */
    public void sendPanelBleConnectStatusNotice(int status) {
        LgUtils.i(TAG + "    设备蓝牙连接状态 --> 发数据给rn -->panelWifiConfigFlag=" + panelWifiConfigFlag);
        if (panelWifiConfigFlag) return;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bleConnectStatus", status >= 3 ? 1 : status);
            WritableMap writableMap = BundleJSONConverter.jsonToReact(jsonObject);
            LgUtils.i(TAG + "    设备蓝牙连接状态 --> 发数据给rn -->status=" + status);
            EventManager.getInstance().sendData(RNConstant.DEVICE_INFO_UPDATE, writableMap);
        } catch (Exception e) {
            LgUtils.i(TAG + "    设备蓝牙连接状态 --> 发数据给rn --> error:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 常用功能 数据通过消息发送给面板
     */
    public void sendPanelCommonFunNotice(String devId, List<DeviceDpBean> dpBeanList) {
        JSONObject jsonObject = new JSONObject();
        try {
            LgUtils.i(TAG + "    常用功能 --> 发数据给rn -->devId=" + devId + "    dpBeanList=" + dpBeanList);
            jsonObject.put("id", devId);
            jsonObject.put("dataPointJson", new Gson().toJson(dpBeanList));
            WritableMap writableMap = BundleJSONConverter.jsonToReact(jsonObject);
            EventManager.getInstance().sendData(RNConstant.SHORT_CUT_DATA_NOTIFY, writableMap);
        } catch (Exception e) {
            LgUtils.i(TAG + "     常用功能 --> 发数据给rn --> error:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
