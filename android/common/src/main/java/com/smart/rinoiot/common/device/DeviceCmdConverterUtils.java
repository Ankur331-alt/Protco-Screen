package com.smart.rinoiot.common.device;

import android.util.Log;

import com.google.gson.Gson;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.datastore.DataSourceManager;
import com.smart.rinoiot.common.datastore.persistence.UnifiedDeviceState;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.utils.StringUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.reactivex.schedulers.Schedulers;

/**
 * @author edwin
 */
public class DeviceCmdConverterUtils {

    private static final String TAG = "DeviceCmdConverterUtils";

    private static final int MIN_COLOR_TEMP_K = 2700;

    private static final int MAX_COLOR_TEMP_K = 6500;

    private static final int PAINT_COLOUR_LEN = 22;

    private static final int COLOR_DATA_LEN = 12;

    private static final int PAINT_COLOUR_TEMP_LEN = 18;

    /**
     * Color work mode.
     */
    private static final String PAINT_COLOUR_C_MODE_PREFIX = "0001000f00";

    /**
     * White work mode.
     */
    private static final String PAINT_COLOUR_W_MODE_PREFIX = "0000000f00";

    /**
     * Mixed work mode.
     */
    private static final String PAINT_COLOUR_M_MODE_PREFIX = "0003000f00";

    /**
     * Getter for the device's data points
     *
     * @param deviceId device identifier
     * @return the device's data points
     */
    private static Set<String> getDataPointKeys(String deviceId){
        if(null == deviceId){
            return new HashSet<>();
        }

        List<DeviceDpBean> deviceDpData = CacheDataManager.getInstance().getDeviceDpList(deviceId);
        if(deviceDpData.isEmpty()) {
            return new HashSet<>();
        }

        return deviceDpData.stream().map(DeviceDpBean::getKey).collect(Collectors.toSet());
    }

    /**
     * Generates the power command for control over Mqtt
     *
     * @param deviceId device identifier
     * @param power power status
     * @return the command data
     */
    public static Map<String, Object> toPowerCmd(String deviceId, boolean power){
        Map<String, Object> command = new HashMap<>(1);
        Set<String> dataPoints = getDataPointKeys(deviceId);
        if(dataPoints.isEmpty()) {
            return command;
        }

        List<String> switchDataPoints = Arrays.asList(
                RinoDataPoint.RINO_SWITCH_DP.getValue(),
                RinoDataPoint.RINO_SWITCH_1_DP.getValue(),
                RinoDataPoint.RINO_SWITCH_2_DP.getValue(),
                RinoDataPoint.RINO_SWITCH_3_DP.getValue(),
                RinoDataPoint.RINO_SWITCH_4_DP.getValue(),
                RinoDataPoint.RINO_SWITCH_LED_DP.getValue()
        );

        Predicate<String> switchPredicate = switchDataPoints::contains;
        dataPoints.stream()
                .filter(switchPredicate).forEach(dataPoint -> command.put(dataPoint, power));
        return command;
    }

    /**
     * Generates the power command for control over Mqtt
     *
     * @param deviceId device identifier
     * @param power power status
     * @param position switch position
     * @return the command data
     */
    public static Map<String, Object> toPowerCmd(String deviceId, boolean power, int position ){
        Map<String, Object> command = new HashMap<>(1);
        Set<String> dataPoints = getDataPointKeys(deviceId);
        if(dataPoints.isEmpty()) {
            return command;
        }

        Predicate<String> predicate = dp -> dp.contentEquals(RinoDataPoint.RINO_SWITCH_DP.getValue());
        switch (position) {
            case 0:
                predicate = dp -> dp.contentEquals(RinoDataPoint.RINO_SWITCH_1_DP.getValue());
                break;
            case 1:
                predicate = dp -> dp.contentEquals(RinoDataPoint.RINO_SWITCH_2_DP.getValue());
                break;
            case 2:
                predicate = dp -> dp.contentEquals(RinoDataPoint.RINO_SWITCH_3_DP.getValue());
                break;
            case 3:
                predicate = dp -> dp.contentEquals(RinoDataPoint.RINO_SWITCH_4_DP.getValue());
                break;
            default:
                break;
        }

        Optional<String> match = dataPoints.stream()
                .filter(predicate)
                .findFirst();

        match.ifPresent(dp -> command.put(dp, power));
        return command;
    }

    /**
     * Generates the brightness command for control over Mqtt
     *
     * @param deviceId device identifier
     * @param brightness power status
     * @return the command data
     */
    public static Map<String, Object> toBrightnessCmd(String deviceId, int brightness){
        Map<String, Object> command = new HashMap<>(1);
        DeviceDpBean dataPoint = getBrightnessDp(deviceId);
        if(null == dataPoint){
            return command;
        }

        if(dataPoint.getKey().contentEquals(RinoDataPoint.RINO_BRIGHT_VALUE_DP.getValue())){
            command.put(dataPoint.getKey(), (brightness * 10));
        }else{
            command.put(dataPoint.getKey(), brightness);
        }
        return command;
    }

    @SuppressWarnings("all")
    public static int toHue(String colorData){
        if(null == colorData){
            return -1;
        }

        if(colorData.length() == COLOR_DATA_LEN){
            int hue = Integer.parseInt(colorData.substring(0, 4), 16);
            return hue;
        } else if(colorData.length() == PAINT_COLOUR_TEMP_LEN){
            String prefix = colorData.substring(0, 10);
        }else if(colorData.length() >= PAINT_COLOUR_LEN){
            int hue = Integer.parseInt(colorData.substring(10, 14), 16);
            return hue;
        }
        return -1;
    }

    /**
     * Checks if the paint data is for white mode.
     * @param paintData paint data
     * @return true if it is, false otherwise.
     */
    public static boolean isWhiteModePaint(String paintData) {
        if(StringUtil.isBlank(paintData)){
            return false;
        }

        if(paintData.length() != PAINT_COLOUR_TEMP_LEN){
            return false;
        }

        return paintData.startsWith(PAINT_COLOUR_W_MODE_PREFIX);
    }

    /**
     * Extracts the color temperature from paint data.
     * @param paintData the paint data.
     * @return true on success false otherwise.
     */
    public static int toColorTemperature(String paintData) {
        if(!isWhiteModePaint(paintData)){
            return -1;
        }

        String colorTemp = paintData.substring(paintData.length() - 4);
        int temperature = Integer.parseInt(colorTemp, 16);
        Log.d(TAG, "toColorTemperature: temperature=" + temperature);
        return temperature;
    }

    /**
     * Retrieves the device current brightness
     * @param deviceId device identifier
     * @return the current brightness
     */
    private static DeviceDpBean getBrightnessDp(String deviceId) {
        List<DeviceDpBean> deviceDpData = CacheDataManager.getInstance().getDeviceDpList(deviceId);
        Log.d(TAG, "getBrightnessDp: device DP data = " + new Gson().toJson(deviceDpData));
        if(deviceDpData.isEmpty()) {
            return null;
        }

        Predicate<DeviceDpBean> deviceDpBeanPredicate = dp->
                dp.getKey().contains(RinoDataPoint.RINO_BRIGHTNESS_DP.getValue()) ||
                dp.getKey().contains(RinoDataPoint.RINO_BRIGHT_VALUE_DP.getValue());


        // Get actual brightness data point info
        Optional<DeviceDpBean> matchBrightness = deviceDpData.stream()
                .filter(deviceDpBeanPredicate)
                .findFirst();
        return matchBrightness.orElse(null);
    }

    /**
     * Retrieves the device's current color dp data
     *
     * @param deviceId device identifier
     * @return the current color data point
     */
    private static DeviceDpBean getColorDp(String deviceId){
        List<DeviceDpBean> deviceDpData = CacheDataManager.getInstance().getDeviceDpList(deviceId);
        Log.d(TAG, "getColorDp: device DP data = " + new Gson().toJson(deviceDpData));
        if(deviceDpData.isEmpty()) {
            return null;
        }

        Optional<DeviceDpBean> matchColor = deviceDpData.stream()
                .filter(dp-> dp.getKey().contains(RinoDataPoint.RINO_COLOR_DP.getValue()))
                .filter(dp-> !dp.getKey().contains(RinoDataPoint.RINO_COLOR_TEMP_DP.getValue()))
                .findFirst();

        if(matchColor.isPresent()){
            return matchColor.get();
        }else{
            Optional<DeviceDpBean> matchPaint = deviceDpData.stream()
                    .filter(dp-> dp.getKey().contains(RinoDataPoint.RINO_PAINT_COLOUR_DP.getValue()))
                    .findFirst();
            if(matchPaint.isPresent()) {
                return matchPaint.get();
            }else{
                Optional<DeviceDpBean> matchColour = deviceDpData.stream()
                        .filter(dp-> dp.getKey().contains(
                                RinoDataPoint.RINO_COLOUR_DATA_DP.getValue())
                        )
                        .findFirst();
                if(matchColour.isPresent()){
                    return matchColour.get();
                }
            }
        }
        return null;
    }

    /**
     * Generates the color command for control over Mqtt
     *
     * @param deviceId device identifier
     * @param hue the hue
     * @param saturation the saturation
     * @param value the value
     * @return the color command
     */
    public static Map<String, Object> toColorCmd(
            String deviceId, int hue, int saturation, int value
    ) {
        Map<String, Object> command = new HashMap<>(1);
        DeviceDpBean dataPoint = getColorDp(deviceId);
        if(null == dataPoint){
            return command;
        }

        Log.d(TAG, "toColorCmd: data point = " + new Gson().toJson(dataPoint));
        String key = dataPoint.getKey();
        if(key.contains(RinoDataPoint.RINO_COLOR_DP.getValue())){
            command.put(key, String.format("%04x%02x%02x", hue, saturation, value));
        }else if(key.contentEquals(RinoDataPoint.RINO_COLOUR_DATA_DP.getValue())){
            command.put(key, String.format("%04x%04x%04x", hue, saturation, value));
        }else{
            //The format for color command -> 0001000f0000b703e803e8
            String h = String.format("%04x", hue);
            String s = String.format("%04x", (saturation * 10));
            String v = String.format("%04x", (value * 10));
            Log.d(TAG, "toColorCmd: h=" + h + " s=" + s + " v=" + v);
            String colorValue = PAINT_COLOUR_C_MODE_PREFIX.concat(h).concat(s).concat(v);
            Log.d(TAG, "toColorCmd: command= " +colorValue);
            command.put(dataPoint.getKey(), colorValue);
        }
        Log.d(TAG, "toColorCmd: data" +  new Gson().toJson(command));
        return command;
    }

    /**
     * Generates the color temperature command for control over Mqtt
     *
     * @param deviceId the device identifier
     * @param temperature the color temperature in percentage
     * @return the color temperature command
     */
    public static Map<String, Object> toColorTemperatureCmd(String deviceId, int temperature) {
        Map<String, Object> command = new HashMap<>(1);
        Log.d(TAG, "toColorTemperatureCmd: temperature =" + temperature);
        DeviceDpBean dataPoint = getColorTempDp(deviceId);
        if(null != dataPoint) {
            // adjust temperature range
            int colorTemperature = temperature * 10;
            command.put(dataPoint.getKey(), colorTemperature);
            return command;
        }

        // invert the color temperature and adjust temperature range
        int colorTemperature = (100 - temperature) * 10;

        // try paint color data for size
        dataPoint = getColorDp(deviceId);
        if(null == dataPoint){
            return command;
        }

        String value = String.format("%04x", 1000);
        String saturation = String.format("%04x", colorTemperature);
        String colorData = String.valueOf(dataPoint.getValue());
        if(colorData.length() == PAINT_COLOUR_TEMP_LEN){
            value = colorData.substring(10, 14);
        }else if(colorData.length() == PAINT_COLOUR_LEN){
            value = colorData.substring(14, 18);
        }

        String colorTempCommand = PAINT_COLOUR_W_MODE_PREFIX.concat(value).concat(saturation);
        Log.d(TAG, "toColorTemperatureCmd: painted = " + colorTempCommand);
        command.put(dataPoint.getKey(), colorTempCommand);
        return command;
    }

    /**
     * Gets the current color temperature data point value
     *
     * @param deviceId the device identifier
     * @return the color temperature data point
     */
    private static DeviceDpBean getColorTempDp(String deviceId) {
        List<DeviceDpBean> deviceDpData = CacheDataManager.getInstance().getDeviceDpList(deviceId);
        Log.d(TAG, "getColorTempDp: device DP data = " + new Gson().toJson(deviceDpData));
        if(deviceDpData.isEmpty()) {
            return null;
        }

        Optional<DeviceDpBean> matchColorTemp = deviceDpData.stream()
                .filter(dp-> dp.getKey().contains(RinoDataPoint.RINO_COLOR_TEMP_DP.getValue()))
                .findFirst();

        return matchColorTemp.orElse(null);
    }

    /**
     * Converts kelvin to percentage
     * @param kelvin color temperature
     * @return color temperature value
     */
    public static int kelvinToPercentage(int kelvin) {
        if (kelvin < MIN_COLOR_TEMP_K) {
            kelvin = MIN_COLOR_TEMP_K;
        } else if (kelvin > MAX_COLOR_TEMP_K) {
            kelvin = MAX_COLOR_TEMP_K;
        }

        double minKelvin = (MIN_COLOR_TEMP_K * 1.0);
        double maxKelvin = (MAX_COLOR_TEMP_K * 1.0);
        double percentage = (kelvin - minKelvin) / (maxKelvin - minKelvin) * 100.0;
        return 100 - (int) percentage;
    }
}
