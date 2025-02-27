package com.smart.rinoiot.common.listener;

/**
 * @author tw
 * @time 2023/2/13 18:45
 * @description 单设备配网设备，多次点击设备超过最大连接数时
 */
public interface SingleDevMultiDisNetworkListener {
    void singleDeviceBleConnectCount(String uuid);
}
