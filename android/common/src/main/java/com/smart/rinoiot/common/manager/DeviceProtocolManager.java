package com.smart.rinoiot.common.manager;

import android.text.TextUtils;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.bean.DeviceInfoBean;

/**
 * @author tw
 * @time 2023/2/23 17:18
 * @description 设备通信协议管理类
 */
public class DeviceProtocolManager {
    private static DeviceProtocolManager instance;

    public static DeviceProtocolManager getInstance() {
        if (instance == null) {
            instance = new DeviceProtocolManager();
        }
        return instance;
    }

    /**
     * 设备是否设置蓝牙协议
     */
    public boolean supportBleProtocol(DeviceInfoBean deviceInfoBean) {
        boolean isSupport = false;
        if ((TextUtils.equals(deviceInfoBean.getProtocolType(), Constant.PROTOCOL_TYPE_FOR_WIFI_BLE) ||
                TextUtils.equals(deviceInfoBean.getProtocolType(), Constant.PROTOCOL_TYPE_FOR_BLUETOOTH_MESH)
                ||TextUtils.equals(deviceInfoBean.getProtocolType(), Constant.PROTOCOL_TYPE_FOR_SINGLE_BLUETOOTH)
        ) && (deviceInfoBean.getDistributionNetMode() == 1 || deviceInfoBean.getDistributionNetMode() == 3)) {
            isSupport = true;
        }
        return isSupport;
    }

    /**
     * 设备是否设置蓝牙协议
     */
    public boolean supportGroup(DeviceInfoBean deviceInfoBean) {
        boolean isSupport = true;
        if ((TextUtils.equals(deviceInfoBean.getProtocolType(), Constant.PROTOCOL_TYPE_FOR_WIFI_BLE) ||
                TextUtils.equals(deviceInfoBean.getProtocolType(), Constant.PROTOCOL_TYPE_FOR_BLUETOOTH_MESH)
                ||TextUtils.equals(deviceInfoBean.getProtocolType(), Constant.PROTOCOL_TYPE_FOR_SINGLE_BLUETOOTH)
        ) && deviceInfoBean.getBindMode()==1||deviceInfoBean.getIsGroup() == 0) {
            isSupport = false;
        }
        return isSupport;
    }
}
