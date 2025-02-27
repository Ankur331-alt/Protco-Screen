package com.smart.rinoiot.common.datastore.persistence;

import androidx.annotation.NonNull;

import com.smart.rinoiot.common.utils.StringUtil;

/**
 * @author edwin
 */
public class UnifiedDeviceStateBuilder {
    private final String deviceId;
    private boolean online;
    private int hue;
    private int brightness;
    private int colorTemperature;
    private double temperature;
    private int humidity;
    private boolean switchOne;
    private boolean switchTwo;
    private boolean switchThree;
    private boolean switchFour;

    private boolean liked;

    public UnifiedDeviceStateBuilder(@NonNull String deviceId){
        this.deviceId = deviceId;
    }

    public UnifiedDeviceStateBuilder(@NonNull UnifiedDeviceState unifiedDeviceState){
        this.deviceId = unifiedDeviceState.getDeviceId();
        this.online = unifiedDeviceState.isOnline();
        this.hue = unifiedDeviceState.getHue();
        this.brightness =unifiedDeviceState.getBrightness();
        this.colorTemperature = unifiedDeviceState.getColorTemperature();
        this.temperature = unifiedDeviceState.getTemperature();
        this.humidity = unifiedDeviceState.getHumidity();
        this.switchOne = unifiedDeviceState.isSwitchOne();
        this.switchTwo = unifiedDeviceState.isSwitchTwo();
        this.switchThree = unifiedDeviceState.isSwitchThree();
        this.switchFour = unifiedDeviceState.isSwitchFour();
        this.liked = unifiedDeviceState.isLiked();
    }

    public UnifiedDeviceStateBuilder setOnline(Boolean online) {
        this.online = online;
        return this;
    }

    public UnifiedDeviceStateBuilder setHue(Integer hue) {
        this.hue = hue;
        return this;
    }

    public UnifiedDeviceStateBuilder setBrightness(Integer brightness) {
        this.brightness = brightness;
        return this;
    }

    public UnifiedDeviceStateBuilder setColorTemperature(Integer colorTemperature) {
        this.colorTemperature = colorTemperature;
        return this;
    }

    public UnifiedDeviceStateBuilder setTemperature(Double temperature) {
        this.temperature = temperature;
        return this;
    }

    public UnifiedDeviceStateBuilder setHumidity(Integer humidity) {
        this.humidity = humidity;
        return this;
    }

    public UnifiedDeviceStateBuilder setSwitchOne(Boolean switchOne) {
        this.switchOne = switchOne;
        return this;
    }

    public UnifiedDeviceStateBuilder setSwitchTwo(Boolean switchTwo) {
        this.switchTwo = switchTwo;
        return this;
    }

    public UnifiedDeviceStateBuilder setSwitchThree(Boolean switchThree) {
        this.switchThree = switchThree;
        return this;
    }

    public UnifiedDeviceStateBuilder setSwitchFour(Boolean switchFour) {
        this.switchFour = switchFour;
        return this;
    }

    public UnifiedDeviceStateBuilder setLiked(boolean liked) {
        this.liked = liked;
        return this;
    }

    public UnifiedDeviceState build(){
        if(StringUtil.isBlank(this.deviceId)){
            throw new IllegalArgumentException("Device identifier cannot be blank");
        }

        return  new UnifiedDeviceState(
            this.deviceId, this.online, this.hue,
            this.brightness, this.colorTemperature,
            this.temperature, this.humidity,
            this.switchOne, this.switchTwo,
            this.switchThree, this.switchFour, this.liked
        );
    }
}
