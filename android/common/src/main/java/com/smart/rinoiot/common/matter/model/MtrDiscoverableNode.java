package com.smart.rinoiot.common.matter.model;

import com.dsh.matter.model.scanner.DeviceSharePayload;

import java.io.Serializable;

/**
 * @author edwin
 */
public class MtrDiscoverableNode implements Serializable {

    private String deviceId;

    private DeviceSharePayload payload;

    public MtrDiscoverableNode(String deviceId, DeviceSharePayload payload) {
        this.deviceId = deviceId;
        this.payload = payload;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public DeviceSharePayload getPayload() {
        return payload;
    }

    public void setPayload(DeviceSharePayload payload) {
        this.payload = payload;
    }
}
