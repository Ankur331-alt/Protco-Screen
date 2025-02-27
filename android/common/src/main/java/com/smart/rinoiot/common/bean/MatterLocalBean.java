package com.smart.rinoiot.common.bean;

import java.io.Serializable;

/**
 * @author tw
 * @time 2023/4/7 14:07
 * @description matter设备本地数据保存
 */
public class MatterLocalBean implements Serializable {
    private boolean Switch;//开关状态
    private boolean Online;//是否在线
    private double Brightness;//亮度
    private MatterColor Color;//颜色值

    private double ColorTemperature;//色温

    public boolean isSwitch() {
        return Switch;
    }

    public void setSwitch(boolean aSwitch) {
        Switch = aSwitch;
    }

    public boolean isOnline() {
        return Online;
    }

    public void setOnline(boolean online) {
        Online = online;
    }

    public double getBrightness() {
        return Brightness;
    }

    public void setBrightness(double brightness) {
        Brightness = brightness;
    }

    public MatterColor getColor() {
        return Color;
    }

    public void setColor(MatterColor color) {
        Color = color;
    }

    public double getColorTemperature() {
        return ColorTemperature;
    }

    public void setColorTemperature(double colorTemperature) {
        ColorTemperature = colorTemperature;
    }

    public static class MatterColor implements Serializable {
        private double hue;//色调
        private double saturation;//饱和度
        private double value;//色温

        public double getHue() {
            return hue;
        }

        public void setHue(double hue) {
            this.hue = hue;
        }

        public double getSaturation() {
            return saturation;
        }

        public void setSaturation(double saturation) {
            this.saturation = saturation;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }
}
