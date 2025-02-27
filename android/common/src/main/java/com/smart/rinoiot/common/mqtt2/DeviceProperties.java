package com.smart.rinoiot.common.mqtt2;

import java.io.Serializable;
import java.util.Map;

/**
 * @author edwin
 */
public class DeviceProperties implements Serializable {
    private final String deviceId;
    private final Map<String, Object> properties;

    public DeviceProperties(String deviceId, Map<String, Object> properties) {
        this.deviceId = deviceId;
        this.properties = properties;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
