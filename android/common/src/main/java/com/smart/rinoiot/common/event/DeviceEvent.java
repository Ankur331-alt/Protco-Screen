package com.smart.rinoiot.common.event;

/**
 * Copyright (C) BlakeQu All Rights Reserved <blakequ@gmail.com>
 * <p/>
 * Licensed under the blakequ.com License, Version 1.0 (the "License");
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * author  : quhao <blakequ@gmail.com> <br>
 * date     : 2016/8/24 14:37 <br>
 * last modify author : <br>
 * version : 1.0 <br>
 * description:
 */
public class DeviceEvent {

    private Type type;
    private Object obj;
    private String msg;
    private int arg1;

    public DeviceEvent(Type type) {
        this.type = type;
    }

    public DeviceEvent(Type type, Object obj) {
        this.obj = obj;
        this.type = type;
    }

    public DeviceEvent(Type type, Object obj, String msg) {
        this.obj = obj;
        this.type = type;
        this.msg = msg;
    }

    public DeviceEvent(Type type, int arg1) {
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

    public enum Type {
        BIND_SUCCESS,
        BIND_FAIL,

        /**
         * 下发开关状态
         */
        SWITCH_STATUS,
        OTA_UPGRADE,
        /**
         * 从面板进入配网，wifi配网成功
         */
        PANEL_WIFI_CONFIG_SUCCESS,

        /**
         * 我的目录扫一扫配网设备
         */
        MINE_SCAN_BIND_DEVICE,
        CHANGE_FAMILY,

        /**
         * 当群组设备为空时移除群组
         */
        REMOVE_GROUP,

        /**
         * 操作功能点
         */
        DP_OPERATOR,
        DEVICE_CHANGED,

        DEVICE_INFO_CHANGED,
        NFC_ADD_DEVICE,
        DEVICE_REMOVE,
        DEVICE_BIND_SUCCESS,

        CHANGE_FAMILY_NEW,
    }
}
