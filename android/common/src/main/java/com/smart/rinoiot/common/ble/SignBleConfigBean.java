package com.smart.rinoiot.common.ble;

import java.io.Serializable;

/**
 * @author tw
 * @time 2022/10/18 15:17
 * @description 单蓝牙设备配网，需要通过蓝牙协议获取设备信息
 */
public class SignBleConfigBean implements Serializable {
    private String version;
    private String msgType;
    private String pid;
    private boolean bind;
    private int gateway;
    private int gatewayType;
    private String wifi_mac;
    private String ble_mac;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
    }

    public int getGateway() {
        return gateway;
    }

    public void setGateway(int gateway) {
        this.gateway = gateway;
    }

    public int getGatewayType() {
        return gatewayType;
    }

    public void setGatewayType(int gatewayType) {
        this.gatewayType = gatewayType;
    }

    public String getWifi_mac() {
        return wifi_mac;
    }

    public void setWifi_mac(String wifi_mac) {
        this.wifi_mac = wifi_mac;
    }

    public String getBle_mac() {
        return ble_mac;
    }

    public void setBle_mac(String ble_mac) {
        this.ble_mac = ble_mac;
    }
}
