package com.smart.rinoiot.common.listener;

/**
 * @author tw
 * @time 2023/2/13 18:45
 * @description 多设备配网设备，判断当前连接数，默认5个，超过5，需要等待某个配设备配网状态在进行
 */
public interface MultiDevDisNetworkListener {
    void bleConnectCount(int connectCount);
}
