package com.smart.rinoiot.common.bean;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public class BluetoothControlBean {
    private String address;
    private BluetoothGatt gatt;
    private String uuid;
    private BluetoothGattCharacteristic characteristic;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BluetoothGatt getGatt() {
        return gatt;
    }

    public void setGatt(BluetoothGatt gatt) {
        this.gatt = gatt;
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }
}
