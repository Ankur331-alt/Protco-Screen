package com.smart.rinoiot.common.device;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author edwin
 */
public class UnifiedDataPoint implements Serializable {
    public static final UnifiedDataPoint UNI_UNKNOWN_DP = new UnifiedDataPoint("unknown");
    public static final UnifiedDataPoint UNI_ONLINE_DP = new UnifiedDataPoint("online");
    public static final UnifiedDataPoint UNI_COLOR_DP = new UnifiedDataPoint("color");
    public static final UnifiedDataPoint UNI_SWITCH_DP = new UnifiedDataPoint("switch");
    public static final UnifiedDataPoint UNI_SWITCH_1_DP = new UnifiedDataPoint("switch_1");
    public static final UnifiedDataPoint UNI_SWITCH_2_DP = new UnifiedDataPoint("switch_2");
    public static final UnifiedDataPoint UNI_SWITCH_3_DP = new UnifiedDataPoint("switch_3");
    public static final UnifiedDataPoint UNI_SWITCH_4_DP = new UnifiedDataPoint("switch_4");
    public static final UnifiedDataPoint UNI_COLOR_TEMP_DP = new UnifiedDataPoint("color_temp");
    public static final UnifiedDataPoint UNI_BRIGHTNESS_DP = new UnifiedDataPoint("brightness");

    private final String value;

    public UnifiedDataPoint(String value) {
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
        return Objects.hash(value);
    }
}
