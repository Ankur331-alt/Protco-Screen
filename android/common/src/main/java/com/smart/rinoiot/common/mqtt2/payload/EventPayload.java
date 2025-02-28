package com.smart.rinoiot.common.mqtt2.payload;

import java.util.ArrayList;
import java.util.List;

public class EventPayload {
    private String msgId;
    private long ts;
    private ItemData data;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public ItemData getData() {
        return data;
    }

    public void setData(ItemData data) {
        this.data = data;
    }

    public static class ItemData {
        private List<Device> devices = new ArrayList<>();

        public List<Device> getDevices() {
            return devices;
        }

        public void setDevices(List<Device> devices) {
            this.devices = devices;
        }
    }

    public static class Device {
        private String deviceId;
        private String gatewayId;
        private Object properties;
        private Object trigger;
        private Object header;

        public Object getHeader() {
            return header;
        }

        public void setHeader(Object header) {
            this.header = header;
        }

        public String getDevId() {
            return deviceId;
        }

        public void setDevId(String devId) {
            this.deviceId = devId;
        }

        public Object getProperties() {
            return properties;
        }

        public void setProperties(Object properties) {
            this.properties = properties;
        }

        public Object getTrigger() {
            return trigger;
        }

        public void setTrigger(Object trigger) {
            this.trigger = trigger;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getGatewayId() {
            return gatewayId;
        }

        public void setGatewayId(String gatewayId) {
            this.gatewayId = gatewayId;
        }
    }
}
