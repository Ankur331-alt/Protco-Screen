package com.smart.rinoiot.common.device;

import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.Metadata;
import com.smart.rinoiot.common.datastore.persistence.UnifiedDeviceState;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.matter.MtrDeviceDataUtils;
import com.smart.rinoiot.common.matter.MtrDeviceTypeUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author edwin
 */
public class DeviceDataPoint {

    private static final String TAG = "DeviceDataPoint";

    /**
     * Gets the unified data points
     *
     * @param deviceInfo the device information.
     * @return the set of device data points.
     */
    public static Set<UnifiedDataPoint> getUnifiedDataPoints(DeviceInfoBean deviceInfo) {
        if(MtrDeviceDataUtils.isMatterDevice(deviceInfo)){
            Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfo.getMetaInfo());
            if(MtrDeviceDataUtils.isEmpty(metadata)){
                return new HashSet<>();
            }
            return MtrDeviceTypeUtils.getUnifiedDataPoints(metadata.getDeviceType());
        }else{
            List<DeviceDpBean> deviceDpData = CacheDataManager.getInstance()
                    .getDeviceDpList(deviceInfo.getId());
            if(null == deviceDpData || deviceDpData.isEmpty()) {
                return new HashSet<>();
            }
            Set<String> dataPoints = deviceDpData.stream()
                    .map(DeviceDpBean::getKey).collect(Collectors.toSet());
            if(dataPoints.isEmpty()){
                return new HashSet<>();
            }
            return toUnifiedDataPoints(dataPoints);
        }
    }

    /**
     * Converts data points to unified data points
     * @param dataPoints the data points
     * @return the unified data points
     */
    private static Set<UnifiedDataPoint> toUnifiedDataPoints(Set<String> dataPoints){
        if(null == dataPoints || dataPoints.isEmpty()) {
            return new HashSet<>();
        }
        return dataPoints.stream().map(dp -> {
            if (dp.contentEquals(RinoDataPoint.RINO_COLOR_TEMP_DP.getValue())) {
                return UnifiedDataPoint.UNI_COLOR_TEMP_DP;
            } else if (dp.contentEquals(RinoDataPoint.RINO_BRIGHTNESS_DP.getValue())) {
                return UnifiedDataPoint.UNI_BRIGHTNESS_DP;
            } else if (dp.contentEquals(RinoDataPoint.RINO_COLOR_DP.getValue())) {
                return UnifiedDataPoint.UNI_COLOR_DP;
            } else if (dp.contentEquals(RinoDataPoint.RINO_COLOUR_DATA_DP.getValue())) {
                return UnifiedDataPoint.UNI_COLOR_DP;
            } else if(dp.contentEquals(RinoDataPoint.RINO_SWITCH_DP.getValue())) {
                return UnifiedDataPoint.UNI_SWITCH_1_DP;
            } else if(dp.contentEquals(RinoDataPoint.RINO_SWITCH_1_DP.getValue())){
                return UnifiedDataPoint.UNI_SWITCH_1_DP;
            } else if(dp.contentEquals(RinoDataPoint.RINO_SWITCH_2_DP.getValue())){
                return UnifiedDataPoint.UNI_SWITCH_2_DP;
            } else if(dp.contentEquals(RinoDataPoint.RINO_SWITCH_3_DP.getValue())){
                return UnifiedDataPoint.UNI_SWITCH_3_DP;
            } else if(dp.contentEquals(RinoDataPoint.RINO_SWITCH_4_DP.getValue())){
                return UnifiedDataPoint.UNI_SWITCH_4_DP;
            } else if(dp.contentEquals(RinoDataPoint.RINO_SWITCH_LED_DP.getValue())) {
                return UnifiedDataPoint.UNI_SWITCH_1_DP;
            } else if(dp.contentEquals(RinoDataPoint.RINO_BRIGHT_VALUE_DP.getValue())) {
                return UnifiedDataPoint.UNI_BRIGHTNESS_DP;
            } else if(dp.contentEquals(RinoDataPoint.RINO_ONLINE_DP.getValue())){
                return UnifiedDataPoint.UNI_ONLINE_DP;
            }
            return UnifiedDataPoint.UNI_UNKNOWN_DP;
        }).filter(unifiedDataPoint ->
                unifiedDataPoint != UnifiedDataPoint.UNI_UNKNOWN_DP
        ).collect(Collectors.toSet());
    }

    public static Map<UnifiedDataPoint, Object> toUnifiedDataPoints(UnifiedDeviceState states) {
        if (null == states) {
            return new HashMap<>(0);
        }

        Map<UnifiedDataPoint, Object> unifiedDataPoints = new HashMap<>(9);
        unifiedDataPoints.put(UnifiedDataPoint.UNI_BRIGHTNESS_DP, states.getBrightness());
        unifiedDataPoints.put(UnifiedDataPoint.UNI_SWITCH_1_DP, states.isSwitchOne());
        unifiedDataPoints.put(UnifiedDataPoint.UNI_SWITCH_2_DP, states.isSwitchTwo());
        unifiedDataPoints.put(UnifiedDataPoint.UNI_SWITCH_3_DP, states.isSwitchThree());
        unifiedDataPoints.put(UnifiedDataPoint.UNI_SWITCH_4_DP, states.isSwitchFour());
        unifiedDataPoints.put(UnifiedDataPoint.UNI_COLOR_DP, states.getHue());
        unifiedDataPoints.put(UnifiedDataPoint.UNI_COLOR_TEMP_DP, states.getColorTemperature());
        unifiedDataPoints.put(UnifiedDataPoint.UNI_ONLINE_DP, states.isOnline());
        return unifiedDataPoints;
    }

    public static boolean getOnlineStatus(Map<UnifiedDataPoint, Object> unifiedDataPoints){
        if(null == unifiedDataPoints || unifiedDataPoints.isEmpty()){
            return false;
        }

        // get the online status
        return (boolean) Optional.ofNullable(unifiedDataPoints.getOrDefault(
                UnifiedDataPoint.UNI_ONLINE_DP, false
        )).orElse(false);
    }

    public static boolean getPower(Map<UnifiedDataPoint, Object> unifiedDataPoints) {
        if(null == unifiedDataPoints || unifiedDataPoints.isEmpty()){
            return false;
        }

        return (boolean) Optional.ofNullable(unifiedDataPoints.getOrDefault(
                UnifiedDataPoint.UNI_SWITCH_1_DP, false
        )).orElse(false);
    }

    public static boolean isOnline(Map<UnifiedDataPoint, Object> unifiedDataPoints) {
        if(null == unifiedDataPoints || unifiedDataPoints.isEmpty()){
            return false;
        }

        return (boolean) Optional.ofNullable(unifiedDataPoints.getOrDefault(
                UnifiedDataPoint.UNI_ONLINE_DP, false
        )).orElse(false);
    }

    public static boolean[] getSwitches(Map<UnifiedDataPoint, Object> unifiedDataPoints) {
        if(null == unifiedDataPoints || unifiedDataPoints.isEmpty()){
            return new boolean[]{false, false, false, false};
        }

        boolean switchOne = (boolean) Optional.ofNullable(unifiedDataPoints.getOrDefault(
                UnifiedDataPoint.UNI_SWITCH_1_DP, false
        )).orElse(false);

        boolean switchTwo = (boolean) Optional.ofNullable(unifiedDataPoints.getOrDefault(
                UnifiedDataPoint.UNI_SWITCH_2_DP, false
        )).orElse(false);

        boolean switchThree = (boolean) Optional.ofNullable(unifiedDataPoints.getOrDefault(
                UnifiedDataPoint.UNI_SWITCH_3_DP, false
        )).orElse(false);

        boolean switchFour = (boolean) Optional.ofNullable(unifiedDataPoints.getOrDefault(
                UnifiedDataPoint.UNI_SWITCH_4_DP, false
        )).orElse(false);
        return new boolean[]{switchOne, switchTwo, switchThree, switchFour};
    }

    public static int getColor(Map<UnifiedDataPoint, Object> unifiedDataPoints){
        if(null == unifiedDataPoints || unifiedDataPoints.isEmpty()){
            return 0;
        }
        // get the color
        return  (int) Optional.ofNullable(unifiedDataPoints.getOrDefault(
                UnifiedDataPoint.UNI_COLOR_DP, 0
        )).orElse(0);
    }

    public static int getBrightness(Map<UnifiedDataPoint, Object> unifiedDataPoints){
        if(null == unifiedDataPoints || unifiedDataPoints.isEmpty()){
            return 0;
        }

        // get the brightness
        return (int) Optional.ofNullable(unifiedDataPoints.getOrDefault(
                UnifiedDataPoint.UNI_BRIGHTNESS_DP, 0
        )).orElse(0);
    }

    public static int getColorTemperature(Map<UnifiedDataPoint, Object> unifiedDataPoints){
        if(null == unifiedDataPoints || unifiedDataPoints.isEmpty()){
            return 0;
        }

        // get the color temperature
        return (int)Optional.ofNullable(unifiedDataPoints.getOrDefault(
                UnifiedDataPoint.UNI_COLOR_TEMP_DP, 0
        )).orElse(0);
    }
}
