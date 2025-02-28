package com.smart.rinoiot.common.datastore;

import android.content.Context;
import android.util.Log;

import com.dsh.matter.model.device.StateAttribute;
import com.google.gson.Gson;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.datastore.persistence.UnifiedDeviceState;
import com.smart.rinoiot.common.datastore.persistence.UnifiedDeviceStateBuilder;
import com.smart.rinoiot.common.datastore.persistence.WeatherData;
import com.smart.rinoiot.common.device.DeviceCmdConverterUtils;
import com.smart.rinoiot.common.device.RinoDataPoint;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.common.weather.model.RinoWeatherData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author edwin
 */
public class DataSourceManager {
    private static DataSourceManager instance;
    private final WeatherDataSource mWeatherDataSource;
    private final UnifiedDeviceStateDataSource mStatesDataSource;
    private static final String TAG = "DataSourceManager";

    private DataSourceManager(
            UnifiedDeviceStateDataSource statesDataSource, WeatherDataSource weatherDataSource
    ) {
        // set the data sources
        this.mStatesDataSource = statesDataSource;
        this.mWeatherDataSource = weatherDataSource;

        // removes any cached data.
        this.mStatesDataSource.deleteStates();
    }

    public static void init(@ApplicationContext Context context) {
        if (instance != null) {
            return;
        }

        // inject the device states data source
        UnifiedDeviceStateDataSource statesDataSource = DataSourceInjection
                .provideUnifiedDeviceStateDataSource(context);

        // inject the weather data source
        WeatherDataSource weatherDataSource = DataSourceInjection.provideWeatherDataSource(context);

        // create an instance of the data sources
        instance = new DataSourceManager(statesDataSource, weatherDataSource);
    }

    public static DataSourceManager getInstance() {
        if (instance == null) {
            throw new RuntimeException("Data source manager is not initialized yet");
        }
        return instance;
    }

    public Flowable<UnifiedDeviceState> getDeviceStates(String deviceId){
        return mStatesDataSource.getStates(deviceId);
    }

    public Flowable<List<UnifiedDeviceState>> getDeviceStates(Set<String> deviceIds) {
        List<String> ids = new ArrayList<>(deviceIds);
        return mStatesDataSource.getStates(ids);
    }

    public Flowable<WeatherData> getWeatherData() {
        return mWeatherDataSource.select();
    }

    public void saveWeatherData(RinoWeatherData rinoWeatherData) {
        if(null == rinoWeatherData){
            throw new IllegalArgumentException("Weather data cannot be null");
        }
        String id = "identifier";
        int temperature = (int) rinoWeatherData.getTemperature();
        WeatherData weatherData =  new WeatherData(
                id, "", temperature,
                rinoWeatherData.getWindSpeed(),
                rinoWeatherData.getHumidity(),
                rinoWeatherData.getAir()
        );
        mWeatherDataSource.insert(weatherData).subscribeOn(Schedulers.io()).subscribe();
    }

    public void clear() {
        if(null == mStatesDataSource){
            return;
        }
        this.mStatesDataSource.deleteStates().subscribeOn(Schedulers.io()).subscribe();
    }

    public void addDeviceStates(List<DeviceInfoBean> devices) {
        if(devices.isEmpty()) {
            return;
        }

        List<UnifiedDeviceState> deviceStates = devices.stream()
                .filter(deviceInfoBean -> {
                    if(StringUtil.isBlank(deviceInfoBean.getId())) {
                        Log.d(TAG, "addDeviceStates: device Id=" + deviceInfoBean.getName());
                        return false;
                    }else {
                        return true;
                    }
                })
                .map(
                deviceInfoBean -> new UnifiedDeviceState(
                        deviceInfoBean.getId(), (deviceInfoBean.getOnlineStatus() == 1)
                )
        ).collect(Collectors.toList());
        mStatesDataSource.addStates(deviceStates).subscribeOn(Schedulers.io()).subscribe();
    }

    private boolean equals(UnifiedDeviceState previous, UnifiedDeviceState current) {
        if(!previous.getDeviceId().contentEquals(current.getDeviceId())){
            return false;
        }

        if(previous.isOnline() != current.isOnline()){
            return false;
        }

        if(previous.isSwitchOne() != current.isSwitchOne()){
            return false;
        }

        if(previous.isSwitchTwo() != current.isSwitchTwo()){
            return false;
        }

        if(previous.isSwitchThree() != current.isSwitchThree()){
            return false;
        }

        if(previous.isSwitchFour() != current.isSwitchFour()){
            return false;
        }

        if(previous.getHue() != current.getHue()){
            return false;
        }

        if(previous.getBrightness() != current.getBrightness()){
            return false;
        }

        if(previous.getColorTemperature() != current.getColorTemperature()) {
            return false;
        }

        return true;
    }

    public void updateDeviceStates(String deviceId, Map<String, Object> states) {
        if (states.isEmpty()) {
            return;
        }

        try{
            Log.d(TAG, "updateDeviceStates: hash states=" + new Gson().toJson(states));
            UnifiedDeviceState unifiedDeviceState = mStatesDataSource.getStates(deviceId)
                    .subscribeOn(Schedulers.io()).blockingFirst();
            UnifiedDeviceStateBuilder builder = new UnifiedDeviceStateBuilder(unifiedDeviceState);
            states.keySet().forEach(key-> {
                if(key.contentEquals(RinoDataPoint.RINO_SWITCH_DP.getValue()) ||
                        key.contentEquals(RinoDataPoint.RINO_SWITCH_1_DP.getValue()) ||
                        key.contentEquals(RinoDataPoint.RINO_SWITCH_LED_DP.getValue()) ||
                        key.contentEquals(RinoDataPoint.RINO_SPIN_SWITCH_DP.getValue()) ||
                        key.contentEquals(RinoDataPoint.RINO_MTR_SWITCH_DP.getValue())
                ){
                    boolean status = (boolean)Optional.ofNullable(states.getOrDefault(
                            key, false
                    )).orElse(false);
                    builder.setSwitchOne(status);
                } else if(key.contentEquals(RinoDataPoint.RINO_SWITCH_2_DP.getValue())){
                    boolean status = (boolean)Optional.ofNullable(states.getOrDefault(
                            key, false
                    )).orElse(false);
                    builder.setSwitchTwo(status);
                } else if(key.contentEquals(RinoDataPoint.RINO_SWITCH_3_DP.getValue())){
                    boolean status = (boolean)Optional.ofNullable(states.getOrDefault(
                            key, false
                    )).orElse(false);
                    builder.setSwitchThree(status);
                } else if(key.contentEquals(RinoDataPoint.RINO_SWITCH_4_DP.getValue())){
                    boolean status = (boolean)Optional.ofNullable(states.getOrDefault(
                            key, false
                    )).orElse(false);
                    builder.setSwitchFour(status);
                } else if(key.contentEquals(RinoDataPoint.RINO_COLOR_TEMP_DP.getValue())) {
                    int temperature = Double.valueOf(String.valueOf(states.getOrDefault(
                            key, 0
                    ))).intValue();
                    if(!states.containsKey(RinoDataPoint.RINO_PAINT_COLOUR_DP.getValue())){
                        temperature /= 10;
                        builder.setColorTemperature(temperature);
                    }
                }else if(key.contentEquals(RinoDataPoint.RINO_BRIGHTNESS_DP.getValue())){
                    int brightness = Double.valueOf(String.valueOf(states.getOrDefault(
                            key, 0
                    ))).intValue();
                    if(!states.containsKey(RinoDataPoint.RINO_COLOUR_DATA_DP.getValue())){
                        builder.setBrightness(brightness);
                    }
                } else if (key.contentEquals(RinoDataPoint.RINO_BRIGHT_VALUE_DP.getValue())){
                    int brightness = Double.valueOf(String.valueOf(states.getOrDefault(
                            key, 0
                    ))).intValue();
                    brightness /= 10;
                    builder.setBrightness(brightness);
                }
                else if (key.contentEquals(RinoDataPoint.RINO_COLOR_DP.getValue())) {
                    String color = (String) states.getOrDefault(key, "");
                    Log.d(TAG, "updateDeviceProperties: color=" + color);
                } else if (key.contentEquals(RinoDataPoint.RINO_COLOUR_DATA_DP.getValue())) {
                    String colorData = (String) states.getOrDefault(key, "");
                    int hue = DeviceCmdConverterUtils.toHue(colorData);
                    builder.setHue(hue);
                } else if (key.contentEquals(RinoDataPoint.RINO_PAINT_COLOUR_DP.getValue())) {
                    String paintData = (String) states.getOrDefault(key, "");
                    if (DeviceCmdConverterUtils.isWhiteModePaint(paintData)) {
                        int temperature = DeviceCmdConverterUtils.toColorTemperature(paintData);
                        temperature /= 10;
                        builder.setColorTemperature(temperature);
                    } else {
                        int hue = DeviceCmdConverterUtils.toHue(paintData);
                        builder.setHue(hue);
                    }
                } else if(key.contentEquals(RinoDataPoint.RINO_ONLINE_DP.getValue())){
                    boolean status = (boolean)Optional.ofNullable(states.getOrDefault(
                            key, false
                    )).orElse(false);
                    builder.setOnline(status);
                }
            });

            UnifiedDeviceState currentStates = builder.build();
            if(equals(unifiedDeviceState, currentStates)){
                return;
            }

            mStatesDataSource.updateStates(currentStates)
                    .subscribeOn(Schedulers.io()).subscribe();
        } catch (Exception ex) {
            Log.e(TAG, "updateDeviceStates: failed to update. " + ex.getLocalizedMessage());
        }
    }

    @SuppressWarnings("all")
    public void updateDeviceStates(String deviceId, HashMap<StateAttribute, Object> states){
        if(states.isEmpty()){
            return;
        }

        UnifiedDeviceStateBuilder builder = new UnifiedDeviceStateBuilder(deviceId);
        // set brightness
        Double brightness = Double.parseDouble(String.valueOf(
                states.getOrDefault(StateAttribute.Brightness, 0)
        ));
        builder.setBrightness(brightness.intValue());

        // set color temperature
        Double colorTemperature = Double.parseDouble(String.valueOf(
                states.getOrDefault(StateAttribute.ColorTemperature, 0)
        ));
        builder.setColorTemperature(Math.min(Math.max((100-colorTemperature.intValue()), 0), 100));

        // set power
        boolean power = (boolean) Optional.ofNullable(
                states.getOrDefault(StateAttribute.Switch, false)
        ).orElse(false);
        builder.setSwitchOne(power);

        //set online status
        boolean online = (boolean) Optional.ofNullable(
                states.getOrDefault(StateAttribute.Online, false)
        ).orElse(false);
        builder.setOnline(online);

        try{
            UnifiedDeviceState unifiedDeviceState = mStatesDataSource.getStates(deviceId)
                    .subscribeOn(Schedulers.io()).blockingFirst();
            builder.setLiked(unifiedDeviceState.isLiked());
            mStatesDataSource.updateStates(builder.build())
                    .subscribeOn(Schedulers.io()).subscribe();
        } catch (Exception ex) {
            Log.e(TAG, "updateDeviceStates: failed to update. " + ex.getLocalizedMessage());
        }
    }

    @SuppressWarnings("all")
    public void updateDeviceStates(String deviceId, List<DeviceDpBean> dataPoints){
        if(StringUtil.isBlank(deviceId) || null == dataPoints || dataPoints.isEmpty()){
            return;
        }

        // build the states
        UnifiedDeviceStateBuilder builder = new UnifiedDeviceStateBuilder(deviceId);
        dataPoints.forEach(deviceDpBean -> {
            try {
                String key = deviceDpBean.getKey();
                if (key.contentEquals(RinoDataPoint.RINO_SWITCH_DP.getValue()) ||
                        key.contentEquals(RinoDataPoint.RINO_SWITCH_1_DP.getValue()) ||
                        key.contentEquals(RinoDataPoint.RINO_SWITCH_LED_DP.getValue()) ||
                        key.contentEquals(RinoDataPoint.RINO_SPIN_SWITCH_DP.getValue()) ||
                        key.contentEquals(RinoDataPoint.RINO_MTR_SWITCH_DP.getValue())
                ) {
                    boolean status = Boolean.parseBoolean(String.valueOf(deviceDpBean.getValue()));
                    builder.setSwitchOne(status);
                } else if (key.contentEquals(RinoDataPoint.RINO_SWITCH_2_DP.getValue())) {
                    boolean status = Boolean.parseBoolean(String.valueOf(deviceDpBean.getValue()));
                    builder.setSwitchTwo(status);
                } else if (key.contentEquals(RinoDataPoint.RINO_SWITCH_3_DP.getValue())) {
                    boolean status = Boolean.parseBoolean(String.valueOf(deviceDpBean.getValue()));
                    builder.setSwitchThree(status);
                } else if (key.contentEquals(RinoDataPoint.RINO_SWITCH_4_DP.getValue())) {
                    boolean status = Boolean.parseBoolean(String.valueOf(deviceDpBean.getValue()));
                    builder.setSwitchFour(status);
                } else if (key.contentEquals(RinoDataPoint.RINO_COLOR_TEMP_DP.getValue())) {
                    int temperature = Double.valueOf(String.valueOf(deviceDpBean.getValue())).intValue();
                    temperature /= 10;
                    builder.setColorTemperature(temperature);
                } else if(key.contentEquals(RinoDataPoint.RINO_BRIGHTNESS_DP.getValue())){
                    int brightness = Double.valueOf(String.valueOf(deviceDpBean.getValue())).intValue();
                    builder.setBrightness(brightness);
                } else if(key.contentEquals(RinoDataPoint.RINO_BRIGHT_VALUE_DP.getValue())) {
                    int brightness = Double.valueOf(String.valueOf(deviceDpBean.getValue())).intValue();
                    brightness /= 10;
                    builder.setBrightness(brightness);
                } else if (key.contentEquals(RinoDataPoint.RINO_COLOR_DP.getValue())) {
                    String color = String.valueOf(deviceDpBean.getValue());
                    Log.d(TAG, "updateDeviceProperties: color=" + color);
                } else if (key.contentEquals(RinoDataPoint.RINO_COLOUR_DATA_DP.getValue())) {
                    String colorData = String.valueOf(deviceDpBean.getValue());
                    int hue = DeviceCmdConverterUtils.toHue(colorData);
                    builder.setHue(hue);
                } else if(key.contentEquals(RinoDataPoint.RINO_PAINT_COLOUR_DP.getValue())) {
                    String paintData = String.valueOf(deviceDpBean.getValue());
                    if (DeviceCmdConverterUtils.isWhiteModePaint(paintData)) {
                        int temperature = DeviceCmdConverterUtils.toColorTemperature(paintData);
                        temperature /= 10;
                        builder.setColorTemperature(temperature);
                    }else{
                        int hue = DeviceCmdConverterUtils.toHue(paintData);
                        builder.setHue(hue);
                    }
                } else if(key.contentEquals(RinoDataPoint.RINO_ONLINE_DP.getValue())){
                    boolean status = Boolean.parseBoolean(String.valueOf(deviceDpBean.getValue()));
                    builder.setOnline(status);
                }
            }catch (Exception exception){
                Log.d(TAG, "updateDeviceStates: failed to convert. " + exception.getLocalizedMessage());
            }
        });

        try{
            UnifiedDeviceState unifiedDeviceState = mStatesDataSource.getStates(deviceId)
                    .subscribeOn(Schedulers.io()).blockingFirst();
            builder.setLiked(unifiedDeviceState.isLiked());
            builder.setOnline(unifiedDeviceState.isOnline());
            mStatesDataSource.updateStates(builder.build())
                    .subscribeOn(Schedulers.io()).subscribe();
        } catch (Exception ex) {
            Log.e(TAG, "updateDeviceStates: failed to update. " + ex.getLocalizedMessage());
        }
    }

    public void updateDeviceSwitchState(String deviceId, int switchPos, boolean power) {
        switch (switchPos) {
            case 0:
                mStatesDataSource.updateSwitchOneState(deviceId, power)
                        .subscribeOn(Schedulers.io())
                        .subscribe();
                break;
            case 1:
                mStatesDataSource.updateSwitchTwoState(deviceId, power)
                        .subscribeOn(Schedulers.io())
                        .subscribe();
                break;
            case 2:
                mStatesDataSource.updateSwitchThreeState(deviceId, power)
                        .subscribeOn(Schedulers.io())
                        .subscribe();
                break;
            case 3:
                mStatesDataSource.updateSwitchFourState(deviceId, power)
                        .subscribeOn(Schedulers.io())
                        .subscribe();
                break;
            default:
                break;
        }
    }

    public void updateDevicePowerState(String deviceId, boolean power) {
        mStatesDataSource.updateSwitchOneState(deviceId, power)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void updateDeviceColorState(String deviceId, int hue) {
        mStatesDataSource.updateHueState(deviceId, hue)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void updateDeviceBrightnessState(String deviceId, int brightness) {
        mStatesDataSource.updateBrightnessState(deviceId, brightness)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void updateDeviceColorTempState(String deviceId, int temperature) {
        mStatesDataSource.updateColorTemperatureState(deviceId, temperature)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void updateDeviceLikeStatus(Set<String> deviceIds, boolean liked) {
        List<String> ids = new ArrayList<>(deviceIds);
        mStatesDataSource.updateLiked(ids, liked).subscribeOn(Schedulers.io()).subscribe();
    }

    public void updateDeviceLikeStatus(String deviceId, boolean liked) {
        mStatesDataSource.updateLiked(Collections.singletonList(deviceId), liked)
                .subscribeOn(Schedulers.io()).subscribe();
    }
}
