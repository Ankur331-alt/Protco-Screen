package com.smart.rinoiot.common.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MultiDeviceDpBean implements Serializable {
    private Map<String, List<DeviceDpBean>> dpValueMapping;

    public Map<String, List<DeviceDpBean>> getDpValueMapping() {
        return dpValueMapping;
    }

    public void setDpValueMapping(Map<String, List<DeviceDpBean>> dpValueMapping) {
        this.dpValueMapping = dpValueMapping;
    }
}
