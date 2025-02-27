package com.smart.rinoiot.common.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author tw
 * @time 2022/11/14 13:52
 * @description 首页设备和群组列表实体类
 */
public class DeviceGroupBean implements Serializable {
    private List<DeviceInfoBean> deviceList;
    private List<GroupBean> groupList;

    public List<DeviceInfoBean> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<DeviceInfoBean> deviceList) {
        this.deviceList = deviceList;
    }

    public List<GroupBean> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<GroupBean> groupList) {
        this.groupList = groupList;
    }
}
