package com.smart.rinoiot.common.device;

import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * @author edwin
 */
public class RinoDataPoint {

    public static final RinoDataPoint RINO_SWITCH_DP = new RinoDataPoint("switch");

    public static final RinoDataPoint RINO_ONLINE_DP = new RinoDataPoint("online");
    public static final RinoDataPoint RINO_MTR_SWITCH_DP = new RinoDataPoint("matter_switch");
    public static final RinoDataPoint RINO_SWITCH_LED_DP = new RinoDataPoint("switch_led");
    public static final RinoDataPoint RINO_SWITCH_1_DP = new RinoDataPoint("switch_1");
    public static final RinoDataPoint RINO_SWITCH_2_DP = new RinoDataPoint("switch_2");
    public static final RinoDataPoint RINO_SWITCH_3_DP = new RinoDataPoint("switch_3");
    public static final RinoDataPoint RINO_SWITCH_4_DP = new RinoDataPoint("switch_4");
    public static final RinoDataPoint RINO_COLOR_DP = new RinoDataPoint("color");
    public static final RinoDataPoint RINO_BRIGHTNESS_DP = new RinoDataPoint("brightness");
    public static final RinoDataPoint RINO_BRIGHT_VALUE_DP = new RinoDataPoint("bright_value");
    public static final RinoDataPoint RINO_COLOUR_DATA_DP = new RinoDataPoint("colour_data");
    public static final RinoDataPoint RINO_PAINT_COLOUR_DP = new RinoDataPoint("paint_colour_data");
    public static final RinoDataPoint RINO_COLOR_TEMP_DP = new RinoDataPoint("temp_value");

    public static final RinoDataPoint RINO_SPIN_SWITCH_DP = new RinoDataPoint("spin_switch");
    public static final RinoDataPoint RINO_CAMERA_TILT_START_DP = new RinoDataPoint("ptz_control");
    public static final RinoDataPoint RINO_CAMERA_TILT_STOP_DP = new RinoDataPoint("ptz_stop");

    private final String value;

    public RinoDataPoint(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(null == obj){
            return false;
        }

        if(!(obj instanceof UnifiedDataPoint)) {
            return false;
        }

        String that = ((UnifiedDataPoint) obj).getValue();
        return value.contentEquals(that);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
