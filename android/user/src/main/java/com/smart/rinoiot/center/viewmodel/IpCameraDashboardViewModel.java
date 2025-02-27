package com.smart.rinoiot.center.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart.rinoiot.center.bean.CameraTiltDirection;
import com.smart.rinoiot.center.bean.IpCameraIntercomStatus;
import com.smart.rinoiot.common.api.IPCService;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.device.RinoDataPoint;
import com.smart.rinoiot.common.ipcimpl.RinoIPCContainerView;
import com.smart.rinoiot.common.ipcimpl.RinoIPCController;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.manager.CacheDataManager;
import com.smart.rinoiot.common.mqtt2.Manager.MqttConvertManager;
import com.smart.rinoiot.common.network.RetrofitUtils;
import com.smart.rinoiot.common.utils.StringUtil;
import com.smart.rinoiot.common.utils.ToastUtil;
import com.smart.rinoiot.family.manager.FamilyNetworkManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author edwin
 */
public class IpCameraDashboardViewModel extends BaseViewModel {

    private static final String TAG = "IpCameraDashboardViewModel";

    private final MutableLiveData<String> ipCameraStreamUidLiveData = new MutableLiveData<>();
    private final MutableLiveData<IpCameraIntercomStatus> intercomStatusMutableLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<List<DeviceInfoBean>> ipCamerasLiveData = new MutableLiveData<>();

    public IpCameraDashboardViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<DeviceInfoBean>> getIpCamerasLiveData() {
        return ipCamerasLiveData;
    }

    public MutableLiveData<String> getIpCameraStreamUidLiveData() {
        return ipCameraStreamUidLiveData;
    }

    public MutableLiveData<IpCameraIntercomStatus> getIntercomStatusMutableLiveData() {
        return intercomStatusMutableLiveData;
    }

    public void fetchCameras(String homeId) {
        if (StringUtil.isBlank(homeId)) {
            Log.e(TAG, "fetchCameras: home id is blank");
            return;
        }

        List<DeviceInfoBean> deviceList = CacheDataManager.getInstance().getAllDeviceList(homeId);
        if (null == deviceList) {
            CallbackListener<List<DeviceInfoBean>> callback = getHomeDeviceListCallback();
            FamilyNetworkManager.getInstance().getHomeDeviceListAsync(
                    Collections.singletonList(homeId), callback
            );
        } else {
            filterDeviceList(deviceList);
        }
    }

    /**
     * Home device list request callback
     *
     * @return the home device list callback listener.
     */
    CallbackListener<List<DeviceInfoBean>> getHomeDeviceListCallback() {
        return new CallbackListener<List<DeviceInfoBean>>() {
            @Override
            public void onSuccess(List<DeviceInfoBean> data) {
                filterDeviceList(data);
            }

            @Override
            public void onError(String code, String error) {
                ipCamerasLiveData.postValue(getAddEmptyList(null));
            }
        };
    }

    /**
     * Filters cameras from device list
     *
     * @param deviceInfoList the list of devices
     */
    public void filterDeviceList(List<DeviceInfoBean> deviceInfoList) {
        if (null == deviceInfoList || deviceInfoList.isEmpty()) {
            ipCamerasLiveData.postValue(getAddEmptyList(deviceInfoList));
            return;
        }

        // Stub [BEGIN]
        Predicate<DeviceInfoBean> testCams = deviceInfoBean -> !deviceInfoBean.getName()
                .contains("vdev");
        // Stub [END]

        List<DeviceInfoBean> cameras = deviceInfoList.stream()
                .filter(DeviceInfoBean::isIpc)
                .filter(testCams)
                .collect(Collectors.toList());
        ipCamerasLiveData.postValue(getAddEmptyList(cameras));
    }

    /**
     * 设备不足4个，添加空的数据
     */
    private List<DeviceInfoBean> getAddEmptyList(List<DeviceInfoBean> deviceInfoList) {
        List<DeviceInfoBean> tempList = new ArrayList<>();
        if (deviceInfoList == null || deviceInfoList.isEmpty()) {
            deviceInfoList = new ArrayList<>();
        }
        if (!deviceInfoList.isEmpty()) {
            tempList.addAll(deviceInfoList);
        }
        for (int i = 0; i < 4 - deviceInfoList.size(); i++) {
            tempList.add(new DeviceInfoBean());
        }
        return tempList;
    }

    /**
     * Fetches the camera stream token
     *
     * @param view       the RinoIPCContainerView
     * @param deviceId   the device identifier
     * @param deviceName the device name
     */
    public void fetchCameraStreamToken(RinoIPCContainerView view, String deviceId, String deviceName) {
        String params = "{\"che.audio.custom_payload_type\":0}";
        view.fetchToken(
                deviceId, deviceName, params, false, ipCameraStreamUidLiveData::postValue
        );
    }

    /**
     * Stop camera tilting
     *
     * @param deviceId the device identifier
     */
    public void stopCameraTilt(String deviceId) {
        Map<String, Object> map = new HashMap<>(1);
        map.put(RinoDataPoint.RINO_CAMERA_TILT_STOP_DP.getValue(), true);
        MqttConvertManager.getInstance().publish(deviceId, map);
    }

    /**
     * Start camera tilting
     *
     * @param deviceId  device identifier
     * @param direction the tilt direction
     */
    public void startCameraTilt(String deviceId, CameraTiltDirection direction) {
        Map<String, Object> map = new HashMap<>(1);
        Log.d(TAG, "tiltCamera: direction" + direction.getValue());
        map.put(
                RinoDataPoint.RINO_CAMERA_TILT_START_DP.getValue(),
                String.valueOf(direction.getValue())
        );
        MqttConvertManager.getInstance().publish(deviceId, map);
    }

    /**
     * 可以加一些UI上的等待过程
     *
     * @param uid      the uid
     * @param deviceId the device identifier
     */
    public void openIntercom(String uid, String deviceId) {
        // Build params
        Map<String, Object> params = new HashMap<>(3);
        Map<String, Object> cmdData = new HashMap<>(7);
        cmdData.put("Samples", 16000);
        cmdData.put("ack", 1);
        cmdData.put("channel", 1);
        cmdData.put("format", 0);
        cmdData.put("rate", 0);
        cmdData.put("volume", 0);
        cmdData.put("uid", uid);
        params.put("cmdType", "start_tall");
        params.put("deviceId", deviceId);
        params.put("cmdData", cmdData);
        BaseRequestListener<Object> requestListener = new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                String controllerParams = "{\"che.audio.custom_payload_type\":0}";
                RinoIPCController.getInstance().setParameters(deviceId, controllerParams, null);
                RinoIPCController.getInstance().enableTalk(deviceId, true, null);
                intercomStatusMutableLiveData.postValue(IpCameraIntercomStatus.IS_OPEN);
            }

            @Override
            public void onError(String error, String msg) {
                Log.e(TAG, "onError: code=" + error + " | msg=" + msg);
                ToastUtil.showErrorMsg(msg);
            }
        };
        RetrofitUtils.getService(IPCService.class).commonCmd(params).enqueue(requestListener);
    }


    /**
     * 可以加一些UI上的等待过程
     *
     * @param uid      the uid
     * @param deviceId the device identifier
     */
    public void closeIntercom(String uid, String deviceId) {
        Map<String, Object> params = new HashMap<>(3);
        Map<String, Object> cmdData = new HashMap<>(1);
        cmdData.put("uid", uid);
        params.put("cmdType", "stop_tall");
        params.put("deviceId", deviceId);
        params.put("cmdData", cmdData);
        BaseRequestListener<Object> listener = new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                RinoIPCController.getInstance().enableTalk(deviceId, false, null);
                intercomStatusMutableLiveData.postValue(IpCameraIntercomStatus.IS_CLOSE);
            }

            @Override
            public void onError(String error, String msg) {
                Log.e(TAG, "onError: code=" + error + " | msg=" + msg);
            }
        };
        RetrofitUtils.getService(IPCService.class).commonCmd(params).enqueue(listener);
    }
}
