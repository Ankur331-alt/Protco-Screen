package com.smart.rinoiot.common.ble;

import android.bluetooth.BluetoothGattCharacteristic;

import com.smart.rinoiot.ftms.BleRssiDevice;

/**
 * @author tw
 * @time 2022/10/31 10:45
 * @description 蓝牙设备连接监听
 */
public interface ConnectStatusListener {
    void connectionState(String uuid,int connectStatus);//连接状态

    void notifyChanged(String uuid, BluetoothGattCharacteristic characteristic);//蓝牙数据变化

    void notifySuccess(String uuid,BleRssiDevice device);//当前蓝牙设备绑定通知成功

    void connectFail(String uuid);//连接失败
}
