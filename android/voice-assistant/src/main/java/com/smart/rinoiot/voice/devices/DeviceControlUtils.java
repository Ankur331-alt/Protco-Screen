package com.smart.rinoiot.voice.devices;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dsh.openai.home.model.ControlIntent;
import com.google.gson.Gson;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.Metadata;
import com.smart.rinoiot.common.datastore.DataSourceManager;
import com.smart.rinoiot.common.device.DeviceCmdConverterUtils;
import com.smart.rinoiot.common.matter.MtrDeviceControlManager;
import com.smart.rinoiot.common.matter.MtrDeviceDataUtils;
import com.smart.rinoiot.common.mqtt2.Manager.MqttConvertManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * @author edwin
 */
public class DeviceControlUtils {

    private static final String TAG = "DeviceControlUtils";

    /**
     * The entry point for device control
     * @param context the application context
     * @param list the list of devices
     * @param intent the control intent
     * @param value the control value
     * @return true on success, false otherwise.
     */
    public static boolean handleControl(
        @ApplicationContext Context context,
        @NonNull List<String> list,
        ControlIntent intent,
        String value
    ){
        boolean status = false;
        String intentValue = intent.getIntent();
        Log.d(TAG, "onDeviceControl: devices=" +
                new Gson().toJson(list) + " | control intent=" +
                intentValue + " | value=" + value
        );
        if(ControlIntent.Brightness.getIntent().contentEquals(intentValue)) {
            double brightness = Double.parseDouble(value);
            Log.d(TAG, "onDeviceControl: brightness=" + (int) brightness);
            status = DeviceControlUtils.brightness(context, list, (int) brightness);
        }else if(ControlIntent.Power.getIntent().contentEquals(intentValue)) {
            boolean power =  Boolean.parseBoolean(value);
            Log.d(TAG, "onDeviceControl: power=" + power);
            status = DeviceControlUtils.power(context, list, power);
        }else if(ControlIntent.Color.getIntent().contentEquals(intentValue)) {
            String[] colorArray = value.split(",");
            double hue = Double.parseDouble(colorArray[0]);
            double saturation = Double.parseDouble(colorArray[1]);
            double brightness = Double.parseDouble(colorArray[2]);
            Log.d(TAG, "onDeviceControl: color=hsv("
                    + (int) hue + ", "
                    + (int) saturation + " ,"
                    + (int) brightness + ")"
            );
            status = DeviceControlUtils.color(
                    context,
                    list,
                    (int) hue,
                    (int) saturation,
                    (int) brightness
            );
        }else if(ControlIntent.ColorTemperature.getIntent().contentEquals(intentValue)){
            double temperature = Double.parseDouble(value);
            Log.d(TAG, "onDeviceControl: colorTemperature=" + (int) temperature);
            status = DeviceControlUtils.colorTemperature(context, list, (int) temperature);
        }
        return status;
    }

    /**
     * Dispatches the color temperature commands
     *
     * @param context the application context
     * @param list the list of devices
     * @param temperature the color temperature value in kelvins
     * @return true when at least one, more or all commands have been
     * dispatched successfully, false otherwise
     */
    public static boolean colorTemperature(
            @ApplicationContext Context context,
            @NonNull List<String> list,
            int temperature
    ){
        Map<String, DeviceInfoBean> deviceMap = DeviceDataUtils.getCachedDevicesMap();
        if(deviceMap.isEmpty()){
            return false;
        }

        int colorTemp = DeviceCmdConverterUtils.kelvinToPercentage(temperature);

        AtomicInteger counter = new AtomicInteger(0);
        list.parallelStream().forEach(deviceId -> {
            try{
                boolean status;
                DeviceInfoBean deviceInfo = deviceMap.get(deviceId);
                if(null == deviceInfo){
                    return;
                }

                if(MtrDeviceDataUtils.isMatterDevice(deviceInfo)){
                    Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfo.getMetaInfo());
                    status = MtrDeviceControlManager.getInstance(context)
                            .colorTemperature(metadata, colorTemp);
                    if(status){
                        DataSourceManager.getInstance().updateDeviceColorTempState(
                                deviceId, Math.min(Math.max((100-temperature), 0), 100)
                        );
                    }
                }else{
                    Map<String, Object> command = DeviceCmdConverterUtils
                            .toColorTemperatureCmd(deviceInfo.getId(), colorTemp);
                    status = MqttConvertManager.getInstance().publish(deviceInfo.getId(), command);
                }
                if(status){
                    counter.getAndIncrement();
                }
            }catch (Exception ex) {
                Log.e(TAG, "colorTemperature: failed. Cause= " + ex.getLocalizedMessage());
            }
        });
        return counter.get() > 0;
    }

    /**
     * Dispatches the color temperature commands
     *
     * @param context the application context
     * @param list the list of devices
     * @param hue the hue value
     * @param saturation the saturation value
     * @param value the brightness value
     * @return true when at least one, more or all commands have been dispatched
     * successfully, false otherwise
     */
    public static boolean color(
            @ApplicationContext Context context,
            @NonNull List<String> list,
            int hue,
            int saturation,
            int value
    ){
        Map<String, DeviceInfoBean> deviceMap = DeviceDataUtils.getCachedDevicesMap();
        if(deviceMap.isEmpty()){
            return false;
        }

        AtomicInteger counter = new AtomicInteger(0);
        list.parallelStream().forEach(deviceId -> {
            try{
                boolean status;
                DeviceInfoBean deviceInfo = deviceMap.get(deviceId);
                if(null == deviceInfo){
                    return;
                }

                if(MtrDeviceDataUtils.isMatterDevice(deviceInfo)){
                    Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfo.getMetaInfo());
                    status = MtrDeviceControlManager.getInstance(context)
                            .color(metadata, hue, saturation, value);
                    if(status){
                        DataSourceManager.getInstance().updateDeviceColorState(
                                deviceId, hue
                        );
                    }
                }else{
                    Map<String, Object> command = DeviceCmdConverterUtils
                            .toColorCmd(deviceInfo.getId(), hue, saturation, value);
                    status = MqttConvertManager.getInstance().publish(deviceInfo.getId(), command);
                }
                if(status){
                    counter.getAndIncrement();
                }
            }catch (Exception ex) {
                Log.e(TAG, "color: failed. Cause= " + ex.getLocalizedMessage());
            }
        });
        return counter.get() > 0;
    }

    /**
     * Dispatches the brightness commands
     *
     * @param context the application context
     * @param list the list of devices
     * @param brightness the brightness
     * @return true when at least one, more or all commands have been dispatched successfully,
     * false otherwise
     */
    public static boolean brightness(
            @ApplicationContext Context context,
            @NonNull List<String> list,
            int brightness
    ){
        Map<String, DeviceInfoBean> deviceMap = DeviceDataUtils.getCachedDevicesMap();
        if(deviceMap.isEmpty()){
            return false;
        }

        AtomicInteger counter = new AtomicInteger(0);
        list.parallelStream().forEach(deviceId -> {
            try{
                boolean status;
                DeviceInfoBean deviceInfo = deviceMap.get(deviceId);
                if(null == deviceInfo){
                    return;
                }

                if(MtrDeviceDataUtils.isMatterDevice(deviceInfo)){
                    Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfo.getMetaInfo());
                    status = MtrDeviceControlManager.getInstance(context)
                            .brightness(metadata, brightness);
                    if(status){
                        DataSourceManager.getInstance().updateDeviceBrightnessState(
                                deviceId, brightness
                        );
                    }
                }else{
                    Map<String, Object> command = DeviceCmdConverterUtils
                            .toBrightnessCmd(deviceInfo.getId(), brightness);
                    status = MqttConvertManager.getInstance().publish(deviceInfo.getId(), command);
                }
                if(status){
                    counter.getAndIncrement();
                }
            }catch (Exception ex) {
                Log.e(TAG, "brightness: failed. Cause= " + ex.getLocalizedMessage());
            }
        });
        return counter.get() > 0;
    }

    /**
     * Dispatches the power commands
     *
     * @param context the application context
     * @param list the list of devices
     * @param power the power status
     * @return true when at least one, more or all commands have been dispatched successfully,
     * false otherwise
     */
    public static boolean power(
            @ApplicationContext Context context,
            @NonNull List<String> list,
            boolean power
    ){
        Map<String, DeviceInfoBean> deviceMap = DeviceDataUtils.getCachedDevicesMap();
        if(deviceMap.isEmpty()){
            return false;
        }

        AtomicInteger counter = new AtomicInteger(0);
        list.parallelStream().forEach(deviceId -> {
            try{
                boolean status;
                DeviceInfoBean deviceInfo = deviceMap.get(deviceId);
                if(null == deviceInfo){
                    return;
                }

                if(MtrDeviceDataUtils.isMatterDevice(deviceInfo)){
                    Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfo.getMetaInfo());
                    status = MtrDeviceControlManager.getInstance(context)
                            .power(metadata, power);
                    if(status){
                        DataSourceManager.getInstance().updateDevicePowerState(
                                deviceId, power
                        );
                    }
                }else{
                    Map<String, Object> command = DeviceCmdConverterUtils
                            .toPowerCmd(deviceInfo.getId(), power);
                    status = MqttConvertManager.getInstance().publish(deviceInfo.getId(), command);
                }
                if(status){
                    counter.getAndIncrement();
                }
            }catch (Exception ex) {
                Log.e(TAG, "power: failed. Cause= " + ex.getLocalizedMessage());
            }
        });
        return counter.get() > 0;
    }
}
