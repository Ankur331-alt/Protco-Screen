package com.smart.device.manager;

import android.util.Log;

import com.google.gson.Gson;
import com.smart.device.api.DeviceApiService;
import com.smart.device.bean.OtaUpgradeBean;
import com.smart.rinoiot.common.api.CommonApiService;
import com.smart.rinoiot.common.bean.DeviceInfoBean;
import com.smart.rinoiot.common.listener.BaseRequestListener;
import com.smart.rinoiot.common.listener.CallbackListener;
import com.smart.rinoiot.common.network.RetrofitUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DeviceNetworkManager {

    private static final String TAG = "DeviceNetworkManager";
    private static DeviceNetworkManager instance;

    private DeviceNetworkManager() {
    }

    public static DeviceNetworkManager getInstance() {
        if (instance == null) {
            instance = new DeviceNetworkManager();
        }
        return instance;
    }

    /**
     * 解除资产设备绑定
     *
     * @param isCleanData 是否清除数据(1=清除，0=不清除)
     */
    public void unbindToAsset(String deviceId, int isCleanData, CallbackListener<Object> callback) {
        Map<String, Object> map = new HashMap<>();
        map.put("deviceId", deviceId);
        map.put("isCleanData", isCleanData);
        RetrofitUtils.getService(DeviceApiService.class).unbindToAsset(map).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 配网成功，将设备绑定到对于子资产（房间）下
     */
    public void initDevice(String assetId, String deviceId, String deviceName, CallbackListener<Objects> callback) {
        Map<String, Object> map = new HashMap<>();
        map.put("assetId", assetId);
        map.put("deviceUuid", deviceId);
        map.put("deviceName", deviceName);
        RetrofitUtils.getService(DeviceApiService.class).initDevice(map).enqueue(new BaseRequestListener<Object>() {
            @Override
            public void onResult(Object result) {
                if (callback != null) {
                    callback.onSuccess((Objects) result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 检查设备固件是否需要升级
     */
    public void checkUpgrade(String deviceId, CallbackListener<OtaUpgradeBean> callback) {
        RetrofitUtils.getService(DeviceApiService.class).checkUpgrade(deviceId).enqueue(new BaseRequestListener<OtaUpgradeBean>() {
            @Override
            public void onResult(OtaUpgradeBean result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 绑定资产设备
     */
    public void bindToAsset(Map<String, Object> map, CallbackListener<DeviceInfoBean> callback) {
        RetrofitUtils.getService(DeviceApiService.class).bindToAsset(map).enqueue(new BaseRequestListener<DeviceInfoBean>() {
            @Override
            public void onResult(DeviceInfoBean result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * 绑定资产设备
     * action:AddVirtualDevice,productId=5T6VDAomhtNoS3,token=95545b92b9d748bfa9896baf8fe04477
     */
    public void bindMatter(Map<String, Object> map, CallbackListener<String> callback) {
        RetrofitUtils.getService(CommonApiService.class).bindMatter(map).enqueue(new BaseRequestListener<String>() {
            @Override
            public void onResult(String result) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }

            @Override
            public void onError(String error, String msg) {
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        });
    }

    /**
     * Request the matter devices that can be paired
     * @param homeId the home identifier
     * @param callback the callback
     */
    public void matterFind(String homeId, CallbackListener<Object> callback) {
        Map<String, Object> params = new HashMap<>(1);
        params.put("rootAssetId", homeId);
        BaseRequestListener<Boolean> listener = new BaseRequestListener<Boolean>() {
            @Override
            public void onResult(Boolean data) {
                Log.d(TAG, "onResult: data=" + data);
                if (callback != null) {
                    callback.onSuccess(data);
                }
            }

            @Override
            public void onError(String error, String msg) {
                Log.e(TAG, "onError: code=" + error +  " | message= " + msg);
                if (callback != null) {
                    callback.onError(error, msg);
                }
            }
        };

        RetrofitUtils.getService(CommonApiService.class).matterFind(params).enqueue(listener);
    }
}
