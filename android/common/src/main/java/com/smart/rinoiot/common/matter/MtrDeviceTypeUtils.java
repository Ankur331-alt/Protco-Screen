package com.smart.rinoiot.common.matter;

import com.dsh.matter.model.device.DeviceType;
import com.dsh.matter.model.device.StateAttribute;
import com.smart.rinoiot.common.device.UnifiedDataPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author edwin
 */
public class MtrDeviceTypeUtils {

    /**
     * Device types and supported control attributes
     */
    private static final Map<StateAttribute, List<Long>> DEVICE_TYPE_CONTROL_ATTRIBUTES = new HashMap<>();

    static {
        // Map device types to control attributes
        DEVICE_TYPE_CONTROL_ATTRIBUTES.put(StateAttribute.Color, getColorTypes());
        DEVICE_TYPE_CONTROL_ATTRIBUTES.put(StateAttribute.Switch, getSwitchTypes());
        DEVICE_TYPE_CONTROL_ATTRIBUTES.put(StateAttribute.Brightness, getBrightnessTypes());
        DEVICE_TYPE_CONTROL_ATTRIBUTES.put(StateAttribute.ColorTemperature, getColorTemperatureTypes());
    }

    /**
     * Getter for the device types that supports switch commands
     *
     * @return the list of device types
     */
    private static List<Long> getSwitchTypes(){
        List<Long> types = new ArrayList<>();
        types.add(DeviceType.Socket.getType());
        types.add(DeviceType.OnOffLightSwitch.getType());
        types.add(DeviceType.OnOffLight.getType());
        types.add(DeviceType.ExtendedColorLight.getType());
        types.add(DeviceType.DimmableLight.getType());
        types.add(DeviceType.ColorTemperatureLight.getType());
        types.add(DeviceType.ColorDimmerSwitch.getType());
        return types;
    }

    /**
     * Getter for the device types that supports brightness commands
     *
     * @return the list of device types
     */
    private static List<Long> getBrightnessTypes(){
        List<Long> types = new ArrayList<>();
        types.add(DeviceType.ExtendedColorLight.getType());
        types.add(DeviceType.DimmableLight.getType());
        types.add(DeviceType.ColorTemperatureLight.getType());
        types.add(DeviceType.ColorDimmerSwitch.getType());
        return types;
    }

    /**
     * Getter for the device types that supports color types
     *
     * @return the list of device types
     */
    private static List<Long> getColorTypes(){
        List<Long> types = new ArrayList<>();
        types.add(DeviceType.ExtendedColorLight.getType());
        types.add(DeviceType.ColorTemperatureLight.getType());
        types.add(DeviceType.ColorDimmerSwitch.getType());
        return types;
    }

    /**
     * Getter for device types that supports color temperature commands
     *
     * @return the list of device types
     */
    private static List<Long> getColorTemperatureTypes(){
        List<Long> switchTypes = new ArrayList<>();
        switchTypes.add(DeviceType.ColorTemperatureLight.getType());
        switchTypes.add(DeviceType.ColorDimmerSwitch.getType());
        switchTypes.add(DeviceType.ExtendedColorLight.getType());
        return switchTypes;
    }

    /**
     * Checks if device supports the command
     * @param deviceType matter device type
     * @param attribute the control attribute
     * @return true if it does, false otherwise.
     */
    @SuppressWarnings("all")
    public static boolean supportsCommand(long deviceType, StateAttribute attribute){
        if(0 == deviceType || null == attribute){
            return false;
        }

        if(!DEVICE_TYPE_CONTROL_ATTRIBUTES.containsKey(attribute)){
            return false;
        }

        List<Long> types = DEVICE_TYPE_CONTROL_ATTRIBUTES.get(attribute);
        if(null == types){
            return false;
        }

        return types.contains(deviceType);
    }

    /**
     * Returns the data points that mimics the WiFi device's data points
     * @param deviceType matter
     * @return the data points that mimics the WiFi device's data points
     */
    public static Set<UnifiedDataPoint> getUnifiedDataPoints(long deviceType) {
        if(0 == deviceType){
            return new HashSet<>();
        }

        Set<UnifiedDataPoint> dataPoints = new HashSet<>();
        dataPoints.add(UnifiedDataPoint.UNI_ONLINE_DP);
        if(supportsCommand(deviceType, StateAttribute.Switch)) {
            dataPoints.add(UnifiedDataPoint.UNI_SWITCH_1_DP);
        }
        if(supportsCommand(deviceType, StateAttribute.Color)){
            dataPoints.add(UnifiedDataPoint.UNI_COLOR_DP);
        }
        if(supportsCommand(deviceType, StateAttribute.ColorTemperature)) {
            dataPoints.add(UnifiedDataPoint.UNI_COLOR_TEMP_DP);
        }
        if(supportsCommand(deviceType, StateAttribute.Brightness)){
            dataPoints.add(UnifiedDataPoint.UNI_BRIGHTNESS_DP);
        }
        return dataPoints;
    }
}
