package com.smart.rinoiot.core.ble.callback;


import com.smart.rinoiot.core.ble.model.BleDevice;

/**
 * Created by LiuLei on 2018/6/2.
 */

public abstract class BleMtuCallback<T> {

    public void onMtuChanged(BleDevice device, int mtu, int status){}

}
