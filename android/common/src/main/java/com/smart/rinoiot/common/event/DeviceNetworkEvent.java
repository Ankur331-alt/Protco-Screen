package com.smart.rinoiot.common.event;

/**
 * 设备配网事件通知类
 */
public class DeviceNetworkEvent {

    private Type type;
    private Object obj;
    private String msg;
    private int arg1;
    private String deviceUuid;//多设备配网时，设备uuid作为唯一标识去修改对应的状态

    public DeviceNetworkEvent(Type type) {
        this.type = type;
    }

    public DeviceNetworkEvent(Type type, Object obj) {
        this.obj = obj;
        this.type = type;
    }

    public DeviceNetworkEvent(Type type, Object obj, String msg) {
        this.obj = obj;
        this.type = type;
        this.msg = msg;
    }
    public DeviceNetworkEvent(Type type, Object obj, String msg, String deviceUuid) {
        this.obj = obj;
        this.type = type;
        this.msg = msg;
        this.deviceUuid = deviceUuid;
    }

    public DeviceNetworkEvent(Type type, int arg1) {
        this.type = type;
        this.arg1 = arg1;
    }

    public int getArg1() {
        return arg1;
    }

    public void setArg1(int arg1) {
        this.arg1 = arg1;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getDeviceUuid() {
        return deviceUuid;
    }

    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
    }

    public static enum Type {
        BIND_SUCCESS,
        BIND_FAIL
    }
}
