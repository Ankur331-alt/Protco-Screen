package com.smart.rinoiot.family.bean;

import com.smart.rinoiot.common.bean.DeviceInfoBean;

import java.io.Serializable;

/**
 * @author tw
 * @time 2022/11/8 16:01
 * @description 房间管理
 */
public class RoomDeviceBean implements Serializable {
    private boolean isCurrentRoomFlag;
    private String roomName;
    private DeviceInfoBean deviceInfoBean;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public DeviceInfoBean getDeviceInfoBean() {
        return deviceInfoBean;
    }

    public void setDeviceInfoBean(DeviceInfoBean deviceInfoBean) {
        this.deviceInfoBean = deviceInfoBean;
    }

    public boolean isCurrentRoomFlag() {
        return isCurrentRoomFlag;
    }

    public void setCurrentRoomFlag(boolean currentRoomFlag) {
        isCurrentRoomFlag = currentRoomFlag;
    }
}
