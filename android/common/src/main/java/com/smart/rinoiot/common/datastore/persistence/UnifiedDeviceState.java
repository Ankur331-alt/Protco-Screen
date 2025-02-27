package com.smart.rinoiot.common.datastore.persistence;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * @author edwin
 * Immutable class for unified device states
 */
@Entity(tableName = "unified_device_states")
public class UnifiedDeviceState {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "device_id")
    private final String deviceId;

    @ColumnInfo(name = "online")
    private final boolean online;

    @ColumnInfo(name = "hue")
    private final int hue;

    @ColumnInfo(name = "brightness")
    private final int brightness;

    @ColumnInfo(name = "color_temperature")
    private final int colorTemperature;

    @ColumnInfo(name = "temperature")
    private final double temperature;

    @ColumnInfo(name = "humidity")
    private final int humidity;

    @ColumnInfo(name = "switch_1")
    private final boolean switchOne;

    @ColumnInfo(name = "switch_2")
    private final boolean switchTwo;

    @ColumnInfo(name = "switch_3")
    private final boolean switchThree;

    @ColumnInfo(name = "switch_4")
    private final boolean switchFour;

    @ColumnInfo(name = "liked")
    private final boolean liked;

    /**
     * The main constructor
     * @param deviceId the device identifier
     * @param online whether the device is online.
     * @param hue the device color
     * @param brightness the device brightness
     * @param colorTemperature the device color temperature
     * @param temperature the device temperature
     * @param humidity the device humidity
     * @param switchOne the switch value for position one
     * @param switchTwo the switch value for position two
     * @param switchThree the switch value for position three
     * @param switchFour the switch value for position four
     */
    public UnifiedDeviceState(
            @NonNull String deviceId,
            boolean online, int hue,
            int brightness, int colorTemperature,
            double temperature, int humidity,
            boolean switchOne, boolean switchTwo,
            boolean switchThree, boolean switchFour, boolean liked
    ) {
        this.deviceId = deviceId;
        this.online = online;
        this.hue = hue;
        this.brightness = brightness;
        this.colorTemperature = colorTemperature;
        this.temperature = temperature;
        this.humidity = humidity;
        this.switchOne = switchOne;
        this.switchTwo = switchTwo;
        this.switchThree = switchThree;
        this.switchFour = switchFour;
        this.liked = liked;
    }

    /**
     * This will take care of default cases
     * @param deviceId the device identifier
     * @param online whether the device is online
     */
    @Ignore
    public UnifiedDeviceState(@NonNull String deviceId, boolean online) {
        this.deviceId = deviceId;
        this.online = online;
        this.hue = 0;
        this.brightness = 0;
        this.colorTemperature = 0;
        this.temperature = 0;
        this.humidity = 0;
        this.switchOne = false;
        this.switchTwo = false;
        this.switchThree = false;
        this.switchFour = false;
        this.liked = false;
    }

    /**
     * This should be useful when dealing with those pesky switches.
     * @param deviceId the device identifier
     * @param online whether the device is online
     * @param switchOne the switch value for position one
     * @param switchTwo the switch value for position two
     * @param switchThree the switch value for position three
     * @param switchFour the switch value for position four
     */
    @Ignore
    public UnifiedDeviceState(
            @NonNull String deviceId,
            boolean online, boolean switchOne,
            boolean switchTwo, boolean switchThree, boolean switchFour
    ) {
        this.deviceId = deviceId;
        this.online = online;
        this.hue = 0;
        this.brightness = 0;
        this.colorTemperature = 0;
        this.temperature = 0;
        this.humidity = 0;
        this.switchOne = switchOne;
        this.switchTwo = switchTwo;
        this.switchThree = switchThree;
        this.switchFour = switchFour;
        this.liked = false;
    }

    /**
     * This will come in handy for lights
     *
     * @param deviceId the device identifier
     * @param online whether the device is online
     * @param hue the device color
     * @param brightness the brightness
     * @param colorTemperature the color temperature
     * @param switchOne the switch status
     */
    @Ignore
    public UnifiedDeviceState(
            @NonNull String deviceId,
            boolean online, int hue,
            int brightness, int colorTemperature,
            boolean switchOne
    ) {
        this.deviceId = deviceId;
        this.online = online;
        this.hue = hue;
        this.brightness = brightness;
        this.colorTemperature = colorTemperature;
        this.switchOne = switchOne;
        this.temperature = 0;
        this.humidity = 0;
        this.switchTwo = false;
        this.switchThree = false;
        this.switchFour = false;
        this.liked = false;
    }

    @NonNull
    public String getDeviceId() {
        return deviceId;
    }

    public boolean isOnline(){
        return online;
    }

    public int getHue() {
        return hue;
    }

    public int getBrightness() {
        return brightness;
    }

    public int getColorTemperature() {
        return colorTemperature;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public boolean isSwitchOne() {
        return switchOne;
    }

    public boolean isSwitchTwo() {
        return switchTwo;
    }

    public boolean isSwitchThree() {
        return switchThree;
    }

    public boolean isSwitchFour() {
        return switchFour;
    }

    public boolean isLiked() {
        return liked;
    }
}
