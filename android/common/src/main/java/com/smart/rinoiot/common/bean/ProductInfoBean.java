package com.smart.rinoiot.common.bean;

/**
 *
 */
public class ProductInfoBean extends CategoryBean.SupportProtocol {
    /**
     * 设备UUID
     */
    private String deviceUuid;
    /**
     * 设备pid
     */
    private String devicePid;
    /**
     * 设备是否绑定 1 绑定；0: 未绑定  绑定成功则为非配网状态bit7置1，其他情况都在配网状态
     */
    private boolean canBind;
    /**
     * 加密类型
     */
    private int encryptType;
    /**
     * 自定义属性
     */
    private int timeout;
    /**
     * 自定义属性
     */
    private String address;
    /**
     * uuid
     */
    private String uuid;
    /**
     * 0：不显示是否可配网标题；1:可配网；2、不可配网
     */
    private int configStatusType;

    /**
     * 是否配网失败 状态
     */
    private String configFailTips;

    /**
     * 图片链接
     */
    private String imageUrl;

    /**蓝牙设备配网连接状态  DISCONNECT = 0  CONNECTING = 1  CONNECTED = 2*/
    private int connectStatus;

    /**
     *（1： 通用固件， 0：非通用固件）
     */
    private int universalFirmware;

    /**
     * 配网模式（1=wifi+ble,2=wifi，3=ble,4=二维码，5=条形码）
     */
    private int distributionNetMode;

    /**
     * 0:未配网；1、配网中；2、等待配网中
     */
    private int networkType;

    public int getNetworkType() {
        return networkType;
    }

    public void setNetworkType(int networkType) {
        this.networkType = networkType;
    }

    public int getDistributionNetMode() {
        return distributionNetMode;
    }

    public void setDistributionNetMode(int distributionNetMode) {
        this.distributionNetMode = distributionNetMode;
    }

    public int getUniversalFirmware() {
        return universalFirmware;
    }

    public void setUniversalFirmware(int universalFirmware) {
        this.universalFirmware = universalFirmware;
    }

    public int getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(int connectStatus) {
        this.connectStatus = connectStatus;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getConfigStatusType() {
        return configStatusType;
    }

    public void setConfigStatusType(int configStatusType) {
        this.configStatusType = configStatusType;
    }


    public String getDeviceUuid() {
        return deviceUuid;
    }

    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
    }

    public String getDevicePid() {
        return devicePid;
    }

    public void setDevicePid(String devicePid) {
        this.devicePid = devicePid;
    }


    public boolean isCanBind() {
        return canBind;
    }

    public void setCanBind(boolean canBind) {
        this.canBind = canBind;
    }

    public int getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(int encryptType) {
        this.encryptType = encryptType;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getConfigFailTips() {
        return configFailTips;
    }

    public void setConfigFailTips(String configFailTips) {
        this.configFailTips = configFailTips;
    }
}
