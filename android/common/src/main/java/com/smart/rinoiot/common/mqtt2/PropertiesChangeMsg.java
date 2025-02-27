package com.smart.rinoiot.common.mqtt2;

import java.io.Serializable;
import java.util.List;

/**
 * @author edwin
 */
public class PropertiesChangeMsg implements Serializable {
    private final List<DeviceProperties> devices;

    public PropertiesChangeMsg(List<DeviceProperties> devices) {
        this.devices = devices;
    }

    public List<DeviceProperties> getDevices() {
        return devices;
    }
}
