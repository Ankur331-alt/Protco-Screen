package com.smart.rinoiot.common.bean;

import java.io.Serializable;

/**
 * @author tw
 * @time 2022/11/14 16:38
 * @description 创建或者编辑群组
 */
public class CreateGroupBean implements Serializable {
    private String roomName;//房间名称
    private String imageUrl;//群组设备图标
    private String deviceName;//群组设备名称
    private String devId;//设备id
    private String assetId;//资产id
    private boolean isCurrentRoomFlag;

    public boolean isCurrentRoomFlag() {
        return isCurrentRoomFlag;
    }

    public void setCurrentRoomFlag(boolean currentRoomFlag) {
        isCurrentRoomFlag = currentRoomFlag;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }
}
