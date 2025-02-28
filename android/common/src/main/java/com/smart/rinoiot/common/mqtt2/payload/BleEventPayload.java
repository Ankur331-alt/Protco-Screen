package com.smart.rinoiot.common.mqtt2.payload;

public class BleEventPayload {
    private String msgId;
    private long ts;
    private Object data;

    /**
     * 可选，app替设备上报时加上此字段1
     */
    private int appReport;

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getAppReport() {
        return appReport;
    }

    public void setAppReport(int appReport) {
        this.appReport = appReport;
    }
}
