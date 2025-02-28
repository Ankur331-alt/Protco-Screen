package com.smart.rinoiot.common.mqtt2;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;

public class DevicePropertiesFuture {
    private final String deviceId;
    private Disposable mDisposable;
    private final Map<String, Object> mProperties = new HashMap<>();

    public DevicePropertiesFuture(DeviceProperties properties, Disposable disposable) {
        this.deviceId = properties.getDeviceId();
        this.mProperties.putAll(properties.getProperties());
        this.mDisposable = disposable;
    }

    public void addProperties(DeviceProperties properties){
        this.mProperties.putAll(properties.getProperties());
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Disposable getDisposable() {
        return mDisposable;
    }

    public Map<String, Object> getProperties() {
        return mProperties;
    }

    public void updateDisposable(Disposable disposable) {
        if(this.mDisposable != null && !this.mDisposable.isDisposed()){
            this.mDisposable.dispose();
        }
        this.mDisposable = disposable;
    }
}
