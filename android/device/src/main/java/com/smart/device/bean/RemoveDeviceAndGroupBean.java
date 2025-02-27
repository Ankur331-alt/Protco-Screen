package com.smart.device.bean;

import com.smart.rinoiot.common.bean.DeviceInfoBean;

import java.io.Serializable;

/**
 * @author tw
 * @time 2022/11/15 10:58
 * @description
 */
public class RemoveDeviceAndGroupBean implements Serializable {
    private boolean isRemoveSuccess;
    private DeviceInfoBean deviceInfoBean;

    public boolean isRemoveSuccess() {
        return isRemoveSuccess;
    }

    public void setRemoveSuccess(boolean removeSuccess) {
        isRemoveSuccess = removeSuccess;
    }

    public DeviceInfoBean getDeviceInfoBean() {
        return deviceInfoBean;
    }

    public void setDeviceInfoBean(DeviceInfoBean deviceInfoBean) {
        this.deviceInfoBean = deviceInfoBean;
    }


}
