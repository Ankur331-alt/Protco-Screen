package com.smart.rinoiot.voice.devices;

import com.dsh.matter.model.device.DeviceType;
import com.dsh.matter.util.device.DeviceTypeUtil;
import com.dsh.openai.home.model.HomeDevice;
import com.dsh.openai.home.model.HomeDeviceStatesBuilder;
import com.smart.rinoiot.common.bean.AssetBean;
import com.smart.rinoiot.common.bean.DeviceDpBean;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.bean.Metadata;
import com.smart.rinoiot.common.device.RinoDataPoint;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.matter.MtrDeviceDataUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author edwin
 */
public class DeviceDataUtils {
    /**
     * Converts the cached device to home devices
     *
     * @return the list of home devices
     */
    public static List<HomeDevice> getCachedHomeDevice(){
        AssetBean home = CacheDataManager.getInstance().getCurrentFamily();
        if(null == home){
            return new ArrayList<>();
        }

        List<DeviceInfoBean> deviceInfoBeans = CacheDataManager
                .getInstance().getAllDeviceList(home.getId());

        return deviceInfoBeans.stream()
                .map(deviceInfoBean -> {
                    Map<String, Object> states =
                            new HomeDeviceStatesBuilder().setPower(false).build();
                    Metadata metadata = MtrDeviceDataUtils.toMetadata(deviceInfoBean.getMetaInfo());
                    String deviceType;
                    if(MtrDeviceDataUtils.isMatterDevice(deviceInfoBean)) {
                        deviceType = null != metadata ?
                                DeviceTypeUtil.toEnum(metadata.getDeviceType()).name() : "Unknown";
                    }else{
                        deviceType = getDeviceType(deviceInfoBean.getId());
                    }

                    return new HomeDevice(
                            deviceInfoBean.getId(),
                            deviceInfoBean.getName(),
                            deviceType,
                            home.getName(),
                            home.getName(),
                            states
                    );
                }).collect(Collectors.toList());
    }

    /**
     * Map cached devices' metadata
     *
     * @return the mapped devices' metadata
     */
    public static HashMap<String, DeviceInfoBean> getCachedDevicesMap() {
        AssetBean home = CacheDataManager.getInstance().getCurrentFamily();
        if(null == home){
            return new HashMap<>(0);
        }

        List<DeviceInfoBean> deviceInfoBeans = CacheDataManager
                .getInstance().getAllDeviceList(home.getId());
        HashMap<String, DeviceInfoBean> deviceMap = new HashMap<>(0);
        deviceInfoBeans.forEach(deviceInfoBean -> deviceMap.put(
                deviceInfoBean.getId(),
                deviceInfoBean
        ));
        return deviceMap;
    }

    /**
     * Infers the device type from device's data points
     *
     * @param deviceId device identifier.
     * @return the device type.
     */
    private static String getDeviceType(String deviceId) {
        List<DeviceDpBean> deviceDpData = CacheDataManager.getInstance().getDeviceDpList(deviceId);
        if(deviceDpData.isEmpty()) {
            return DeviceType.Socket.name();
        }

        Set<String> dataPoints = deviceDpData.stream().map(DeviceDpBean::getKey).collect(Collectors.toSet());
        if(dataPoints.isEmpty()){
            return DeviceType.Socket.name();
        }
        Optional<String> match = dataPoints.stream().filter(dp ->
                dp.contains(RinoDataPoint.RINO_COLOR_DP.getValue())||
                dp.contains(RinoDataPoint.RINO_BRIGHTNESS_DP.getValue())||
                dp.contains(RinoDataPoint.RINO_COLOR_TEMP_DP.getValue())
        ).findFirst();
        if(match.isPresent()){
            return DeviceType.ExtendedColorLight.name();
        }else{
            return DeviceType.Socket.name();
        }
    }
}
